import { useState } from "react";
import { Navigate } from "react-router-dom";
import { ArrowRightIcon, CloseIcon } from "../components/icons";
import { useAuth } from "../auth/useAuth";

// Split-screen sign-in: a brand panel on the left (wraps above the form on narrow screens), the
// SSO action on the right. The only way in is CMC SSO (OAuth2 + PKCE), so there is no password form.
export default function Login() {
  const { login, authError, clearAuthError, isAuthenticated, isLoading } = useAuth();
  const [busy, setBusy] = useState(false);

  const handleLogin = () => {
    setBusy(true);
    login();
  };

  // Already signed in, or signed in again from the saved refresh token (AuthProvider does that
  // while isLoading): nothing to press, go straight to the app.
  if (isLoading) return null;
  if (isAuthenticated) return <Navigate to="/" replace />;

  return (
    <div className="cms-login">
      <section className="cms-login-brand">
        <div className="cms-login-glow" aria-hidden />
        <div className="cms-login-ring cms-login-ring-lg" aria-hidden />
        <div className="cms-login-ring cms-login-ring-sm" aria-hidden />
        <div className="cms-login-brandtop">
          <img src="/logo.svg" alt="CMC Global" style={{ height: 28, filter: "brightness(0) invert(1)" }} />
          <span className="cms-brand-pill">CMS ADMIN</span>
        </div>
        <div className="cms-login-pitch">
          <h1>Your website, in one place.</h1>
        </div>
      </section>

      <section className="cms-login-panel">
        <div className="cms-login-card">
          <h2>Sign in</h2>

          {authError && (
            <div className="cms-login-error" role="alert">
              <div style={{ flex: 1 }}>{authError}</div>
              <button type="button" onClick={clearAuthError} aria-label="Dismiss">
                <CloseIcon />
              </button>
            </div>
          )}

          {busy ? (
            <div className="cms-login-busy" role="status">
              <span style={{ display: "flex", gap: 6 }}>
                {[0, 0.16, 0.32].map((delay) => (
                  <span
                    key={delay}
                    style={{
                      width: 6,
                      height: 6,
                      borderRadius: "50%",
                      background: "#0083ff",
                      animation: `cms-dotpulse 1.2s ease-in-out ${delay}s infinite`,
                    }}
                  />
                ))}
              </span>
              Redirecting…
            </div>
          ) : (
            <button type="button" className="cms-login-sso" onClick={handleLogin}>
              Continue with CMC SSO
              <ArrowRightIcon style={{ fontSize: 16 }} />
            </button>
          )}
        </div>
      </section>
    </div>
  );
}
