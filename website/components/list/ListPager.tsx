import { Link } from "@/i18n/navigation";
import { LinkPendingFlag } from "@/components/list/ResultsPending";

const MAX_NUMBERS = 7;

function windowOf(current: number, total: number): number[] {
  if (total <= MAX_NUMBERS)
    return Array.from({ length: total }, (_, i) => i + 1);
  const start = Math.min(
    Math.max(1, current - Math.floor(MAX_NUMBERS / 2)),
    total - MAX_NUMBERS + 1,
  );
  return Array.from({ length: MAX_NUMBERS }, (_, i) => start + i);
}

/**
 * Numbered pager of the listing pages (Figma Case Studies "slider"): chevrons either side of up to
 * seven page numbers, the current one in a filled navy circle. Every control is a link, so paging
 * works without JavaScript; `href` builds a page's URL (keeping the page's own filters).
 */
export function ListPager({
  page,
  totalPages,
  href,
  label,
}: {
  page: number;
  totalPages: number;
  href: (page: number) => string;
  /** Accessible name of the nav, e.g. "Case studies pages". */
  label: string;
}) {
  if (totalPages <= 1) return null;

  return (
    <nav className="csl-pager" aria-label={label}>
      {page > 1 ? (
        <Link
          href={href(page - 1)}
          className="csl-pager-arrow"
          aria-label="Previous page"
        >
          <svg viewBox="0 0 7 14" aria-hidden>
            <path
              d="M6 1 1 7l5 6"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
            />
          </svg>
          <LinkPendingFlag />
        </Link>
      ) : (
        <span className="csl-pager-arrow" aria-hidden />
      )}
      {windowOf(page, totalPages).map((p) => (
        <Link
          key={p}
          href={href(p)}
          className="csl-pager-num"
          aria-current={p === page ? "page" : undefined}
          aria-label={`Page ${p}`}
        >
          {p}
          <LinkPendingFlag />
        </Link>
      ))}
      {page < totalPages ? (
        <Link
          href={href(page + 1)}
          className="csl-pager-arrow"
          aria-label="Next page"
        >
          <svg viewBox="0 0 7 14" aria-hidden>
            <path
              d="m1 1 5 6-5 6"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
            />
          </svg>
          <LinkPendingFlag />
        </Link>
      ) : (
        <span className="csl-pager-arrow" aria-hidden />
      )}
    </nav>
  );
}
