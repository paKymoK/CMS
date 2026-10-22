import { CoverImage } from "@/components/ui/CoverImage";
import type { AboutValue, SectionIntro } from "@/content/home/types";

const FALLBACK_INTRO: SectionIntro = {
  section: "values",
  eyebrow: "Our Values 4C",
  title: "The DNA of Our Growth",
};

/**
 * "The DNA of Our Growth" (Figma "About Us" → "Content"): the four values as tall photo cards with
 * the value's name across the bottom.
 */
export function Values({
  values,
  intro = FALLBACK_INTRO,
}: {
  values: AboutValue[];
  intro?: SectionIntro;
}) {
  if (values.length === 0) return null;
  return (
    <section id="values" aria-labelledby="values-title" className="design-canvas">
      <div className="abt-values sec">
        <div className="sec-eyebrow-wrap">
          <div className="cmc-eyebrow">{intro.eyebrow ?? "Our Values 4C"}</div>
        </div>
        <div className="abt-values-head">
          <h2 id="values-title" className="sec-title">
            {intro.title}
          </h2>
          {intro.body && <p>{intro.body}</p>}
        </div>
        <ul className="abt-values-cards">
          {values.slice(0, 4).map((v) => (
            <li key={v.title} className="abt-value">
              {v.image && (
                <CoverImage
                  src={v.image}
                  alt=""
                  sizes="(min-width: 1280px) 20vw, 50vw"
                  className="object-cover"
                />
              )}
              <h3>{v.title}</h3>
            </li>
          ))}
        </ul>
      </div>
    </section>
  );
}
