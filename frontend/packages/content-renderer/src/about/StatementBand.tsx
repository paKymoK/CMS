import { defaultSlots } from "../defaults";
import type { PostArticleSlots } from "../types";

/**
 * The two statement bands of the About page: "Our Vision" (dark, with the earth photo and one big
 * centred sentence) and "Our Mission" (light, with the curved-architecture artwork on the left and
 * a paragraph on the right).
 */
export function StatementBand({
  variant,
  eyebrow,
  title,
  body,
  image,
  slots = defaultSlots,
}: {
  variant: "vision" | "mission";
  eyebrow?: string;
  title?: string;
  body?: string;
  image?: string;
  slots?: PostArticleSlots;
}) {
  const { Image } = slots;
  const isVision = variant === "vision";
  const paragraphs = (body ?? "").split(/\n\s*\n/).filter(Boolean);

  return (
    <section id={variant} aria-label={eyebrow ?? variant} className="design-canvas">
      <div className={`abt-band abt-band--${variant}`}>
        {isVision && image && <Image src={image} sizes="100vw" className="object-cover" />}
        {isVision && <div className="abt-vision-shade" aria-hidden />}
        {!isVision && image && (
          <div className="abt-mission-art" aria-hidden>
            <Image src={image} sizes="90vw" className="object-contain" />
          </div>
        )}

        <div className="sec-eyebrow-wrap abt-band-eyebrow">
          <div className={`cmc-eyebrow${isVision ? " cmc-eyebrow--light" : ""}`}>{eyebrow}</div>
        </div>

        {isVision && title && <h2 className="abt-vision-title">{title}</h2>}
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
