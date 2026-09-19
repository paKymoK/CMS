"use client";

import { useEffect, useRef, useState, type ReactNode } from "react";

/**
 * Fade-and-slide-up-on-scroll wrapper, matching the real site's custom
 * `__reveal` / `__horizon-reveal` classes (an IntersectionObserver toggling
 * an "active" class — confirmed via computed styles on 2026-08-27; the exact
 * from-state values weren't extractable since the plugin's stylesheet is
 * blocked from cross-origin JS reads, so the transform distance/easing here
 * are a reasonable approximation, not scraped numbers).
 */
export function Reveal({
  children,
  className = "",
  delayMs = 0,
  once = false,
  travelPx = 36,
}: {
  children: ReactNode;
  className?: string;
  delayMs?: number;
  /** Fire once and stop observing, instead of replaying on every re-entry
   * (the redesigned homepage body sections want fire-once per the new
   * motion spec; existing callers keep the old repeat-both-ways default). */
  once?: boolean;
  /** translateY distance the element travels in from, in px. */
  travelPx?: number;
}) {
  const ref = useRef<HTMLDivElement>(null);
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    const el = ref.current;
    if (!el) return;
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setVisible(true);
          if (once) observer.disconnect();
        } else if (!once) {
          setVisible(false);
        }
      },
      { threshold: 0.15, rootMargin: "0px 0px -60px 0px" },
    );
    observer.observe(el);
    return () => observer.disconnect();
  }, [once]);

  return (
    <div
      ref={ref}
      className={`reveal ${visible ? "is-visible" : ""} ${className}`}
      style={{
        ...(delayMs ? { transitionDelay: `${delayMs}ms` } : undefined),
        ...(travelPx !== 36
          ? ({ "--reveal-travel": `${travelPx}px` } as React.CSSProperties)
          : undefined),
      }}
    >
      {children}
    </div>
  );
}
