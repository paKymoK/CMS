import { Link } from "@/i18n/navigation";
import type { SearchResolution } from "@/lib/cms/search";

/**
 * The line under a listing's result count when the search wasn't taken literally: either the
 * results are for a corrected spelling ("Showing results for X — search instead for Y"), or there
 * were none and a likelier spelling exists ("Did you mean X?"). Nothing at all for a search that
 * simply worked.
 */
export function SearchNotice({
  resolution,
  exactHref,
  suggestedHref,
}: {
  resolution: SearchResolution | null;
  /** The same search with spelling correction turned off. */
  exactHref: string;
  /** A search for the `didYouMean` spelling. */
  suggestedHref: string;
}) {
  if (resolution?.correctedQuery) {
    return (
      <p className="psl-notice" role="status">
        Showing results for <strong>{resolution.correctedQuery}</strong>.{" "}
        <Link href={exactHref}>Search instead for “{resolution.query}”</Link>
      </p>
    );
  }
  if (resolution?.didYouMean) {
    return (
      <p className="psl-notice" role="status">
        No results for “{resolution.query}”. Did you mean{" "}
        <Link href={suggestedHref}>{resolution.didYouMean}</Link>?
      </p>
    );
  }
  return null;
}
