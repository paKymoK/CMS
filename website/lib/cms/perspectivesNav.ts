/** id of the results section: page/filter changes navigate to this hash so the reader lands on the results. */
export const PERSPECTIVES_RESULTS_ID = "perspectives-results";

export type PerspectivesQuery = {
  page?: number;
  /** Content type (a post's category). */
  category?: string;
  /** Topic (one of a post's tags). */
  tag?: string;
  q?: string;
  /** Search exactly what was typed (no spelling correction). */
  exact?: boolean;
  /** "oldest" reverses the default newest-first order. */
  sort?: "oldest";
};

/** URL of the Perspectives listing for a filter/page state, ending at the results section. */
export function perspectivesHref(opts: PerspectivesQuery): string {
  const query = new URLSearchParams();
  if (opts.category) query.set("category", opts.category);
  if (opts.tag) query.set("tag", opts.tag);
  if (opts.q) query.set("q", opts.q);
  if (opts.q && opts.exact) query.set("exact", "1");
  if (opts.sort) query.set("sort", opts.sort);
  if (opts.page && opts.page > 1) query.set("page", String(opts.page));
  const qs = query.toString();
  return `/insights${qs ? `?${qs}` : ""}#${PERSPECTIVES_RESULTS_ID}`;
}
