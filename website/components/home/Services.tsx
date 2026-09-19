"use client";

import Image from "next/image";
import type { ServiceCard } from "@/content/home/types";
import { Reveal } from "@/components/ui/Reveal";
import { Placeholder } from "@/components/ui/Placeholder";
import { CarouselArrows } from "@/components/ui/Carousel";
import { useDragCarousel } from "@/lib/useDragCarousel";

export function Services({ services }: { services: ServiceCard[] }) {
  const { trackRef, index, goTo, translateX, atStart, atEnd, dragHandlers } =
    useDragCarousel(services.length);

  return (
    <section id="whatwedo" className="py-[clamp(56px,7vw,96px)]">
      <div className="mx-auto max-w-[1120px] px-6">
        <Reveal once>
          <div className="font-mono-wave mb-3.5 text-[11px] tracking-[0.16em] text-[#5a5d64] uppercase">
            — What We Do
          </div>
        </Reveal>
        <Reveal once className="mx-auto max-w-[640px] text-center">
          <h2 className="text-[clamp(24px,3.2vw,34px)] font-bold tracking-[-0.02em] text-brand-primary">
            Architecting Your Next-Gen Digital Core
          </h2>
        </Reveal>
        <Reveal once delayMs={60} className="mx-auto mb-11 max-w-[540px] text-center">
          <p className="font-wave-sans text-[15px] leading-[1.6] text-[#55585f]">
            We accelerate your digital evolution by blending advanced AI
            engineering, robust cloud infrastructure and deep domain
            expertise.
          </p>
        </Reveal>

        {/* Shares this same max-w-[1120px]/px-6 box with the heading above
            (rather than its own separately-centered wrapper) so the track's
            edges line up exactly with the heading at every viewport width —
            a mismatch here was a reported bug in the original design too. */}
        <div className="cursor-grab overflow-hidden active:cursor-grabbing">
          <div
            ref={trackRef}
            {...dragHandlers}
            className="relative flex touch-pan-y gap-5 transition-transform duration-300 ease-out"
            style={{ transform: `translateX(${translateX}px)` }}
          >
            {services.map((service, i) => (
              <Reveal key={service.name} once delayMs={i * 60} travelPx={20} className="flex-none">
                <div className="group w-[224px] select-none transition-transform duration-200 ease-out hover:-translate-y-1">
                  {service.image ? (
                    <div className="relative h-[284px] overflow-hidden transition-transform duration-200 ease-out group-hover:scale-[1.04]">
                      <Image
                        src={service.image}
                        alt={service.name}
                        fill
                        className="object-cover"
                        sizes="224px"
                      />
                      <div
                        className="absolute inset-0"
                        style={{ background: "linear-gradient(transparent 55%, rgba(3,9,22,.85))" }}
                      />
                      <span className="absolute top-3.5 left-3.5 max-w-[85%] text-[14px] leading-[1.3] font-bold text-white">
                        {service.name}
                      </span>
                    </div>
                  ) : (
                    <Placeholder
                      tone="dark"
                      label="Service Image"
                      className="h-[284px] transition-transform duration-200 ease-out group-hover:scale-[1.04]"
                    >
                      <span className="absolute top-3.5 left-3.5 max-w-[85%] text-[14px] leading-[1.3] font-bold text-white">
                        {service.name}
                      </span>
                    </Placeholder>
                  )}
                </div>
              </Reveal>
            ))}
          </div>
        </div>

        <CarouselArrows
          onPrev={() => goTo(index - 1)}
          onNext={() => goTo(index + 1)}
          prevDisabled={atStart}
          nextDisabled={atEnd}
          className="mt-8 justify-center"
        />
      </div>
    </section>
  );
}
