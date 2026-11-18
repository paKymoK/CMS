import { ServiceHighlights as SharedServiceHighlights } from "@cms/content-renderer";
import { siteSlots } from "@/components/content/siteSlots";
import { Tooltip } from "@/components/ui/Tooltip";
import type { InsightItem, SectionIntro } from "@/content/home/types";

/** The shared latest-highlights cards with the site's router link and tooltip. */
export function ServiceHighlights({ insights, intro }: { insights: InsightItem[]; intro?: SectionIntro }) {
  return <SharedServiceHighlights insights={insights} intro={intro} slots={siteSlots} Tooltip={Tooltip} />;
}
