import Image from "next/image";
import { Reveal } from "@/components/ui/Reveal";
import { Placeholder } from "@/components/ui/Placeholder";
import type { CaseStudy } from "@/content/home/types";

const TONES = ["warm", "cool", "navy", "dark"] as const;

export function CaseStudies({ caseStudies }: { caseStudies: CaseStudy[] }) {
  return (
    <section id="cases" className="py-[clamp(56px,7vw,96px)]">
      <div className="mx-auto max-w-[1120px] px-6">
        <Reveal once className="text-center">
          <div className="font-mono-wave text-[11px] tracking-[0.16em] text-[#5a5d64] uppercase">
            — Case Studies
          </div>
          <h2 className="mt-3.5 mb-11 text-[clamp(24px,3.2vw,34px)] font-bold tracking-[-0.02em] text-brand-primary">
            Stories of Transformation
          </h2>
        </Reveal>

        <div className="grid grid-cols-[repeat(auto-fit,minmax(224px,1fr))] gap-5">
          {caseStudies.map((cs, i) => (
            <Reveal key={cs.title} delayMs={100 + i * 80} once travelPx={24}>
              <a
                href="#"
                className="group relative block h-[328px] overflow-hidden bg-[#061225]"
              >
                {cs.image ? (
                  <Image
                    src={cs.image}
                    alt={cs.title}
                    fill
                    className="absolute inset-0 scale-[1.05] object-cover transition-transform duration-[900ms] ease-[cubic-bezier(.25,1,.5,1)] group-hover:scale-[1.02]"
                    sizes="(min-width: 1120px) 260px, 33vw"
                  />
                ) : (
                  <Placeholder
                    tone={TONES[i % TONES.length]}
                    label=""
                    className="absolute inset-0 scale-[1.05] transition-transform duration-[900ms] ease-[cubic-bezier(.25,1,.5,1)] group-hover:scale-[1.02]"
                  />
                )}
                <div
                  className="absolute inset-0 opacity-85 transition-opacity duration-200 ease-linear group-hover:opacity-95"
                  style={{ background: "linear-gradient(transparent 32%, rgba(3,9,22,.92))" }}
                />
                <div className="font-mono-wave absolute top-3.5 left-3.5 text-[9px] tracking-[0.06em] text-[#cfe1f6] uppercase">
                  {cs.category}
                </div>
                <div className="absolute right-3.5 bottom-3.5 left-3.5 text-white transition-transform duration-200 group-hover:-translate-y-0.5">
                  <div className="font-mono-wave mb-2 text-[9px] tracking-[0.08em] text-[#8fb5e8] uppercase">
                    {cs.date}
                  </div>
                  <h3 className="line-clamp-4 text-[14px] leading-[1.42] font-bold">{cs.title}</h3>
                </div>
              </a>
            </Reveal>
          ))}
        </div>

        <div className="mt-7 text-right">
          <a
            href="#"
            className="font-mono-wave text-[11px] tracking-[0.08em] text-brand-primary uppercase transition-colors hover:text-brand-primary-dark"
          >
            Explore All Case Studies ↗
          </a>
        </div>
      </div>
    </section>
  );
}
