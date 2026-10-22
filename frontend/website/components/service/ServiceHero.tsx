import { CoverImage } from "@/components/ui/CoverImage";
import type { ServicePageData } from "@/lib/cms/services";

/**
 * Top of a service page (Figma "Digital Engineering" → hero): dark gradient, breadcrumb, headline,
 * intro line, sky-blue button and the page's artwork on the right; an optional glass card with up
 * to two figures. The site header overlays the top 58px.
 */
export function ServiceHero({
  page,
  categoryName,
}: {
  page: ServicePageData;
  categoryName: string;
}) {
  const crumb = `Services > ${categoryName} > ${page.shortLabel || page.navLabel}`;
  return (
    <section aria-labelledby="svc-hero-title" className="design-canvas">
      <div className={`svc-hero${page.heroStats?.length ? " svc-hero--stats" : ""}`}>
        {page.heroImage && (
          <div className="svc-hero-art">
            <CoverImage
              src={page.heroImage}
              alt=""
              priority
              sizes="(min-width: 1280px) 60vw, 100vw"
              className="object-contain object-right"
            />
          </div>
        )}
        <div className="svc-hero-copy">
          <p className="svc-crumb">{crumb}</p>
          <h1 id="svc-hero-title" className="svc-hero-title">
            {page.heroTitle}
          </h1>
          {page.heroSubtitle && <p className="svc-hero-sub">{page.heroSubtitle}</p>}
          {page.ctaLabel && page.ctaUrl && (
            <a href={page.ctaUrl} className="svc-hero-cta">
              {page.ctaLabel}
            </a>
          )}
        </div>
        {page.heroStats && page.heroStats.length > 0 && (
          <dl className="svc-hero-stats">
            {page.heroStats.slice(0, 2).map((stat) => (
              <div key={stat.value} className="svc-hero-stat">
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
