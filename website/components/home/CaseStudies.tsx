import { Link } from "@/i18n/navigation";
import { DiagonalArrow } from "@/components/ui/DiagonalArrow";
import { CoverImage } from "@/components/ui/CoverImage";
import { Placeholder } from "@/components/ui/Placeholder";
import type { CaseStudy, SectionIntro } from "@/content/home/types";

const FALLBACK_INTRO: SectionIntro = {
  section: "cases",
  eyebrow: "Case Studies",
  title: "Stories of Transformation",
  kicker: "Latest Case Study",
  linkLabel: "Explore all Case Studies",
  linkUrl: "#cases",
};

/**
 * "Stories of Transformation" (Figma "Stories of Transformation"): four photo cards in a row, each
 * with a category tag. On hover/focus the photo grows to full size and a grey panel slides up with
 * the date and headline (always shown on touch screens, which can't hover). Cards open the case
 * study's page.
 */
export function CaseStudies({
  caseStudies,
  intro = FALLBACK_INTRO,
}: {
  caseStudies: CaseStudy[];
  intro?: SectionIntro;
}) {
  return (
    <section id="cases" aria-labelledby="cases-title" className="design-canvas">
      <div className="cs sec">
        <div className="sec-eyebrow-wrap">
          <div className="cmc-eyebrow">{intro.eyebrow ?? "Case Studies"}</div>
        </div>
        <h2 id="cases-title" className="sec-title cs-title">
          {intro.title}
        </h2>
        {intro.kicker && <p className="cs-kicker">{intro.kicker}</p>}

        <div className="cs-grid">
          {caseStudies.slice(0, 4).map((study) => {
            const inner = (
              <>
                <div className="cs-img">
                  {study.image ? (
                    <CoverImage
                      src={study.image}
                      alt=""
                      sizes="(min-width: 1280px) 18vw, (min-width: 900px) 24vw, 46vw"
                      className="object-cover"
                    />
                  ) : (
                    <Placeholder
                      tone="dark"
                      label="Case study image"
                      className="absolute inset-0"
                    />
                  )}
                </div>
                <span className="cs-tag">{study.category}</span>
                <div className="cs-info">
                  <p className="cs-date">{study.date}</p>
                  <div className="cs-rule" aria-hidden />
                  <h3 className="cs-name">{study.title}</h3>
                </div>
              </>
            );
            return study.slug ? (
              <Link key={study.title} href={`/case-studies/${study.slug}`} className="cs-card">
                {inner}
              </Link>
            ) : (
              <div key={study.title} className="cs-card" tabIndex={0}>
                {inner}
              </div>
            );
          })}
        </div>

        {intro.linkLabel && intro.linkUrl && (
          <div className="cs-more">
            <a href={intro.linkUrl} className="sec-link">
              {intro.linkLabel}
              <DiagonalArrow />
            </a>
          </div>
        )}
      </div>
    </section>
  );
}
