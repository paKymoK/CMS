import type { IndustryPageData } from "@cms/content-renderer";
import { resolveMediaUrls } from "./media";
import { cmsFetch } from "./cmsFetch";

const CMS_BASE = process.env.NEXT_PUBLIC_CMS_API_BASE_URL!;

/** content_item type "industry_page" — see content-service's IndustryPageFields. */
export type { IndustryPageData } from "@cms/content-renderer";

export async function getIndustryPage(slug: string): Promise<IndustryPageData | null> {
  try {
    const res = await cmsFetch(`${CMS_BASE}/content-service/v1/industries/${encodeURIComponent(slug)}`, {
      next: { revalidate: 60 },
    });
    if (!res.ok) return null;
    const body = (await res.json()) as { data: { data: IndustryPageData } };
    return body.data?.data ? resolveMediaUrls(body.data.data) : null;
  } catch (err) {
    console.error(`Failed to load industry "${slug}"`, err);
    return null;
  }
}
