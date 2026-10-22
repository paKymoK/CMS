import Image from "next/image";
import { Link } from "@/i18n/navigation";

/**
 * Top of the listing pages (Figma Case Studies "Frame 17"): a near-black to indigo wash with the
 * glossy 3D ribbon on the right, a breadcrumb and the 80px title (a "\n" breaks the line), plus an
 * optional one-line intro under it. The site header overlays the top 58px. A crumb without an
 * `href` is plain text; the last one is the current page.
 */
export function ListHero({
  crumbs,
  title,
  subtitle,
}: {
  crumbs: { label: string; href?: string }[];
  title: string;
  subtitle?: string;
}) {
  const lines = title.split("\n");
  return (
    <section aria-labelledby="csl-hero-title" className="design-canvas">
      <div className="csl-hero">
        <div className="csl-hero-art" aria-hidden>
          <Image
            src="/images/case-studies-hero.webp"
            alt=""
            width={1446}
            height={1186}
            priority
            sizes="(min-width: 1280px) 76vw, 90vw"
          />
        </div>
        <div className="csl-hero-copy">
          <p className="csl-crumb">
            {crumbs.map((c, i) => {
              const last = i === crumbs.length - 1;
              return (
                <span key={c.label}>
                  {c.href && !last ? (
                    <Link href={c.href}>{c.label}</Link>
                  ) : (
                    <span aria-current={last ? "page" : undefined}>
                      {c.label}
                    </span>
                  )}
                  {!last && <span aria-hidden> &gt; </span>}
                </span>
              );
            })}
          </p>
          <h1 id="csl-hero-title" className="csl-hero-title">
            {lines.map((line, i) => (
              <span key={line}>
                {i > 0 && <br />}
                {line}
              </span>
            ))}
          </h1>
          {subtitle && <p className="csl-hero-sub">{subtitle}</p>}
        </div>
      </div>
    </section>
  );
}
