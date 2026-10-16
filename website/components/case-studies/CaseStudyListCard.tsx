import { CoverImage } from "@/components/ui/CoverImage";
import { Placeholder } from "@/components/ui/Placeholder";
import { Link } from "@/i18n/navigation";
import type { CaseStudyCard } from "@/lib/cms/types";

/**
 * One card on the Case Studies listing (Figma "#CS1"): thumbnail, bold title, summary, then the
 * date on the left and a "Discover" link with its circled arrow on the right, on a white card with
 * a 2px light-grey edge.
 */
export function CaseStudyListCard({ study }: { study: CaseStudyCard }) {
  return (
    <Link href={`/case-studies/${study.slug}`} className="csl-card">
      <div className="csl-card-img">
        {study.image ? (
          <CoverImage
            src={study.image}
            alt=""
            sizes="(min-width: 1280px) 22vw, (min-width: 640px) 46vw, 92vw"
            className="object-cover"
          />
        ) : (
          <Placeholder tone="navy" label="Case study" fill />
        )}
      </div>
      <h2 className="csl-card-title">{study.title}</h2>
      {study.summary && <p className="csl-card-sum">{study.summary}</p>}
      <div className="csl-card-foot">
        <span className="csl-card-date">
          <svg viewBox="0 0 28 28" aria-hidden>
            <rect x="5" y="6.5" width="18" height="16" rx="2" fill="none" stroke="currentColor" strokeWidth="2" />
            <path d="M5 11.5h18M10 4v5M18 4v5" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
          </svg>
          {study.date}
        </span>
        <span className="csl-card-more">
          Discover
          <svg viewBox="0 0 28 28" aria-hidden>
            <circle cx="14" cy="14" r="11.5" fill="#003f79" />
            <path d="M8.8 14h10.4M15.6 9.6l4.4 4.4-4.4 4.4" fill="none" stroke="#fff" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" />
          </svg>
        </span>
      </div>
    </Link>
  );
}
