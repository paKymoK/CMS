import type { ReactNode } from "react";
import { AboutHero } from "./about/AboutHero";
import { defaultSlots } from "./defaults";
import { PageBlocks, type PageBlockContext } from "./PageBlocks";
import type { AboutPageData, PostArticleSlots } from "./types";

/**
 * The About page, drawn the same way for the live site and for the admin preview: the hero, then the
 * page's blocks in the order they are stored (hidden ones skipped). The blocks that read site-wide
 * content are handed in by the host in `context` (see {@link PageBlockContext}).
 */
export function AboutPage({
  page,
  slots = defaultSlots,
  context,
  contact,
}: {
  page: AboutPageData;
  slots?: PostArticleSlots;
  context?: PageBlockContext;
  contact?: ReactNode;
}) {
  return (
    <>
      <AboutHero page={page} slots={slots} />
      <PageBlocks blocks={page.blocks ?? []} slots={slots} context={context} />
      {contact}
    </>
  );
}
