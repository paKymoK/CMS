import { Fragment, type ReactNode } from "react";
import { defaultSlots } from "./defaults";
import { IndustryStories } from "./IndustryStories";
import { IndustryHero } from "./industry/IndustryHero";
import { IndustryReasons } from "./industry/IndustryReasons";
import { IndustrySolutions } from "./industry/IndustrySolutions";
import type { IndustryPageData, InsightsBlockProps, PostArticleSlots } from "./types";

/**
 * An industry page, drawn the same way for the live site and for the admin preview: the hero, then the
 * page's blocks in the order they are stored (hidden ones skipped), then the contact form. The blocks
 * that read site-wide content rather than this page (the latest-insights strip, the contact form) are
 * handed in by the host: `insights` gets the block's intro text, `contact` is drawn last.
 */
export function IndustryPage({
  page,
  slots = defaultSlots,
  insights,
  contact,
}: {
  page: IndustryPageData;
  slots?: PostArticleSlots;
  insights?: (block: InsightsBlockProps) => ReactNode;
  contact?: ReactNode;
}) {
  return (
    <>
      <IndustryHero page={page} slots={slots} />
      {(page.blocks ?? []).map((block) => {
        if (block.hidden) return null;
        switch (block.type) {
          case "solutions":
            return <IndustrySolutions key={block.id} block={block.props} slots={slots} />;
          case "reasons":
            return <IndustryReasons key={block.id} block={block.props} slots={slots} />;
          case "stories": {
            const p = block.props;
            // The first story carries the left column's heading and figures.
            const stories = (p.items ?? []).map((s, i) => (i === 0 ? { ...s, heading: p.heading, metrics: p.metrics } : s));
            return (
              <IndustryStories
                key={block.id}
                stories={stories}
                intro={{ eyebrow: p.eyebrow, title: p.title ?? "Success Stories", body: p.body }}
                slots={slots}
              />
            );
          }
          case "insights":
            return <Fragment key={block.id}>{insights?.(block.props)}</Fragment>;
        }
      })}
      {contact}
    </>
  );
}
