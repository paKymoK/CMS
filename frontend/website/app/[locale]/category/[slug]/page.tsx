import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { setRequestLocale } from "next-intl/server";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { getTerm } from "@/lib/cms/taxonomy";
import { TermListing } from "@/components/insights/TermListing";

export async function generateMetadata(props: PageProps<"/[locale]/category/[slug]">): Promise<Metadata> {
  const { locale, slug } = await props.params;
  const found = await getTerm("category", slug);
  if (!found) {
    return buildPageMetadata({ locale, path: `category/${slug}`, title: "Insights — CMC Global", description: "", noIndex: true });
  }
  return buildPageMetadata({
    locale,
    path: `category/${slug}`,
    title: `${found.term.name} — CMC Global Insights`,
    description: found.term.description || `Published insights in the category “${found.term.name}”.`,
  });
}

export default async function CategoryPage(props: PageProps<"/[locale]/category/[slug]">) {
  const { locale, slug } = await props.params;
  if (locale !== "en") notFound();
  setRequestLocale(locale);
  const sp = await props.searchParams;
  const raw = Array.isArray(sp.page) ? sp.page[0] : sp.page;
  return <TermListing kind="category" slug={slug} page={Number.parseInt(raw ?? "1", 10)} />;
}
