"use client";

import { useEffect, useState } from "react";

export type Tone = "light" | "dark";

const RGB = /rgba?\(\s*(\d+)[\s,]+(\d+)[\s,]+(\d+)(?:[\s,/]+([\d.]+%?))?\s*\)/g;

const luminance = (r: number, g: number, b: number) => (0.2126 * r + 0.7152 * g + 0.0722 * b) / 255;

/** Average luminance of the opaque-enough rgb()/rgba() colours in a CSS colour or gradient string. */
function paintLuminance(css: string): number | null {
  const values: number[] = [];
  for (const m of css.matchAll(RGB)) {
    const raw = m[4];
    const alpha = raw === undefined ? 1 : raw.endsWith("%") ? parseFloat(raw) / 100 : parseFloat(raw);
    if (alpha >= 0.5) values.push(luminance(+m[1], +m[2], +m[3]));
  }
  return values.length ? values.reduce((a, b) => a + b, 0) / values.length : null;
}

/** Tone of whatever is painted behind the point (x, y), ignoring `skip` and its children. */
function toneAt(x: number, y: number, skip: Element | null): Tone {
  for (const el of document.elementsFromPoint(x, y)) {
    if (skip?.contains(el)) continue;
    if (el instanceof HTMLImageElement || el instanceof HTMLVideoElement) return "dark";
    const style = getComputedStyle(el);
    const fromColor = paintLuminance(style.backgroundColor);
    if (fromColor !== null) return fromColor < 0.5 ? "dark" : "light";
    if (style.backgroundImage !== "none") {
      const fromImage = paintLuminance(style.backgroundImage);
      if (fromImage !== null) return fromImage < 0.5 ? "dark" : "light";
      if (style.backgroundImage.includes("url(")) return "dark";
    }
  }
  return "light";
}

/**
 * Whether the page behind a bottom-docked element is light or dark, re-checked while scrolling, so
 * its text can switch between white and navy. `nav` is excluded from the lookup.
 */
export function useBackdropTone(nav: React.RefObject<Element | null>, active: boolean): Tone {
  const [tone, setTone] = useState<Tone>("dark");

  useEffect(() => {
    if (!active) return;
    let frame = 0;
    const measure = () => {
      frame = 0;
      const el = nav.current;
      if (!el) return;
      const rect = el.getBoundingClientRect();
      const next = toneAt(window.innerWidth / 2, Math.min(window.innerHeight - 1, rect.top + rect.height / 2), el);
      setTone((prev) => (prev === next ? prev : next));
    };
    const schedule = () => {
      if (!frame) frame = requestAnimationFrame(measure);
    };
    measure();
    window.addEventListener("scroll", schedule, { passive: true });
    window.addEventListener("resize", schedule);
    return () => {
      window.removeEventListener("scroll", schedule);
      window.removeEventListener("resize", schedule);
      if (frame) cancelAnimationFrame(frame);
    };
  }, [nav, active]);

  return tone;
}
