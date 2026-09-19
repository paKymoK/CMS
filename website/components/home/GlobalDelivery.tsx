"use client";

import { useEffect, useRef, useState } from "react";
import type { Stat, Office } from "@/content/home/types";
import { Reveal } from "@/components/ui/Reveal";
import { Globe } from "@/components/home/Globe";

const COUNT_UP_MS = 1400;
const STAGGER_MS = 120;

function parseStat(value: string): { target: number; suffix: string } {
  const match = value.match(/^(\d+)(.*)$/);
  if (!match) return { target: 0, suffix: value };
  return { target: Number(match[1]), suffix: match[2] };
}

// Exponential ease-out count-up (matches StatsBand.tsx's pattern), plus a
// progress value so the label can fade in at 60% per the motion spec.
function useCountUp(target: number, active: boolean, delayMs: number) {
  const [count, setCount] = useState(0);
  const [progress, setProgress] = useState(0);

  useEffect(() => {
    if (!active) return;
    let frame: number;
    const startTimer = setTimeout(() => {
      const start = performance.now();
      const tick = (now: number) => {
        const p = Math.min((now - start) / COUNT_UP_MS, 1);
        const eased = p < 1 ? 1 - Math.pow(2, -10 * p) : 1;
        setCount(Math.round(target * eased));
        setProgress(p);
        if (p < 1) frame = requestAnimationFrame(tick);
      };
      frame = requestAnimationFrame(tick);
    }, delayMs);

    return () => {
      clearTimeout(startTimer);
      cancelAnimationFrame(frame);
    };
  }, [active, target, delayMs]);

  return { count, progress };
}

function StatNumber({
  stat,
  active,
  delayMs,
  align,
  size = "normal",
}: {
  stat: Stat;
  active: boolean;
  delayMs: number;
  align: "left" | "right" | "center";
  size?: "normal" | "large";
}) {
  const { target, suffix } = parseStat(stat.value);
  const { count, progress } = useCountUp(target, active, delayMs);
  const labelVisible = progress >= 0.6;

  return (
    <div className={align === "right" ? "text-right" : align === "center" ? "text-center" : "text-left"}>
      <div
        className={`font-bold text-[#123a63] tabular-nums ${
          size === "large" ? "text-[clamp(32px,5vw,48px)]" : "text-[clamp(28px,4vw,40px)]"
        } leading-none tracking-[-0.02em]`}
      >
        {count}
        <span>{suffix}</span>
      </div>
      <div
        className="mt-0.5 text-[13px] text-[#6a7c90] transition-opacity duration-200"
        style={{ opacity: labelVisible ? 1 : 0 }}
      >
        {stat.label}
      </div>
    </div>
  );
}

export function GlobalDelivery({ stats, offices }: { stats: Stat[]; offices: Office[] }) {
  const ref = useRef<HTMLDivElement>(null);
  const [active, setActive] = useState(false);

  useEffect(() => {
    const el = ref.current;
    if (!el) return;
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setActive(true);
          observer.disconnect();
        }
      },
      { threshold: 0.4 },
    );
    observer.observe(el);
    return () => observer.disconnect();
  }, []);

  const [years, countries, clients, partners, employees] = stats;

  return (
    <section id="global" ref={ref} className="px-6 py-[clamp(56px,7vw,96px)]">
      <div className="mx-auto max-w-[1120px]">
        <Reveal once travelPx={16} className="mx-auto mb-11 max-w-[680px] text-center">
          <div className="font-mono-wave mb-3.5 text-[11px] tracking-[0.16em] text-[#5a5d64] uppercase">
            — Global Delivery
          </div>
          <h2 className="text-[clamp(21px,2.7vw,29px)] leading-[1.34] font-bold tracking-[-0.015em] text-[#10314f]">
            Seamless global presence. <span className="text-brand-primary">Non-stop innovation.</span>
            <br />
            We accelerate the digital <span className="text-brand-primary">evolution across every time zone</span>
          </h2>
        </Reveal>

        <div className="flex flex-wrap items-center justify-center gap-[clamp(18px,3.4vw,52px)]">
          <div className="flex flex-1 min-w-[118px] flex-col items-end gap-[58px] pb-[18px]">
            {years && (
              <div className="mr-[clamp(0px,2.6vw,34px)]">
                <StatNumber stat={years} active={active} delayMs={0} align="right" />
              </div>
            )}
            {clients && <StatNumber stat={clients} active={active} delayMs={2 * STAGGER_MS} align="right" />}
          </div>

          <div className="flex flex-none flex-col items-center">
            <Globe offices={offices} />
            {employees && (
              <div className="mt-[30px]">
                <StatNumber stat={employees} active={active} delayMs={4 * STAGGER_MS} align="center" size="large" />
              </div>
            )}
          </div>

          <div className="flex flex-1 min-w-[118px] flex-col items-start gap-[58px] pb-[18px]">
            {countries && (
              <div className="ml-[clamp(0px,2.6vw,34px)]">
                <StatNumber stat={countries} active={active} delayMs={STAGGER_MS} align="left" />
              </div>
            )}
            {partners && <StatNumber stat={partners} active={active} delayMs={3 * STAGGER_MS} align="left" />}
          </div>
        </div>

        <p className="font-mono-wave mt-[30px] text-center text-[10px] text-[#6a6d74]">
          DRAG THE GLOBE · HOVER A DOT TO HOLD ROTATION
        </p>
      </div>
    </section>
  );
}
