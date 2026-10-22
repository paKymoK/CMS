"use client";

import { useState, type ReactNode } from "react";

const Chevron = ({ dir }: { dir: "left" | "right" }) => (
  <svg viewBox="0 0 12 22" className="h-[14px] w-[8px] fill-none stroke-current stroke-[2]" aria-hidden>
    <path d={dir === "left" ? "M10 2 2 11l8 9" : "m2 2 8 9-8 9"} strokeLinecap="round" strokeLinejoin="round" />
  </svg>
);

/**
 * "Reasons to Partner With Us" (Figma "Content" → "Why choose us"): a slider — big title and text on
 * the left, a large photo with a smaller one overlapping it on the right, and a "1/3" counter with
 * arrows. The counter and arrows only appear with more than one reason.
 */
export interface ReasonSlide {
  title: string;
  body?: string;
  /** The large photo and the small overlapping one, already drawn by the host's image slot. */
  main?: ReactNode;
  side?: ReactNode;
}

export function ReasonsSlider({ title, subtitle, reasons }: { title: string; subtitle?: string; reasons: ReasonSlide[] }) {
  const n = reasons.length;
  const [i, setI] = useState(0);
  if (n === 0) return null;
  const go = (next: number) => setI(((next % n) + n) % n);
  const r = reasons[i];
  return (
    <section id="reasons" aria-labelledby="svc-reasons-title" className="design-canvas">
      <div className="svc-reasons sec">
        <div className="sec-eyebrow-wrap">
          <div className="cmc-eyebrow">Why choose us</div>
        </div>
        <div className="svc-sec-head">
          <h2 id="svc-reasons-title" className="sec-title">
            {title}
          </h2>
          {subtitle && <p>{subtitle}</p>}
        </div>

        <div className="svc-reason">
          <div className="svc-reason-text">
            <h3>{r.title}</h3>
            {r.body && <p>{r.body}</p>}
          </div>
          <div className="svc-reason-main">{r.main}</div>
          {r.side && <div className="svc-reason-side">{r.side}</div>}
        </div>

        {n > 1 && (
          <div className="svc-reason-pager">
            <span aria-live="polite">
              {i + 1}/{n}
            </span>
            <button type="button" aria-label="Previous reason" onClick={() => go(i - 1)}>
              <Chevron dir="left" />
            </button>
            <button type="button" aria-label="Next reason" onClick={() => go(i + 1)}>
              <Chevron dir="right" />
            </button>
          </div>
        )}
      </div>
    </section>
  );
}
