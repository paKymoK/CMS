import { InsightsAccordion as SharedInsightsAccordion } from "@cms/content-renderer";
import { siteSlots } from "@/components/content/siteSlots";
import type { InsightItem, SectionIntro } from "@/content/home/types";

/** The shared insights accordion with the site's router link and optimised images. */
export function InsightsAccordion({ insights, intro }: { insights: InsightItem[]; intro?: SectionIntro }) {
  return <SharedInsightsAccordion insights={insights} intro={intro} slots={siteSlots} />;
}
