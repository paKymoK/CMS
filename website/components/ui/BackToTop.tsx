"use client";

import { useEffect, useState } from "react";

/** Fixed back-to-top button, hidden until scrollY > 600, per the handoff. */
export function BackToTop() {
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    const onScroll = () => setVisible(window.scrollY > 600);
    onScroll();
    window.addEventListener("scroll", onScroll, { passive: true });
    return () => window.removeEventListener("scroll", onScroll);
  }, []);

  return (
    <button
      type="button"
      onClick={() => window.scrollTo({ top: 0, behavior: "smooth" })}
      aria-label="Back to top"
      className={`fixed right-[18px] bottom-[18px] z-30 flex h-10 w-10 items-center justify-center rounded-full bg-brand-primary text-white shadow-[0_10px_24px_rgba(24,159,224,0.36)] transition-opacity duration-200 hover:bg-brand-primary-dark ${
        visible ? "opacity-100" : "pointer-events-none opacity-0"
      }`}
    >
      <svg viewBox="0 0 24 24" className="h-4 w-4 fill-none stroke-current stroke-2" aria-hidden>
        <path d="M12 19V5M5 12l7-7 7 7" strokeLinecap="round" strokeLinejoin="round" />
      </svg>
    </button>
  );
}
