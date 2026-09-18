import { Layout, Menu, Select, Button, Empty, Typography } from "antd";
import { LogoutOutlined, PictureOutlined } from "@ant-design/icons";
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/useAuth";
import { useSite } from "../lib/useSite";
import { RESOURCES } from "../config/resources";
import { SITES } from "../config/sites";

const { Header, Sider, Content } = Layout;

export default function AppShell() {
  const { logout } = useAuth();
  const { site, setSite, accessibleSites } = useSite();
  const location = useLocation();
  const navigate = useNavigate();

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
