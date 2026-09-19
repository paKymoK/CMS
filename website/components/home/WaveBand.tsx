"use client";

import dynamic from "next/dynamic";
import { useEffect, useState } from "react";
import { NAV_SECTIONS } from "@/content/nav";
import { AssistantPanel } from "./AssistantPanel";
import { SectionNav } from "./SectionNav";

// Vanta/three touch `window` at module scope — must never be imported
// during a server render.
const HeroCanvas = dynamic(() => import("./HeroCanvas").then((m) => m.HeroCanvas), {
  ssr: false,
});

// Fires on load, gated behind the hero canvas's first frame so the type
// never lands on an empty box. Per the handoff's explicit warning: the
// animated opacity/transform must have exactly one writer — this component
// never authors opacity:0 in JSX, it writes the whole style from state set
// once after mount, so a re-render can never fight itself.
function HeroContent() {
  const [entered, setEntered] = useState(false);

  useEffect(() => {
    const id = requestAnimationFrame(() => setEntered(true));
    return () => cancelAnimationFrame(id);
  }, []);

  const line = (delayMs: number, extra?: React.CSSProperties): React.CSSProperties => ({
    opacity: entered ? 1 : 0,
    transform: entered ? "none" : "translateY(16px)",
    transition: `opacity 420ms cubic-bezier(.22,1,.36,1) ${delayMs}ms, transform 420ms cubic-bezier(.22,1,.36,1) ${delayMs}ms`,
    ...extra,
  });

  return (
    <section
      id="overview"
      className="relative z-[2] flex min-h-[520px] items-center justify-center px-6 pt-[86px] pb-24 text-center"
    >
      <div className="max-w-[720px]">
        <h1
          className="text-[clamp(30px,4.4vw,50px)] leading-[1.16] font-medium tracking-[-0.01em] text-white"
        >
          <span style={line(0)}>
            Riding The <span className="text-[#43a4ff]">Next Wave</span>
          </span>
          <br />
          <span style={line(70)}>Of AI Transformation</span>
        </h1>
        <p style={line(140)} className="mt-4 mb-[30px] text-[13.5px] font-bold text-white">
          Reinvent Your Base - Scale Without Limits - Engineer Your Tomorrow
        </p>
        <a
          href="#contact"
          style={{
            opacity: entered ? 1 : 0,
            transform: entered ? "scale(1)" : "scale(.98)",
            transition: "opacity 320ms cubic-bezier(.22,1,.36,1) 240ms, transform 320ms cubic-bezier(.22,1,.36,1) 240ms",
          }}
          className="inline-flex items-center rounded-full bg-white px-[30px] py-3 text-[13px] font-medium text-[#0d2b52] transition-colors hover:bg-[#e8f1fb]"
        >
          Contact Us
        </a>
      </div>
    </section>
  );
}

export function WaveBand() {
  return (
    <div id="waveBand" className="relative overflow-x-clip" style={{ background: "#03091a" }}>
      <div className="absolute inset-0 z-0">
        <HeroCanvas />
      </div>
      <HeroContent />
      <AssistantPanel />
      <SectionNav sections={NAV_SECTIONS} />
    </div>
  );
}
