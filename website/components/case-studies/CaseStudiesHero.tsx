import Image from "next/image";
import { Link } from "@/i18n/navigation";

/**
 * Top of the Case Studies listing (Figma "Insight/ Case Studies" → "Frame 17"): a near-black to
 * indigo wash with the glossy 3D ribbon on the right, an "Insights > Case Studies" breadcrumb and
 * the 80px two-line title. The site header overlays the top 58px.
 */
export function CaseStudiesHero() {
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
            <Link href="/insights">Insights</Link>
            <span aria-hidden> &gt; </span>
            <span aria-current="page">Case Studies</span>
          </p>
          <h1 id="csl-hero-title" className="csl-hero-title">
            Case
            <br />
            Studies
          </h1>
        </div>
      </div>
    </section>
  );
}
