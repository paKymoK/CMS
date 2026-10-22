import { defaultSlots } from "../defaults";
import type { AboutPageData, PostArticleSlots } from "../types";

/**
 * "The DNA of Our Growth" (Figma "About Us" → "Content"): the four values as tall photo cards with
 * the value's name across the bottom.
 */
export function Values({ page, slots = defaultSlots }: { page: AboutPageData; slots?: PostArticleSlots }) {
  const { Image } = slots;
  const values = page.values ?? [];
  if (values.length === 0) return null;
  return (
    <section id="values" aria-labelledby="values-title" className="design-canvas">
      <div className="abt-values sec">
        <div className="sec-eyebrow-wrap">
          <div className="cmc-eyebrow">{page.valuesEyebrow ?? "Our Values 4C"}</div>
        </div>
        <div className="abt-values-head">
          <h2 id="values-title" className="sec-title">
            {page.valuesTitle ?? "The DNA of Our Growth"}
          </h2>
          {page.valuesBody && <p>{page.valuesBody}</p>}
        </div>
        <ul className="abt-values-cards">
          {values.slice(0, 4).map((v) => (
            <li key={v.title} className="abt-value">
              {v.image && <Image src={v.image} sizes="(min-width: 1280px) 20vw, 50vw" className="object-cover" />}
              <h3>{v.title}</h3>
            </li>
          ))}
        </ul>
      </div>
    </section>
  );
}
