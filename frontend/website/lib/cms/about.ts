import type { AboutPageData } from "@cms/content-renderer";
import { resolveMediaUrls } from "./media";
import { cmsFetch } from "./cmsFetch";

const CMS_BASE = process.env.NEXT_PUBLIC_CMS_API_BASE_URL!;

/** content_item type "about_page" — see content-service's AboutPageFields. */
export type { AboutPageData } from "@cms/content-renderer";

export async function getAboutPage(): Promise<AboutPageData | null> {
  try {
    const res = await cmsFetch(`${CMS_BASE}/content-service/v1/about`, { cache: "no-store" });
    if (!res.ok) return null;
    const body = (await res.json()) as { data: { data: AboutPageData } };
    return body.data?.data ? resolveMediaUrls(body.data.data) : null;
  } catch (err) {
    console.error("Failed to load the About page", err);
    return null;
  }
}
