import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { cookies, draftMode } from "next/headers";
import { setRequestLocale } from "next-intl/server";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { JsonLd, organizationJsonLd } from "@/lib/seo/jsonld";
import { getHomeContent, getPreviewHomeContent } from "@/lib/cms/homeContent";
import { sectionIntro } from "@/lib/sectionIntro";
import { PREVIEW_TOKEN_COOKIE } from "@/lib/cms/previewCookie";
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

  // Phase 0 only builds the English home page — see plan's i18n scope notes.
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

/**
 * Draft mode is entered exclusively through /api/preview, which validates the token before
 * enabling it — so a real visitor's request always has draftMode().isEnabled === false and this
 * always takes the getHomeContent() branch below, same as before this feature existed. The
 * missing-cookie case (draft mode on, but no cms_preview_token — e.g. it expired/was cleared out
 * from under an already-rendered page) degrades to published content with a console.error rather
 * than throwing, since this is a page render, not the explicit /api/preview entry point.
 */
async function loadContent(): Promise<{
  content: Awaited<ReturnType<typeof getHomeContent>>;
  isPreview: boolean;
}> {
  const draft = await draftMode();
  if (!draft.isEnabled) {
    return { content: await getHomeContent(), isPreview: false };
  }

  const token = (await cookies()).get(PREVIEW_TOKEN_COOKIE)?.value;
  if (!token) {
    console.error("Draft mode enabled with no preview token cookie — falling back to published content");
    return { content: await getHomeContent(), isPreview: false };
  }

  try {
    return { content: await getPreviewHomeContent(token), isPreview: true };
  } catch (err) {
    console.error("Failed to load preview content, falling back to published content", err);
    return { content: await getHomeContent(), isPreview: false };
  }
}
