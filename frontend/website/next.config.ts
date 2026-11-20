import type { NextConfig } from "next";
import createNextIntlPlugin from "next-intl/plugin";

const withNextIntl = createNextIntlPlugin("./i18n/request.ts");

// next/image may only load media from the gateway the website talks to, so derive the allowed
// origin from the same URL (localhost:8080 in dev, the LAN/prod gateway elsewhere).
const gatewayUrl = new URL(process.env.NEXT_PUBLIC_CMS_API_BASE_URL ?? "http://localhost:8080");

const nextConfig: NextConfig = {
  // Workspace packages ship TypeScript source.
  transpilePackages: ["@cms/content-renderer"],
  // Docker builds set NEXT_OUTPUT=standalone; it conflicts with Vercel's own output tracing, so it stays off otherwise.
  output: process.env.NEXT_OUTPUT === "standalone" ? "standalone" : undefined,
  // Dev only: extra hosts (e.g. a LAN IP) allowed to load the dev server, comma-separated.
  allowedDevOrigins: process.env.ALLOWED_DEV_ORIGINS?.split(",").map((o) => o.trim()).filter(Boolean),
  images: {
    // media-service-hosted images/logos come back as full absolute URLs from content-service's
    // /v1/home, routed through gateway-service — allowlist its origin for next/image.
    remotePatterns: [
      {
        protocol: gatewayUrl.protocol === "https:" ? "https" : "http",
        hostname: gatewayUrl.hostname,
        port: gatewayUrl.port,
      },
    ],
    // gateway-service is always reached over a private address (localhost in dev, an
    // internal Docker/cluster network in deployment) — Next 16's SSRF guard blocks
    // private-IP targets by default even when they match remotePatterns above, so this
    // must stay on for media-service images to load at all. Safe here because
    // remotePatterns already pins the exact host:port, not an open allowlist.
    dangerouslyAllowLocalIP: true,
  },
};

export default withNextIntl(nextConfig);
