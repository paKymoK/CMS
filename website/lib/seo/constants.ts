import { routing } from "@/i18n/routing";

export const SITE_NAME = "CMC Global";

/**
 * Defaults to localhost for local dev/build; must be set to the real staging
 * hostname (behind the existing Cloudflare Tunnel) for deploys, so canonical/OG
 * URLs and the PageSpeed comparison target a real reachable URL.
 *
 * A scheme-less value (e.g. a bare hostname pasted into the deploy env vars)
 * is normalized to https:// rather than left relative — a relative canonical/
 * og:url silently breaks search and social-share previews.
 */
const rawSiteUrl = process.env.SITE_URL ?? "http://localhost:3000";
export const SITE_URL = (
  /^https?:\/\//i.test(rawSiteUrl) ? rawSiteUrl : `https://${rawSiteUrl}`
).replace(/\/$/, "");

export const DEFAULT_OG_IMAGE = "/images/og-default.jpg";

export const LOCALES = routing.locales;

/**
 * Locales that actually have built content. Phase 0 only ships /en — keep this
 * list in sync with which content/home/{locale}.ts files exist so hreflang
 * alternates don't advertise pages that 404.
 */
export const LOCALES_WITH_CONTENT = ["en"] as const;
