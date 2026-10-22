import { defaultSlots } from "../defaults";
import type { IndustryPageData, PostArticleSlots } from "../types";

/**
 * Top of an industry page (Figma "BFSI" → "Frame 17"): dark-to-bronze wash with the 3D artwork on the
 * right, breadcrumb, headline, intro line, orange button and a glass card with two headline figures.
 * The site header overlays the top 58px.
 */
export function IndustryHero({ page, slots = defaultSlots }: { page: IndustryPageData; slots?: PostArticleSlots }) {
  const { Image } = slots;
  return (
    <section aria-labelledby="ind-hero-title" className="design-canvas">
      <div className="ind-hero">
        {page.heroImage && (
          <div className="ind-hero-art">
            <Image
              src={page.heroImage}
              priority
              sizes="(min-width: 1280px) 45vw, 100vw"
              className="object-contain object-right-top"
            />
          </div>
        )}

        <div className="ind-hero-copy">
          {page.heroBreadcrumb && <p className="ind-crumb">{page.heroBreadcrumb}</p>}
          <h1 id="ind-hero-title" className="ind-hero-title">
            {page.heroTitle}
          </h1>
          {page.heroSubtitle && <p className="ind-hero-sub">{page.heroSubtitle}</p>}
          {page.ctaLabel && page.ctaUrl && (
            <a href={page.ctaUrl} className="ind-hero-cta">
              {page.ctaLabel}
            </a>
          )}
        </div>

        {page.heroStats && page.heroStats.length > 0 && (
          <dl className="ind-hero-stats">
            {page.heroStats.slice(0, 2).map((stat) => (
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
