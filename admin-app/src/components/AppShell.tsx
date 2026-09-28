import { useMemo, useState, type CSSProperties } from "react";
import { Layout, Menu, Select, Button, Empty, message } from "antd";
import type { MenuProps } from "antd";
import { LogoutOutlined, EyeOutlined, MenuOutlined } from "@ant-design/icons";
import { useMutation } from "@tanstack/react-query";
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/useAuth";
import { useSiteAccess } from "../auth/useSiteAccess";
import { useSite } from "../lib/useSite";
import { contentApi } from "../lib/api";
import { NAVIGATION, type NavItem } from "../config/navigation";
import { SITES } from "../config/sites";

const { Header, Sider, Content } = Layout;

interface PreviewToken {
  token: string;
  expiresAt: string;
}

function websiteOriginFor(siteCode: string | null): string | undefined {
  const subdomain = SITES.find((s) => s.code === siteCode)?.subdomain;
  return subdomain ? `https://${subdomain}` : undefined;
}

const numberStyle: CSSProperties = {
  fontFamily: "var(--font-mono)",
  fontSize: 10,
  letterSpacing: ".06em",
  width: 18,
  display: "inline-block",
};

// One flat button per leaf, numbered 01/02/... within its own group — mirrors the design's static
// (non-collapsible) two-level nav, via antd Menu's `type: "group"` rather than a submenu.
function toMenuItems(items: NavItem[]): MenuProps["items"] {
  return items.map((item) =>
    item.items
      ? {
          key: item.key,
          type: "group",
          label: item.label,
          children: item.items.map((child, i) => ({
            key: child.key,
            label: (
              <span style={{ display: "flex", alignItems: "center", gap: 12 }}>
                <span style={numberStyle}>{String(i + 1).padStart(2, "0")}</span>
                <span>{child.label}</span>
              </span>
            ),
          })),
        }
      : { key: item.key, label: item.label },
  );
}

function findGroupLabel(items: NavItem[], pathname: string): string | undefined {
  for (const item of items) {
    if (item.items?.some((child) => child.key === pathname)) return item.label;
  }
  return undefined;
}

function findLeafLabel(items: NavItem[], pathname: string): string | undefined {
  for (const item of items) {
    if (item.key === pathname) return item.label;
    const child = item.items?.find((c) => c.key === pathname);
    if (child) return child.label;
  }
  return undefined;
}

function initialsOf(name: string): string {
  const parts = name.trim().split(/\s+/);
  if (parts.length >= 2) return (parts[0][0] + parts[1][0]).toUpperCase();
  return name.slice(0, 2).toUpperCase();
}

