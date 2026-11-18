import { defaultSlots } from "./defaults";
import { InsightsAccordionView } from "./InsightsAccordionView";
import { Placeholder } from "./Placeholder";
import type { InsightItemData, PostArticleSlots, SectionLinkIntroData } from "./types";

const FALLBACK_INTRO: SectionLinkIntroData = {
  eyebrow: "Insights",
  title: "Building Tomorrow Together",
  linkLabel: "Explore all Insights",
  linkUrl: "/insights",
};

const DiagonalArrow = () => (
  <svg viewBox="0 0 20 20" className="fill-none stroke-current stroke-[1.5]" aria-hidden>
    <path d="M5 15 15 5M7 5h8v8" strokeLinecap="round" strokeLinejoin="round" />
  </svg>
);

/**
 * "Building Tomorrow Together" (Figma "Building"): a photo accordion of the newest articles. The
 * accordion is a client component; the photos and the "see all" link are drawn here with the host's
 * slots and handed to it, since slots (functions) cannot cross into a client component.
 */
export function InsightsAccordion({
  insights,
  intro = FALLBACK_INTRO,
  slots = defaultSlots,
}: {
  insights: InsightItemData[];
  intro?: SectionLinkIntroData;
  slots?: PostArticleSlots;
}) {
  const { Link, Image } = slots;
  return (
    <InsightsAccordionView
      insights={insights.slice(0, 7).map((item) => ({
        ...item,
        photo: item.image ? (
          <div className="ins-panel-img">
            <Image src={item.image} alt="" sizes="(min-width: 1280px) 55vw, 90vw" className="object-cover" />
          </div>
        ) : (
          <Placeholder tone="navy" label="Insight image" fill />
        ),
      }))}
      intro={intro}
      moreLink={
        intro.linkLabel && intro.linkUrl ? (
          <Link href={intro.linkUrl} className="sec-link">
            {intro.linkLabel}
            <DiagonalArrow />
          </Link>
        ) : undefined
      }
    />
  );
}
