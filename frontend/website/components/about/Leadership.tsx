import { CoverImage } from "@/components/ui/CoverImage";
import { Placeholder } from "@/components/ui/Placeholder";
import type { Leader, SectionIntro } from "@/content/home/types";

const FALLBACK_INTRO: SectionIntro = {
  section: "leaders",
  eyebrow: "Our People",
  title: "Leadership Team",
};

// Where each card sits on the 1920px artboard: three on the first row, the rest centred below.
const SLOTS: [number, number][] = [
  [358, 246],
  [794, 246],
  [1233, 246],
  [579, 604],
  [1024, 604],
];
const slot = (i: number): [number, number] =>
  SLOTS[i] ?? [358 + (i % 3) * 437, 246 + Math.floor(i / 3) * 358];

/**
 * "Leadership Team" (Figma "Our People"): cut-out portraits on pale-blue cards with name and role
 * underneath, between two ring graphics. Order follows the CMS display order.
 */
export function Leadership({
  leaders,
  intro = FALLBACK_INTRO,
  ringImage,
}: {
  leaders: Leader[];
  intro?: SectionIntro;
  ringImage?: string;
}) {
  if (leaders.length === 0) return null;
  return (
    <section id="leaders" aria-labelledby="leaders-title" className="design-canvas">
      <div className="abt-people">
        {ringImage && (
          <>
            <div className="abt-ring abt-ring--a" aria-hidden>
              <CoverImage src={ringImage} alt="" sizes="30vw" className="object-contain" />
            </div>
            <div className="abt-ring abt-ring--b" aria-hidden>
              <CoverImage src={ringImage} alt="" sizes="30vw" className="object-contain" />
            </div>
          </>
        )}
        <div className="sec-eyebrow-wrap">
          <div className="cmc-eyebrow">{intro.eyebrow ?? "Our People"}</div>
        </div>
        <h2 id="leaders-title" className="sec-title abt-people-title">
          {intro.title}
        </h2>

        <ul className="abt-leaders">
          {leaders.map((leader, i) => {
            const [x, y] = slot(i);
            return (
              <li
                key={leader.name}
                className="abt-leader"
                style={{ "--lx": x, "--ly": y } as React.CSSProperties}
              >
                <div className="abt-leader-card">
                  {leader.photo ? (
                    <div className="abt-leader-photo">
                      <CoverImage
                        src={leader.photo}
                        alt={leader.name}
                        sizes="(min-width: 1280px) 22vw, 50vw"
                        className="object-cover object-top"
                      />
                    </div>
                  ) : (
                    <Placeholder tone="light" label="Portrait" fill />
                  )}
                </div>
                <p className="abt-leader-name">
                  <strong>{leader.name}</strong>
                  {leader.title && <span>{leader.title}</span>}
                </p>
              </li>
            );
          })}
        </ul>
      </div>
    </section>
  );
}
