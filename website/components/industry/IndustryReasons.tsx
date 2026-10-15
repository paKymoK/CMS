import { CoverImage } from "@/components/ui/CoverImage";
import type { IndustryReason, SectionIntro } from "@/content/home/types";

const FALLBACK_INTRO: SectionIntro = { section: "industry-why", title: "Reasons to Partner with Us" };

/**
 * "Reasons to Partner with Us" (Figma "BFSI" → "Content"): a dark band with three tall photo cards,
 * each with a numbered white caption strip.
 */
export function IndustryReasons({
  reasons,
  intro = FALLBACK_INTRO,
}: {
  reasons: IndustryReason[];
  intro?: SectionIntro;
}) {
  return (
    <section id="why" aria-labelledby="ind-why-title" className="design-canvas">
      <div className="ind-why sec">
        <div className="sec-eyebrow-wrap">
          <div className="cmc-eyebrow cmc-eyebrow--light">{intro.eyebrow ?? "Why CMCG"}</div>
        </div>
        <div className="ind-sec-head ind-sec-head--light">
          <h2 id="ind-why-title" className="ind-why-title">
            {intro.title}
          </h2>
          {intro.body && <p className="ind-sec-sub">{intro.body}</p>}
        </div>
        <ol className="ind-why-cards">
          {reasons.slice(0, 3).map((r, i) => (
            <li key={r.title} className="ind-why-card">
              {r.image && (
                <CoverImage
                  src={r.image}
                  alt=""
                  sizes="(min-width: 1280px) 22vw, 90vw"
                  className="object-cover"
                />
              )}
              <div className="ind-why-cap">
                <span className="ind-why-num">{i + 1}</span>
                <h3>{r.title}</h3>
              </div>
            </li>
          ))}
        </ol>
      </div>
    </section>
  );
}
