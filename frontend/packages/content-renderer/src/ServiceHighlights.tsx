import type { ComponentType, ElementType, ReactNode } from "react";
import { defaultSlots } from "./defaults";
import type { InsightItemData, PostArticleSlots, SectionLinkIntroData } from "./types";

/** The host's tooltip (shows a clipped title in full); without one the card is a plain list item. */
export type CardTooltip = ComponentType<{ as?: ElementType; className?: string; content: ReactNode; truncatedSelector?: string; children: ReactNode }>;

const PlainCard: CardTooltip = ({ className, children }) => <li className={className}>{children}</li>;

const FALLBACK_INTRO: SectionLinkIntroData = {
  eyebrow: "Insight/ news",
  title: "Latest Highlights",
  linkLabel: "Read more CMC Global’s News >",
  linkUrl: "/insights",
};

/** "Latest Highlights" (Figma "Insight/ news"): the four newest articles as grey cards. The whole card is the link (stretched from
 * "Learn more →", which stays pinned bottom-left); long titles are clamped to three lines. */
export function ServiceHighlights({
  insights,
  intro = FALLBACK_INTRO,
  slots = defaultSlots,
  Tooltip = PlainCard,
}: {
  insights: InsightItemData[];
  intro?: SectionLinkIntroData;
  slots?: PostArticleSlots;
  Tooltip?: CardTooltip;
}) {
  const { Link } = slots;
  const items = insights.slice(0, 4);
  if (items.length === 0) return null;
  return (
    <section
      id="highlights"
      aria-labelledby="svc-news-title"
      className="design-canvas"
    >
      <div className="svc-news sec">
        <div className="sec-eyebrow-wrap">
          <div className="cmc-eyebrow">{intro.eyebrow ?? "Insight/ news"}</div>
        </div>
        <div className="svc-sec-head">
          <h2 id="svc-news-title" className="sec-title">
            {intro.title}
          </h2>
        </div>
        <ul className="svc-news-cards">
          {items.map((p) => (
            <Tooltip
              as="li"
              key={p.slug ?? p.title}
              className="svc-news-card"
              content={p.title}
              truncatedSelector="h3"
            >
              {p.date && <time>{p.date}</time>}
              <h3>{p.title}</h3>
              {p.slug && (
                <Link
                  href={`/insights/${p.slug}`}
                  className="svc-news-more"
                  aria-label={`Learn more: ${p.title}`}
                >
                  Learn more →
                </Link>
              )}
            </Tooltip>
          ))}
        </ul>
        {intro.linkLabel && intro.linkUrl && (
          <Link href={intro.linkUrl} className="svc-news-all">
            {intro.linkLabel}
          </Link>
        )}
      </div>
    </section>
  );
}
