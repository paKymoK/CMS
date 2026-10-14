"use client";

import { useRef, useState } from "react";
import { CoverImage } from "@/components/ui/CoverImage";
import type { SectionIntro, Testimonial } from "@/content/home/types";

const FALLBACK_INTRO: SectionIntro = {
  section: "testimonials",
  eyebrow: "Testimonials",
  title: "Hear More from Our Clients",
};

const Chevron = ({ dir }: { dir: "left" | "right" }) => (
  <svg
    viewBox="0 0 12 22"
    className="h-[14px] w-[8px] fill-none stroke-current stroke-[2]"
    aria-hidden
  >
    <path
      d={dir === "left" ? "M10 2 2 11l8 9" : "m2 2 8 9-8 9"}
      strokeLinecap="round"
      strokeLinejoin="round"
    />
  </svg>
);

/**
 * "Hear More from Our Clients" (Figma "Hear More"): one large quote card — abstract panel with the
 * client's logo, quote and name on the left, portrait on the right — flanked on the artboard by the
 * previous and next clients' brand cards. Arrows, dots and swipe move between clients; the flanks
 * are simply the neighbours in the loop. Below 1280px only the quote card shows.
 */
export function Testimonials({
  testimonials,
  intro = FALLBACK_INTRO,
}: {
  testimonials: Testimonial[];
  intro?: SectionIntro;
}) {
  const n = testimonials.length;
  const [index, setIndex] = useState(0);
  const dragStart = useRef<number | null>(null);

  if (n === 0) return null;

  const go = (i: number) => setIndex(((i % n) + n) % n);
  // Position of testimonial i relative to the active one, in the loop.
  const rel = (i: number) => (((i - index) % n) + n) % n;
  const side = (i: number): "left" | "right" | "none" => {
    const r = rel(i);
    if (r === 0) return "none";
    if (r === 1) return "right";
    if (r === n - 1) return "left";
    return "none";
  };

  return (
    <section id="testimonials" aria-labelledby="testimonials-title" className="design-canvas">
      <div className="tst">
        <div className="sec-eyebrow-wrap">
          <div className="cmc-eyebrow">{intro.eyebrow ?? "Testimonials"}</div>
        </div>
        <h2 id="testimonials-title" className="sec-title tst-title">
          {intro.title}
        </h2>

        <div
          className="tst-stage"
          aria-roledescription="carousel"
          aria-label="Client testimonials"
          onPointerDown={(e) => (dragStart.current = e.clientX)}
          onPointerUp={(e) => {
            if (dragStart.current === null) return;
            const dx = e.clientX - dragStart.current;
            dragStart.current = null;
            if (dx < -50) go(index + 1);
            else if (dx > 50) go(index - 1);
          }}
          onPointerCancel={() => (dragStart.current = null)}
        >
          {testimonials.map((t, i) => (
            <div
              key={`flank-${i}`}
              className="tst-flank"
              data-side={n > 1 ? side(i) : "none"}
              aria-hidden
              style={
                {
                  "--ff": t.flankFrom ?? "#404040",
                  "--ft": t.flankTo ?? "#000000",
                } as React.CSSProperties
              }
            >
              {/* SVG/PNG brand marks: plain <img> so SVGs render without next/image's SVG guard. */}
              {t.flankLogo ? (
                // eslint-disable-next-line @next/next/no-img-element
                <img src={t.flankLogo} alt="" />
              ) : (
                <span className="text-2xl font-bold text-white">{t.company}</span>
              )}
            </div>
          ))}

          {testimonials.map((t, i) => {
            const active = i === index;
            return (
              <article
                key={`card-${i}`}
                className="tst-card"
                data-pos={active ? "center" : "hidden"}
                aria-hidden={!active}
                aria-label={`${t.name}, ${t.title}, ${t.company}`}
              >
                <div className="tst-panel">
                  {t.backgroundImage && (
                    <div className="tst-panel-img">
                      <CoverImage
                        src={t.backgroundImage}
                        alt=""
                        sizes="(min-width: 1280px) 30vw, 90vw"
                        className="object-cover object-left-bottom"
                      />
                    </div>
                  )}
                  {t.logo ? (
                    // eslint-disable-next-line @next/next/no-img-element
                    <img className="tst-logo" src={t.logo} alt={t.company} />
                  ) : (
                    <div className="tst-logo relative text-xl font-bold">{t.company}</div>
                  )}
                  {t.quote && <p className="tst-quote">{t.quote}</p>}
                  <div className="tst-name">
                    <div>{t.name}</div>
                    <div>{t.title}</div>
                  </div>
                </div>
                <div className="tst-photo">
                  {t.photo && (
                    <CoverImage
                      src={t.photo}
                      alt={t.name}
                      sizes="(min-width: 1280px) 30vw, 90vw"
                      className="object-cover"
                    />
                  )}
                </div>
              </article>
            );
          })}
        </div>

        {n > 1 && (
          <div className="tst-controls">
            <button
              type="button"
              className="tst-arrow"
              aria-label="Previous testimonial"
              onClick={() => go(index - 1)}
            >
              <Chevron dir="left" />
            </button>
            <div className="tst-dots">
              {testimonials.map((t, i) => (
                <button
                  key={i}
                  type="button"
                  className="tst-dot"
                  aria-label={`Show testimonial from ${t.name}`}
                  aria-current={i === index}
                  onClick={() => go(i)}
                />
              ))}
            </div>
            <button
              type="button"
              className="tst-arrow"
              aria-label="Next testimonial"
              onClick={() => go(index + 1)}
            >
              <Chevron dir="right" />
            </button>
          </div>
        )}
      </div>
    </section>
  );
}
