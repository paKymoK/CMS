"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import { flushSync } from "react-dom";
import { usePrefersReducedMotion } from "@/lib/usePrefersReducedMotion";
import type { PixelEngine } from "@/lib/pixel-slider/engine";
import type { EffectKey } from "@/lib/pixel-slider/effects";

export interface PixelSliderImage {
  src: string;
  alt: string;
}

interface PixelSliderProps {
  images: PixelSliderImage[];
  className?: string;
  /** The transition between slides. */
  transition?: EffectKey;
  /** Hide the previous/next buttons (arrow keys and swipes still work). */
  hideControls?: boolean;
}

const SWIPE_PX = 50;

/**
 * Image slider whose transition rebuilds the next image out of the current one's pixels, each
 * flying to a place of the same brightness (WebGPU, or WebGL 2). Size it with `className`: the
 * slider fills its box and frames each image like `object-fit: cover`. Where WebGL is unavailable,
 * the browser renders in software, the images can't be read (no CORS headers on a cross-origin
 * host) or the visitor prefers reduced motion, slides crossfade instead.
 */
export function PixelSlider({ images, className = "", transition = "rearrange", hideControls = false }: PixelSliderProps) {
  const rootRef = useRef<HTMLElement>(null);
  const engineRef = useRef<PixelEngine | null>(null);
  const busyRef = useRef(false);
  const swipeX = useRef<number | null>(null);
  const reduced = usePrefersReducedMotion();
  const [index, setIndex] = useState(0);
  const [fade, setFade] = useState(false); // crossfade slides (only while the fallback runs)
  const count = images.length;
  const key = images.map((i) => i.src).join("\n");

  // The WebGL engine loads on the client only, and only when there is something to move between
  useEffect(() => {
    const root = rootRef.current;
    if (!root || count < 2) return;
    let cancelled = false;
    let engine: PixelEngine | null = null;
    import("@/lib/pixel-slider/engine")
      .then(({ PixelEngine }) => {
        if (cancelled) return;
        engine = new PixelEngine(root, key.split("\n"), [transition], () => (engineRef.current = null));
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
  }, [key, count, transition]);

  const go = useCallback(
    (direction: 1 | -1) => {
      if (busyRef.current || count < 2) return;
      busyRef.current = true;
      const target = (index + direction + count) % count;
      const engine = engineRef.current;

      if (!engine || !engine.isReady || reduced) {
        setFade(true);
        setIndex(target);
        window.setTimeout(() => (busyRef.current = false), 500);
        return;
      }

      setFade(false);
      engine.run(transition, index, target, direction).then(() => {
        // The last frame equals the target slide: swap the DOM underneath, then drop the canvas
        flushSync(() => setIndex(target));
        requestAnimationFrame(() => {
          engine.hide();
          busyRef.current = false;
        });
      });
    },
    [index, count, reduced, transition]
  );

  return (
    <section
      ref={rootRef}
      aria-roledescription="carousel"
      aria-label="Image slider"
      tabIndex={0}
      className={`relative overflow-hidden bg-black ${className}`}
      onKeyDown={(e) => {
        if (e.key === "ArrowLeft" || e.key === "ArrowUp") go(-1);
        if (e.key === "ArrowRight" || e.key === "ArrowDown") go(1);
      }}
      onPointerDown={(e) => (swipeX.current = e.clientX)}
      onPointerUp={(e) => {
        if (swipeX.current === null) return;
        const dx = e.clientX - swipeX.current;
        swipeX.current = null;
        if (Math.abs(dx) > SWIPE_PX) go(dx < 0 ? 1 : -1);
      }}
      onPointerCancel={() => (swipeX.current = null)}
    >
      {images.map((image, i) => (
        // eslint-disable-next-line @next/next/no-img-element -- the WebGL engine loads the same URL as a texture
        <img
          key={image.src}
          src={image.src}
          alt={image.alt}
          aria-hidden={i !== index}
          draggable={false}
          className="absolute inset-0 h-full w-full object-cover"
          style={{
            opacity: i === index ? 1 : 0,
            transition: fade ? "opacity 500ms ease-in-out" : "none",
          }}
        />
      ))}

      {!hideControls && count > 1 && (
        <div className="absolute bottom-5 right-5 z-[2] flex gap-2">
          {([-1, 1] as const).map((direction) => (
            <button
              key={direction}
              type="button"
              aria-label={direction === 1 ? "Next slide" : "Previous slide"}
              onClick={() => go(direction)}
              className="grid h-12 w-12 cursor-pointer place-items-center rounded-full bg-white text-black hover:bg-neutral-200 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-red-500"
            >
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden>
                <path d={direction === 1 ? "M7 3l7 7-7 7" : "M13 3l-7 7 7 7"} />
              </svg>
            </button>
          ))}
        </div>
      )}
    </section>
  );
}
