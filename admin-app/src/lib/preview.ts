import { message } from "antd";
import { contentApi } from "./api";
import { SITES } from "../config/sites";

interface PreviewToken {
  token: string;
  expiresAt: string;
}

function websiteOriginFor(siteCode: string): string | undefined {
  const subdomain = SITES.find((s) => s.code === siteCode)?.subdomain;
  return subdomain ? `https://${subdomain}` : undefined;
}

/**
 * Mints a short-lived, site-scoped preview_token and opens the website's /api/preview with it in
 * a new tab. `path` deep-links to one previewable page (e.g. "/en/insights/my-post"); omit it for
 * the whole-homepage preview. content-service never returns the site's subdomain — admin-app
 * already has it in SITES.
 *
 * The tab is opened synchronously, *before* the mint request, then pointed at the preview URL —
 * browsers (Safari especially) block window.open calls made after an awaited network round-trip,
 * but allow navigating a tab that was opened inside the click handler.
 */
export async function openPreview(site: string, path?: string): Promise<void> {
  // VITE_WEBSITE_ORIGIN is a local-dev-only override (one `next dev` instance covers every site
  // locally, and preview content resolution doesn't care which origin you hit — siteId comes from
  // the token, see PreviewController). Unset in production, where this falls back to the real
  // per-site subdomain.
  const localOrigin = import.meta.env.VITE_WEBSITE_ORIGIN as string | undefined;
  const origin = localOrigin || websiteOriginFor(site);
  if (!origin) {
    message.error("Unknown site subdomain");
    return;
  }

  const tab = window.open("", "_blank");
  if (tab) tab.opener = null;
  try {
    const { data } = await contentApi.post<{ data: PreviewToken }>(
      "/v1/admin/preview-tokens",
      null,
      { params: { site } },
    );
    const qs = new URLSearchParams({ token: data.data.token });
    if (path) qs.set("path", path);
    const url = `${origin}/api/preview?${qs.toString()}`;
    if (tab) {
      tab.location.href = url;
    } else {
      window.open(url, "_blank", "noopener,noreferrer");
    }
  } catch (err) {
    tab?.close();
    throw err;
  }
}
