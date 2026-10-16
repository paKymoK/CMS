import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { setRequestLocale } from "next-intl/server";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { getCaseStudyPage } from "@/lib/cms/caseStudies";
import { loadContent } from "@/lib/cms/loadContent";
import { sectionIntro } from "@/lib/sectionIntro";
import { DraftBanner } from "@/components/preview/DraftBanner";
import { CaseStudiesHero } from "@/components/case-studies/CaseStudiesHero";
import { CaseStudyFilterBar } from "@/components/case-studies/CaseStudyFilterBar";
import { CaseStudyListCard } from "@/components/case-studies/CaseStudyListCard";
import { CaseStudyPager } from "@/components/case-studies/CaseStudyPager";
import { ContactForm } from "@/components/home/ContactForm";

const TITLE = "Case Studies — CMC Global";
const DESCRIPTION =
  "How CMC Global engineers help clients modernize, automate and grow — real products, real challenges, real results.";

export async function generateMetadata(
  props: PageProps<"/[locale]/case-studies">,
): Promise<Metadata> {
  const { locale } = await props.params;
  return buildPageMetadata({ locale, path: "case-studies", title: TITLE, description: DESCRIPTION });
}

const one = (v: string | string[] | undefined) => (Array.isArray(v) ? v[0] : v);

export default async function CaseStudiesPage(props: PageProps<"/[locale]/case-studies">) {
  const { locale } = await props.params;
  if (locale !== "en") notFound();
  setRequestLocale(locale);

  const sp = await props.searchParams;
  const category = one(sp.category)?.trim() || undefined;
  const q = one(sp.q)?.trim() || undefined;
  const requested = Number.parseInt(one(sp.page) ?? "1", 10);

  const [result, { content, isPreview }] = await Promise.all([
    getCaseStudyPage({ page: Number.isFinite(requested) ? requested : 1, category, q }),
    loadContent(),
  ]);

  return (
    <>
      {isPreview && <DraftBanner />}
      <CaseStudiesHero />
      <section aria-label="Case studies" className="design-canvas">
        <div className="csl-body">
          <CaseStudyFilterBar categories={result.categories} category={category} q={q} />
          <hr className="csl-rule" />
          <p className="csl-count" aria-live="polite">
            Showing {result.totalItems} {result.totalItems === 1 ? "result" : "results"}
          </p>
          {result.items.length === 0 ? (
            <p className="csl-empty">No case studies match — try another filter.</p>
          ) : (
            <div className="csl-grid">
              {result.items.map((study) => (
                <CaseStudyListCard key={study.id} study={study} />
              ))}
            </div>
          )}
          <CaseStudyPager page={result.page} totalPages={result.totalPages} category={category} q={q} />
        </div>
      </section>
      <ContactForm intro={sectionIntro(content, "contact")} />
    </>
  );
}
