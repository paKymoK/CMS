"use client";

import { useEffect, useRef, useState } from "react";
import { Placeholder } from "@/components/ui/Placeholder";
import { Reveal } from "@/components/ui/Reveal";

export function WorkforceBand() {
  const ref = useRef<HTMLDivElement>(null);
  const [entered, setEntered] = useState(false);

  useEffect(() => {
    const el = ref.current;
    if (!el) return;
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setEntered(true);
          observer.disconnect();
        }
      },
      { threshold: 0.15 },
    );
    observer.observe(el);
    return () => observer.disconnect();
  }, []);

  return (
    <section
      id="about"
      ref={ref}
      className="relative flex min-h-[440px] items-end overflow-hidden bg-[#0b1a2b]"
    >
      <Placeholder
        tone="cool"
        label="TEAM PHOTOGRAPHY"
        className="absolute inset-0 transition-transform duration-[1200ms] ease-[cubic-bezier(.25,1,.5,1)]"
        style={{ transform: entered ? "scale(1)" : "scale(1.06)" }}
      />
      <div
        className="absolute inset-0 transition-opacity duration-[1200ms] ease-[cubic-bezier(.25,1,.5,1)]"
        style={{
          background: "linear-gradient(90deg, rgba(4,12,24,.85), rgba(4,12,24,.25))",
          opacity: entered ? 0.75 : 0.55,
        }}
        aria-hidden
      />
      <div className="relative z-[2] max-w-[760px] px-6 pt-16 pb-14">
        <Reveal travelPx={20} delayMs={200}>
          <div className="font-mono-wave mb-3 text-[10px] tracking-[0.16em] text-[#7fc0ff] uppercase">
            Your Extended Tech Workforce
          </div>
        </Reveal>
        <Reveal travelPx={20} delayMs={280}>
          <h2 className="mb-3.5 text-[clamp(26px,4vw,40px)] leading-[1.12] font-bold tracking-[-0.02em] text-white text-balance">
            Scale Your Tech Capabilities With Precision And Agility
          </h2>
        </Reveal>
        <Reveal travelPx={20} delayMs={360}>
          <p className="mb-6 max-w-[560px] text-sm leading-[1.6] text-[#c5d6e8]">
            Expand your engineering bandwidth with flexible, cross-functional
            tech teams that seamlessly align with your workflows and scale as
            your goals evolve.
          </p>
        </Reveal>
        <Reveal travelPx={20} delayMs={440}>
          <a
            href="#contact"
            className="group inline-flex items-center gap-2 border border-white/50 px-6 py-3 font-mono-wave text-xs tracking-[0.05em] text-white uppercase transition-colors duration-200 hover:bg-white/16"
          >
            Meet Our Experts
            <span className="inline-block transition-transform duration-200 group-hover:translate-x-[3px]" aria-hidden>
              →
            </span>
          </a>
        </Reveal>
      </div>
    </section>
  );
}
