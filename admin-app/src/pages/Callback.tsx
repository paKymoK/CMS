import { useEffect, useMemo, useRef, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { Button } from "antd";
import { useAuth } from "../auth/useAuth";

const heroBg = {
  minHeight: "100vh",
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  padding: "24px 20px",
  background: "linear-gradient(180deg, #03091a, #04102a, #061634)",
  fontFamily: "Montserrat, Arial, Helvetica, sans-serif",
};

export default function Callback() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { handleCallback } = useAuth();
  const [exchangeError, setExchangeError] = useState<string | null>(null);
  // Authorization codes are single-use — guard against the effect re-running (StrictMode, etc.)
  // and replaying the same code.
  const exchangedCodeRef = useRef<string | null>(null);

  const validationError = useMemo(() => {
    const oauthError = searchParams.get("error");
    if (oauthError) {
      const desc = searchParams.get("error_description");
      return desc
        ? decodeURIComponent(desc.replace(/\+/g, " "))
        : `Authorization failed: ${oauthError}`;
    }

    const code = searchParams.get("code");
    if (!code) return "No authorization code received";

    const state = searchParams.get("state");
    const savedState = sessionStorage.getItem("pkce_state");
    const codeVerifier = sessionStorage.getItem("pkce_code_verifier");
    if (state !== savedState) return "State mismatch — possible CSRF attack";
    if (!codeVerifier) return "No code verifier found — please try logging in again";
    return null;
  }, [searchParams]);

  useEffect(() => {
    if (validationError) return;
    const code = searchParams.get("code")!;
    if (exchangedCodeRef.current === code) return;
    exchangedCodeRef.current = code;

    const codeVerifier = sessionStorage.getItem("pkce_code_verifier")!;
    handleCallback(code, codeVerifier)
      .then(() => {
        sessionStorage.removeItem("pkce_code_verifier");
        sessionStorage.removeItem("pkce_state");
        navigate("/", { replace: true });
      })
      .catch((err) => {
        setExchangeError(err instanceof Error ? err.message : "Token exchange failed");
      });
  }, [searchParams, validationError, handleCallback, navigate]);

  const error = validationError ?? exchangeError;

  if (error) {
    return (
      <div style={heroBg}>
        <div style={{ width: "100%", maxWidth: 420, textAlign: "center" }}>
          <h1 style={{ margin: 0, fontSize: 24, fontWeight: 700, color: "#ffffff" }}>
            Authentication error
          </h1>
          <div
            style={{
              margin: "16px 0",
              textAlign: "left",
              borderRadius: 10,
              padding: "12px 16px",
              fontSize: 13,
              lineHeight: 1.5,
              background: "rgba(255,120,120,.14)",
              border: "1px solid rgba(255,150,150,.4)",
              color: "#ffd9d9",
            }}
          >
            {error}
          </div>
          <Button
            onClick={() => (window.location.href = "/login")}
            style={{
              border: "none",
              borderRadius: 999,
              background: "#ffffff",
              padding: "0 26px",
              height: 44,
              fontWeight: 600,
              color: "#0d2b52",
            }}
          >
            Back to login
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div style={heroBg}>
      <div style={{ textAlign: "center" }}>
        <div style={{ display: "flex", gap: 6, justifyContent: "center", marginBottom: 18 }}>
          {[0, 0.16, 0.32].map((delay) => (
            <span
              key={delay}
              style={{
                width: 7,
                height: 7,
                borderRadius: "50%",
                background: "#ffffff",
                animation: `cms-dotpulse 1.2s ease-in-out ${delay}s infinite`,
              }}
            />
          ))}
        </div>
        <h1 style={{ margin: 0, fontSize: 24, fontWeight: 700, color: "#ffffff" }}>
          Redirecting to CMS Admin…
        </h1>
        <p style={{ margin: "10px 0 0", fontSize: 14, color: "#c9d9ee" }}>
          Exchanging authorization code
        </p>
      </div>
    </div>
  );
}
