"use client";

import { useRef, useState } from "react";
import { CoverImage } from "@/components/ui/CoverImage";
import { Placeholder } from "@/components/ui/Placeholder";
import type { SectionIntro, ServiceCard } from "@/content/home/types";

const FALLBACK_INTRO: SectionIntro = {
  section: "whatwedo",
  eyebrow: "What We Do",
  title: "Architecting Your Next-Gen Digital Core",
};

function Card({
  service,
  index,
  total,
  hidden,
}: {
  service: ServiceCard;
  index: number;
  total: number;
  hidden: boolean;
}) {
  return (
    <article
      className="wwd-card"
      // Tap/keyboard focus reveals the description the same way hover does.
      tabIndex={hidden ? -1 : 0}
      aria-hidden={hidden}
      aria-label={`${service.name} (${index + 1} of ${total})`}
    >
      {service.image ? (
        <CoverImage
          src={service.image}
          alt=""
          sizes="(min-width: 1280px) 13vw, 244px"
          className="object-cover"
        />
      ) : (
        <Placeholder tone="dark" label="Service Image" className="absolute inset-0" />
      )}
      <h3 className="wwd-card-title">{service.name}</h3>
      {service.description && (
        <div className="wwd-card-veil">
          <p className="wwd-card-desc">{service.description}</p>
        </div>
      )}
    </article>
  );
}

/**
 * "What We Do" (Figma "Architect"): eyebrow, gradient heading + paragraph, and the looping strip of
 * service cards with the two round arrows under it. Hovering or focusing a card swaps its image
 * overlay for the description. The strip is two back-to-back copies of the list so it can wrap
 * around in either direction without a visible jump; swiping works on touch screens.
 */
export function WhatWeDo({
  services,
  intro = FALLBACK_INTRO,
}: {
  services: ServiceCard[];
  intro?: SectionIntro;
}) {
  const n = services.length;
  const [pos, setPos] = useState(0);
  const [instant, setInstant] = useState(false);
  const dragStart = useRef<number | null>(null);

  if (n === 0) return null;

  // Re-enable the slide on the frame after an off-screen jump.
  const settle = () => requestAnimationFrame(() => requestAnimationFrame(() => setInstant(false)));

  const next = () => {
    if (!instant) setPos((p) => p + 1);
  };
  const prev = () => {
    if (instant) return;
    if (pos === 0) {
      // Jump (invisibly) to the identical position one copy along, then slide back one card.
      setInstant(true);
      setPos(n);
      requestAnimationFrame(() =>
        requestAnimationFrame(() => {
          setInstant(false);
          setPos(n - 1);
        }),
      );
    } else {
      setPos(pos - 1);
    }
  };
  const onTransitionEnd = () => {
    if (pos >= n) {
      setInstant(true);
      setPos(pos - n);
      settle();
    }
  };

  const copies = [...services, ...services];

  return (
    <section id="whatwedo" aria-labelledby="whatwedo-title" className="design-canvas">
      <div className="wwd">
        <div className="wwd-eyebrow-wrap px-6 min-[1280px]:px-0">
          <div className="cmc-eyebrow">{intro.eyebrow ?? "What We Do"}</div>
        </div>

        <div className="wwd-head">
          <h2 id="whatwedo-title" className="wwd-title">
            {intro.title}
          </h2>
          {intro.body && <p className="wwd-body">{intro.body}</p>}
        </div>

        <div
          className="wwd-viewport"
          aria-roledescription="carousel"
          aria-label="Services"
          onPointerDown={(e) => (dragStart.current = e.clientX)}
          onPointerUp={(e) => {
            if (dragStart.current === null) return;
            const dx = e.clientX - dragStart.current;
            dragStart.current = null;
            if (dx < -40) next();
            else if (dx > 40) prev();
          }}
          onPointerCancel={() => (dragStart.current = null)}
        >
          <div
            className={`wwd-track${instant ? " wwd-track--instant" : ""}`}
            style={{ transform: `translateX(calc(var(--step) * ${-pos}))` }}
            onTransitionEnd={onTransitionEnd}
          >
            {copies.map((service, i) => (
              <Card key={i} service={service} index={i % n} total={n} hidden={i >= n} />
            ))}
          </div>
        </div>

        <div className="wwd-arrows">
          <button type="button" className="wwd-arrow" aria-label="Previous service" onClick={prev}>
            <svg
              viewBox="0 0 7 14"
              className="h-3.5 w-[7px] fill-none stroke-current stroke-[1.5]"
              aria-hidden
            >
              <path d="M6 1 1 7l5 6" strokeLinecap="round" strokeLinejoin="round" />
            </svg>
          </button>
          <button type="button" className="wwd-arrow" aria-label="Next service" onClick={next}>
            <svg
              viewBox="0 0 7 14"
              className="h-3.5 w-[7px] fill-none stroke-current stroke-[1.5]"
              aria-hidden
            >
              <path d="m1 1 5 6-5 6" strokeLinecap="round" strokeLinejoin="round" />
            </svg>
          </button>
        </div>
      </div>
    </section>
  );
}
