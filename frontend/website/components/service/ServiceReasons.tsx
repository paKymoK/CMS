"use client";

import { useState } from "react";
import { CoverImage } from "@/components/ui/CoverImage";
import type { ServicePageData } from "@/lib/cms/services";

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
export function ServiceReasons({ page }: { page: ServicePageData }) {
  const reasons = page.reasons ?? [];
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
            {page.reasonsTitle ?? "Reasons to Partner With Us"}
          </h2>
          {page.reasonsSubtitle && <p>{page.reasonsSubtitle}</p>}
        </div>

        <div className="svc-reason">
          <div className="svc-reason-text">
            <h3>{r.title}</h3>
            {r.body && <p>{r.body}</p>}
          </div>
          <div className="svc-reason-main">
            {r.image && (
              <CoverImage src={r.image} alt="" sizes="(min-width: 1280px) 36vw, 90vw" className="object-cover" />
            )}
          </div>
          {r.sideImage && (
            <div className="svc-reason-side">
              <CoverImage src={r.sideImage} alt="" sizes="(min-width: 1280px) 21vw, 50vw" className="object-cover" />
            </div>
          )}
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
