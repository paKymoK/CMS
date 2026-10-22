import { defaultSlots } from "../defaults";
import type { AboutPageData, PostArticleSlots } from "../types";

/**
 * Top of the About page (Figma "About Us" → "Frame 18"): campus photo under a navy wash, the
 * breadcrumb, headline, intro line and button on the left, and a glass card with three figures.
 * The site header overlays the top 58px, as on the homepage.
 */
export function AboutHero({ page, slots = defaultSlots }: { page: AboutPageData; slots?: PostArticleSlots }) {
  const { Image } = slots;
  return (
    <section aria-labelledby="about-hero-title" className="design-canvas">
      <div className="abt-hero">
        {page.heroImage && (
          <Image src={page.heroImage} priority sizes="(min-width: 1920px) 1920px, 100vw" className="object-cover" />
        )}
        <div className="abt-hero-shade" aria-hidden />

        <div className="abt-hero-copy">
          {page.heroBreadcrumb && <p className="abt-crumb">{page.heroBreadcrumb}</p>}
          <h1 id="about-hero-title" className="abt-hero-title">
            {page.heroTitle}
          </h1>
          {page.heroSubtitle && <p className="abt-hero-sub">{page.heroSubtitle}</p>}
          {page.ctaLabel && page.ctaUrl && (
            <a href={page.ctaUrl} className="abt-hero-cta">
              {page.ctaLabel}
            </a>
          )}
        </div>

        {page.heroStats && page.heroStats.length > 0 && (
          <dl className="abt-hero-stats">
            {page.heroStats.slice(0, 3).map((stat) => (
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
