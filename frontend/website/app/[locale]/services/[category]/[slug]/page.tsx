import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { setRequestLocale } from "next-intl/server";
import { ServicePage as ServicePageView } from "@cms/content-renderer";
import { CustomJsonLd } from "@/lib/seo/jsonld";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { getServicePage } from "@/lib/cms/services";
import { redirectIfRenamed } from "@/lib/cms/slugRedirect";
import { getHomeContent } from "@/lib/cms/homeContent";
import { sectionIntro } from "@/lib/sectionIntro";
import { pageBlockContext } from "@/lib/cms/pageBlockContext";
import { siteSlots } from "@/components/content/siteSlots";
import { ContactForm } from "@/components/home/ContactForm";

export async function generateMetadata(
  props: PageProps<"/[locale]/services/[category]/[slug]">,
): Promise<Metadata> {
  const { locale, category, slug } = await props.params;
  const page = await getServicePage(category, slug);
  const path = `services/${category}/${slug}`;
  if (!page) {
    return buildPageMetadata({ locale, path, title: "Services — CMC Global", description: "", noIndex: true });
  }
  const d = page.data;
  return buildPageMetadata({
    locale,
    path,
    title: d.seoTitle || `${d.navLabel} | CMC Global`,
    description: d.seoDescription || d.summary || d.heroSubtitle || "",
    ogImage: d.seoImage || d.featuredImage || d.heroImage,
    noIndex: d.seoNoindex,
    seo: d,
  });
}

export default async function ServicePage(props: PageProps<"/[locale]/services/[category]/[slug]">) {
  const { locale, category, slug } = await props.params;
  if (locale !== "en") notFound();
  setRequestLocale(locale);

  const page = await getServicePage(category, slug);
  if (!page) {
    await redirectIfRenamed(locale, "services", "services", [category, slug]);
    notFound();
  }

  const content = await getHomeContent();

  return (
    <>
      <CustomJsonLd text={page.data.seoJsonLd} />
      <ServicePageView
        page={page.data}
        categoryName={page.category.name}
        slots={siteSlots}
        context={pageBlockContext(content, page.caseStudies)}
        contact={<ContactForm intro={sectionIntro(content, "contact")} />}
      />
    </>
  );
}
