import { CoverImage } from "@/components/ui/CoverImage";
import { Link } from "@/i18n/navigation";
import type { IndustryCard, SectionIntro } from "@/content/home/types";

const FALLBACK_INTRO: SectionIntro = { section: "service-industries", eyebrow: "Industries", title: "Industries" };

/**
 * "Industries" (Figma "Industries"): a row of white cards, each an industry icon + name. A card
 * links to its industry page when it has one.
 */
export function ServiceIndustries({
  cards,
  intro = FALLBACK_INTRO,
}: {
  cards: IndustryCard[];
  intro?: SectionIntro;
}) {
  if (cards.length === 0) return null;
  return (
    <section id="industries" aria-labelledby="svc-ind-title" className="design-canvas">
      <div className="svc-ind sec">
        <div className="sec-eyebrow-wrap">
          <div className="cmc-eyebrow">{intro.eyebrow ?? "Industries"}</div>
        </div>
        <div className="svc-sec-head">
          <h2 id="svc-ind-title" className="sec-title">
            {intro.title}
          </h2>
        </div>
        <ul className="svc-ind-cards">
          {cards.map((c) => {
            const inner = c.image ? (
              <CoverImage src={c.image} alt={c.label} sizes="(min-width: 1280px) 19vw, 50vw" className="object-contain" />
            ) : (
              <span className="svc-ind-label">{c.label}</span>
            );
            return (
              <li key={c.label}>
                {c.url ? (
                  <Link href={c.url} className="svc-ind-card" aria-label={c.label}>
                    {inner}
                  </Link>
                ) : (
                  <div className="svc-ind-card">{inner}</div>
                )}
              </li>
            );
          })}
        </ul>
      </div>
    </section>
  );
}
