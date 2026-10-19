import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { setRequestLocale } from "next-intl/server";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { JsonLd, organizationJsonLd } from "@/lib/seo/jsonld";
import { loadContent } from "@/lib/cms/loadContent";
import { sectionIntro } from "@/lib/sectionIntro";
import { DraftBanner } from "@/components/preview/DraftBanner";
import { HeroCarousel } from "@/components/home/HeroCarousel";
import { AssistantBand } from "@/components/home/AssistantBand";
import { WhatWeDo } from "@/components/home/WhatWeDo";
import { Testimonials } from "@/components/home/Testimonials";
import { GlobalDelivery } from "@/components/home/GlobalDelivery";
import { ExtendedWorkforce } from "@/components/home/ExtendedWorkforce";
import { CaseStudies } from "@/components/home/CaseStudies";
import { InsightsAccordion } from "@/components/home/InsightsAccordion";
import { Recognition } from "@/components/home/Recognition";
import { ContactForm } from "@/components/home/ContactForm";

const TITLE = "CMC Global - Leading software development company since 1993";
const DESCRIPTION =
  "CMC Global is a member of CMC Corporation with an aspiration to bring ICT products, solutions, and services of Vietnam to the international market. We are proud to be the leading company in Vietnam in providing a wide variety of comprehensive IT Solutions & Services";

export async function generateMetadata(
  props: PageProps<"/[locale]">,
): Promise<Metadata> {
  const { locale } = await props.params;

  return buildPageMetadata({
    locale,
    path: "",
    title: TITLE,
    description: DESCRIPTION,
  });
}

export default async function HomePage(props: PageProps<"/[locale]">) {
  const { locale } = await props.params;

  // Only the English home page is built so far.
  if (locale !== "en") {
    notFound();
  }

  setRequestLocale(locale);

  const { content, isPreview } = await loadContent();

  return (
    <>
      {isPreview && <DraftBanner />}
      <JsonLd data={organizationJsonLd()} />
      <HeroCarousel slides={content.sections?.hero_slide ?? []} />
      {content.sections?.assistant_band?.[0] && (
        <AssistantBand band={content.sections.assistant_band[0]} />
      )}
      <WhatWeDo services={content.services} intro={sectionIntro(content, "whatwedo")} />
      <Testimonials
        testimonials={content.testimonials}
        intro={sectionIntro(content, "testimonials")}
      />
      <GlobalDelivery
        stats={content.stats}
        offices={content.offices}
        intro={sectionIntro(content, "global")}
      />
      <ExtendedWorkforce band={content.sections?.feature_band?.[0]} />
      <CaseStudies caseStudies={content.caseStudies} intro={sectionIntro(content, "cases")} />
      <InsightsAccordion insights={content.insights} intro={sectionIntro(content, "insights")} />
      <Recognition
        awards={content.awards}
        certifications={content.certifications}
        partners={content.partners}
        intro={sectionIntro(content, "recognition")}
      />
      <ContactForm intro={sectionIntro(content, "contact")} />
    </>
  );
}
