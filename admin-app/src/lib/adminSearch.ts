import { contentApi } from "./api";
import { RESOURCES } from "../config/resources";

/** One item matching a search; `title` is the readable one, `rank` orders best-first. */
export interface SearchHit {
  id: number;
  type: string;
  status: string;
  slug: string | null;
  title: string;
  rank: number;
}

/**
 * The outcome of a search. `correctedQuery` is set when the query as typed found nothing and these
 * hits are for a corrected spelling; `didYouMean` when even then there were no hits but a likelier
 * spelling exists. At most one of them is set.
 */
export interface AdminSearchResult {
  query: string;
  correctedQuery: string | null;
  didYouMean: string | null;
  hits: SearchHit[];
}

/** One entry of the as-you-type dropdown: a completed query ("term") or a matching item ("title"). */
export interface Suggestion {
  kind: "term" | "title";
  text: string;
  type: string | null;
  slug: string | null;
  id: number | null;
}

/** Search every content type of a site, drafts included. `exact` turns spelling correction off. */
export async function adminSearch(
  site: string,
  q: string,
  exact = false,
  signal?: AbortSignal,
): Promise<AdminSearchResult> {
  const { data } = await contentApi.get("/v1/admin/search", {
    params: { site, q, ...(exact ? { exact: true } : {}), limit: 50 },
    signal,
  });
  return data.data as AdminSearchResult;
}

/** Completions and matching items for a query that is still being typed. */
export async function adminSuggest(site: string, q: string, signal?: AbortSignal): Promise<Suggestion[]> {
  const { data } = await contentApi.get("/v1/admin/search/suggest", { params: { site, q }, signal });
  return (data.data?.suggestions ?? []) as Suggestion[];
}

/** Posts and case studies have editors of their own; the rest are edited from their content page. */
const SPECIAL: Record<string, { label: string; path: (id: number) => string }> = {
  post: { label: "Post", path: (id) => `/posts/${id}` },
  case_study: { label: "Case study", path: (id) => `/case-studies/${id}` },
};

/** Where an item is edited, or null for a content type the admin has no screen for. */
export function editorPathFor(type: string, id: number): string | null {
  if (SPECIAL[type]) return SPECIAL[type].path(id);
  const resource = RESOURCES.find((r) => r.contentType === type);
  // The content page opens the item's drawer for ?edit=<id> (see ContentPage).
  return resource ? `/content/${resource.key}?edit=${id}` : null;
}

/** "service_page" → the resource's label ("Service Pages"), "post" → "Post". */
export function typeLabel(type: string): string {
  if (SPECIAL[type]) return SPECIAL[type].label;
  return RESOURCES.find((r) => r.contentType === type)?.label ?? type.replace(/_/g, " ");
}

/** The route of the search results page for a query. */
export function searchPageHref(q: string, exact = false): string {
  const params = new URLSearchParams({ q });
  if (exact) params.set("exact", "1");
  return `/search?${params}`;
}
