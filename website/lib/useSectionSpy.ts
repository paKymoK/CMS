"use client";

import { useEffect, useState } from "react";
import { NAV_SECTIONS } from "@/content/nav";

/** Index into NAV_SECTIONS of the section currently most in view (scrollspy). */
export function useSectionSpy(): number {
  const [active, setActive] = useState(0);

  useEffect(() => {
    const targets = NAV_SECTIONS.map((s) => document.getElementById(s.id));
    const observer = new IntersectionObserver(
      (entries) => {
        const visible = entries
          .filter((e) => e.isIntersecting)
          .sort((a, b) => b.intersectionRatio - a.intersectionRatio)[0];
        if (!visible) return;
        const idx = targets.findIndex((el) => el === visible.target);
        if (idx !== -1) setActive(idx);
      },
      { threshold: [0.2, 0.4, 0.6], rootMargin: "-15% 0px -50% 0px" },
    );
    targets.forEach((el) => el && observer.observe(el));
    return () => observer.disconnect();
  }, []);

  return active;
}
