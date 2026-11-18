import type { AboutPageData } from "@cms/content-renderer";
import { resolveMediaUrls } from "./media";
import { cmsFetch } from "./cmsFetch";
import type { FeaturedCaseStudy } from "./services";

const CMS_BASE = process.env.NEXT_PUBLIC_CMS_API_BASE_URL!;

/** content_item type "about_page" — see content-service's AboutPageFields. */
export type { AboutPageData } from "@cms/content-renderer";

export type AboutPage = {
  data: AboutPageData;
  caseStudies: FeaturedCaseStudy[];
};

export async function getAboutPage(): Promise<AboutPage | null> {
  try {
    const res = await cmsFetch(`${CMS_BASE}/content-service/v1/about`, { cache: "no-store" });
    if (!res.ok) return null;
    const body = (await res.json()) as { data: AboutPage };
    return body.data?.data ? resolveMediaUrls({ data: body.data.data, caseStudies: body.data.caseStudies ?? [] }) : null;
  } catch (err) {
    console.error("Failed to load the About page", err);
    return null;
  }
}
