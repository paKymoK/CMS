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
          colorPrimary: "#00a9ff",
          colorLink: "#00a9ff",
          colorInfo: "#00a9ff",
          colorBgLayout: "#edf5ff",
          colorBorder: "#e0e4e9",
          colorBorderSecondary: "#eef1f4",
          colorText: "#0f172a",
          fontFamily: "'Inter', 'Helvetica Neue', Helvetica, Arial, sans-serif",
          // Same shape language as the landing page: soft 10px controls, 14px cards, pill CTAs.
          borderRadius: 10,
          borderRadiusLG: 14,
          borderRadiusSM: 8,
          controlHeight: 40,
          motionDurationMid: "0.22s",
          motionEaseOut: "cubic-bezier(0.22, 1, 0.36, 1)",
          boxShadowSecondary: "0 18px 44px rgba(10,37,64,.16), 0 2px 8px rgba(10,37,64,.06)",
        },
        components: {
          Menu: {
            darkItemBg: "transparent",
            darkItemColor: "#c9d9ee",
            darkItemHoverColor: "#ffffff",
            darkItemSelectedColor: "#ffffff",
            darkItemSelectedBg: "rgba(255,255,255,.1)",
            darkGroupTitleColor: "rgba(201,217,238,.7)",
            itemBorderRadius: 10,
            itemHeight: 38,
            darkItemHoverBg: "rgba(255,255,255,.06)",
          },
          Table: {
            headerBg: "#f6f9fc",
            headerColor: "#5a5d64",
            headerSplitColor: "transparent",
            borderColor: "#e8edf3",
            rowHoverBg: "#f3f9ff",
            cellPaddingBlock: 14,
          },
          Button: {
            fontWeight: 600,
            primaryShadow: "none",
            // The landing page's CTAs ("Contact Us", "see all") are pills.
            borderRadius: 999,
            borderRadiusLG: 999,
            borderRadiusSM: 999,
          },
          Tabs: {
            inkBarColor: "#00a9ff",
            itemActiveColor: "#002d68",
            itemSelectedColor: "#002d68",
            itemHoverColor: "#00a9ff",
          },
          Drawer: {
            paddingLG: 24,
          },
          Modal: { borderRadiusLG: 18 },
          Popover: { borderRadiusLG: 14 },
          Select: { optionSelectedBg: "#edf5ff" },
          Switch: { colorPrimary: "#00a9ff" },
        },
      }}
    >
      <AppRouter />
    </ConfigProvider>
  );
}
