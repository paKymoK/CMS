"use client";

import { useEffect, useRef, useState } from "react";
import { Globe } from "@/components/home/Globe";
import { GlobeRings } from "@/components/home/GlobeRings";
import type { Office, SectionIntro, Stat } from "@/content/home/types";

const COUNT_UP_MS = 1400;
const STAGGER_MS = 120;

const FALLBACK_INTRO: SectionIntro = {
  section: "global",
  eyebrow: "Global Delivery",
  title:
    "Seamless global presence. Non-stop innovation.\nWe accelerate the digital evolution across every time zone",
};

/** Where each stat sits on the 1920px artboard (CMS order: years, countries, clients, partners, employees). */
const STAT_POSITIONS: { left: number; top: number; align?: "center" }[] = [
  { left: 401, top: 478 },
  { left: 1402, top: 478 },
  { left: 515, top: 666 },
  { left: 1250, top: 666 },
  { left: 830, top: 854, align: "center" },
];

function parseStat(value: string): { target: number; suffix: string } {
  const match = value.match(/^(\d+)(.*)$/);
  if (!match) return { target: 0, suffix: value };
  return { target: Number(match[1]), suffix: match[2] };
}

// Exponential ease-out count-up, plus a progress value so the label can fade in at 60%.
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

function StatBlock({
  stat,
  active,
  delayMs,
  position,
}: {
  stat: Stat;
  active: boolean;
  delayMs: number;
  position: { left: number; top: number; align?: "center" };
}) {
  const { target, suffix } = parseStat(stat.value);
  const { count, progress } = useCountUp(target, active, delayMs);

  return (
    <div
      className="gdl-stat"
      data-align={position.align}
      style={
        {
          "--sl": `calc(${position.left} * var(--u))`,
          "--st": `calc(${position.top} * var(--u))`,
        } as React.CSSProperties
      }
    >
      <div className="gdl-stat-value">
        {count}
        {suffix}
      </div>
      <div
        className="gdl-stat-label transition-opacity duration-200"
        style={{ opacity: progress >= 0.6 ? 1 : 0 }}
      >
        {stat.label}
      </div>
    </div>
  );
}

/**
 * "Global Delivery" (Figma "Global Recognition"): gradient heading, the interactive globe with its
 * orbit rings and the five headline figures placed around it; hovering or tapping a dot opens that
 * office's card.
 * Below 1280px the figures drop into a grid under the globe.
 */
export function GlobalDelivery({
  stats,
  offices,
  intro = FALLBACK_INTRO,
}: {
  stats: Stat[];
  offices: Office[];
  intro?: SectionIntro;
}) {
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
      { threshold: 0.25 },
    );
    observer.observe(el);
    return () => observer.disconnect();
  }, []);

  return (
    <section id="global" aria-labelledby="global-title" className="design-canvas">
      <div ref={ref} className="gdl sec">
        <div className="sec-eyebrow-wrap">
          <div className="cmc-eyebrow">{intro.eyebrow ?? "Global Delivery"}</div>
        </div>
        <h2 id="global-title" className="sec-title gdl-title">
          {intro.title}
        </h2>

        <GlobeRings className="gdl-rings gdl-rings--a" />
        <svg
          viewBox="0 0 599 599"
          className="gdl-rings gdl-rings--b"
          aria-hidden
          focusable="false"
          fill="none"
        >
          <circle
            cx="299.5"
            cy="299.5"
            r="290"
            stroke="#cfe6fb"
            strokeWidth="2"
            strokeDasharray="4 7"
          />
        </svg>

        <div className="gdl-globe-wrap">
          <Globe offices={offices} width="calc(524 * var(--u) + 52px)" />
        </div>

        <div className="gdl-stats">
          {stats.slice(0, 5).map((stat, i) => (
            <StatBlock
              key={stat.label}
              stat={stat}
              active={active}
              delayMs={i * STAGGER_MS}
              position={STAT_POSITIONS[i] ?? STAT_POSITIONS[0]}
            />
          ))}
        </div>

        <p className="gdl-hint">DRAG THE GLOBE · HOVER A DOT TO HOLD ROTATION</p>
      </div>
    </section>
  );
}
