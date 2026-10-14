"use client";

import Image from "next/image";
import { useCallback, useEffect, useRef, useState, type CSSProperties } from "react";
import type { HeroSlide } from "@/content/home/types";
import { usePrefersReducedMotion } from "@/lib/usePrefersReducedMotion";

const AUTOPLAY_MS = 6000;

type Layout = NonNullable<HeroSlide["layout"]>;

/** Artboard (1920px) pixels → a length that scales with the hero's width. */
const u = (n: number) => `calc(${n} * var(--u))`;

type Preset = {
  /** Custom properties consumed by .hero-copy and friends (see globals.css). */
  vars: CSSProperties;
  gradientTitle?: boolean;
  /** Dark gradient along the bottom edge (the "Great Place to Work" photo slide). */
  bottomShade?: boolean;
  /** object-position of the background photo (the design crops this one low, not centred). */
  imagePosition?: string;
};

// Every number below is read straight off the Figma "Homepage Carousel" variants (1920×699).
const PRESETS: Record<Layout, Preset> = {
  // Variant "Default": centred title, tagline and a white pill.
  center: {
    vars: {
      "--l": "50%",
      "--tf": "translateX(-50%)",
      "--t": u(110),
      "--w": u(780),
      "--tmw": u(640),
      "--ta": "center",
      "--ai": "center",
      "--ts": u(56),
      "--tw": 600,
      "--tl": 1.212,
      "--ss": u(20),
      "--sw": 600,
      "--sl": 1.32,
      "--sm": u(12),
      "--cm": u(37),
      "--cp": u(21),
    } as CSSProperties,
  },
  // Variant2: light-weight headline on the left, blue gradient pill.
  left: {
    vars: {
      "--l": u(149),
      "--t": u(163),
      "--w": u(613),
      "--ta": "left",
      "--ai": "flex-start",
      "--ts": u(50),
      "--tw": 300,
      "--tl": 1.14,
      "--cm": u(43),
      "--cp": u(25),
    } as CSSProperties,
  },
  // Variant5: navy right-aligned headline + body copy, pill pushed well below.
  right: {
    vars: {
      "--r": u(249),
      "--t": u(134),
      "--w": u(506),
      "--ta": "right",
      "--ai": "flex-end",
      "--fg": "#011a50",
      "--ts": u(50),
      "--tw": 300,
      "--tl": 1.02,
      "--ss": u(18),
      "--sw": 400,
      "--sl": 1.212,
      "--sm": u(19),
      "--cm": u(124),
      "--cp": u(25),
    } as CSSProperties,
  },
  // Variant3: big regular-weight headline with white→blue gradient fill.
  "left-wide": {
    gradientTitle: true,
    vars: {
      "--l": u(316),
      "--t": u(134),
      "--w": u(723),
      "--ta": "left",
      "--ai": "flex-start",
      "--ts": u(69),
      "--tw": 400,
      "--tl": 0.98,
      "--ss": u(22),
      "--sw": 600,
      "--sl": 1.212,
      "--sm": u(21),
      "--cm": u(52),
      "--cp": u(38),
    } as CSSProperties,
  },
  // Variant4: photo slide, headline low on the left, no button.
  "bottom-left": {
    bottomShade: true,
    imagePosition: "50% 64%",
    vars: {
      "--l": u(290),
      "--t": u(335),
      "--w": u(591),
      "--ta": "left",
      "--ai": "flex-start",
      "--ts": u(56),
      "--tw": 400,
      "--tl": 1,
    } as CSSProperties,
  },
};

function background(slide: HeroSlide): CSSProperties {
  const { backgroundColor: from, backgroundColorEnd: to } = slide;
  if (from && to) return { background: `linear-gradient(120deg, ${from}, ${to})` };
  if (from) return { background: from };
  return { background: "#00152d" };
}

function Slide({
  slide,
  active,
  priority,
}: {
  slide: HeroSlide;
  active: boolean;
  priority: boolean;
}) {
  const preset = PRESETS[slide.layout ?? "center"] ?? PRESETS.center;
  return (
    <div
      aria-hidden={!active}
      className="absolute inset-0 transition-opacity duration-700 ease-out"
      style={{
        opacity: active ? 1 : 0,
        pointerEvents: active ? "auto" : "none",
        ...background(slide),
      }}
    >
      {slide.backgroundImage && (
        <Image
          src={slide.backgroundImage}
          alt=""
          fill
          priority={priority}
          sizes="100vw"
          className="object-cover"
          style={preset.imagePosition ? { objectPosition: preset.imagePosition } : undefined}
        />
      )}
      {slide.overlayImage && (
        <Image
          src={slide.overlayImage}
          alt=""
          fill
          priority={priority}
          sizes="100vw"
          className="object-cover object-[50%_5%]"
        />
      )}
      {preset.bottomShade && (
        <div
          aria-hidden
          className="absolute inset-x-0 bottom-0 h-[55%]"
          style={{
            background: "linear-gradient(to bottom, rgba(217,217,217,0), #313131)",
            mixBlendMode: "multiply",
          }}
        />
      )}

      {slide.backgroundImage && (
        <div
          aria-hidden
          className={`hero-scrim ${slide.layout === "right" ? "hero-scrim--light" : "hero-scrim--dark"}`}
        />
      )}

      <div className="hero-copy" style={preset.vars}>
        <h2 className={`hero-title${preset.gradientTitle ? " hero-title--gradient" : ""}`}>
          {slide.title}
        </h2>
        {slide.subtitle && (
          <p className={`hero-sub${preset.gradientTitle ? " hero-sub--gradient" : ""}`}>
            {slide.subtitle}
          </p>
        )}
        {slide.ctaLabel && slide.ctaUrl && (
          <a
            href={slide.ctaUrl}
            tabIndex={active ? 0 : -1}
            className={`hero-cta hero-cta--${slide.ctaStyle ?? "white"}`}
          >
            {slide.ctaLabel}
          </a>
        )}
      </div>
    </div>
  );
}

/**
 * Full-width hero carousel from the Figma "Homepage Carousel": cross-fading slides, copy placed by
 * layout preset, and the 5-segment progress bar centred near the bottom. Slides come from the CMS
 * (content_item type "hero_slide"). Autoplays unless the visitor prefers reduced motion; pauses
 * while hovered or focused.
 */
export function HeroCarousel({ slides }: { slides: HeroSlide[] }) {
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
      className="design-canvas relative min-h-[560px] overflow-hidden bg-[#00152d] min-[1280px]:aspect-[1920/699] min-[1280px]:min-h-0"
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
              aria-label={`Go to slide ${i + 1}: ${slide.title.replace(/\s+/g, " ")}`}
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
