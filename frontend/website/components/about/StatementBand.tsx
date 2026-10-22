import { CoverImage } from "@/components/ui/CoverImage";
import type { AboutStatement } from "@/content/home/types";

/**
 * The two statement bands of the About page: "Our Vision" (dark, with the earth photo and one big
 * centred sentence) and "Our Mission" (light, with the curved-architecture artwork on the left and
 * a paragraph on the right).
 */
export function StatementBand({ statement }: { statement: AboutStatement }) {
  const isVision = statement.variant === "vision";
  const id = isVision ? "vision" : "mission";
  const paragraphs = (statement.body ?? "").split(/\n\s*\n/).filter(Boolean);

  return (
    <section id={id} aria-label={statement.eyebrow ?? id} className="design-canvas">
      <div className={`abt-band abt-band--${id}`}>
        {isVision && statement.image && (
          <CoverImage src={statement.image} alt="" sizes="100vw" className="object-cover" />
        )}
        {isVision && <div className="abt-vision-shade" aria-hidden />}
        {!isVision && statement.image && (
          <div className="abt-mission-art" aria-hidden>
            <CoverImage src={statement.image} alt="" sizes="90vw" className="object-contain" />
          </div>
        )}

        <div className="sec-eyebrow-wrap abt-band-eyebrow">
          <div className={`cmc-eyebrow${isVision ? " cmc-eyebrow--light" : ""}`}>
            {statement.eyebrow}
          </div>
        </div>

        {isVision && statement.title && <h2 className="abt-vision-title">{statement.title}</h2>}
        {!isVision && (
          <div className="abt-mission-text">
            {paragraphs.map((text, i) => (
              <p key={i}>{text}</p>
            ))}
          </div>
        )}
      </div>
    </section>
  );
}
