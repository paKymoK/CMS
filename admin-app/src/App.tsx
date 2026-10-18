import { ConfigProvider } from "antd";
import AppRouter from "./router";

// Brand tokens mirrored from website/app/globals.css (--brand-primary/-dark/-accent/-surface) —
// same source of truth, kept in sync by hand since this is a separate Vite app, not a shared
// design-token package.
export default function App() {
  return (
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: "#0083ff",
          colorLink: "#0069cc",
          colorInfo: "#0083ff",
          colorBgLayout: "#f4f7fc",
          colorBorder: "#dde4f0",
          colorBorderSecondary: "#e3e9f4",
          colorText: "#0b1b3a",
          fontFamily: "'Plus Jakarta Sans', 'Inter', 'Helvetica Neue', Helvetica, Arial, sans-serif",
          // 2026 admin redesign: 12px controls, 16px cards, solid (not pill) buttons.
          borderRadius: 12,
          borderRadiusLG: 16,
          borderRadiusSM: 8,
          controlHeight: 42,
          motionDurationMid: "0.22s",
          motionEaseOut: "cubic-bezier(0.22, 1, 0.36, 1)",
          boxShadowSecondary: "0 18px 44px rgba(10,37,64,.16), 0 2px 8px rgba(10,37,64,.06)",
        },
        components: {
          Table: {
            headerBg: "#fafcff",
            headerColor: "#7b8aa5",
            headerSplitColor: "transparent",
            borderColor: "#edf1f8",
            rowHoverBg: "#f6f9ff",
            cellPaddingBlock: 14,
          },
          Button: {
            fontWeight: 600,
            primaryShadow: "none",
            borderRadius: 12,
            borderRadiusLG: 12,
            borderRadiusSM: 10,
          },
          Tabs: {
            inkBarColor: "#0083ff",
            itemActiveColor: "#001449",
            itemSelectedColor: "#001449",
            itemHoverColor: "#0083ff",
          },
          Drawer: {
            paddingLG: 24,
          },
          Modal: { borderRadiusLG: 20 },
          Popover: { borderRadiusLG: 16 },
          Select: { optionSelectedBg: "#eaf3ff" },
          Switch: { colorPrimary: "#0083ff" },
        },
      }}
    >
      <AppRouter />
    </ConfigProvider>
  );
}
