"use client";

import Image from "next/image";
import { Reveal } from "@/components/ui/Reveal";
import { Placeholder } from "@/components/ui/Placeholder";
import { useDragCarousel } from "@/lib/useDragCarousel";
import type { InsightItem } from "@/content/home/types";

const TONES = ["navy", "cool", "warm", "dark"] as const;

export function Insights({ insights }: { insights: InsightItem[] }) {
  const { trackRef, index, goTo, translateX, isDragging, atStart, atEnd, dragHandlers } =
    useDragCarousel(insights.length);

  const active = insights[index];

  return (
    <section id="insights" className="pb-[clamp(56px,7vw,96px)]">
      <div className="mx-auto max-w-[1120px] px-6">
        <div className="overflow-hidden">
          <div
            ref={trackRef}
            className="relative flex touch-pan-y gap-[18px] select-none"
            style={{
              transform: `translateX(${translateX}px)`,
              transition: isDragging ? "none" : "transform 520ms cubic-bezier(.25,1,.5,1)",
            }}
            {...dragHandlers}
          >
            {insights.map((item, i) => (
              <Reveal key={item.title} delayMs={i * 70} className="flex-none">
                <div
                  className={`relative h-[300px] cursor-grab overflow-hidden active:cursor-grabbing ${
                    i === 0 ? "w-[62vw] max-w-[680px] min-w-[280px]" : "w-[29vw] max-w-[320px] min-w-[180px]"
                  }`}
                >
                  {item.image ? (
                    <Image
                      src={item.image}
                      alt={item.title}
                      fill
                      className="object-cover"
                      sizes={i === 0 ? "62vw" : "29vw"}
                    />
                  ) : (
                    <Placeholder
                      tone={TONES[i % TONES.length]}
                      label="EVENT PHOTOGRAPHY"
                      className="h-full w-full"
                    />
                  )}
                </div>
              </Reveal>
            ))}
          </div>
        </div>

        <div className="mt-5 flex flex-wrap items-center gap-5">
          <h3 key={index} className="min-w-[320px] flex-1 text-[clamp(15px,2vw,19px)] font-bold text-[#10141c]">
            {active?.title}
          </h3>
          <a
            href="#"
            className="font-mono-wave rounded-[2px] border border-brand-primary/40 px-4 py-2.5 text-[11px] tracking-[0.08em] text-brand-primary uppercase transition-colors hover:bg-brand-primary hover:text-white"
          >
            Read This Article
          </a>
          <div className="flex items-center gap-2.5">
            <button
              type="button"
              onClick={() => goTo(index - 1)}
              disabled={atStart}
              aria-label="Previous insight"
              className="flex h-8 w-8 items-center justify-center rounded-full border border-[#cfd2d6] text-[13px] text-[#3d4046] transition-[opacity,border-color,color] hover:border-brand-primary hover:text-brand-primary disabled:opacity-30"
            >
              ‹
            </button>
            <button
              type="button"
              onClick={() => goTo(index + 1)}
              disabled={atEnd}
              aria-label="Next insight"
              className="flex h-8 w-8 items-center justify-center rounded-full border border-[#cfd2d6] text-[13px] text-[#3d4046] transition-[opacity,border-color,color] hover:border-brand-primary hover:text-brand-primary disabled:opacity-30"
            >
              ›
            </button>
          </div>
        </div>
      </div>
    </section>
  );
}
