import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { setRequestLocale } from "next-intl/server";
import { AboutPage as AboutPageView } from "@cms/content-renderer";
import { CustomJsonLd } from "@/lib/seo/jsonld";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { getAboutPage } from "@/lib/cms/about";
import { siteSlots } from "@/components/content/siteSlots";

export async function generateMetadata(props: PageProps<"/[locale]/about">): Promise<Metadata> {
  const { locale } = await props.params;
  const page = await getAboutPage();
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

  const page = await getAboutPage();
  if (!page) notFound();

  return (
    <>
      <CustomJsonLd text={page.seoJsonLd} />
      <AboutPageView page={page} slots={siteSlots} />
    </>
  );
}
