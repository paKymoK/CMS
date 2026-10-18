import type { ReactNode } from "react";
import { loadContent } from "@/lib/cms/loadContent";
import { sectionIntro } from "@/lib/sectionIntro";
import { DraftBanner } from "@/components/preview/DraftBanner";
import { ListHero } from "@/components/list/ListHero";
import { ContactForm } from "@/components/home/ContactForm";

/**
 * Frame of the Perspectives (Insights) listing. Like the Case Studies listing, the hero and the
 * contact form live in a layout so that paging, filtering and searching — which only change the
 * query string — swap just the results while the hero stays put, and the page's own first element
 * is the results section. (The route group keeps this off the article pages beside it.)
 */
export default async function PerspectivesListLayout({
  children,
}: {
  children: ReactNode;
}) {
  const { content, isPreview } = await loadContent();
  const hero = sectionIntro(content, "insights-page");
  return (
    <>
      {isPreview && <DraftBanner />}
      <ListHero
        crumbs={[
          { label: hero?.eyebrow ?? "Insights" },
          { label: hero?.title ?? "Perspectives" },
        ]}
        title={hero?.title ?? "Perspectives"}
        subtitle={
          hero?.body ??
          "Explore our latest thought leadership, research and resources to help you navigate what's next."
        }
      />
      {children}
      <ContactForm intro={sectionIntro(content, "contact")} />
    </>
  );
}
