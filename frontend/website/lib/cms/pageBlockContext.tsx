import type { PageBlockContext } from "@cms/content-renderer";
import { InsightsAccordion } from "@/components/home/InsightsAccordion";
import { ServiceHighlights } from "@/components/service/ServiceHighlights";
import { ServiceIndustries } from "@/components/service/ServiceIndustries";
import type { HomeContent } from "@/content/home/types";
import { sectionIntro } from "@/lib/sectionIntro";
import type { FeaturedCaseStudy } from "./services";

/** What the blocks that read site-wide content need, the same for a Service and an Industry page. */
export function pageBlockContext(content: HomeContent, caseStudies: FeaturedCaseStudy[]): PageBlockContext {
  const intro = sectionIntro(content, "service-stories");
  const first = caseStudies[0];
  return {
    stories: caseStudies.map((c, i) => ({
      tag: c.category,
      title: c.title,
      body: c.summary,
      image: c.image,
      linkLabel: "Read the case study →",
      linkUrl: `/case-studies/${c.slug}`,
      ...(i === 0 ? { metrics: first?.results } : {}),
    })),
    storiesIntro: intro,
    industries: (
      <ServiceIndustries
        cards={content.sections?.industry_card ?? []}
        intro={sectionIntro(content, "service-industries")}
      />
    ),
    highlights: <ServiceHighlights insights={content.insights} intro={sectionIntro(content, "service-highlights")} />,
    insights: (block) => (
      <InsightsAccordion
        insights={content.insights}
        intro={
          block.title
            ? {
                section: "industry-insights",
                eyebrow: block.eyebrow,
                title: block.title,
                body: block.body,
                linkLabel: block.linkLabel,
                linkUrl: block.linkUrl,
              }
            : undefined
        }
      />
    ),
  };
}
