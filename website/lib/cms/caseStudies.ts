import type { CaseStudyDetail } from "./types";

const CMS_BASE = process.env.NEXT_PUBLIC_CMS_API_BASE_URL!;

/**
 * Same Host-header site resolution and no-static-fallback convention as posts.ts /
 * homeContent.ts — see posts.ts's getPost for why a failure returns null instead of throwing.
 */
export async function getCaseStudy(slug: string): Promise<CaseStudyDetail | null> {
  try {
    const res = await fetch(
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
    return body.data ?? null;
  } catch (err) {
    console.error(`Failed to load case study "${slug}"`, err);
    return null;
  }
}
