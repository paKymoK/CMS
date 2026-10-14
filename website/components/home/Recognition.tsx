import { LogoImage } from "@/components/ui/LogoImage";
import { Placeholder } from "@/components/ui/Placeholder";
import type { LogoBadge, SectionIntro } from "@/content/home/types";

const FALLBACK_INTRO: SectionIntro = {
  section: "recognition",
  title: "Global Recognition for Innovative Engineering",
  body: "Our technical expertise and proven business impact earn us industry-leading awards and elite credentials.",
};

// Enough copies that one half of the track is always wider than the visible row.
const copies = (items: LogoBadge[]) => {
  const reps = Math.max(2, Math.ceil(10 / Math.max(items.length, 1)));
  return Array.from({ length: reps * 2 }, () => items).flat();
};

function Row({ label, items, row }: { label: string; items: LogoBadge[]; row: number }) {
  if (items.length === 0) return null;
  const track = copies(items);
  const half = track.length / 2;
  return (
    <>
      <h3 className="rec-label" data-row={row}>
        {label}
      </h3>
      <div className="rec-row" data-row={row} role="group" aria-label={label}>
        <div
          className="rec-track"
          style={{ "--rec-dur": `${Math.max(30, half * 4)}s` } as React.CSSProperties}
        >
          {track.map((item, i) => (
            <div key={i} className="rec-tile" aria-hidden={i >= items.length}>
              <span>
                {item.logo ? (
                  <LogoImage
                    src={item.logo}
                    alt={i < items.length ? item.name : ""}
                    width={150}
                    height={72}
                    sizes="160px"
                    className="!h-full !w-full"
                  />
                ) : (
                  <Placeholder tone="light" label="" className="h-full w-full" />
                )}
              </span>
            </div>
          ))}
        </div>
      </div>
    </>
  );
}

/**
 * "Global Recognition for Innovative Engineering" (Figma "Global Presence"): the heading and a
 * sentence on the left, three slowly-scrolling rows of gradient-bordered logo tiles on the right —
 * Awards, Certificates, Partners — fading in from the left. Rows pause on hover and stand still for
 * visitors who prefer reduced motion.
 */
export function Recognition({
  awards,
  certifications,
  partners,
  intro = FALLBACK_INTRO,
}: {
  awards: LogoBadge[];
  certifications: LogoBadge[];
  partners: LogoBadge[];
  intro?: SectionIntro;
}) {
  return (
    <section id="recognition" aria-labelledby="recognition-title" className="design-canvas">
      <div className="rec">
        <div className="rec-intro">
          <h2 id="recognition-title" className="sec-title rec-title">
            {intro.title}
          </h2>
          {intro.body && <p className="rec-body">{intro.body}</p>}
        </div>
        <div className="rec-rows">
          <Row label="Awards" items={awards} row={0} />
          <Row label="Certificates" items={certifications} row={1} />
          <Row label="Partners" items={partners} row={2} />
        </div>
      </div>
    </section>
  );
}
