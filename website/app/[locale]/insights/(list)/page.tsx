import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { setRequestLocale } from "next-intl/server";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { getPosts } from "@/lib/cms/posts";
import { loadContent } from "@/lib/cms/loadContent";
import { sectionIntro } from "@/lib/sectionIntro";
import { DraftBanner } from "@/components/preview/DraftBanner";
import { InsightsHero } from "@/components/insights/InsightsHero";
import { InsightsBrowser } from "@/components/insights/InsightsBrowser";
import { ContactForm } from "@/components/home/ContactForm";

const TITLE = "Insights — CMC Global";
const DESCRIPTION =
  "Perspectives on AI, cloud and software delivery from CMC Global engineers, advisors and partners.";

export async function generateMetadata(
  props: PageProps<"/[locale]/insights">,
): Promise<Metadata> {
  const { locale } = await props.params;
  return buildPageMetadata({ locale, path: "insights", title: TITLE, description: DESCRIPTION });
}

export default async function InsightsPage(props: PageProps<"/[locale]/insights">) {
  const { locale } = await props.params;
  if (locale !== "en") notFound();
  setRequestLocale(locale);

  const [posts, { content, isPreview }] = await Promise.all([getPosts(), loadContent()]);
  const hero = sectionIntro(content, "insights-page");

  return (
    <>
      {isPreview && <DraftBanner />}
      <InsightsHero
        eyebrow={hero?.eyebrow ?? "Insights"}
        title={hero?.title ?? "Perspectives"}
        image={hero?.image}
      />
      <InsightsBrowser posts={posts} />
      <ContactForm intro={sectionIntro(content, "contact")} />
    </>
  );
}