export default function AppShell() {
  const { logout, user } = useAuth();
  const { isGlobalAdmin } = useSiteAccess();
  const { site, setSite, accessibleSites } = useSite();
  const location = useLocation();
  const navigate = useNavigate();
  const [navOpen, setNavOpen] = useState(false);

  // Preview is whole-page, not per-row (the site is one composed homepage, not per-post pages —
  // see cms-platform-plan.md), so it lives here next to the site switcher rather than in
  // ContentPage's per-item drawer. Mints a short-lived, site-scoped preview_token and opens the
  // website's own /api/preview with it in a new tab — content-service never returns this site's
  // subdomain, admin-app already has it in SITES.
  const previewMutation = useMutation({
    mutationFn: async () => {
      const { data } = await contentApi.post<{ data: PreviewToken }>(
        "/v1/admin/preview-tokens",
        null,
        { params: { site } },
      );
      return data.data;
    },
    onSuccess: ({ token }) => {
      // VITE_WEBSITE_ORIGIN is a local-dev-only override (one `next dev` instance covers every
      // site locally, and preview content resolution already doesn't care which origin you hit —
      // see PreviewController, siteId comes from the token). Unset in production, where this
      // falls back to the real per-site subdomain.
      const localOrigin = import.meta.env.VITE_WEBSITE_ORIGIN as string | undefined;
      const origin = localOrigin || websiteOriginFor(site);
      if (!origin) {
        message.error("Unknown site subdomain");
        return;
      }
      window.open(
        `${origin}/api/preview?token=${encodeURIComponent(token)}`,
        "_blank",
        "noopener,noreferrer",
      );
    },
  });

  const menuItems = useMemo(() => toMenuItems(NAVIGATION), []);
  const crumbGroup = findGroupLabel(NAVIGATION, location.pathname) ?? "";
  const pageTitle = findLeafLabel(NAVIGATION, location.pathname) ?? "";

  const displayName = (user?.name as string) || (user?.email as string) || "Signed in";
  const roleLine = isGlobalAdmin
    ? "Global admin"
    : `Site editor · ${accessibleSites.map((c) => c.toUpperCase()).join(", ")}`;

  if (accessibleSites.length === 0) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <Empty description="Your account has no CMS site access assigned. Ask a global admin to grant you access to at least one site." />
      </div>
    );
  }

  return (
    <Layout style={{ minHeight: "100vh" }}>
      <Sider
        width={248}
        className={`cms-sider${navOpen ? " cms-sider-open" : ""}`}
        style={{
          position: "sticky",
          top: 0,
          height: "100vh",
          background: "linear-gradient(180deg, #03091a, #04102a 60%, #061634)",
        }}
      >
        {/* antd's Sider wraps children in its own .ant-layout-sider-children (display:block), so
            this wrapper is what actually needs to be the flex column — flex props on Sider itself
            never reach the real children. */}
        <div style={{ display: "flex", flexDirection: "column", height: "100%" }}>
        <div
          style={{
            display: "flex",
            alignItems: "center",
            gap: 10,
            padding: "26px 22px 22px",
            flexShrink: 0,
          }}
        >
          <img src="/logo.svg" alt="CMC Global" style={{ height: 24, filter: "brightness(0) invert(1)" }} />
          <span
            style={{
              fontFamily: "var(--font-mono)",
              fontSize: 10,
              letterSpacing: ".12em",
              padding: "3px 8px",
              borderRadius: 999,
              border: "1px solid rgba(255,255,255,.28)",
              color: "#c9d9ee",
            }}
          >
            CMS
          </span>
        </div>
        <Menu
          theme="dark"
          mode="inline"
          style={{
            flex: 1,
            minHeight: 0,
            overflowY: "auto",
            background: "transparent",
            border: "none",
            padding: "4px 12px",
          }}
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={({ key }) => {
            navigate(key);
            setNavOpen(false);
          }}
        />
        <div
          style={{
            display: "flex",
            alignItems: "center",
            gap: 10,
            margin: 12,
            padding: 12,
            borderRadius: 10,
            border: "1px solid rgba(255,255,255,.18)",
            background: "rgba(255,255,255,.05)",
            flexShrink: 0,
          }}
        >
          <div
            style={{
              width: 34,
              height: 34,
              flex: "none",
              borderRadius: "50%",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              fontSize: 12,
              fontWeight: 700,
              color: "#ffffff",
              background:
                "radial-gradient(circle at 34% 30%, #7cc4f7 0%, #1a6fc4 60%, #0c3f7d 100%)",
            }}
          >
            {initialsOf(displayName)}
          </div>
          <div style={{ flex: 1, minWidth: 0, display: "flex", flexDirection: "column" }}>
            <span
              style={{
                fontSize: 13,
                fontWeight: 600,
                color: "#ffffff",
                overflow: "hidden",
                textOverflow: "ellipsis",
                whiteSpace: "nowrap",
              }}
            >
              {displayName}
            </span>
            <span
              style={{
                fontSize: 11.5,
                color: "#c9d9ee",
                overflow: "hidden",
                textOverflow: "ellipsis",
                whiteSpace: "nowrap",
              }}
            >
              {roleLine}
            </span>
          </div>
          <Button
            type="text"
            size="small"
            icon={<LogoutOutlined />}
            onClick={logout}
            title="Log out"
            style={{ color: "#8fd0ff", flex: "none" }}
          />
        </div>
        </div>
      </Sider>
      <div
        className={`cms-nav-backdrop${navOpen ? " open" : ""}`}
        onClick={() => setNavOpen(false)}
      />
      <Layout>
        <div style={{ position: "sticky", top: 0, zIndex: 20, padding: "16px clamp(16px,3vw,32px) 0" }}>
          <Header
            style={{
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
              gap: 16,
              height: 64,
              padding: "0 12px 0 22px",
              borderRadius: 18,
              border: "1px solid rgba(24,159,224,.2)",
              background: "rgba(255,255,255,.85)",
              backdropFilter: "blur(24px)",
              boxShadow: "0 10px 34px rgba(10,37,64,.1), 0 2px 6px rgba(10,37,64,.05)",
            }}
          >
            <div
              style={{
                display: "flex",
                alignItems: "center",
                gap: 8,
                minWidth: 0,
                fontSize: 15,
                fontWeight: 500,
                color: "#3c4858",
                whiteSpace: "nowrap",
                overflow: "hidden",
              }}
            >
              <button
                className="cms-nav-toggle"
                aria-label="Open navigation"
                onClick={() => setNavOpen(true)}
                style={{
                  alignItems: "center",
                  justifyContent: "center",
                  width: 32,
                  height: 32,
                  flex: "none",
                  border: "1px solid #dceaf5",
                  borderRadius: 8,
                  background: "#ffffff",
                  color: "#189fe0",
                  cursor: "pointer",
                }}
              >
                <MenuOutlined />
              </button>
              <span style={{ overflow: "hidden", textOverflow: "ellipsis" }}>{crumbGroup}</span>
              <span style={{ color: "#cfd2d6" }}>/</span>
              <span style={{ fontWeight: 600, color: "#10314f" }}>{pageTitle}</span>
            </div>
            <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
              <Select
                value={site ?? undefined}
                className="cms-site-select"
                style={{ width: 170 }}
                onChange={setSite}
                options={accessibleSites.map((code) => ({
                  value: code,
                  label: SITES.find((s) => s.code === code)?.label ?? code,
                }))}
              />
              <Button
                icon={<EyeOutlined />}
                disabled={!site}
                loading={previewMutation.isPending}
                onClick={() => previewMutation.mutate()}
                style={{
                  background: "linear-gradient(90deg, #189fe0, #00bbe4)",
                  border: "none",
                  color: "#ffffff",
                  fontWeight: 700,
                  boxShadow: "0 10px 24px rgba(24,159,224,.36)",
                }}
              >
                <span className="cms-preview-label">Preview site ↗</span>
              </Button>
            </div>
          </Header>
        </div>
        <Content
          style={{
            flex: 1,
            width: "100%",
            maxWidth: 1180,
            margin: "0 auto",
            padding: "36px clamp(16px,3vw,32px) 64px",
          }}
        >
          <div key={location.pathname} className="cms-page-enter">
            <Outlet />
          </div>
        </Content>
      </Layout>
    </Layout>
  );
}
