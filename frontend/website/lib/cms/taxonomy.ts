import type { PostSummary } from "./types";
import { resolveMediaUrls } from "./media";
import { cmsFetch } from "./cmsFetch";

const CMS_BASE = process.env.NEXT_PUBLIC_CMS_API_BASE_URL!;

export type TaxonomyKind = "category" | "tag";

/** A category or tag on the public site: only Active ones are ever returned. */
export type Term = {
  id: number;
  name: string;
  slug: string;
  description?: string | null;
  /** A sub-category's parent. */
  parentName?: string | null;
  /** Published posts that carry it. */
  count: number;
};

const PATH: Record<TaxonomyKind, string> = { category: "post-categories", tag: "post-tags" };

/** The category or tag at /category/<slug> or /tag/<slug> with its published posts, or null (not found, or Inactive). */
export async function getTerm(kind: TaxonomyKind, slug: string): Promise<{ term: Term; posts: PostSummary[] } | null> {
  try {
    const res = await cmsFetch(`${CMS_BASE}/content-service/v1/${PATH[kind]}/${encodeURIComponent(slug)}`, {
      next: { revalidate: 60 },
    });
    if (!res.ok) {
      if (res.status !== 400 && res.status !== 404) {
        console.error(`Failed to load ${kind} "${slug}": ${res.status} ${res.statusText}`);
      }
      return null;
    }
    const body = (await res.json()) as { data?: { term: Term; posts: PostSummary[] } };
    return body.data ? { term: body.data.term, posts: resolveMediaUrls(body.data.posts ?? []) } : null;
  } catch (err) {
    console.error(`Failed to load ${kind} "${slug}"`, err);
    return null;
  }
}
