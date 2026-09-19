import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { setRequestLocale } from "next-intl/server";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { JsonLd, organizationJsonLd } from "@/lib/seo/jsonld";
import { getHomeContent } from "@/lib/cms/homeContent";
import { WaveBand } from "@/components/home/WaveBand";
import { Services } from "@/components/home/Services";
import { TestimonialsCarousel } from "@/components/home/TestimonialsCarousel";
import { GlobalDelivery } from "@/components/home/GlobalDelivery";
import { WorkforceBand } from "@/components/home/WorkforceBand";
import { CaseStudies } from "@/components/home/CaseStudies";
import { BuildingTomorrowBanner } from "@/components/home/BuildingTomorrowBanner";
import { Insights } from "@/components/home/Insights";
import { Certifications } from "@/components/home/Certifications";
import { ContactForm } from "@/components/home/ContactForm";
import { BackToTop } from "@/components/ui/BackToTop";

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

  const content = await getHomeContent();

  return (
    <>
      <JsonLd data={organizationJsonLd()} />
      <WaveBand />
      <Services services={content.services} />
      <TestimonialsCarousel testimonials={content.testimonials} />
      <GlobalDelivery stats={content.stats} offices={content.offices} />
      <WorkforceBand />
      <CaseStudies caseStudies={content.caseStudies} />
      <BuildingTomorrowBanner />
      <Insights insights={content.insights} />
      <Certifications
        awards={content.awards}
        certifications={content.certifications}
        partners={content.partners}
      />
      <ContactForm />
      <BackToTop />
    </>
  );
}
