"use client";

import Image from "next/image";
import { useEffect, useRef, useState } from "react";
import { Reveal } from "@/components/ui/Reveal";
import { Placeholder } from "@/components/ui/Placeholder";
import type { Testimonial } from "@/content/home/types";

const AUTOPLAY_MS = 7000;
const OUT_MS = 240;
const GAP_MS = 120;

export function TestimonialsCarousel({ testimonials }: { testimonials: Testimonial[] }) {
  const [index, setIndex] = useState(0);
  const [phase, setPhase] = useState<"in" | "out">("in");
  const [manual, setManual] = useState(false);
  const [paused, setPaused] = useState(false);
  const timer = useRef<ReturnType<typeof setInterval> | null>(null);

  const goTo = (next: number, isManual: boolean) => {
    if (isManual) setManual(true);
    setPhase("out");
    window.setTimeout(() => {
      setIndex((next + testimonials.length) % testimonials.length);
      setPhase("in");
    }, OUT_MS + GAP_MS);
  };

  useEffect(() => {
    if (manual || testimonials.length < 2) return;
    timer.current = setInterval(() => {
      if (paused || document.hidden) return;
      goTo(index + 1, false);
    }, AUTOPLAY_MS);
    return () => {
      if (timer.current) clearInterval(timer.current);
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [index, manual, paused, testimonials.length]);

  const active = testimonials[index];
  if (!active) return null;

  return (
    <section
      id="testimonials"
      className="bg-gradient-to-b from-[#f2f7fd] to-[#e7f0fa] py-[clamp(56px,7vw,96px)]"
    >
      <div className="mx-auto max-w-[1120px] px-6 text-center">
        <Reveal once>
          <div className="font-mono-wave text-[11px] tracking-[0.16em] text-[#5a5d64] uppercase">
            — Testimonials
          </div>
          <h2 className="mt-3.5 text-[clamp(24px,3.2vw,34px)] font-bold tracking-[-0.02em] text-[#10314f]">
            Hear More from Our <span className="text-brand-primary">Clients</span>
          </h2>
        </Reveal>

        <div
          className="mx-auto mt-12 flex max-w-[1280px] items-center px-5"
          onPointerEnter={() => setPaused(true)}
          onPointerLeave={() => setPaused(false)}
          onFocus={() => setPaused(true)}
          onBlur={() => setPaused(false)}
        >
          <FlankCard logo={active.flankLogos?.[0]} phase={phase} />

          <div
            className="relative z-[2] flex min-w-0 flex-[0_1_580px] overflow-hidden rounded-[6px] text-left shadow-[0_20px_48px_rgba(6,18,37,0.18)] transition-[opacity,transform] duration-300 ease-out"
            style={{
              opacity: phase === "out" ? 0 : 1,
              transform: phase === "out" ? "translateX(-32px)" : "translateX(0)",
            }}
          >
            <div className="flex min-h-[262px] flex-[1_1_262px] flex-col justify-between bg-[#eceeed] p-6">
              <div>
                <div className="text-[19px] font-bold text-[#0f2b1f]">{active.company}</div>
                <p className="mt-3 text-[12.5px] leading-[1.72] text-[#3d4046]">
                  &ldquo;{active.quote}&rdquo;
                </p>
              </div>
              <div
                className="relative -mx-6 -mb-6 mt-4 h-[94px] px-6 py-3"
                style={{
                  background:
                    "linear-gradient(104deg,#0f7a5f 0%,#2fa36b 42%,#7cc243 78%,#c9dd5a 100%)",
                }}
              >
                <div className="absolute bottom-3 left-6 z-[2] flex items-center gap-3">
                  {active.photo && (
                    <div className="relative h-10 w-10 shrink-0 overflow-hidden rounded-full ring-2 ring-white/50">
                      <Image
                        src={active.photo}
                        alt={active.name}
                        fill
                        className="object-cover"
                        sizes="40px"
                      />
                    </div>
                  )}
                  <div>
                    <div className="text-[13.5px] font-bold text-white">{active.name}</div>
                    <div className="text-[11.5px] text-white/88">{active.title}</div>
                  </div>
                </div>
              </div>
            </div>

            <div className="group relative hidden min-h-[262px] flex-[1_1_300px] sm:block">
              <Placeholder tone="cool" label="CLIENT VIDEO" className="absolute inset-0">
                <button
                  type="button"
                  aria-label="Play video"
                  className="absolute top-1/2 left-1/2 flex h-[52px] w-[52px] -translate-x-1/2 -translate-y-1/2 items-center justify-center rounded-full bg-white/92 text-brand-primary opacity-0 transition-opacity duration-200 group-hover:opacity-100"
                >
                  ▶
                </button>
              </Placeholder>
            </div>
          </div>

          <FlankCard logo={active.flankLogos?.[1]} phase={phase} />
        </div>

        <div className="mt-[26px] flex items-center justify-center gap-2.5">
          <button
            type="button"
            onClick={() => goTo(index - 1, true)}
            aria-label="Previous testimonial"
            className="text-[17px] text-[#3d4046] transition-colors hover:text-brand-primary"
          >
            ‹
          </button>
          {testimonials.map((t, i) => (
            <button
              key={t.name}
              type="button"
              onClick={() => goTo(i, true)}
              aria-label={`Show testimonial from ${t.name}`}
              className="h-2.5 w-2.5 rounded-full transition-colors"
              style={{ background: i === index ? "#0b63c5" : "#c2cad3" }}
            />
          ))}
          <button
            type="button"
            onClick={() => goTo(index + 1, true)}
            aria-label="Next testimonial"
            className="text-[17px] text-[#3d4046] transition-colors hover:text-brand-primary"
          >
            ›
          </button>
        </div>
      </div>
    </section>
  );
}

function FlankCard({
  logo,
  phase,
}: {
  logo?: { name: string; color: string };
  phase: "in" | "out";
}) {
  if (!logo) return <div className="hidden min-w-[156px] flex-[1_1_156px] max-w-[240px] md:block" />;
  return (
    <div
      className="hidden h-[202px] min-w-[156px] flex-[1_1_156px] max-w-[240px] items-center justify-center overflow-hidden rounded-2xl text-[clamp(15px,2.1vw,25px)] font-bold whitespace-nowrap text-white transition-opacity duration-300 md:flex"
      style={{ background: logo.color, opacity: phase === "out" ? 0 : 1, margin: "0 -8px" }}
    >
      {logo.name}
    </div>
  );
}
