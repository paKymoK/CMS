import { CoverImage } from "@/components/ui/CoverImage";
import type { ReactNode } from "react";

/**
 * Top of the Insights pages (Figma "Insight" → "Frame 17"): a photo under a navy wash with a small
 * "Insights" label and the big page title bottom-left. The site header overlays the top 58px.
 * The detail page passes its byline as children.
 */
export function InsightsHero({
  eyebrow,
  title,
  image,
  small = false,
  children,
}: {
  eyebrow?: string;
  title: string;
  image?: string | null;
  /** Long article headlines use a smaller title than the listing's one-word title. */
  small?: boolean;
  children?: ReactNode;
}) {
  return (
    <section aria-labelledby="ins-hero-title" className="design-canvas">
      <div className="ins-hero">
        {image && (
          <CoverImage
            src={image}
            alt=""
            priority
            sizes="(min-width: 1920px) 1920px, 100vw"
            className="object-cover"
          />
        )}
        <div className="ins-hero-shade" aria-hidden />
        <div className="ins-hero-copy">
          {eyebrow && <p className="ins-hero-eyebrow">{eyebrow}</p>}
          <h1 id="ins-hero-title" className={`ins-hero-title${small ? " ins-hero-title--sm" : ""}`}>
            {title}
          </h1>
          {children}
        </div>
      </div>
    </section>
  );
}
