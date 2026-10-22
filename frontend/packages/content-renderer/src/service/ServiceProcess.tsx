import type { CSSProperties } from "react";
import { defaultSlots } from "../defaults";
import type { PostArticleSlots, ServicePageData } from "../types";

/**
 * "Our Process" (Figma "Content" → "Our Process"): a dark band with a looping strip of numbered
 * step cards (photo with a white caption). The strip scrolls on its own and pauses on hover; with
 * reduced motion it sits still and scrolls by hand.
 */
export function ServiceProcess({ page, slots = defaultSlots }: { page: ServicePageData; slots?: PostArticleSlots }) {
  const { Image } = slots;
  const steps = page.processSteps ?? [];
  if (steps.length === 0) return null;
  const card = (s: (typeof steps)[number], i: number, copy: boolean) => (
    <li key={`${copy ? "b" : "a"}-${i}`} className="svc-step" aria-hidden={copy || undefined}>
      {s.image && (
        <Image src={s.image} sizes="(min-width: 1280px) 21vw, 70vw" className="object-cover" />
      )}
      <div className="svc-step-cap">
        <span className="svc-step-num">{i + 1}</span>
        <h3>{s.title}</h3>
      </div>
    </li>
  );
  return (
    <section id="process" aria-labelledby="svc-process-title" className="design-canvas">
      <div className="svc-process sec">
        <div className="sec-eyebrow-wrap">
          <div className="cmc-eyebrow cmc-eyebrow--light">Capabilities/ Process</div>
        </div>
        <div className="svc-sec-head svc-sec-head--light">
          <h2 id="svc-process-title" className="svc-light-title">
            {page.processTitle ?? "Our Process"}
          </h2>
          {page.processSubtitle && <p>{page.processSubtitle}</p>}
        </div>
        <div className="svc-strip">
          <ol className="svc-strip-track" style={{ "--n": steps.length } as CSSProperties}>
            {steps.map((s, i) => card(s, i, false))}
            {steps.map((s, i) => card(s, i, true))}
          </ol>
        </div>
      </div>
    </section>
  );
}
