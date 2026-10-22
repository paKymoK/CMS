import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { setRequestLocale } from "next-intl/server";
import { getTranslations } from "next-intl/server";
import { PostArticle } from "@cms/post-renderer";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { SITE_URL } from "@/lib/seo/constants";
import { getPost } from "@/lib/cms/posts";
import { siteSlots } from "@/components/insights/postSlots";

export async function generateMetadata(props: PageProps<"/[locale]/insights/[slug]">): Promise<Metadata> {
  const { locale, slug } = await props.params;
  const post = await getPost(slug);
  if (!post) {
    return buildPageMetadata({
      locale,
      path: `insights/${slug}`,
      title: "Insights — CMC Global",
      description: "",
      noIndex: true,
    });
  }
  return buildPageMetadata({
    locale,
    path: `insights/${slug}`,
    title: `${post.title} — CMC Global Insights`,
    description: post.excerpt ?? "",
    ogImage: post.image ?? undefined,
  });
}

export default async function PostDetailPage(props: PageProps<"/[locale]/insights/[slug]">) {
  const { locale, slug } = await props.params;

  if (locale !== "en") {
    notFound();
  }
  setRequestLocale(locale);

  const t = await getTranslations("insights");
  const post = await getPost(slug);
  if (!post) {
    notFound();
  }

  return (
    <PostArticle
      post={post}
      pageUrl={`${SITE_URL}/${locale}/insights/${slug}`}
      slots={siteSlots}
      labels={{
        eyebrow: t("eyebrow"),
        minRead: t("minRead"),
        onThisPage: t("onThisPage"),
        share: t("share"),
        keepReading: t("keepReading"),
        relatedInsights: t("relatedInsights"),
        viewAllInsights: t("viewAllInsights"),
      }}
    />
  );
}
