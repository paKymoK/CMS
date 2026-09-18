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
import { refreshTokenIfPossible } from "../lib/refreshSingleton";

export function AuthProvider({ children }: { children: ReactNode }) {
  const [accessToken, setAccessToken] = useState<string | null>(null);
  const [user, setUser] = useState<Record<string, unknown> | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [authError, setAuthError] = useState<string | null>(null);

  const setTokenResponseRef = useRef<(t: TokenResponse) => void>(() => {});

  const setTokenResponse = (tokenResponse: TokenResponse) => {
    setAccessToken(tokenResponse.access_token);
    sessionStorage.setItem("access_token", tokenResponse.access_token);
    if (tokenResponse.refresh_token) {
      sessionStorage.setItem("refresh_token", tokenResponse.refresh_token);
    }
    if (tokenResponse.id_token) {
      sessionStorage.setItem("id_token", tokenResponse.id_token);
    }
    try {
      setUser(parseJwtPayload(tokenResponse.access_token));
    } catch {
      // opaque token — no user claims available
    }
  };
  useEffect(() => {
    setTokenResponseRef.current = setTokenResponse;
  });

  useEffect(() => {
    const init = async () => {
      const storedToken = sessionStorage.getItem("access_token");
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
          sessionStorage.removeItem("access_token");
        } catch {
          setAccessToken(storedToken);
          setIsLoading(false);
          return;
        }
      }

      const newToken = await refreshTokenIfPossible();
      if (!newToken) {
        sessionStorage.removeItem("access_token");
        sessionStorage.removeItem("refresh_token");
      } else {
        setAccessToken(newToken);
        try {
          setUser(parseJwtPayload(newToken));
        } catch {
          // opaque token
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
    const idToken = sessionStorage.getItem("id_token") ?? undefined;
    setAccessToken(null);
    setUser(null);
    sessionStorage.removeItem("access_token");
    sessionStorage.removeItem("refresh_token");
    sessionStorage.removeItem("id_token");
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
