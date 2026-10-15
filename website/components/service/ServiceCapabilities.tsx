import { CoverImage } from "@/components/ui/CoverImage";
import type { ServicePageData } from "@/lib/cms/services";

/**
 * "Our Capabilities" (Figma "Capabilities"): a heading, a thin tab-style rule and a row of image
 * cards, each with a title and a short description. Two cards render wide, three or more narrow.
 */
export function ServiceCapabilities({ page }: { page: ServicePageData }) {
  const items = page.capabilities ?? [];
  if (items.length === 0) return null;
  const wide = items.length <= 2;
  return (
    <section id="capabilities" aria-labelledby="svc-cap-title" className="design-canvas">
      <div className={`svc-cap sec${wide ? " svc-cap--wide" : ""}`}>
        <div className="sec-eyebrow-wrap">
          <div className="cmc-eyebrow">{page.capabilitiesEyebrow ?? "Capabilities"}</div>
        </div>
        <div className="svc-sec-head">
          <h2 id="svc-cap-title" className="sec-title">
            {page.capabilitiesTitle ?? "Our Capabilities"}
          </h2>
          {page.capabilitiesSubtitle && <p>{page.capabilitiesSubtitle}</p>}
        </div>
        <div className="svc-cap-rule" aria-hidden />
        <ul className={`svc-cap-cards${wide ? " svc-cap-cards--wide" : ""}`}>
          {items.map((c) => (
            <li key={c.title} className="svc-cap-card">
              <div className="svc-cap-img">
                {c.image && (
                  <CoverImage
                    src={c.image}
                    alt=""
                    sizes={wide ? "(min-width: 1280px) 31vw, 90vw" : "(min-width: 1280px) 14vw, 45vw"}
                    className="object-cover"
                  />
                )}
              </div>
              <h3>{c.title}</h3>
              {c.body && <p>{c.body}</p>}
            </li>
          ))}
        </ul>
      </div>
    </section>
  );
}
