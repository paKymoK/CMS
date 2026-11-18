import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { setRequestLocale } from "next-intl/server";
import { AboutPage as AboutPageView } from "@cms/content-renderer";
import { CustomJsonLd } from "@/lib/seo/jsonld";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { getAboutPage } from "@/lib/cms/about";
import { getHomeContent } from "@/lib/cms/homeContent";
import { pageBlockContext } from "@/lib/cms/pageBlockContext";
import { siteSlots } from "@/components/content/siteSlots";

export async function generateMetadata(props: PageProps<"/[locale]/about">): Promise<Metadata> {
  const { locale } = await props.params;
  const found = await getAboutPage();
  const page = found?.data;
  if (!page) {
    return buildPageMetadata({ locale, path: "about", title: "About Us | CMC Global", description: "", noIndex: true });
  }
  return buildPageMetadata({
    locale,
    path: "about",
    title: page.seoTitle || "About Us | CMC Global",
    description: page.seoDescription || page.summary || page.heroSubtitle || "",
    ogImage: page.seoImage || page.featuredImage || page.heroImage,
    noIndex: page.seoNoindex,
    seo: page,
  });
}

export default async function AboutPage(props: PageProps<"/[locale]/about">) {
  const { locale } = await props.params;
  if (locale !== "en") notFound();
  setRequestLocale(locale);

  const found = await getAboutPage();
  if (!found) notFound();
  const page = found.data;
  const content = await getHomeContent();

  return (
    <>
      <CustomJsonLd text={page.seoJsonLd} />
      <AboutPageView page={page} slots={siteSlots} context={pageBlockContext(content, found.caseStudies)} />
    </>
  );
}
