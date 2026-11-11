import { useEffect, useRef, useState, useCallback, type ReactNode } from "react";
import {
  generateCodeVerifier,
  generateCodeChallenge,
  generateState,
  buildAuthorizationUrl,
  buildLogoutUrl,
  exchangeCodeForToken,
  parseJwtPayload,
  type TokenResponse,
} from "./pkce";
import { AuthContext } from "./AuthContext";
import { registerTokenSync, registerLogout } from "../lib/tokenSync";
import { tokenStore, TOKEN_KEY } from "./tokenStore";
import { refreshIfExpiring, refreshTokenIfPossible } from "../lib/refreshSingleton";

export function AuthProvider({ children }: { children: ReactNode }) {
  const [accessToken, setAccessToken] = useState<string | null>(null);
  const [user, setUser] = useState<Record<string, unknown> | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [authError, setAuthError] = useState<string | null>(null);

  const userRef = useRef<Record<string, unknown> | null>(null);
  const setTokenResponseRef = useRef<(t: TokenResponse) => void>(() => {});

  const setTokenResponse = (tokenResponse: TokenResponse) => {
    setAccessToken(tokenResponse.access_token);
    tokenStore.save(tokenResponse);
    try {
      setUser(parseJwtPayload(tokenResponse.access_token));
    } catch {
      // opaque token — no user claims available
    }
  };
  useEffect(() => {
    setTokenResponseRef.current = setTokenResponse;
    userRef.current = user;
  });

  useEffect(() => {
    const init = async () => {
      const storedToken = tokenStore.getAccess();
      if (storedToken) {
        try {
          const claims = parseJwtPayload(storedToken);
          const exp = claims["exp"] as number | undefined;
          if (!exp || Date.now() / 1000 < exp) {
            setAccessToken(storedToken);
            setUser(claims);
            setIsLoading(false);
            return;
          }
        } catch {
          setAccessToken(storedToken);
          setIsLoading(false);
          return;
        }
      }

      if (!tokenStore.getRefresh()) {
        tokenStore.clear();
        setIsLoading(false);
        return;
      }
      try {
        const newToken = await refreshTokenIfPossible(storedToken);
        if (!newToken) {
          tokenStore.clear();
        } else {
          setAccessToken(newToken);
          try {
            setUser(parseJwtPayload(newToken));
          } catch {
            // opaque token
          }
        }
      } catch {
        // The sign-in server could not be reached: keep the sign-in. The expired token is shown
        // as is and the first API call renews it (or retries) once the connection is back.
        if (storedToken) {
          setAccessToken(storedToken);
          try {
            setUser(parseJwtPayload(storedToken));
          } catch {
            // opaque token
          }
        }
      }
      setIsLoading(false);
    };
    init();
  }, []);

  const login = async () => {
    setAuthError(null);
    const codeVerifier = generateCodeVerifier();
    const codeChallenge = await generateCodeChallenge(codeVerifier);
    const state = generateState();
    sessionStorage.setItem("pkce_code_verifier", codeVerifier);
    sessionStorage.setItem("pkce_state", state);
    window.location.href = buildAuthorizationUrl(codeChallenge, state);
  };

  const handleCallback = useCallback(async (code: string, codeVerifier: string) => {
    const tokenResponse = await exchangeCodeForToken(code, codeVerifier);
    setTokenResponseRef.current(tokenResponse);
  }, []);

  const logout = useCallback(() => {
    const idToken = tokenStore.getId() ?? undefined;
    setAccessToken(null);
    setUser(null);
    tokenStore.clear();
    sessionStorage.removeItem("pkce_code_verifier");
    sessionStorage.removeItem("pkce_state");
    window.location.href = buildLogoutUrl(`${window.location.origin}/login`, idToken);
  }, []);

  useEffect(() => {
    registerTokenSync((t) => setTokenResponseRef.current(t));
    registerLogout(() => {
      logout();
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Another tab of this browser changed the sign-in (this one gets no event for its own writes).
  useEffect(() => {
    const onStorage = (e: StorageEvent) => {
      if (e.storageArea !== localStorage || (e.key !== null && e.key !== TOKEN_KEY)) return;
      const token = tokenStore.getAccess();
      if (!token) {
        // Signed out elsewhere: that tab already ended the session on the server.
        setAccessToken(null);
        setUser(null);
        if (window.location.pathname !== "/login") window.location.href = "/login";
        return;
      }
      let claims: Record<string, unknown> | null = null;
      try {
        claims = parseJwtPayload(token);
      } catch {
        // opaque token
      }
      const current = userRef.current;
      if (current && claims && current["sub"] !== claims["sub"]) {
        // Someone else signed in in another tab: this tab's screen belongs to the previous person.
        window.location.reload();
        return;
      }
      if (!current && window.location.pathname === "/login") {
        window.location.replace("/");
        return;
      }
      setAccessToken(token);
      if (claims) setUser(claims);
    };
    window.addEventListener("storage", onStorage);
    return () => window.removeEventListener("storage", onStorage);
  }, []);

  // Waking up (tab shown, window focused, network back): renew an expired token now instead of
  // waiting for the first request to be refused.
  useEffect(() => {
    const wake = () => {
      if (document.visibilityState === "hidden") return;
      void refreshIfExpiring().then((alive) => {
        if (!alive) logout();
      });
    };
    document.addEventListener("visibilitychange", wake);
    window.addEventListener("focus", wake);
    window.addEventListener("online", wake);
    return () => {
      document.removeEventListener("visibilitychange", wake);
      window.removeEventListener("focus", wake);
      window.removeEventListener("online", wake);
    };
  }, [logout]);

  const clearAuthError = () => setAuthError(null);

  return (
    <AuthContext.Provider
      value={{
        isAuthenticated: !!accessToken,
        isLoading,
        accessToken,
        user,
        authError,
        login,
        logout,
        handleCallback,
        clearAuthError,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}
