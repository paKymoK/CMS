import { defaultSlots } from "../defaults";
import type { IndustryPageData, PostArticleSlots } from "../types";

/**
 * "Reasons to Partner with Us" (Figma "BFSI" → "Content"): a dark band with three tall photo cards,
 * each with a numbered white caption strip.
 */
export function IndustryReasons({ page, slots = defaultSlots }: { page: IndustryPageData; slots?: PostArticleSlots }) {
  const { Image } = slots;
  const reasons = page.reasons ?? [];
  if (reasons.length === 0) return null;
  return (
    <section id="why" aria-labelledby="ind-why-title" className="design-canvas">
      <div className="ind-why sec">
        <div className="sec-eyebrow-wrap">
          <div className="cmc-eyebrow cmc-eyebrow--light">{page.reasonsEyebrow ?? "Why CMCG"}</div>
        </div>
        <div className="ind-sec-head ind-sec-head--light">
          <h2 id="ind-why-title" className="ind-why-title">
            {page.reasonsTitle ?? "Reasons to Partner with Us"}
          </h2>
          {page.reasonsBody && <p className="ind-sec-sub">{page.reasonsBody}</p>}
        </div>
        <ol className="ind-why-cards">
          {reasons.slice(0, 3).map((r, i) => (
            <li key={r.title} className="ind-why-card">
              {r.image && <Image src={r.image} sizes="(min-width: 1280px) 22vw, 90vw" className="object-cover" />}
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
