import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { setRequestLocale } from "next-intl/server";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { getHomeContent } from "@/lib/cms/homeContent";
import { sectionIntro } from "@/lib/sectionIntro";
import { IndustryHero } from "@/components/industry/IndustryHero";
import { IndustrySolutions } from "@/components/industry/IndustrySolutions";
import { IndustryReasons } from "@/components/industry/IndustryReasons";
import { IndustryStories } from "@/components/industry/IndustryStories";
import { InsightsAccordion } from "@/components/home/InsightsAccordion";
import { ContactForm } from "@/components/home/ContactForm";

const TITLE = "Banking, Finance & Insurance | CMC Global";
const DESCRIPTION =
  "Turning financial complexity into competitive advantage through intelligent technology — core banking, security and digital transformation for banks and insurers.";

export async function generateMetadata(
  props: PageProps<"/[locale]/industries/bfsi">,
): Promise<Metadata> {
  const { locale } = await props.params;
  return buildPageMetadata({ locale, path: "industries/bfsi", title: TITLE, description: DESCRIPTION });
}

export default async function BfsiPage(props: PageProps<"/[locale]/industries/bfsi">) {
  const { locale } = await props.params;
  if (locale !== "en") notFound();
  setRequestLocale(locale);

  const content = await getHomeContent();
  const s = content.sections;
  const hero = s?.industry_hero?.[0];

  return (
    <>
      {hero && <IndustryHero hero={hero} />}
      <IndustrySolutions
        solutions={s?.industry_solution ?? []}
        intro={sectionIntro(content, "industry-solutions")}
      />
      <IndustryReasons
        reasons={s?.industry_reason ?? []}
        intro={sectionIntro(content, "industry-why")}
      />
      <IndustryStories
        stories={s?.industry_story ?? []}
        intro={sectionIntro(content, "industry-stories")}
      />
      <InsightsAccordion
        insights={content.insights}
        intro={sectionIntro(content, "industry-insights")}
      />
      <ContactForm intro={sectionIntro(content, "contact")} />
    </>
  );
}
