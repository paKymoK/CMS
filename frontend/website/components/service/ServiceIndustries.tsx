import { ServiceIndustries as SharedServiceIndustries } from "@cms/content-renderer";
import { siteSlots } from "@/components/content/siteSlots";
import type { IndustryCard, SectionIntro } from "@/content/home/types";

/** The shared industries strip with the site's router link and optimised images. */
export function ServiceIndustries({ cards, intro }: { cards: IndustryCard[]; intro?: SectionIntro }) {
  return <SharedServiceIndustries cards={cards} intro={intro} slots={siteSlots} />;
}
