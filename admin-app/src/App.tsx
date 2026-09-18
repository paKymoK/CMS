import { ConfigProvider } from "antd";
import AppRouter from "./router";

export default function App() {
  return (
    <ConfigProvider theme={{ token: { colorPrimary: "#1565C0", borderRadius: 8 } }}>
      <AppRouter />
    </ConfigProvider>
  );
}
