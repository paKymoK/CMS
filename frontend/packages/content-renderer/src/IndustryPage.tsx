import type { ReactNode } from "react";
import { defaultSlots } from "./defaults";
import { IndustryStories } from "./IndustryStories";
import { IndustryHero } from "./industry/IndustryHero";
import { IndustryReasons } from "./industry/IndustryReasons";
import { IndustrySolutions } from "./industry/IndustrySolutions";
import type { IndustryPageData, PostArticleSlots } from "./types";

/**
 * An industry page, drawn the same way for the live site and for the admin preview. The sections that
 * read site-wide content rather than this page (the latest-insights strip, the contact form) are
 * handed in by the host as `insights` and `contact`; the page's own switches (`hiddenSections`)
 * decide whether the first one shows.
 */
export function IndustryPage({
  page,
  slots = defaultSlots,
  insights,
  contact,
}: {
  page: IndustryPageData;
  slots?: PostArticleSlots;
  insights?: ReactNode;
  contact?: ReactNode;
}) {
  const hidden = new Set(page.hiddenSections ?? []);
  // The first story carries the left column's heading and figures.
  const stories = (page.stories ?? []).map((s, i) =>
    i === 0 ? { ...s, heading: page.storiesHeading, metrics: page.storiesMetrics } : s,
  );
  return (
    <>
      <IndustryHero page={page} slots={slots} />
      {!hidden.has("solutions") && <IndustrySolutions page={page} slots={slots} />}
      {!hidden.has("reasons") && <IndustryReasons page={page} slots={slots} />}
      {!hidden.has("stories") && (
        <IndustryStories
          stories={stories}
          intro={{ eyebrow: page.storiesEyebrow, title: page.storiesTitle ?? "Success Stories", body: page.storiesBody }}
          slots={slots}
        />
      )}
      {!hidden.has("insights") && insights}
      {contact}
    </>
  );
}
