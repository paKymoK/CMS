import { Layout, Menu, Select, Button, Empty, Typography, message } from "antd";
import { LogoutOutlined, PictureOutlined, EyeOutlined } from "@ant-design/icons";
import { useMutation } from "@tanstack/react-query";
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/useAuth";
import { useSite } from "../lib/useSite";
import { contentApi } from "../lib/api";
import { RESOURCES } from "../config/resources";
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

export default function AppShell() {
  const { logout } = useAuth();
  const { site, setSite, accessibleSites } = useSite();
  const location = useLocation();
  const navigate = useNavigate();

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

  const menuItems = [
    ...RESOURCES.map((r) => ({ key: `/content/${r.key}`, label: r.label })),
    { key: "/media", label: "Media Library", icon: <PictureOutlined /> },
  ];

  if (accessibleSites.length === 0) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <Empty description="Your account has no CMS site access assigned. Ask a global admin to grant you access to at least one site." />
      </div>
    );
  }

  return (
    <Layout style={{ minHeight: "100vh" }}>
      <Sider width={220} theme="light">
        <div className="px-4 py-4 font-bold text-base">CMC Global CMS</div>
        <Menu
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header className="flex items-center justify-between !bg-white !px-4 border-b border-gray-200">
          <Typography.Text type="secondary">Editing site</Typography.Text>
          <div className="flex items-center gap-3">
            <Select
              value={site ?? undefined}
              style={{ width: 160 }}
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
            >
              Preview
            </Button>
            <Button icon={<LogoutOutlined />} onClick={logout}>
              Log out
            </Button>
          </div>
        </Header>
        <Content className="p-6">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
