import { Fragment, type ReactNode } from "react";
import { defaultSlots } from "./defaults";
import { IndustryStories } from "./IndustryStories";
import { ServiceCapabilities } from "./service/ServiceCapabilities";
import { ServiceHero } from "./service/ServiceHero";
import { ServiceProcess } from "./service/ServiceProcess";
import { ServiceReasons } from "./service/ServiceReasons";
import type { IndustryStoryData, PostArticleSlots, SectionIntroData, ServicePageData } from "./types";

/**
 * A service page, drawn the same way for the live site and for the admin preview: the hero, then the
 * page's blocks in the order they are stored (hidden ones skipped), then the contact form. The blocks
 * that read site-wide content rather than this page (industries strip, latest highlights, contact form)
 * are handed in by the host as `industries`, `highlights` and `contact`; the stories block shows the
 * `stories` the host resolved from its case study slugs.
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
  return (
    <>
      <ServiceHero page={page} categoryName={categoryName} slots={slots} />
      {(page.blocks ?? []).map((block) => {
        if (block.hidden) return null;
        switch (block.type) {
          case "capabilities":
            return <ServiceCapabilities key={block.id} block={block.props} slots={slots} />;
          case "process":
            return <ServiceProcess key={block.id} block={block.props} slots={slots} />;
          case "stories":
            // The first story carries the left column's heading.
            return stories.length > 0 ? (
              <IndustryStories
                key={block.id}
                variant="svc-stories"
                stories={stories.map((s, i) => (i === 0 ? { ...s, heading: block.props.heading } : s))}
                intro={storiesIntro}
                slots={slots}
              />
            ) : null;
          case "reasons":
            return <ServiceReasons key={block.id} block={block.props} slots={slots} />;
          case "industries":
            return <Fragment key={block.id}>{industries}</Fragment>;
          case "highlights":
            return <Fragment key={block.id}>{highlights}</Fragment>;
        }
      })}
      {contact}
    </>
  );
}
