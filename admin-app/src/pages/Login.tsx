import { useState } from "react";
import { Button } from "antd";
import { ArrowRightOutlined } from "@ant-design/icons";
import { useAuth } from "../auth/useAuth";
import { SITES } from "../config/sites";

const PERKS = [
  "Homepage content for your regions",
  "Images and videos",
  "The public assistant's knowledge",
];

// Dark "wave band" hero, same visual language as AppShell's sidebar and AssistantPage's hero —
// this is the very first thing anyone sees, so it carries the brand harder than an inner page does.
export default function Login() {
  const { login, authError, clearAuthError } = useAuth();
  const [busy, setBusy] = useState(false);

  const handleLogin = () => {
    setBusy(true);
    login();
  };

  return (
    <div
      style={{
        minHeight: "100vh",
        display: "flex",
        flexDirection: "column",
        background: "linear-gradient(180deg, #03091a, #04102a, #061634)",
        fontFamily: "Montserrat, Arial, Helvetica, sans-serif",
      }}
    >
      <header
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          gap: 16,
          padding: "26px clamp(20px,4vw,48px)",
        }}
      >
        <img src="/logo.svg" alt="CMC Global" style={{ height: 28, filter: "brightness(0) invert(1)" }} />
        <div style={{ display: "flex", alignItems: "center", gap: 14 }}>
          <span
            style={{
              fontFamily: "var(--font-mono)",
              fontSize: 10,
              letterSpacing: ".12em",
              padding: "4px 10px",
              borderRadius: 999,
              border: "1px solid rgba(255,255,255,.28)",
              color: "#c9d9ee",
            }}
          >
            CMS ADMIN
          </span>
          <a
            href={`https://${SITES[0].subdomain}`}
            target="_blank"
            rel="noreferrer"
            style={{ fontSize: 14, fontWeight: 500, color: "#c9d9ee" }}
          >
            {SITES[0].subdomain} ↗
          </a>
        </div>
      </header>

      <main
        style={{
          flex: 1,
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          padding: "24px 20px 56px",
        }}
      >
        <div style={{ width: "100%", maxWidth: 440 }}>
          <div style={{ textAlign: "center", marginBottom: 22 }}>
            <div
              style={{
                margin: "0 auto 16px",
                width: 40,
                height: 40,
                borderRadius: "50%",
                background:
                  "radial-gradient(circle at 34% 30%, #eaf6ff 0%, #7cc4f7 42%, #1a6fc4 78%, #0c3f7d 100%)",
                boxShadow: "0 0 22px rgba(90,180,255,.55)",
              }}
            />
            <h1 style={{ margin: 0, fontSize: "clamp(22px,2.6vw,28px)", fontWeight: 700, color: "#ffffff", letterSpacing: "-.01em" }}>
              CMC Global <span style={{ color: "#43a4ff" }}>CMS</span>
            </h1>
            <p style={{ margin: "8px 0 0", fontSize: 13.5, color: "#c9d9ee" }}>
              Sign in to manage site content
            </p>
          </div>

          <div
            style={{
              borderRadius: 10,
              border: "1px solid rgba(255,255,255,.26)",
              padding: 26,
              backdropFilter: "blur(16px) saturate(120%)",
              WebkitBackdropFilter: "blur(16px) saturate(120%)",
              background: "linear-gradient(105deg, rgba(214,228,245,.2), rgba(150,180,215,.1))",
              boxShadow: "0 18px 44px rgba(3,12,32,.34)",
              display: "flex",
              flexDirection: "column",
              gap: 18,
            }}
          >
            {authError && (
              <div
                style={{
                  display: "flex",
                  alignItems: "flex-start",
                  gap: 12,
                  borderRadius: 8,
                  padding: "11px 12px 11px 14px",
                  fontSize: 13,
                  lineHeight: 1.5,
                  background: "rgba(255,110,110,.16)",
                  border: "1px solid rgba(255,140,140,.45)",
                  color: "#ffe1e1",
                }}
              >
                <div style={{ flex: 1 }}>
                  <strong style={{ color: "#ffffff" }}>Authentication error</strong>
                  <br />
                  {authError}
                </div>
                <button
                  onClick={clearAuthError}
                  type="button"
                  aria-label="Dismiss"
                  style={{
                    flex: "none",
                    width: 24,
                    height: 24,
                    border: "none",
                    borderRadius: "50%",
                    background: "rgba(255,255,255,.12)",
                    color: "#ffffff",
                    fontSize: 14,
                    lineHeight: 1,
                    cursor: "pointer",
                  }}
                >
                  ×
                </button>
              </div>
            )}

            <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
              <span
                style={{
                  fontFamily: "var(--font-mono)",
                  fontSize: 10,
                  letterSpacing: ".16em",
                  textTransform: "uppercase",
                  color: "#c9d9ee",
                }}
              >
                What you can manage
              </span>
              {PERKS.map((perk, i) => (
                <div
                  key={perk}
                  style={{
                    display: "flex",
                    alignItems: "center",
                    gap: 12,
                    padding: "11px 14px",
                    borderRadius: 10,
                    border: "1px solid rgba(255,255,255,.18)",
                    background: "rgba(255,255,255,.05)",
                  }}
                >
                  <span
                    style={{
                      fontFamily: "var(--font-mono)",
                      fontSize: 10,
                      letterSpacing: ".06em",
                      color: "#43a4ff",
                      width: 18,
                    }}
                  >
                    {String(i + 1).padStart(2, "0")}
                  </span>
                  <span style={{ fontSize: 14, color: "#ffffff" }}>{perk}</span>
                </div>
              ))}
            </div>

            {busy ? (
              <div
                style={{
                  marginTop: 4,
                  height: 48,
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                  gap: 12,
                  borderRadius: 999,
                  border: "1px solid rgba(255,255,255,.28)",
                  fontSize: 14,
                  color: "#dce7f5",
                }}
              >
                <span style={{ display: "flex", gap: 6 }}>
                  {[0, 0.16, 0.32].map((delay) => (
                    <span
                      key={delay}
                      style={{
                        width: 6,
                        height: 6,
                        borderRadius: "50%",
                        background: "#ffffff",
                        animation: `cms-dotpulse 1.2s ease-in-out ${delay}s infinite`,
                      }}
                    />
                  ))}
                </span>
                Redirecting to CMC sign-in…
              </div>
            ) : (
              <Button
                onClick={handleLogin}
                style={{
                  marginTop: 4,
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "space-between",
                  border: "none",
                  borderRadius: 999,
                  background: "#ffffff",
                  padding: 6,
                  height: "auto",
                  fontSize: 14,
                  fontWeight: 600,
                  color: "#0d2b52",
                }}
              >
                <span style={{ paddingLeft: 22 }}>Sign in with CMC SSO</span>
                <span
                  style={{
                    display: "flex",
                    width: 36,
                    height: 36,
                    borderRadius: "50%",
                    background: "#0b63c5",
                    color: "#ffffff",
                    alignItems: "center",
                    justifyContent: "center",
                  }}
                >
                  <ArrowRightOutlined style={{ fontSize: 14 }} />
                </span>
              </Button>
            )}
          </div>
          <p
            style={{
              margin: "18px 0 0",
              textAlign: "center",
              fontFamily: "var(--font-mono)",
              fontSize: 10,
              letterSpacing: ".12em",
              textTransform: "uppercase",
              color: "rgba(201,217,238,.75)",
            }}
          >
            Secure OAuth2 · PKCE flow · Session encrypted
          </p>
        </div>
      </main>

      <footer
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          gap: 16,
          padding: "14px clamp(20px,4vw,48px)",
          background: "rgba(4,11,26,.92)",
          fontSize: 13,
          color: "#c9d9ee",
        }}
      >
        <span>© {new Date().getFullYear()} CMC Global</span>
      </footer>
    </div>
  );
}
