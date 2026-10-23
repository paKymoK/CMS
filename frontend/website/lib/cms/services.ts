import { resolveMediaUrls } from "./media";
import { cmsFetch } from "./cmsFetch";
import type { ServicePageData, ServiceStatData } from "@cms/content-renderer";

const CMS_BASE = process.env.NEXT_PUBLIC_CMS_API_BASE_URL!;

export type ServiceLink = { slug: string; label: string };
export type ServiceCategory = { slug: string; name: string; services: ServiceLink[] };
export type ServiceNav = { categories: ServiceCategory[] };

export type {
  ServiceCapabilityData as ServiceCapability,
  ServicePageData,
  ServiceReasonData as ServiceReason,
  ServiceStatData as ServiceStat,
  ServiceStepData as ServiceStep,
} from "@cms/content-renderer";

export type FeaturedCaseStudy = {
  slug: string;
  title: string;
  summary?: string;
  image?: string;
  category?: string;
  results?: ServiceStatData[];
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
  const path = `${encodeURIComponent(category)}/${encodeURIComponent(slug)}`;
  try {
    const res = await cmsFetch(`${CMS_BASE}/content-service/v1/services/${path}`, {
      cache: "no-store",
    });
    if (!res.ok) return null;
    const body = (await res.json()) as { data: ServicePage };
    return body.data ? resolveMediaUrls(body.data) : null;
  } catch (err) {
    console.error(`Failed to load service "${category}/${slug}"`, err);
    return null;
  }
}

