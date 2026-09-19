import type { NextConfig } from "next";
import createNextIntlPlugin from "next-intl/plugin";

const withNextIntl = createNextIntlPlugin("./i18n/request.ts");

const nextConfig: NextConfig = {
  // output: "standalone", // needed for Docker builds; conflicts with Vercel's own output tracing, so disabled while deploying there
  images: {
    // media-service-hosted images/logos come back as full absolute URLs from content-service's
    // /v1/home, routed through gateway-service — allowlist its origin for next/image.
    remotePatterns: [{ protocol: "http", hostname: "localhost", port: "8080" }],
    // gateway-service is always reached over a private address (localhost in dev, an
    // internal Docker/cluster network in deployment) — Next 16's SSRF guard blocks
    // private-IP targets by default even when they match remotePatterns above, so this
    // must stay on for media-service images to load at all. Safe here because
    // remotePatterns already pins the exact host:port, not an open allowlist.
    dangerouslyAllowLocalIP: true,
  },
};

export default withNextIntl(nextConfig);
