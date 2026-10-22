import { resolveMediaUrls } from "./media";
import { cmsFetch } from "./cmsFetch";
import { getPreviewToken } from "./previewSession";

const CMS_BASE = process.env.NEXT_PUBLIC_CMS_API_BASE_URL!;

export type ServiceLink = { slug: string; label: string };
export type ServiceCategory = { slug: string; name: string; services: ServiceLink[] };
export type ServiceNav = { categories: ServiceCategory[] };

export type ServiceCapability = { title: string; body?: string; image?: string };
export type ServiceStep = { title: string; image?: string };
export type ServiceReason = { title: string; body?: string; image?: string; sideImage?: string };
export type ServiceStat = { value: string; label: string };

/** content_item type "service_page" — see content-service's ServicePageFields. */
export type ServicePageData = {
  slug: string;
  category: string;
  navLabel: string;
  shortLabel?: string;
  heroTitle: string;
  heroSubtitle?: string;
  ctaLabel?: string;
  ctaUrl?: string;
  heroImage?: string;
  heroStats?: ServiceStat[];
  capabilitiesEyebrow?: string;
  capabilitiesTitle?: string;
  capabilitiesSubtitle?: string;
  capabilities?: ServiceCapability[];
  processTitle?: string;
  processSubtitle?: string;
  processSteps?: ServiceStep[];
  storiesHeading?: string;
  caseStudies?: string[];
  reasonsTitle?: string;
  reasonsSubtitle?: string;
  reasons?: ServiceReason[];
  hiddenSections?: string[];
  seoTitle?: string;
  seoDescription?: string;
};

export type FeaturedCaseStudy = {
  slug: string;
  title: string;
  summary?: string;
  image?: string;
  category?: string;
  results?: ServiceStat[];
};

export type ServicePage = {
  data: ServicePageData;
  category: ServiceCategory;
  caseStudies: FeaturedCaseStudy[];
};

/**
 * The header's Services menu. A CMS outage degrades to an empty menu rather than failing every page
 * (this runs in the root layout). Categories without a published service are left out — they only
 * exist as empty groups in the CMS until their pages are written.
 */
export async function getServiceNav(): Promise<ServiceNav> {
  try {
    const res = await cmsFetch(`${CMS_BASE}/content-service/v1/services`, {
      next: { revalidate: 60 },
    });
    if (!res.ok) return { categories: [] };
    const body = (await res.json()) as { data: ServiceNav };
    const nav = body.data ?? { categories: [] };
    return { categories: nav.categories.filter((c) => c.services.length > 0) };
  } catch (err) {
    console.error("Failed to load the services menu", err);
    return { categories: [] };
  }
}

export async function getServicePage(category: string, slug: string): Promise<ServicePage | null> {
  const token = await getPreviewToken();
  const path = `${encodeURIComponent(category)}/${encodeURIComponent(slug)}`;
  try {
    const res = token
      ? await cmsFetch(
          `${CMS_BASE}/content-service/v1/preview/services/${path}?token=${encodeURIComponent(token)}`,
          { cache: "no-store" },
        )
      : await cmsFetch(`${CMS_BASE}/content-service/v1/services/${path}`, {
          next: { revalidate: 60 },
        });
    if (!res.ok) return null;
    const body = (await res.json()) as { data: ServicePage };
    return body.data ? resolveMediaUrls(body.data) : null;
  } catch (err) {
    console.error(`Failed to load service "${category}/${slug}"`, err);
    return null;
  }
}

