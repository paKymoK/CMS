import type { LogoBadge } from "@/content/home/types";
import { Reveal } from "@/components/ui/Reveal";
import { LogoImage } from "@/components/ui/LogoImage";

// Above this many items a static grid gets crowded, so the group switches to
// an auto-scrolling row instead of wrapping onto more lines.
const MARQUEE_THRESHOLD = 5;

function Tile({
  item,
  className = "",
  hidden = false,
}: {
  item: LogoBadge;
  className?: string;
  hidden?: boolean;
}) {
  return (
    <div
      aria-hidden={hidden}
      className={`flex h-[66px] items-center justify-center border border-[#e6e6e3] px-3 ${className}`}
    >
      <LogoImage src={item.logo} alt={hidden ? "" : item.name} width={72} height={40} />
    </div>
  );
}

function MarqueeRow({ items }: { items: LogoBadge[] }) {
  // Duplicated so translateX(-50%) hands off to an identical second copy,
  // looping seamlessly; the visible duplicate is hidden from assistive tech
  // (but not from view) to avoid double-announcing every logo.
  const duration = Math.max(20, items.length * 3.5);

  return (
    <div
      className="marquee-mask relative overflow-hidden"
      style={{ "--marquee-duration": `${duration}s` } as React.CSSProperties}
    >
      <div className="marquee-track flex w-max gap-2.5">
        {[...items, ...items].map((item, i) => (
          <Tile
            key={`${item.name}-${i}`}
            item={item}
            className="w-[96px] shrink-0"
            hidden={i >= items.length}
          />
        ))}
      </div>
    </div>
  );
}

function TileGroup({ label, items }: { label: string; items: LogoBadge[] }) {
  return (
    <div>
      <div className="font-mono-wave mb-2.5 text-[10px] tracking-[0.14em] text-[#5a5d64] uppercase">
        {label}
      </div>
      {items.length > MARQUEE_THRESHOLD ? (
        <Reveal>
          <MarqueeRow items={items} />
        </Reveal>
      ) : (
        <div className="grid grid-cols-[repeat(auto-fit,minmax(96px,1fr))] gap-2.5">
          {items.map((item, i) => (
            <Reveal key={item.name} delayMs={i * 40} travelPx={12}>
              <Tile item={item} />
            </Reveal>
          ))}
        </div>
      )}
    </div>
  );
}

export function Certifications({
  awards,
  certifications,
  partners,
}: {
  awards: LogoBadge[];
  certifications: LogoBadge[];
  partners: LogoBadge[];
}) {
  return (
    <section className="py-[clamp(56px,7vw,96px)]">
      <div className="mx-auto flex max-w-[1120px] flex-wrap gap-11 px-6">
        <Reveal className="flex-[1_1_260px]">
          <h2 className="text-[clamp(20px,2.6vw,28px)] font-bold tracking-[-0.015em]">
            Global Recognition for{" "}
            <span className="text-brand-primary">Innovative</span> Engineering
          </h2>
          <p className="font-wave-sans mt-4 text-[13.5px] leading-[1.6] text-[#55585f]">
            Independently verified quality, security and delivery standards,
            backed by 32+ years of experience and partnerships across the
            world&apos;s leading cloud and automation platforms.
          </p>
        </Reveal>

        <div className="flex flex-[2_1_420px] flex-col gap-6">
          <TileGroup label="Awards" items={awards} />
          <TileGroup label="Certificates" items={certifications} />
          <TileGroup label="Partners" items={partners} />
        </div>
      </div>
    </section>
  );
}
