import type { ReactNode } from "react";
import { defaultSlots } from "./defaults";
import { IndustryHero } from "./industry/IndustryHero";
import { PageBlocks, type PageBlockContext } from "./PageBlocks";
import type { IndustryPageData, PostArticleSlots } from "./types";

/**
 * An industry page, drawn the same way for the live site and for the admin preview: the hero, then the
 * page's blocks in the order they are stored (hidden ones skipped), then the contact form. The blocks
 * that read site-wide content rather than this page are handed in by the host in `context` (see
 * {@link PageBlockContext}); the contact form is drawn last.
 */
export function IndustryPage({
  page,
  slots = defaultSlots,
  context,
  contact,
}: {
  page: IndustryPageData;
  slots?: PostArticleSlots;
  context?: PageBlockContext;
  contact?: ReactNode;
}) {
  return (
    <>
      <IndustryHero page={page} slots={slots} />
      <PageBlocks blocks={page.blocks ?? []} slots={slots} context={context} />
      {contact}
    </>
  );
}
