"use client";

import { useState } from "react";
import { CoverImage } from "@/components/ui/CoverImage";
import { DiagonalArrow } from "@/components/ui/DiagonalArrow";
import { Link } from "@/i18n/navigation";
import type { InsightItem, SectionIntro } from "@/content/home/types";

const FALLBACK_INTRO: SectionIntro = {
  section: "insights",
  eyebrow: "Insights",
  title: "Building Tomorrow Together",
  linkLabel: "Explore all Insights",
  linkUrl: "/insights",
  buttonLabel: "Read this letter",
};

// The accordion on the artboard: x and width of each panel by its rank from the open one (in artboard
// px, relative to the 1411px stage that starts at x=257). Rank 0 is the open, wide panel.
const RANK_X = [0, 825, 1048, 1225, 1347, 1380, 1400];
const RANK_W = [809, 203, 158, 102, 24, 11, 11];
const STAGE_W = 1411;

const Chevron = ({ dir }: { dir: "left" | "right" }) => (
  <svg
    viewBox="0 0 15 13"
    className="h-[13px] w-[15px] fill-none stroke-current stroke-[1.8]"
    aria-hidden
  >
    <path
      d={dir === "left" ? "M13 6.5H2M7 1 1.5 6.5 7 12" : "M2 6.5h11M8 1l5.5 5.5L8 12"}
      strokeLinecap="round"
      strokeLinejoin="round"
    />
  </svg>
);

/**
 * "Building Tomorrow Together" (Figma "Building"): a photo accordion. One panel is open at full
 * width with its date; the rest fan out as ever-narrower slivers. Clicking a sliver — or the arrows —
 * opens it. The open item's headline and a button to its page sit underneath.
 */
export function InsightsAccordion({
  insights,
  intro = FALLBACK_INTRO,
}: {
  insights: InsightItem[];
  intro?: SectionIntro;
}) {
  const items = insights.slice(0, RANK_X.length);
  const n = items.length;
  const [active, setActive] = useState(0);
  if (n === 0) return null;

  const go = (i: number) => setActive(((i % n) + n) % n);
  const current = items[active];

  return (
    <section id="insights" aria-labelledby="insights-title" className="design-canvas">
      <div className="ins sec">
        <div className="sec-eyebrow-wrap">
          <div className="cmc-eyebrow">{intro.eyebrow ?? "Insights"}</div>
        </div>
        <h2 id="insights-title" className="sec-title ins-title">
          {intro.title}
        </h2>

        {n > 1 && (
          <div className="ins-nav">
            <button
              type="button"
              className="ins-arrow"
              aria-label="Previous insight"
              onClick={() => go(active - 1)}
            >
              <Chevron dir="left" />
            </button>
            <button
              type="button"
              className="ins-arrow"
              aria-label="Next insight"
              onClick={() => go(active + 1)}
            >
              <Chevron dir="right" />
            </button>
          </div>
        )}

        <div className="ins-stage" role="list" aria-label="Insights">
          {items.map((item, i) => {
            const rank = (i - active + n) % n;
            const isActive = rank === 0;
            return (
              <div
                key={item.title}
                role="listitem"
                tabIndex={0}
                className="ins-panel"
                data-active={isActive}
                aria-label={item.title}
                aria-current={isActive}
                onClick={() => go(i)}
                onKeyDown={(e) => {
                  if (e.key === "Enter" || e.key === " ") {
                    e.preventDefault();
                    go(i);
                  }
                }}
                style={{
                  left: `${(RANK_X[rank] / STAGE_W) * 100}%`,
                  width: `${(RANK_W[rank] / STAGE_W) * 100}%`,
                }}
              >
                {item.image && (
                  <div className="ins-panel-img">
                    <CoverImage
                      src={item.image}
                      alt=""
                      sizes="(min-width: 1280px) 55vw, 90vw"
                      className="object-cover"
                    />
                  </div>
                )}
                <div className="ins-scrim" aria-hidden />
                {item.date && <span className="ins-date">{item.date}</span>}
              </div>
            );
          })}
        </div>

        <div className="ins-foot">
          <h3 className="ins-active-title" aria-live="polite">
            {current.title}
          </h3>
          {current.slug && (
            <Link href={`/insights/${current.slug}`} className="ins-read">
              {intro.buttonLabel ?? "Read this letter"}
            </Link>
          )}
        </div>

        {intro.linkLabel && intro.linkUrl && (
          <div className="ins-more">
            <Link href={intro.linkUrl} className="sec-link">
              {intro.linkLabel}
              <DiagonalArrow />
            </Link>
          </div>
        )}
      </div>
    </section>
  );
}
