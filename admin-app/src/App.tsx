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
          colorPrimary: "#189fe0",
          colorLink: "#189fe0",
          colorInfo: "#189fe0",
          colorBgLayout: "#f6f9fc",
          colorBorder: "#e0e4e9",
          colorBorderSecondary: "#eef1f4",
          colorText: "#0f172a",
          fontFamily: "'Montserrat', Arial, Helvetica, sans-serif",
          borderRadius: 2,
        },
        components: {
          Menu: {
            darkItemBg: "transparent",
            darkItemColor: "#c9d9ee",
            darkItemHoverColor: "#ffffff",
            darkItemSelectedColor: "#ffffff",
            darkItemSelectedBg: "rgba(255,255,255,.1)",
            darkGroupTitleColor: "rgba(201,217,238,.7)",
            itemBorderRadius: 8,
          },
          Table: {
            headerBg: "#f6f9fc",
            headerColor: "#5a5d64",
            borderColor: "#e0e4e9",
            rowHoverBg: "#f9fbfd",
          },
          Button: {
            fontWeight: 600,
            primaryShadow: "none",
          },
          Tabs: {
            inkBarColor: "#189fe0",
            itemActiveColor: "#10314f",
            itemSelectedColor: "#10314f",
            itemHoverColor: "#189fe0",
          },
          Drawer: {
            paddingLG: 24,
          },
        },
      }}
    >
      <AppRouter />
    </ConfigProvider>
  );
}
