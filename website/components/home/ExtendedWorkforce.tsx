import { CoverImage } from "@/components/ui/CoverImage";
import type { FeatureBand } from "@/content/home/types";

const FALLBACK: FeatureBand = {
  eyebrow: "Your Extended Tech Workforce",
  title: "Scale your tech capabilities with precision and agility",
  body: "Expand your engineering bandwidth with flexible, cross-functional tech teams that seamlessly align with your workflows and scale as your goals evolve.",
  ctaLabel: "Meet our experts",
  ctaUrl: "#contact",
};

/**
 * "Your Extended Tech Workforce" (Figma "About Us"): a full-width photo with a grey wash and the
 * copy low on the left, an outlined pill on the right. Everything — copy, button, photo — comes
 * from the CMS ("feature_band").
 */
export function ExtendedWorkforce({ band = FALLBACK }: { band?: FeatureBand }) {
  return (
    <section id="about" aria-labelledby="about-title" className="design-canvas">
      <div className="wfc">
        {band.image && (
          <CoverImage
            src={band.image}
            alt=""
            sizes="(min-width: 1920px) 1920px, 100vw"
            className="object-cover"
          />
        )}
        <div className="wfc-shade" aria-hidden />
        <div className="wfc-copy">
          {band.eyebrow && <p className="wfc-eyebrow">{band.eyebrow}</p>}
          <h2 id="about-title" className="wfc-title">
            {band.title}
          </h2>
          {band.body && <p className="wfc-body">{band.body}</p>}
          {band.ctaLabel && band.ctaUrl && (
            <a href={band.ctaUrl} className="wfc-cta">
              {band.ctaLabel}
              <svg
                viewBox="0 0 30 30"
                className="h-[26px] w-[26px] fill-none stroke-current stroke-[1.6]"
                aria-hidden
              >
                <path d="M7 15h15M16 9l6 6-6 6" strokeLinecap="round" strokeLinejoin="round" />
              </svg>
            </a>
          )}
        </div>
      </div>
    </section>
  );
}
