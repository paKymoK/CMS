"use client";

import { NAV_SECTIONS } from "@/content/nav";

/**
 * The section menu (Overview, What We Do, …) docked to the bottom of the screen. It appears once the
 * in-card menu in the assistant band has scrolled out of view, so the page's sections stay one tap
 * away all the way down. Sits above the mobile bottom bar on phones.
 */
export function SectionDock({ active, show }: { active: number; show: boolean }) {
  return (
    <nav
      aria-label="Page sections"
      aria-hidden={!show}
      className={`fixed left-1/2 z-[90] max-w-[calc(100vw-24px)] -translate-x-1/2 overflow-x-auto rounded-full border border-white/15 bg-[#040b1a]/85 px-6 py-3 shadow-[0_12px_32px_rgba(2,7,20,0.45)] backdrop-blur-xl transition-[opacity,transform] duration-300 [scrollbar-width:none] bottom-[calc(64px+env(safe-area-inset-bottom)+12px)] md:bottom-5 [&::-webkit-scrollbar]:hidden ${
        show ? "translate-y-0 opacity-100" : "pointer-events-none translate-y-6 opacity-0"
      }`}
    >
      <ul className="flex w-max items-center gap-6">
        {NAV_SECTIONS.map((s, i) => (
          <li key={s.id}>
            <a
              href={`#${s.id}`}
              tabIndex={show ? 0 : -1}
              aria-current={i === active ? "true" : undefined}
              className={`relative block text-[15px] leading-[1.31] font-medium whitespace-nowrap transition-colors hover:text-[#00b2ff] ${
                i === active ? "text-[#00b2ff]" : "text-white"
              }`}
            >
              {s.label}
              {i === active && (
                <span
                  aria-hidden
                  className="absolute top-full left-1/2 mt-0.5 h-0.5 w-[38px] -translate-x-1/2 rounded-sm bg-[#00b2ff]"
                />
              )}
            </a>
          </li>
        ))}
      </ul>
    </nav>
  );
}
