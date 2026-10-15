import { CoverImage } from "@/components/ui/CoverImage";
import type { IndustryHero as IndustryHeroContent } from "@/content/home/types";

/**
 * Top of the Banking, Finance & Insurance page (Figma "BFSI" → "Frame 17"): dark-to-bronze wash with
 * the 3D artwork on the right, breadcrumb, headline, intro line, orange button and a glass card with
 * two headline figures. The site header overlays the top 58px.
 */
export function IndustryHero({ hero }: { hero: IndustryHeroContent }) {
  return (
    <section aria-labelledby="ind-hero-title" className="design-canvas">
      <div className="ind-hero">
        {hero.image && (
          <div className="ind-hero-art">
            <CoverImage
              src={hero.image}
              alt=""
              priority
              sizes="(min-width: 1280px) 45vw, 100vw"
              className="object-contain object-right-top"
            />
          </div>
        )}

        <div className="ind-hero-copy">
          {hero.breadcrumb && <p className="ind-crumb">{hero.breadcrumb}</p>}
          <h1 id="ind-hero-title" className="ind-hero-title">
            {hero.title}
          </h1>
          {hero.subtitle && <p className="ind-hero-sub">{hero.subtitle}</p>}
          {hero.ctaLabel && hero.ctaUrl && (
            <a href={hero.ctaUrl} className="ind-hero-cta">
              {hero.ctaLabel}
            </a>
          )}
        </div>

        {hero.stats && hero.stats.length > 0 && (
          <dl className="ind-hero-stats">
            {hero.stats.slice(0, 2).map((stat) => (
              <div key={stat.value} className="ind-hero-stat">
                <dt>{stat.value}</dt>
                <dd>{stat.label}</dd>
              </div>
            ))}
          </dl>
        )}
      </div>
    </section>
  );
}
