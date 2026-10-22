"use client";

import { useState, type ReactNode } from "react";
import type { IndustryStoryData, SectionIntroData } from "./types";

const FALLBACK_INTRO: SectionIntroData = { title: "Success Stories" };

/**
 * "Success Stories" (Figma "BFSI" → "Success Stories"): a headline and four figures on the left, and
 * a stack of story cards on the right — the front card is the open story, the others peek out
 * behind it. The two squares (or a click on a card behind) bring another story forward.
 */
export type StoryCard = IndustryStoryData & {
  /** The card photo, already drawn by the host's image slot (slots are functions and cannot cross into this client component). */
  photo?: ReactNode;
};

export function IndustryStoriesView({
  stories,
  intro = FALLBACK_INTRO,
  variant,
}: {
  stories: StoryCard[];
  intro?: SectionIntroData;
  /** Extra class on the section root, for pages whose layout differs slightly (service pages). */
  variant?: string;
}) {
  const n = stories.length;
  const [active, setActive] = useState(0);
  if (n === 0) return null;
  const go = (i: number) => setActive(((i % n) + n) % n);
  const current = stories[active];
  const lead = current.metrics?.length ? current : stories.find((s) => s.metrics?.length);

  return (
    <section id="stories" aria-labelledby="ind-stories-title" className="design-canvas">
      <div className={`ind-stories sec${variant ? ` ${variant}` : ""}`}>
        <div className="sec-eyebrow-wrap">
          <div className="cmc-eyebrow">{intro.eyebrow ?? "Case Studies"}</div>
        </div>
        <div className="ind-sec-head">
          <h2 id="ind-stories-title" className="sec-title">
            {intro.title}
          </h2>
          {intro.body && <p className="ind-sec-sub">{intro.body}</p>}
        </div>

        {lead && (
          <div className="ind-stories-lead">
            {lead.heading && <p className="ind-stories-heading">{lead.heading}</p>}
            <dl className="ind-stories-metrics">
              {(lead.metrics ?? []).slice(0, 4).map((m) => (
                <div key={m.label}>
                  <dt>{m.value}</dt>
                  <dd>{m.label}</dd>
                </div>
              ))}
            </dl>
          </div>
        )}

        <div className="ind-stories-stack">
          {stories.map((s, i) => {
            const rel = (((i - active) % n) + n) % n;
            return (
              <article
                key={s.title}
                className="ind-story"
                data-pos={rel === 0 ? "front" : "back"}
                aria-hidden={rel !== 0}
                onClick={rel === 0 ? undefined : () => go(i)}
              >
                {s.photo}
                <div className="ind-story-body">
                  {s.tag && <p className="ind-story-tag">{s.tag}</p>}
                  <h3>{s.title}</h3>
                  {s.body && <p className="ind-story-text">{s.body}</p>}
                  {s.linkLabel && s.linkUrl && rel === 0 && (
                    <a href={s.linkUrl} className="ind-story-link">
                      {s.linkLabel}
                    </a>
                  )}
                </div>
              </article>
            );
          })}
        </div>

        {n > 1 && (
          <div className="ind-stories-controls">
            <button type="button" aria-label="Previous story" onClick={() => go(active - 1)}>
              <svg viewBox="0 0 19 19" aria-hidden><path d="M16 2 3 9.5 16 17Z" /></svg>
            </button>
            <button type="button" aria-label="Next story" onClick={() => go(active + 1)}>
              <svg viewBox="0 0 19 19" aria-hidden><path d="m3 2 13 7.5L3 17Z" /></svg>
            </button>
            <div className="ind-stories-bar" aria-hidden>
              <span style={{ width: `${100 / n}%`, left: `${(active * 100) / n}%` }} />
            </div>
          </div>
        )}
      </div>
    </section>
  );
}
