import { cmsFetch } from "./cmsFetch";

const CMS_BASE = process.env.NEXT_PUBLIC_CMS_API_BASE_URL!;

/** What a search for `query` amounted to — see content-service's /v1/search/resolve. */
export type SearchResolution = {
  query: string;
  /** Set when the query as typed found nothing and the results are for this spelling instead. */
  correctedQuery: string | null;
  /** Set when even the corrected spelling found nothing, but this looks like what was meant. */
  didYouMean: string | null;
  total: number;
};

/** One entry of the as-you-type dropdown: a completed query ("term") or a matching item ("title"). */
export type Suggestion = {
  kind: "term" | "title";
  text: string;
  type: string | null;
  slug: string | null;
  id: number | null;
};

/**
 * Whether (and how) a query was spelling-corrected, for the "Showing results for…" line. The
 * listing itself already applies the same correction; this only reports it. A failure degrades to
 * no notice, never to an error — the results are still right.
 */
export async function getSearchResolution(params: {
  q: string;
  type: string;
  exact?: boolean;
}): Promise<SearchResolution | null> {
  const search = new URLSearchParams({ q: params.q, type: params.type });
  if (params.exact) search.set("exact", "true");
  try {
    const res = await cmsFetch(
      `${CMS_BASE}/content-service/v1/search/resolve?${search}`,
      {
        next: { revalidate: 60 },
      },
    );
    if (!res.ok) return null;
    const body = (await res.json()) as { data: SearchResolution };
    return body.data ?? null;
  } catch (err) {
    console.error("Failed to resolve search", err);
    return null;
  }
}

/** Server-side counterpart of the browser's /api/search/suggest call (see the route handler). */
export async function getSuggestions(params: {
  q: string;
  type: string;
}): Promise<Suggestion[]> {
  const search = new URLSearchParams({ q: params.q, type: params.type });
  try {
    const res = await cmsFetch(
      `${CMS_BASE}/content-service/v1/search/suggest?${search}`,
      {
        cache: "no-store",
      },
    );
    if (!res.ok) return [];
    const body = (await res.json()) as { data: { suggestions: Suggestion[] } };
    return body.data?.suggestions ?? [];
  } catch (err) {
    console.error("Failed to load search suggestions", err);
    return [];
  }
}
