"use client";

import { useEffect, useState } from "react";

function getInitial(): boolean {
  if (typeof window === "undefined") return false;
  return window.matchMedia("(prefers-reduced-motion: reduce)").matches;
}

/**
 * Tracks `prefers-reduced-motion: reduce`. The CSS `@media` rule in
 * globals.css handles pure-CSS keyframes; this hook is for JS-driven motion
 * (Vanta init, canvas render loops, drag-carousel transitions, counters)
 * that need to branch behavior at runtime instead.
 */
export function usePrefersReducedMotion(): boolean {
  const [reduced, setReduced] = useState(getInitial);

  useEffect(() => {
    const mql = window.matchMedia("(prefers-reduced-motion: reduce)");
    const onChange = (e: MediaQueryListEvent) => setReduced(e.matches);
    mql.addEventListener("change", onChange);
    return () => mql.removeEventListener("change", onChange);
  }, []);

  return reduced;
}
