import type { ReactNode } from "react";
import { loadContent } from "@/lib/cms/loadContent";
import { sectionIntro } from "@/lib/sectionIntro";
import { DraftBanner } from "@/components/preview/DraftBanner";
import { ListHero } from "@/components/list/ListHero";
import { ContactForm } from "@/components/home/ContactForm";

/**
 * Frame of the Case Studies listing. The hero and the contact form live here rather than in the
 * page because a layout is not re-rendered when only the query string changes: paging and
 * filtering swap just the results while the hero stays put. It also means the page's own first
 * element is the results section, so Next scrolls there instead of back to the top of the page.
 * (The route group keeps this off the case study detail pages beside it.)
 */
export default async function CaseStudiesListLayout({
  children,
}: {
  children: ReactNode;
}) {
  const { content, isPreview } = await loadContent();
  return (
    <>
      {isPreview && <DraftBanner />}
      <ListHero
        crumbs={[
          { label: "Insights", href: "/insights" },
          { label: "Case Studies" },
        ]}
        title={"Case\nStudies"}
      />
      {children}
      <ContactForm intro={sectionIntro(content, "contact")} />
    </>
  );
}
