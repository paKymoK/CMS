import { defaultSlots } from "../defaults";
import { Placeholder } from "../Placeholder";
import type { AboutPageData, PostArticleSlots } from "../types";

/**
 * "Meet CMC Global" (Figma "Capabilities"): the team collage beside the company story, then "Our
 * Growth in Numbers" in large gradient figures. The wireframe artwork sits behind, bleeding off the
 * edges.
 */
export function MeetCmc({ page, slots = defaultSlots }: { page: AboutPageData; slots?: PostArticleSlots }) {
  const { Image } = slots;
  const title = page.introTitle ?? "";
  const paragraphs = (page.introBody ?? "").split(/\n\s*\n/).filter(Boolean);
  const stats = page.growthStats ?? [];
  return (
    <section id="meet" aria-labelledby="meet-title" className="design-canvas">
      <div className="abt-meet">
        {page.introDecorImage && (
          <>
            <div className="abt-meet-deco" aria-hidden>
              <Image src={page.introDecorImage} sizes="70vw" className="object-contain" />
            </div>
            <div className="abt-meet-deco abt-meet-deco--b" aria-hidden>
              <Image src={page.introDecorImage} sizes="70vw" className="object-contain" />
            </div>
          </>
        )}

        <div className="sec-eyebrow-wrap">
          <div className="cmc-eyebrow">{page.introEyebrow ?? title}</div>
        </div>
        <h2 id="meet-title" className="sec-title abt-meet-title">
          {title}
        </h2>

        <div className="abt-meet-grid">
          <div className="abt-meet-photo">
            {page.introImage ? (
              <Image
                src={page.introImage}
                alt="CMC Global teammates"
                sizes="(min-width: 1280px) 38vw, 90vw"
                className="object-contain"
              />
            ) : (
              <Placeholder tone="light" label="Team photo" fill />
            )}
          </div>
          <div className="abt-meet-body">
            {paragraphs.map((text, i) => (
              <p key={i}>{text}</p>
            ))}
          </div>
        </div>

        {stats.length > 0 && (
          <>
            {page.growthTitle && <h2 className="sec-title abt-stats-title">{page.growthTitle}</h2>}
            <dl className="abt-stats">
              {stats.slice(0, 5).map((stat) => (
                <div key={stat.label} className="abt-stat">
                  <dt className="abt-stat-value abt-grad-text">{stat.value}</dt>
                  <dd className="abt-stat-label abt-grad-text">{stat.label}</dd>
                </div>
              ))}
            </dl>
          </>
        )}
      </div>
    </section>
  );
}
