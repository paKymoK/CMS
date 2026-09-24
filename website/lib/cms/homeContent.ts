import type { HomeContent } from "@/content/home/types";
import { homeContentEn } from "@/content/home/en";

const CMS_BASE = process.env.NEXT_PUBLIC_CMS_API_BASE_URL!;

/**
 * content-service resolves the site from this request's Host header (never a query param),
 * so this must always go through gateway-service, never straight to content-service's own
 * port — see NEXT_PUBLIC_CMS_API_BASE_URL's comment in .env.example.
 *
 * CMS being unreachable or erroring shouldn't take the whole homepage down for a visitor —
 * this is a public marketing page, not an authenticated app. Fall back to the bundled static
 * copy (content/home/en.ts, the pre-CMS content) instead of throwing; each section already
 * renders a Placeholder for any individual item missing an image, so the page degrades to
 * "real text, placeholder imagery" rather than a 500.
 */
export async function getHomeContent(): Promise<HomeContent> {
  try {
    const res = await fetch(`${CMS_BASE}/content-service/v1/home`, {
      next: { revalidate: 60 },
    });
    if (!res.ok) {
      console.error(`Failed to load home content: ${res.status} ${res.statusText}`);
      return homeContentEn;
    }
    const body = (await res.json()) as { data: HomeContent };
    return body.data ?? homeContentEn;
  } catch (err) {
    console.error("Failed to load home content", err);
    return homeContentEn;
  }
}

/**
 * Draft-inclusive counterpart of getHomeContent, used only from the token-gated preview path
 * (app/api/preview and the [locale] page's draftMode() branch). Deliberately does NOT fall back
 * to the bundled static content on failure the way getHomeContent does — that fallback exists so
 * a real visitor never sees a broken public page, but silently showing placeholder content here
 * would hide "your preview token is invalid/expired" from the one person who actually needs to
 * see that error. cache: "no-store" — this must never share getHomeContent's ISR cache entry.
 */
export async function getPreviewHomeContent(token: string): Promise<HomeContent> {
  const res = await fetch(
    `${CMS_BASE}/content-service/v1/preview/home?token=${encodeURIComponent(token)}`,
    { cache: "no-store" },
  );
  if (!res.ok) {
    throw new Error(`Failed to load preview content: ${res.status} ${res.statusText}`);
  }
  const body = (await res.json()) as { data: HomeContent };
  if (!body.data) {
    throw new Error("Preview response had no data");
  }
  return body.data;
}
