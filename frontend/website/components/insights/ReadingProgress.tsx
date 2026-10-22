"use client";

import { useEffect, useRef, useState } from "react";

/**
 * Thin fixed progress bar tracking scroll position through the <article> below it.
 * Mirrors SiteHeader's own rAF-throttled scroll-listener pattern.
 */
export function ReadingProgress({ targetId }: { targetId: string }) {
  const [progress, setProgress] = useState(0);
  const raf = useRef<number | null>(null);

  useEffect(() => {
    const onScroll = () => {
      if (raf.current !== null) return;
      raf.current = requestAnimationFrame(() => {
        raf.current = null;
        const el = document.getElementById(targetId);
        if (!el) return;
        const rect = el.getBoundingClientRect();
        const total = Math.max(1, rect.height - window.innerHeight * 0.6);
        const value = Math.min(1, Math.max(0, (window.innerHeight * 0.2 - rect.top) / total));
        setProgress(value);
      });
    };
    window.addEventListener("scroll", onScroll, { passive: true });
    onScroll();
    return () => {
      window.removeEventListener("scroll", onScroll);
      if (raf.current !== null) cancelAnimationFrame(raf.current);
    };
  }, [targetId]);

  return (
    <div className="fixed inset-x-0 top-0 z-[120] h-[3px] bg-brand-primary/10">
      <div
        className="h-full bg-gradient-to-r from-brand-primary to-brand-accent transition-[width] duration-100 ease-linear"
        style={{ width: `${progress * 100}%` }}
      />
    </div>
  );
}
