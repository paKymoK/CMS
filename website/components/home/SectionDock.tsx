"use client";

import { NAV_SECTIONS } from "@/content/nav";

/**
 * The section menu (Overview, What We Do, …) docked flush to the bottom of the screen with no
 * background of its own: white text under `mix-blend-difference` inverts against whatever is behind it,
 * so it stays readable over both light and dark sections. It appears once the
 * in-card menu in the assistant band has scrolled out of view, so the page's sections stay one tap
 * away all the way down. Sits above the mobile bottom bar on phones.
 */
export function SectionDock({ active, show }: { active: number; show: boolean }) {
  return (
    <nav
      aria-label="Page sections"
      aria-hidden={!show}
      className={`fixed left-1/2 z-[90] max-w-[100vw] -translate-x-1/2 overflow-x-auto px-6 py-2 mix-blend-difference transition-[opacity,transform] duration-300 [scrollbar-width:none] bottom-[calc(64px+env(safe-area-inset-bottom))] md:bottom-1 [&::-webkit-scrollbar]:hidden ${
        show ? "translate-y-0 opacity-100" : "pointer-events-none translate-y-6 opacity-0"
      }`}
    >
      <ul className="flex w-max items-center gap-8">
        {NAV_SECTIONS.map((s, i) => (
          <li key={s.id}>
            <a
              href={`#${s.id}`}
              tabIndex={show ? 0 : -1}
              aria-current={i === active ? "true" : undefined}
              className={`relative block text-[18px] leading-[1.31] whitespace-nowrap text-white transition-opacity ${
                i === active ? "font-bold" : "font-medium opacity-70 hover:opacity-100"
              }`}
            >
              {s.label}
              {i === active && (
                <span
                  aria-hidden
                  className="absolute top-full left-1/2 mt-0.5 h-0.5 w-[38px] -translate-x-1/2 rounded-sm bg-white"
                />
              )}
            </a>
          </li>
        ))}
      </ul>
    </nav>
  );
}
