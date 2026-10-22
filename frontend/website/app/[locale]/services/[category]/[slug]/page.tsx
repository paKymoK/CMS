import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { setRequestLocale } from "next-intl/server";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { getServicePage } from "@/lib/cms/services";
import { getHomeContent } from "@/lib/cms/homeContent";
import { sectionIntro } from "@/lib/sectionIntro";
import { ServiceHero } from "@/components/service/ServiceHero";
import { ServiceCapabilities } from "@/components/service/ServiceCapabilities";
import { ServiceProcess } from "@/components/service/ServiceProcess";
import { ServiceReasons } from "@/components/service/ServiceReasons";
import { ServiceIndustries } from "@/components/service/ServiceIndustries";
import { ServiceHighlights } from "@/components/service/ServiceHighlights";
import { IndustryStories } from "@/components/industry/IndustryStories";
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
  const d = page.data;
  const hidden = new Set(d.hiddenSections ?? []);
  const first = page.caseStudies[0];
  const stories = page.caseStudies.map((c, i) => ({
    tag: c.category,
    title: c.title,
    body: c.summary,
    image: c.image,
    linkLabel: "Read the case study →",
    linkUrl: `/case-studies/${c.slug}`,
    ...(i === 0 ? { heading: d.storiesHeading, metrics: first?.results } : {}),
  }));

  return (
    <>
      <ServiceHero page={d} categoryName={page.category.name} />
      {!hidden.has("capabilities") && <ServiceCapabilities page={d} />}
      {!hidden.has("process") && <ServiceProcess page={d} />}
      {!hidden.has("stories") && stories.length > 0 && (
        <IndustryStories
          variant="svc-stories"
          stories={stories}
          intro={sectionIntro(content, "service-stories")}
        />
      )}
      {!hidden.has("reasons") && <ServiceReasons page={d} />}
      {!hidden.has("industries") && (
        <ServiceIndustries
          cards={content.sections?.industry_card ?? []}
          intro={sectionIntro(content, "service-industries")}
        />
      )}
      {!hidden.has("highlights") && (
        <ServiceHighlights
          insights={content.insights}
          intro={sectionIntro(content, "service-highlights")}
        />
      )}
      <ContactForm intro={sectionIntro(content, "contact")} />
    </>
  );
}
