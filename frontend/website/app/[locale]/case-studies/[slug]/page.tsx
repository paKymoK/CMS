import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { setRequestLocale } from "next-intl/server";
import { getTranslations } from "next-intl/server";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { getCaseStudy } from "@/lib/cms/caseStudies";
import { CaseStudyArticle } from "@cms/content-renderer";
import { siteSlots } from "@/components/content/siteSlots";

export async function generateMetadata(
  props: PageProps<"/[locale]/case-studies/[slug]">,
): Promise<Metadata> {
  const { locale, slug } = await props.params;
  const caseStudy = await getCaseStudy(slug);
  if (!caseStudy) {
    return buildPageMetadata({
      locale,
      path: `case-studies/${slug}`,
      title: "Case Studies — CMC Global",
      description: "",
      noIndex: true,
    });
  }
  return buildPageMetadata({
    locale,
    path: `case-studies/${slug}`,
    title: caseStudy.seoTitle || `${caseStudy.title} — CMC Global`,
    description: caseStudy.seoDescription || caseStudy.summary,
    ogImage: caseStudy.seoImage || caseStudy.image,
    noIndex: caseStudy.seoNoindex,
  });
}

export default async function CaseStudyDetailPage(
  props: PageProps<"/[locale]/case-studies/[slug]">,
) {
  const { locale, slug } = await props.params;

  if (locale !== "en") {
    notFound();
  }
  setRequestLocale(locale);

  const t = await getTranslations("caseStudies");
  const caseStudy = await getCaseStudy(slug);
  if (!caseStudy) {
    notFound();
  }

  return (
    <CaseStudyArticle
      caseStudy={caseStudy}
      slots={siteSlots}
      labels={{
        home: t("home"),
        eyebrow: t("eyebrow"),
        discussProject: t("discussProject"),
        eventPhotography: t("eventPhotography"),
        results: t("results"),
        moreWork: t("moreWork"),
        relatedCaseStudies: t("relatedCaseStudies"),
      }}
    />
  );
}
