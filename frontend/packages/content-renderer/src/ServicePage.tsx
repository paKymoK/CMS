import type { ReactNode } from "react";
import { defaultSlots } from "./defaults";
import { IndustryStories } from "./IndustryStories";
import { ServiceCapabilities } from "./service/ServiceCapabilities";
import { ServiceHero } from "./service/ServiceHero";
import { ServiceProcess } from "./service/ServiceProcess";
import { ServiceReasons } from "./service/ServiceReasons";
import type { IndustryStoryData, PostArticleSlots, SectionIntroData, ServicePageData } from "./types";

/**
 * A service page, drawn the same way for the live site and for the admin preview. The sections that
 * read site-wide content rather than this page (industries strip, latest highlights, contact form) are
 * handed in by the host as `industries`, `highlights` and `contact`; the page's own switches
 * (`hiddenSections`) decide whether the first two show.
 */
export function ServicePage({
  page,
  categoryName,
  stories,
  storiesIntro,
  slots = defaultSlots,
  industries,
  highlights,
  contact,
}: {
  page: ServicePageData;
  categoryName: string;
  stories: IndustryStoryData[];
  storiesIntro?: SectionIntroData;
  slots?: PostArticleSlots;
  industries?: ReactNode;
  highlights?: ReactNode;
  contact?: ReactNode;
}) {
  const hidden = new Set(page.hiddenSections ?? []);
  return (
    <>
      <ServiceHero page={page} categoryName={categoryName} slots={slots} />
      {!hidden.has("capabilities") && <ServiceCapabilities page={page} slots={slots} />}
      {!hidden.has("process") && <ServiceProcess page={page} slots={slots} />}
      {!hidden.has("stories") && stories.length > 0 && (
        <IndustryStories variant="svc-stories" stories={stories} intro={storiesIntro} slots={slots} />
      )}
      {!hidden.has("reasons") && <ServiceReasons page={page} slots={slots} />}
      {!hidden.has("industries") && industries}
      {!hidden.has("highlights") && highlights}
      {contact}
    </>
  );
}
