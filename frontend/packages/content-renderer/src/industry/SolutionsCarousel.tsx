"use client";

import { useState, type CSSProperties, type ReactNode } from "react";
import type { SectionIntroData } from "../types";

const Chevron = ({ dir }: { dir: "left" | "right" }) => (
  <svg viewBox="0 0 12 22" className="h-[22px] w-[12px] fill-none stroke-current stroke-[2]" aria-hidden>
    <path d={dir === "left" ? "M10 2 2 11l8 9" : "m2 2 8 9-8 9"} strokeLinecap="round" strokeLinejoin="round" />
  </svg>
);

const lines = (text?: string) => (text ?? "").split("\n").map((l) => l.trim()).filter(Boolean);

export interface SolutionSlide {
  tabLabel: string;
  title?: string;
  logo?: string;
  columnA?: string;
  columnB?: string;
  /** The slide photo, already drawn by the host's image slot (slots are functions and cannot cross into this client component). */
  photo?: ReactNode;
}

export interface SolutionGroup {
  name: string;
  slides: SolutionSlide[];
}

/**
 * "Our Solutions" (Figma "Industries" → "Capabilities"): a carousel card. On the left, the slide's
 * photo (with the partner logo) over a white panel holding two bullet columns; on the right, a
 * pale-blue panel with the group heading and a vertical tab list (one tab per slide). The arrows
 * move between solution groups, the tabs between a group's slides.
 */
export function SolutionsCarousel({ groups, intro }: { groups: SolutionGroup[]; intro: SectionIntroData }) {
  const n = groups.length;
  const [groupIdx, setGroupIdx] = useState(0);
  const [active, setActive] = useState(0);
  if (n === 0) return null;
  const slides = groups[groupIdx].slides;
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
              <span className="ind-sol-bubble" style={{ "--i": active } as CSSProperties} aria-hidden>
                {active + 1}
              </span>
            </div>
          </div>

          <div className="ind-sol-media">
            <div className="ind-sol-photo">
              {slide.photo}
              {slide.logo && (
                <div className="ind-sol-logo">
                  {/* SVG brand marks: plain <img> so SVGs render without next/image's SVG guard. */}
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
