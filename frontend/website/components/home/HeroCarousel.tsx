"use client";

import { getImageProps } from "next/image";
import { useCallback, useEffect, useRef, useState } from "react";
import type { HeroSlide } from "@/content/home/types";
import { usePrefersReducedMotion } from "@/lib/usePrefersReducedMotion";

const AUTOPLAY_MS = 6000;

/** Artboard (1920px) pixels → a length that scales with the hero's width. */
const u = (n: number) => `calc(${n} * var(--u))`;

// One finished image per screen class, swapped by media query. The sizes are what the admin
// requires of an upload, so each box matches its image exactly and nothing is cropped (the copy is
// part of the picture).
const DESKTOP = { width: 1920, height: 699 }; // ≥1280px
const TABLET = { width: 1536, height: 864 }; //  768–1279px
const PHONE = { width: 750, height: 1000 }; //   <768px

function Slide({
  slide,
  active,
  priority,
}: {
  slide: HeroSlide;
  active: boolean;
  priority: boolean;
}) {
  const common = { alt: "", sizes: "100vw", priority, quality: 80 };
  const { props: phone } = getImageProps({ ...common, src: slide.mobileImage, ...PHONE });
  const { props: tablet } = getImageProps({ ...common, src: slide.tabletImage, ...TABLET });
  const { props: desktop } = getImageProps({ ...common, src: slide.image, ...DESKTOP });

  const picture = (
    <picture>
      <source media="(min-width: 1280px)" srcSet={desktop.srcSet} />
      <source media="(min-width: 768px)" srcSet={tablet.srcSet} />
      {/* eslint-disable-next-line jsx-a11y/alt-text -- decorative: the slide's heading carries the text */}
      <img {...phone} className="block h-full w-full" />
    </picture>
  );

  // The artwork already says it; the alt text is exposed once, as the slide's (visually hidden)
  // heading, which is also the accessible name of the link when the slide has one.
  const heading = <h2 className="sr-only">{slide.alt}</h2>;

  return (
    <div
      aria-hidden={!active}
      className="absolute inset-0 transition-opacity duration-700 ease-out"
      style={{ opacity: active ? 1 : 0, pointerEvents: active ? "auto" : "none" }}
    >
      {picture}
      {slide.linkUrl ? (
        <a
          href={slide.linkUrl}
          tabIndex={active ? 0 : -1}
          className="absolute inset-0 z-[2] focus-visible:outline-2 focus-visible:-outline-offset-4 focus-visible:outline-white"
        >
          {heading}
        </a>
      ) : (
        heading
      )}
    </div>
  );
}

/**
 * Full-width hero carousel: cross-fading slides of finished artwork and the 5-segment progress bar
 * near the bottom. Slides come from the CMS (content_item type "hero_slide"); a slide without all
 * three images (one saved before slides became artwork) is skipped. Autoplays unless the visitor
 * prefers reduced motion; pauses while hovered or focused. Starts below the 58px site header
 * (which is absolutely positioned) so the artwork is not hidden under it.
 */
export function HeroCarousel({ slides: all }: { slides: HeroSlide[] }) {
  const slides = all.filter((s) => s.image && s.tabletImage && s.mobileImage);
  const reduced = usePrefersReducedMotion();
  const [index, setIndex] = useState(0);
  const pausedRef = useRef(false);
  const count = slides.length;

  const go = useCallback((i: number) => setIndex(((i % count) + count) % count), [count]);

  useEffect(() => {
    if (reduced || count < 2) return;
    const id = setInterval(() => {
      if (!pausedRef.current) setIndex((i) => (i + 1) % count);
    }, AUTOPLAY_MS);
    return () => clearInterval(id);
  }, [reduced, count]);

  if (count === 0) return null;

  return (
    <section
      id="overview"
      aria-roledescription="carousel"
      aria-label="Highlights"
      className="design-canvas relative mt-[58px] aspect-[750/1000] overflow-hidden bg-[#00152d] min-[768px]:aspect-[1536/864] min-[1280px]:aspect-[1920/699]"
      onMouseEnter={() => (pausedRef.current = true)}
      onMouseLeave={() => (pausedRef.current = false)}
      onFocus={() => (pausedRef.current = true)}
      onBlur={() => (pausedRef.current = false)}
    >
      {slides.map((slide, i) => (
        <Slide key={i} slide={slide} active={i === index} priority={i === 0} />
      ))}

      {count > 1 && (
        <div
          className="absolute inset-x-0 z-[3] flex justify-center"
          style={{ bottom: `max(20px, ${u(43)})`, gap: `max(6px, ${u(10)})` }}
        >
          {slides.map((slide, i) => (
            <button
              key={i}
              type="button"
              aria-label={`Go to slide ${i + 1}: ${slide.alt.replace(/\s+/g, " ")}`}
              aria-current={i === index}
              onClick={() => go(i)}
              className="hero-seg cursor-pointer p-0"
            />
          ))}
        </div>
      )}
    </section>
  );
}
