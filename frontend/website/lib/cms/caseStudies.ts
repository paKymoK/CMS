import type { CaseStudyDetail, CaseStudyPage } from "./types";
import { resolveMediaUrls } from "./media";
import { cmsFetch } from "./cmsFetch";

const CMS_BASE = process.env.NEXT_PUBLIC_CMS_API_BASE_URL!;

/**
 * Same Host-header site resolution and no-static-fallback convention as posts.ts /
 * homeContent.ts — see posts.ts's getPost for why a failure returns null instead of throwing.
 */
export async function getCaseStudy(slug: string): Promise<CaseStudyDetail | null> {
  try {
    const res = await cmsFetch(
      `${CMS_BASE}/content-service/v1/case-studies/${encodeURIComponent(slug)}`,
      { next: { revalidate: 60 } },
    );
    if (!res.ok) {
      if (res.status !== 404) {
        console.error(`Failed to load case study "${slug}": ${res.status} ${res.statusText}`);
      }
      return null;
    }
    const body = (await res.json()) as { data: CaseStudyDetail };
    return body.data ? resolveMediaUrls(body.data) : null;
  } catch (err) {
    console.error(`Failed to load case study "${slug}"`, err);
    return null;
  }
}

export const CASE_STUDIES_PAGE_SIZE = 6;

const EMPTY_PAGE: CaseStudyPage = {
  items: [],
  page: 1,
  size: CASE_STUDIES_PAGE_SIZE,
  totalItems: 0,
  totalPages: 1,
  categories: [],
};

/**
 * One page of the published Case Studies listing (server-side paging and filtering). Like posts,
 * there is no bundled fallback: a failure degrades to an empty page rather than stale content.
 */
export async function getCaseStudyPage(params: {
  page?: number;
  category?: string;
  q?: string;
  /** Search exactly what was typed: no spelling correction. */
  exact?: boolean;
}): Promise<CaseStudyPage> {
  const search = new URLSearchParams();
  search.set("page", String(params.page && params.page > 0 ? params.page : 1));
  search.set("size", String(CASE_STUDIES_PAGE_SIZE));
  if (params.category) search.set("category", params.category);
  if (params.q) search.set("q", params.q);
  if (params.exact) search.set("exact", "true");

  try {
    const res = await cmsFetch(`${CMS_BASE}/content-service/v1/case-studies?${search}`, {
      next: { revalidate: 60 },
    });
    if (!res.ok) {
      console.error(`Failed to load case studies: ${res.status} ${res.statusText}`);
      return EMPTY_PAGE;
    }
    const body = (await res.json()) as { data: CaseStudyPage };
    return body.data ? resolveMediaUrls(body.data) : EMPTY_PAGE;
  } catch (err) {
    console.error("Failed to load case studies", err);
    return EMPTY_PAGE;
  }
}
