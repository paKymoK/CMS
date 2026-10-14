import { CoverImage } from "@/components/ui/CoverImage";
import type { AboutIntro, Stat } from "@/content/home/types";

/**
 * "Meet CMC Global" (Figma "Capabilities"): the team collage beside the company story, then "Our
 * Growth in Numbers" in large gradient figures (from the Stats CMS screen, like the homepage). The wireframe artwork sits behind, bleeding off
 * the edges.
 */
export function MeetCmc({ intro, stats }: { intro: AboutIntro; stats: Stat[] }) {
  const paragraphs = (intro.body ?? "").split(/\n\s*\n/).filter(Boolean);
  return (
    <section id="meet" aria-labelledby="meet-title" className="design-canvas">
      <div className="abt-meet">
        {intro.decorImage && (
          <>
            <div className="abt-meet-deco" aria-hidden>
              <CoverImage src={intro.decorImage} alt="" sizes="70vw" className="object-contain" />
            </div>
            <div className="abt-meet-deco abt-meet-deco--b" aria-hidden>
              <CoverImage src={intro.decorImage} alt="" sizes="70vw" className="object-contain" />
            </div>
          </>
        )}

        <div className="sec-eyebrow-wrap">
          <div className="cmc-eyebrow">{intro.eyebrow ?? intro.title}</div>
        </div>
        <h2 id="meet-title" className="sec-title abt-meet-title">
          {intro.title}
        </h2>

        <div className="abt-meet-grid">
          <div className="abt-meet-photo">
            {intro.image && (
              <CoverImage
                src={intro.image}
                alt="CMC Global teammates"
                sizes="(min-width: 1280px) 38vw, 90vw"
                className="object-contain"
              />
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
            {intro.statsTitle && <h2 className="sec-title abt-stats-title">{intro.statsTitle}</h2>}
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
