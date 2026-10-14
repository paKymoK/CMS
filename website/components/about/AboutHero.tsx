import { CoverImage } from "@/components/ui/CoverImage";
import type { AboutHero as AboutHeroContent } from "@/content/home/types";

/**
 * Top of the About page (Figma "About Us" → "Frame 18"): campus photo under a navy wash, the
 * breadcrumb, headline, intro line and button on the left, and a glass card with three figures.
 * The site header overlays the top 58px, as on the homepage.
 */
export function AboutHero({ hero }: { hero: AboutHeroContent }) {
  return (
    <section aria-labelledby="about-hero-title" className="design-canvas">
      <div className="abt-hero">
        {hero.image && (
          <CoverImage
            src={hero.image}
            alt=""
            priority
            sizes="(min-width: 1920px) 1920px, 100vw"
            className="object-cover"
          />
        )}
        <div className="abt-hero-shade" aria-hidden />

        <div className="abt-hero-copy">
          {hero.breadcrumb && <p className="abt-crumb">{hero.breadcrumb}</p>}
          <h1 id="about-hero-title" className="abt-hero-title">
            {hero.title}
          </h1>
          {hero.subtitle && <p className="abt-hero-sub">{hero.subtitle}</p>}
          {hero.ctaLabel && hero.ctaUrl && (
            <a href={hero.ctaUrl} className="abt-hero-cta">
              {hero.ctaLabel}
            </a>
          )}
        </div>

        {hero.stats && hero.stats.length > 0 && (
          <dl className="abt-hero-stats">
            {hero.stats.slice(0, 3).map((stat) => (
              <div key={stat.label} className="abt-hero-stat">
                <dt className="abt-hero-stat-value">{stat.value}</dt>
                <dd className="abt-hero-stat-label">{stat.label}</dd>
              </div>
            ))}
          </dl>
        )}
      </div>
    </section>
  );
}
