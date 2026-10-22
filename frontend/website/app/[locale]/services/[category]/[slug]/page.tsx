import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { setRequestLocale } from "next-intl/server";
import { ServicePage as ServicePageView } from "@cms/content-renderer";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { getServicePage } from "@/lib/cms/services";
import { getHomeContent } from "@/lib/cms/homeContent";
import { sectionIntro } from "@/lib/sectionIntro";
import { ServiceIndustries } from "@/components/service/ServiceIndustries";
import { ServiceHighlights } from "@/components/service/ServiceHighlights";
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
    description: d.seoDescription || d.heroSubtitle || "",
    ogImage: d.heroImage,
  });
}

export default async function ServicePage(props: PageProps<"/[locale]/services/[category]/[slug]">) {
  const { locale, category, slug } = await props.params;
  if (locale !== "en") notFound();
  setRequestLocale(locale);

  const page = await getServicePage(category, slug);
  if (!page) notFound();

  const content = await getHomeContent();
  const first = page.caseStudies[0];
  const stories = page.caseStudies.map((c, i) => ({
    tag: c.category,
    title: c.title,
    body: c.summary,
    image: c.image,
    linkLabel: "Read the case study →",
    linkUrl: `/case-studies/${c.slug}`,
    ...(i === 0 ? { heading: page.data.storiesHeading, metrics: first?.results } : {}),
  }));

  return (
    <ServicePageView
      page={page.data}
      categoryName={page.category.name}
      stories={stories}
      storiesIntro={sectionIntro(content, "service-stories")}
      slots={siteSlots}
      industries={
        <ServiceIndustries
          cards={content.sections?.industry_card ?? []}
          intro={sectionIntro(content, "service-industries")}
        />
      }
      highlights={<ServiceHighlights insights={content.insights} intro={sectionIntro(content, "service-highlights")} />}
      contact={<ContactForm intro={sectionIntro(content, "contact")} />}
    />
  );
}
