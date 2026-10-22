import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { setRequestLocale } from "next-intl/server";
import { IndustryPage as IndustryPageView } from "@cms/content-renderer";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { getIndustryPage } from "@/lib/cms/industries";
import { getHomeContent } from "@/lib/cms/homeContent";
import { sectionIntro } from "@/lib/sectionIntro";
import { siteSlots } from "@/components/content/siteSlots";
import { InsightsAccordion } from "@/components/home/InsightsAccordion";
import { ContactForm } from "@/components/home/ContactForm";

export async function generateMetadata(
  props: PageProps<"/[locale]/industries/[slug]">,
): Promise<Metadata> {
  const { locale, slug } = await props.params;
  const page = await getIndustryPage(slug);
  const path = `industries/${slug}`;
  if (!page) {
    return buildPageMetadata({ locale, path, title: "Industries — CMC Global", description: "", noIndex: true });
  }
  return buildPageMetadata({
    locale,
    path,
    title: page.seoTitle || `${page.navLabel} | CMC Global`,
    description: page.seoDescription || page.heroSubtitle || "",
    ogImage: page.seoImage || page.heroImage,
    noIndex: page.seoNoindex,
  });
}

export default async function IndustryPage(props: PageProps<"/[locale]/industries/[slug]">) {
  const { locale, slug } = await props.params;
  if (locale !== "en") notFound();
  setRequestLocale(locale);

  const page = await getIndustryPage(slug);
  if (!page) notFound();

  const content = await getHomeContent();

  return (
    <IndustryPageView
      page={page}
      slots={siteSlots}
      insights={
        <InsightsAccordion
          insights={content.insights}
          intro={
            page.insightsTitle
              ? {
                  section: "industry-insights",
                  eyebrow: page.insightsEyebrow,
                  title: page.insightsTitle,
                  body: page.insightsBody,
                  linkLabel: page.insightsLinkLabel,
                  linkUrl: page.insightsLinkUrl,
                }
              : undefined
          }
        />
      }
      contact={<ContactForm intro={sectionIntro(content, "contact")} />}
    />
  );
}
