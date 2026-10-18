import { useState } from "react";
import { ArrowRightIcon, CloseIcon } from "../components/icons";
import { useAuth } from "../auth/useAuth";
import { SITES } from "../config/sites";

const PERKS = [
  "Homepage content for your regions",
  "Images and videos",
  "The public assistant's knowledge",
];

// Split-screen sign-in: a brand panel on the left (hidden-ish on narrow screens by wrapping), the
// SSO action on the right. The only way in is CMC SSO (OAuth2 + PKCE), so there is no password form.
export default function Login() {
  const { login, authError, clearAuthError } = useAuth();
  const [busy, setBusy] = useState(false);

  const handleLogin = () => {
    setBusy(true);
    login();
  };

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
          <div className="cms-login-eyebrow">Content studio</div>
          <h1>Everything on the website, in one calm place.</h1>
          <p>
            Edit pages, publish posts and manage media for every CMC site, with a full history behind each change.
          </p>
        </div>
        <div className="cms-login-foot">
          <span>Versioned content</span>
          <span>Audit trail</span>
          <span>Multi-site</span>
        </div>
      </section>

      <section className="cms-login-panel">
        <div className="cms-login-card">
          <div>
            <h2>Welcome back</h2>
            <p>Sign in to manage site content.</p>
          </div>

          {authError && (
            <div className="cms-login-error" role="alert">
              <div style={{ flex: 1 }}>
                <strong>Authentication error</strong>
                <br />
                {authError}
              </div>
              <button type="button" onClick={clearAuthError} aria-label="Dismiss">
                <CloseIcon />
              </button>
            </div>
          )}

          <div className="cms-login-perks">
            <span className="cms-eyebrow">What you can manage</span>
            {PERKS.map((perk) => (
              <div key={perk} className="cms-login-perk">
                <span className="cms-login-check" aria-hidden>
                  ✓
                </span>
                {perk}
              </div>
            ))}
          </div>

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
              Redirecting to CMC sign-in…
            </div>
          ) : (
            <button type="button" className="cms-login-sso" onClick={handleLogin}>
              Sign in with CMC SSO
              <ArrowRightIcon style={{ fontSize: 16 }} />
            </button>
          )}

          <p className="cms-login-fine">Secure OAuth2 · PKCE flow · Session encrypted</p>
        </div>
        <a className="cms-login-site" href={`https://${SITES[0].subdomain}`} target="_blank" rel="noreferrer">
          {SITES[0].subdomain} ↗
        </a>
      </section>
    </div>
  );
}
