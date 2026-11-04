import { defaultSlots } from "../defaults";
import { Placeholder } from "../Placeholder";
import type { AboutIntroBlockProps, PostArticleSlots } from "../types";

/**
 * "Meet CMC Global" (Figma "Capabilities"): the team collage beside the company story, then "Our
 * Growth in Numbers" in large gradient figures. The wireframe artwork sits behind, bleeding off the
 * edges.
 */
export function MeetCmc({ block, slots = defaultSlots }: { block: AboutIntroBlockProps; slots?: PostArticleSlots }) {
  const { Image } = slots;
  const title = block.title ?? "";
  const paragraphs = (block.body ?? "").split(/\n\s*\n/).filter(Boolean);
  const stats = block.growthStats ?? [];
  return (
    <section id="meet" aria-labelledby="meet-title" className="design-canvas">
      <div className="abt-meet">
        {block.decorImage && (
          <>
            <div className="abt-meet-deco" aria-hidden>
              <Image src={block.decorImage} sizes="70vw" className="object-contain" />
            </div>
            <div className="abt-meet-deco abt-meet-deco--b" aria-hidden>
              <Image src={block.decorImage} sizes="70vw" className="object-contain" />
            </div>
          </>
        )}

        <div className="sec-eyebrow-wrap">
          <div className="cmc-eyebrow">{block.eyebrow ?? title}</div>
        </div>
        <h2 id="meet-title" className="sec-title abt-meet-title">
          {title}
        </h2>

        <div className="abt-meet-grid">
          <div className="abt-meet-photo">
            {block.image ? (
              <Image
                src={block.image}
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
            {block.growthTitle && <h2 className="sec-title abt-stats-title">{block.growthTitle}</h2>}
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
