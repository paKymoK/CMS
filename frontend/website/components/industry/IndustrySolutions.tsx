"use client";

import { useState } from "react";
import { CoverImage } from "@/components/ui/CoverImage";
import type { IndustrySolution, SectionIntro } from "@/content/home/types";

const FALLBACK_INTRO: SectionIntro = { section: "industry-solutions", title: "Our Solutions" };

const Chevron = ({ dir }: { dir: "left" | "right" }) => (
  <svg viewBox="0 0 12 22" className="h-[22px] w-[12px] fill-none stroke-current stroke-[2]" aria-hidden>
    <path d={dir === "left" ? "M10 2 2 11l8 9" : "m2 2 8 9-8 9"} strokeLinecap="round" strokeLinejoin="round" />
  </svg>
);

const lines = (text?: string) => (text ?? "").split("\n").map((l) => l.trim()).filter(Boolean);

/**
 * "Our Solutions" (Figma "Industries" → "Capabilities"): a carousel card. On the left, the slide's
 * photo (with the partner logo) over a white panel holding two bullet columns; on the right, a
 * pale-blue panel with the group heading and a vertical tab list (one tab per slide). The arrows
 * move between solution groups, the tabs between a group's slides.
 */
export function IndustrySolutions({
  solutions,
  intro = FALLBACK_INTRO,
}: {
  solutions: IndustrySolution[];
  intro?: SectionIntro;
}) {
  // Slides sharing a `group` form one carousel page: the arrows move between groups, the tabs
  // between the slides of the open group.
  const groups = solutions.reduce<IndustrySolution[][]>((acc, sol) => {
    const g = acc.find((list) => list[0].group === sol.group);
    if (g) g.push(sol);
    else acc.push([sol]);
    return acc;
  }, []);
  const n = groups.length;
  const [groupIdx, setGroupIdx] = useState(0);
  const [active, setActive] = useState(0);
  if (n === 0) return null;
  const slides = groups[groupIdx];
  const slide = slides[Math.min(active, slides.length - 1)];
  const goGroup = (i: number) => {
    setGroupIdx(((i % n) + n) % n);
    setActive(0);
  };

  return (
    <section id="solutions" aria-labelledby="ind-solutions-title" className="design-canvas">
      <div className="ind-sol sec">
        <div className="sec-eyebrow-wrap">
          <div className="cmc-eyebrow">{intro.eyebrow ?? "Solutions"}</div>
        </div>
        <div className="ind-sec-head">
          <h2 id="ind-solutions-title" className="sec-title">
            {intro.title}
          </h2>
          {intro.body && <p className="ind-sec-sub">{intro.body}</p>}
        </div>

        <div className="ind-sol-card">
          <div className="ind-sol-bg">
            {slide.title && <h3 className="ind-sol-title">{slide.title}</h3>}
            <div className="ind-sol-tabs" role="tablist" aria-label="Solutions">
              {slides.map((s, i) => (
                <button
                  key={s.tabLabel}
                  type="button"
                  role="tab"
                  aria-selected={i === active}
                  className="ind-sol-tab"
                  onClick={() => setActive(i)}
                >
                  {s.tabLabel}
                </button>
              ))}
              <span className="ind-sol-bubble" style={{ "--i": active } as React.CSSProperties} aria-hidden>
                {active + 1}
              </span>
            </div>
          </div>

          <div className="ind-sol-media">
            <div className="ind-sol-photo">
              {slide.image && (
                <CoverImage
                  src={slide.image}
                  alt=""
                  sizes="(min-width: 1280px) 45vw, 100vw"
                  className="object-cover"
                />
              )}
              {slide.logo && (
                <div className="ind-sol-logo">
                  {/* SVG brand marks: plain <img> so SVGs render without next/image's SVG guard. */}
                  {/* eslint-disable-next-line @next/next/no-img-element */}
                  <img src={slide.logo} alt={slide.tabLabel} />
                </div>
              )}
            </div>
            <div className="ind-sol-cols">
              {[slide.columnA, slide.columnB].map((col, i) => (
                <ul key={i}>
                  {lines(col).map((l) => (
                    <li key={l}>{l}</li>
                  ))}
                </ul>
              ))}
            </div>
          </div>

          {n > 1 && (
            <>
              <button type="button" className="ind-sol-arrow ind-sol-arrow--prev" aria-label="Previous solution group" onClick={() => goGroup(groupIdx - 1)}>
                <Chevron dir="left" />
              </button>
              <button type="button" className="ind-sol-arrow ind-sol-arrow--next" aria-label="Next solution group" onClick={() => goGroup(groupIdx + 1)}>
                <Chevron dir="right" />
              </button>
            </>
          )}
        </div>
      </div>
    </section>
  );
}
