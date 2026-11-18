import { Fragment, type ReactNode } from "react";
import { defaultSlots } from "./defaults";
import { IndustryStories } from "./IndustryStories";
import { IndustryReasons } from "./industry/IndustryReasons";
import { IndustrySolutions } from "./industry/IndustrySolutions";
import { Leadership } from "./about/Leadership";
import { MeetCmc } from "./about/MeetCmc";
import { StatementBand } from "./about/StatementBand";
import { Values } from "./about/Values";
import { ServiceCapabilities } from "./service/ServiceCapabilities";
import { ServiceProcess } from "./service/ServiceProcess";
import { ServiceReasons } from "./service/ServiceReasons";
import type { InsightsBlockProps, IndustryStoryData, PageBlockData, PostArticleSlots, SectionIntroData } from "./types";

/**
 * What the blocks that read site-wide content rather than the page itself need from the host: the
 * case studies the page's case-studies block names (already resolved), the industries strip, the
 * latest-highlights cards, and the latest-insights strip (which gets its block's intro text).
 */
export interface PageBlockContext {
  stories?: IndustryStoryData[];
  storiesIntro?: SectionIntroData;
  industries?: ReactNode;
  highlights?: ReactNode;
  insights?: (block: InsightsBlockProps) => ReactNode;
}

/**
 * The blocks of a Service, Industry or About page in the order they are stored (hidden ones
 * skipped), the same way for the live site and the admin preview. One list serves every page type.
 */
export function PageBlocks({
  blocks,
  slots = defaultSlots,
  context = {},
}: {
  blocks: PageBlockData[];
  slots?: PostArticleSlots;
  context?: PageBlockContext;
}) {
  const { stories = [], storiesIntro, industries, highlights, insights } = context;
  return (
    <>
      {blocks.map((block) => {
        if (block.hidden) return null;
        switch (block.type) {
          case "capabilities":
            return <ServiceCapabilities key={block.id} block={block.props} slots={slots} />;
          case "process":
            return <ServiceProcess key={block.id} block={block.props} slots={slots} />;
          case "case-studies":
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
          case "reasons-slider":
            return <ServiceReasons key={block.id} block={block.props} slots={slots} />;
          case "industries":
            return <Fragment key={block.id}>{industries}</Fragment>;
          case "highlights":
            return <Fragment key={block.id}>{highlights}</Fragment>;
          case "solutions":
            return <IndustrySolutions key={block.id} block={block.props} slots={slots} />;
          case "reasons-cards":
            return <IndustryReasons key={block.id} block={block.props} slots={slots} />;
          case "stories": {
            const p = block.props;
            // The first story carries the left column's heading and figures.
            const items = (p.items ?? []).map((s, i) => (i === 0 ? { ...s, heading: p.heading, metrics: p.metrics } : s));
            return (
              <IndustryStories
                key={block.id}
                stories={items}
                intro={{ eyebrow: p.eyebrow, title: p.title ?? "Success Stories", body: p.body }}
                slots={slots}
              />
            );
          }
          case "insights":
            return <Fragment key={block.id}>{insights?.(block.props)}</Fragment>;
          // A block that lacks its key text (the intro without a title, a band without text) draws nothing.
          case "intro":
            return block.props.title ? <MeetCmc key={block.id} block={block.props} slots={slots} /> : null;
          case "vision":
            return block.props.eyebrow || block.props.title ? (
              <StatementBand key={block.id} variant="vision" eyebrow={block.props.eyebrow} title={block.props.title} image={block.props.image} slots={slots} />
            ) : null;
          case "mission":
            return block.props.eyebrow || block.props.body ? (
              <StatementBand key={block.id} variant="mission" eyebrow={block.props.eyebrow} body={block.props.body} image={block.props.image} slots={slots} />
            ) : null;
          case "leaders":
            return <Leadership key={block.id} block={block.props} slots={slots} />;
          case "values":
            return <Values key={block.id} block={block.props} slots={slots} />;
        }
      })}
    </>
  );
}
