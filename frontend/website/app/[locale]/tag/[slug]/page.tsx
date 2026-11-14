import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { setRequestLocale } from "next-intl/server";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { getTerm } from "@/lib/cms/taxonomy";
import { TermListing } from "@/components/insights/TermListing";

export async function generateMetadata(props: PageProps<"/[locale]/tag/[slug]">): Promise<Metadata> {
  const { locale, slug } = await props.params;
  const found = await getTerm("tag", slug);
  if (!found) {
    return buildPageMetadata({ locale, path: `tag/${slug}`, title: "Insights — CMC Global", description: "", noIndex: true });
  }
  return buildPageMetadata({
    locale,
    path: `tag/${slug}`,
    title: `${found.term.name} — CMC Global Insights`,
    description: found.term.description || `Published insights tagged “${found.term.name}”.`,
  });
}

export default async function TagPage(props: PageProps<"/[locale]/tag/[slug]">) {
  const { locale, slug } = await props.params;
  if (locale !== "en") notFound();
  setRequestLocale(locale);
  const sp = await props.searchParams;
  const raw = Array.isArray(sp.page) ? sp.page[0] : sp.page;
  return <TermListing kind="tag" slug={slug} page={Number.parseInt(raw ?? "1", 10)} />;
}
