"use client";

import { useEffect, useRef, useState } from "react";
import type { NavSection } from "@/content/nav";

/**
 * Sticky in-page anchor nav for the wave band, per the handoff's §1c.
 * Scrollspy via IntersectionObserver. No active-state underline: measuring
 * it off per-link DOM geometry kept drifting out of sync with the label
 * width across breakpoints, so active state is conveyed by text color alone.
 */
export function SectionNav({ sections }: { sections: NavSection[] }) {
  const navRef = useRef<HTMLDivElement>(null);
  const scrollerRef = useRef<HTMLElement>(null);
  const linkRefs = useRef<(HTMLAnchorElement | null)[]>([]);
  const waveBandRef = useRef<HTMLElement | null>(null);

  const [activeIndex, setActiveIndex] = useState(0);
  const [detached, setDetached] = useState(false);

  // Scrollspy: track which section is most visible.
  useEffect(() => {
    const targets = sections
      .map((s) => document.getElementById(s.id))
      .filter((el): el is HTMLElement => !!el);
    if (targets.length === 0) return;

    const observer = new IntersectionObserver(
      (entries) => {
        const visible = entries
          .filter((e) => e.isIntersecting)
          .sort((a, b) => b.intersectionRatio - a.intersectionRatio)[0];
        if (visible) {
          const idx = targets.findIndex((el) => el === visible.target);
          if (idx !== -1) setActiveIndex(idx);
        }
      },
      { threshold: [0.2, 0.4, 0.6], rootMargin: "-15% 0px -50% 0px" },
    );
    targets.forEach((el) => observer.observe(el));
    return () => observer.disconnect();
  }, [sections]);

  // Keep the active link in view, recomputed on active change and resize.
  useEffect(() => {
    const measure = () => {
      const el = linkRefs.current[activeIndex];
      const scroller = scrollerRef.current;
      if (!el || !scroller) return;
      const scrollerRect = scroller.getBoundingClientRect();

      // Horizontal-only scroll to keep the active link in view on mobile —
      // this one uses the full anchor (including padding), not the label,
      // since it's about keeping the whole tappable element on-screen.
      // Deliberately NOT el.scrollIntoView(): its cross-axis "nearest
      // scrollable ancestor" search can end up nudging the page's vertical
      // scroll too when the nav is `position: sticky`, since the browser
      // walks up looking for a container to satisfy the vertical axis and
      // the sticky element's position is mid-recalculation during scroll —
      // that caused a scroll-jump/rollback bug on mobile.
      const elRect = el.getBoundingClientRect();
      const elLeft = elRect.left - scrollerRect.left + scroller.scrollLeft;
      const elRight = elLeft + elRect.width;
      const viewLeft = scroller.scrollLeft;
      const viewRight = viewLeft + scroller.clientWidth;
      if (elLeft < viewLeft) {
        scroller.scrollTo({ left: elLeft, behavior: "smooth" });
      } else if (elRight > viewRight) {
        scroller.scrollTo({ left: elRight - scroller.clientWidth, behavior: "smooth" });
      }
    };
    measure();
    window.addEventListener("resize", measure);
    return () => window.removeEventListener("resize", measure);
  }, [activeIndex]);

  // Detach styling once the wave band scrolls past the nav's own height.
  useEffect(() => {
    waveBandRef.current = navRef.current?.closest("#waveBand") as HTMLElement | null;
    let raf: number | null = null;
    const onScroll = () => {
      if (raf !== null) return;
      raf = requestAnimationFrame(() => {
        raf = null;
        const band = waveBandRef.current;
        const nav = navRef.current;
        if (!band || !nav) return;
        setDetached(band.getBoundingClientRect().bottom <= nav.getBoundingClientRect().height);
      });
    };
    window.addEventListener("scroll", onScroll, { passive: true });
    onScroll();
    return () => {
      window.removeEventListener("scroll", onScroll);
      if (raf !== null) cancelAnimationFrame(raf);
    };
  }, []);

  return (
    <div
      ref={navRef}
      id="secNav"
      className="sticky top-0 z-[15] transition-[background-color,box-shadow] duration-200"
      style={{
        background: detached ? "rgba(4,11,26,.92)" : "transparent",
        backdropFilter: detached ? "blur(12px)" : undefined,
        boxShadow: detached ? "0 2px 16px rgba(2,7,20,.35)" : undefined,
      }}
    >
      <nav
        ref={scrollerRef}
        aria-label="Page sections"
        className="mx-auto max-w-[1120px] overflow-x-auto px-6 [scrollbar-width:none] [&::-webkit-scrollbar]:hidden"
      >
        {/* w-fit + mx-auto centers the link group when it fits; if it ever
            overflows (many sections on a narrow screen) the auto margins
            collapse to 0 per spec, so it falls back to normal left-to-right
            scrolling instead of clipping the first item unreachably — which
            is what plain `justify-center` on the scroll container itself
            would do. */}
        <div className="mx-auto flex w-fit gap-1">
          {sections.map((s, i) => (
            <a
              key={s.id}
              ref={(el) => {
                linkRefs.current[i] = el;
              }}
              href={`#${s.id}`}
              className={`flex-none px-4 py-[18px] text-[15px] font-semibold transition-colors ${
                i === activeIndex ? "text-white" : "text-[#c9d9ee] hover:text-white"
              }`}
            >
              {s.label}
            </a>
          ))}
        </div>
      </nav>
    </div>
  );
}
