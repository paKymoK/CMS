import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { setRequestLocale } from "next-intl/server";
import { IndustryPage as IndustryPageView } from "@cms/content-renderer";
import { CustomJsonLd } from "@/lib/seo/jsonld";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { getIndustryPage } from "@/lib/cms/industries";
import { redirectIfRenamed } from "@/lib/cms/slugRedirect";
import { getHomeContent } from "@/lib/cms/homeContent";
import { sectionIntro } from "@/lib/sectionIntro";
import { siteSlots } from "@/components/content/siteSlots";
import { pageBlockContext } from "@/lib/cms/pageBlockContext";
import { ContactForm } from "@/components/home/ContactForm";

export async function generateMetadata(
  props: PageProps<"/[locale]/industries/[slug]">,
): Promise<Metadata> {
  const { locale, slug } = await props.params;
  const found = await getIndustryPage(slug);
  const page = found?.data;
  const path = `industries/${slug}`;
  if (!page) {
    return buildPageMetadata({ locale, path, title: "Industries — CMC Global", description: "", noIndex: true });
  }
  return buildPageMetadata({
    locale,
    path,
    title: page.seoTitle || `${page.navLabel} | CMC Global`,
    description: page.seoDescription || page.summary || page.heroSubtitle || "",
    ogImage: page.seoImage || page.featuredImage || page.heroImage,
    noIndex: page.seoNoindex,
    seo: page,
  });
}

export default async function IndustryPage(props: PageProps<"/[locale]/industries/[slug]">) {
  const { locale, slug } = await props.params;
  if (locale !== "en") notFound();
  setRequestLocale(locale);

  const found = await getIndustryPage(slug);
  if (!found) {
    await redirectIfRenamed(locale, "industries", "industries", [slug]);
    notFound();
  }

  const content = await getHomeContent();
  const page = found.data;

  return (
    <>
      <CustomJsonLd text={page.seoJsonLd} />
      <IndustryPageView
        page={page}
        slots={siteSlots}
        context={pageBlockContext(content, found.caseStudies)}
        contact={<ContactForm intro={sectionIntro(content, "contact")} />}
      />
    </>
  );
}
