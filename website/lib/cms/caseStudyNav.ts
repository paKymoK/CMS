/** id of the results section: every page/filter change navigates to this hash so the user lands on the results. */
export const RESULTS_ID = "case-studies-results";

/** URL of the listing for a page/filter state, ending at the results section. */
export function caseStudiesHref(opts: { page?: number; category?: string; q?: string }): string {
  const query = new URLSearchParams();
  if (opts.category) query.set("category", opts.category);
  if (opts.q) query.set("q", opts.q);
  if (opts.page && opts.page > 1) query.set("page", String(opts.page));
  const qs = query.toString();
  return `/case-studies${qs ? `?${qs}` : ""}#${RESULTS_ID}`;
}
