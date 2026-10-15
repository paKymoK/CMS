import { Link } from "@/i18n/navigation";
import type { InsightItem, SectionIntro } from "@/content/home/types";

const FALLBACK_INTRO: SectionIntro = {
  section: "service-highlights",
  eyebrow: "Insight/ news",
  title: "Latest Highlights",
  linkLabel: "Read more CMC Global’s News >",
  linkUrl: "/insights",
};

/** "Latest Highlights" (Figma "Insight/ news"): the four newest articles as grey cards. */
export function ServiceHighlights({
  insights,
  intro = FALLBACK_INTRO,
}: {
  insights: InsightItem[];
  intro?: SectionIntro;
}) {
  const items = insights.slice(0, 4);
  if (items.length === 0) return null;
  return (
    <section id="highlights" aria-labelledby="svc-news-title" className="design-canvas">
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
            <li key={p.slug ?? p.title} className="svc-news-card">
              {p.date && <time>{p.date}</time>}
              <h3>{p.title}</h3>
              {p.slug && (
                <Link href={`/insights/${p.slug}`} className="svc-news-more">
                  Learn more →
                </Link>
              )}
            </li>
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
