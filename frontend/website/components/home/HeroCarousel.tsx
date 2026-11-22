"use client";

import { getImageProps } from "next/image";
import { useCallback, useEffect, useRef, useState } from "react";
import { flushSync } from "react-dom";
import type { PixelEngine } from "@/lib/pixel-slider/engine";
import type { EffectKey } from "@/lib/pixel-slider/effects";
import { resolveTransition } from "@/lib/pixel-slider/transitions";
import type { HeroSlide } from "@/content/home/types";
import { usePrefersReducedMotion } from "@/lib/usePrefersReducedMotion";

const AUTOPLAY_MS = 6000;

// The pixel transition loads each slide's image for the current screen class as a texture, through
// Next's image optimizer: same origin (so the engine may read the pixels, which the gateway's
// images would only allow with CORS headers) and sized to the screen instead of the original.
const TEXTURE_WIDTH = { desktop: 1920, tablet: 1200, phone: 750 } as const;
type Tier = keyof typeof TEXTURE_WIDTH;
const textureUrl = (slide: HeroSlide, tier: Tier) => {
  const src = tier === "desktop" ? slide.image : tier === "tablet" ? slide.tabletImage : slide.mobileImage;
  return `/_next/image?url=${encodeURIComponent(src)}&w=${TEXTURE_WIDTH[tier]}&q=75`; // 75 is the only quality next.config allows
};

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
  fade,
}: {
  slide: HeroSlide;
  active: boolean;
  priority: boolean;
  fade: boolean;
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
      className={`absolute inset-0 ${fade ? "transition-opacity duration-700 ease-out" : ""}`}
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
 * Full-width hero carousel: slides of finished artwork that rebuild each other out of their pixels (WebGPU/WebGL 2; they crossfade where that isn't available or with reduced motion) and the 5-segment progress bar
 * near the bottom. Slides come from the CMS (content_item type "hero_slide"); a slide without all
 * three images (one saved before slides became artwork) is skipped. Autoplays unless the visitor
 * prefers reduced motion; pauses while hovered or focused. Starts below the 58px site header
 * (which is absolutely positioned) so the artwork is not hidden under it.
 */
export function HeroCarousel({ slides: all }: { slides: HeroSlide[] }) {
  const slides = all.filter((s) => s.image && s.tabletImage && s.mobileImage);
  const reduced = usePrefersReducedMotion();
  const rootRef = useRef<HTMLElement>(null);
  const engineRef = useRef<PixelEngine | null>(null);
  const busyRef = useRef(false);
  const indexRef = useRef(0);
  const [index, setIndex] = useState(0);
  const [tier, setTier] = useState<Tier | null>(null);
  const [fade, setFade] = useState(true); // slides crossfade (the fallback), unless the canvas does the work
  const pausedRef = useRef(false);
  const count = slides.length;
  const textureKey = tier ? slides.map((s) => textureUrl(s, tier)).join("\n") : "";
  // How each slide comes in (null = a plain crossfade), and the distinct effects to build
  const transitionKey = slides.map((s) => s.transition ?? "").join("|");
  const effectKey = [
    ...new Set(
      transitionKey
        .split("|")
        .map((v) => resolveTransition(v))
        .filter((k): k is EffectKey => k !== null)
    ),
  ].join(",");

  // Which of the three image sets the screen shows (the same breakpoints as the <picture>)
  useEffect(() => {
    const wide = window.matchMedia("(min-width: 1280px)");
    const mid = window.matchMedia("(min-width: 768px)");
    const update = () => setTier(wide.matches ? "desktop" : mid.matches ? "tablet" : "phone");
    update();
    wide.addEventListener("change", update);
    mid.addEventListener("change", update);
    return () => {
      wide.removeEventListener("change", update);
      mid.removeEventListener("change", update);
    };
  }, []);

  // The WebGL transition, loaded on the client only. Until it's ready (or if it can't start, or
  // with reduced motion) the slides crossfade.
  useEffect(() => {
    const root = rootRef.current;
    if (!root || reduced || !textureKey || !effectKey || count < 2) return;
    let cancelled = false;
    let engine: PixelEngine | null = null;
    import("@/lib/pixel-slider/engine")
      .then(({ PixelEngine }) => {
        if (cancelled) return;
        engine = new PixelEngine(root, textureKey.split("\n"), effectKey.split(",") as EffectKey[], () => {
          engineRef.current = null;
        });
        return engine.init().then(() => {
          if (!cancelled) engineRef.current = engine;
        });
      })
      .catch((error) => console.warn("WebGL unavailable, slides will crossfade.", error));
    return () => {
      cancelled = true;
      engineRef.current = null;
      engine?.dispose();
    };
  }, [textureKey, effectKey, reduced, count]);

  // Moves to a slide: the pixel transition when it can draw, a crossfade otherwise. One at a time.
  const move = useCallback(
    (target: number, direction: 1 | -1) => {
      if (busyRef.current || target === indexRef.current) return;
      busyRef.current = true;
      const from = indexRef.current;
      const engine = engineRef.current;
      // Going next, the slide coming in decides; going back, the one being left (it rewinds)
      const key = resolveTransition(transitionKey.split("|")[direction === 1 ? target : from]);
      const commit = () => {
        indexRef.current = target;
        setIndex(target);
      };

      if (!engine || !engine.isReady || !key || !engine.has(key)) {
        setFade(true);
        commit();
        window.setTimeout(() => (busyRef.current = false), 700);
        return;
      }

      setFade(false);
      engine.run(key, from, target, direction).then(() => {
        // The last frame equals the target slide: swap the DOM underneath, then drop the canvas
        flushSync(commit);
        requestAnimationFrame(() => {
          engine.hide();
          busyRef.current = false;
        });
      });
    },
    [transitionKey]
  );

  const go = useCallback(
    (i: number) => {
      const target = ((i % count) + count) % count;
      move(target, target > indexRef.current ? 1 : -1);
    },
    [count, move]
  );

  useEffect(() => {
    if (reduced || count < 2) return;
    const id = setInterval(() => {
      if (!pausedRef.current) move((indexRef.current + 1) % count, 1);
    }, AUTOPLAY_MS);
    return () => clearInterval(id);
  }, [reduced, count, move]);

  if (count === 0) return null;

  return (
    <section
      ref={rootRef}
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
        <Slide key={i} slide={slide} active={i === index} priority={i === 0} fade={fade} />
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
