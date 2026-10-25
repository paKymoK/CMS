import { permanentRedirect } from "next/navigation";
import { cmsFetch } from "./cmsFetch";

const CMS_BASE = process.env.NEXT_PUBLIC_CMS_API_BASE_URL!;

/** The address segments after `/{locale}/{prefix}/`, e.g. `["old-slug"]` or `["web", "old-slug"]`. */
export async function getRenamedPath(section: ApiSection, segments: string[]): Promise<string | null> {
  const qs = new URLSearchParams({ section, path: segments.join("/") });
  try {
    const res = await cmsFetch(`${CMS_BASE}/content-service/v1/slug-redirect?${qs}`, { cache: "no-store" });
    if (!res.ok) return null;
    const body = (await res.json()) as { data?: { path: string } };
    return body.data?.path ?? null;
  } catch (err) {
    console.error(`Failed to look up a renamed page "${section}/${segments.join("/")}"`, err);
    return null;
  }
}

type ApiSection = "posts" | "case-studies" | "industries" | "services";

/**
 * For a detail page that found nothing: if the address is an old slug of a published page, send the
 * visitor (and search engines) to its current address with a permanent redirect. Otherwise returns,
 * and the caller shows its 404. `urlPrefix` is the website's own section, which differs from the API's
 * for posts (insights).
 */
export async function redirectIfRenamed(
  locale: string,
  section: ApiSection,
  urlPrefix: string,
  segments: string[],
): Promise<void> {
  const current = await getRenamedPath(section, segments);
  if (current) permanentRedirect(`/${locale}/${urlPrefix}/${current.split("/").map(encodeURIComponent).join("/")}`);
}
