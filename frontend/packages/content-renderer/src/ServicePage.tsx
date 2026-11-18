import { defaultSlots } from "./defaults";
import { PageBlocks, type PageBlockContext } from "./PageBlocks";
import { ServiceHero } from "./service/ServiceHero";
import type { PostArticleSlots, ServicePageData } from "./types";
import type { ReactNode } from "react";

/**
 * A service page, drawn the same way for the live site and for the admin preview: the hero, then the
 * page's blocks in the order they are stored (hidden ones skipped), then the contact form. The blocks
 * that read site-wide content rather than this page are handed in by the host in `context`
 * (see {@link PageBlockContext}); the contact form is drawn last.
 */
export function ServicePage({
  page,
  categoryName,
  slots = defaultSlots,
  context,
  contact,
}: {
  page: ServicePageData;
  categoryName: string;
  slots?: PostArticleSlots;
  context?: PageBlockContext;
  contact?: ReactNode;
}) {
  return (
    <>
      <ServiceHero page={page} categoryName={categoryName} slots={slots} />
      <PageBlocks blocks={page.blocks ?? []} slots={slots} context={context} />
      {contact}
    </>
  );
}
