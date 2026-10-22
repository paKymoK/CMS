import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { setRequestLocale } from "next-intl/server";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { getCaseStudyPage } from "@/lib/cms/caseStudies";
import { getSearchResolution } from "@/lib/cms/search";
import { RESULTS_ID, caseStudiesHref } from "@/lib/cms/caseStudyNav";
import { CaseStudyFilterBar } from "@/components/case-studies/CaseStudyFilterBar";
import { CaseStudyListCard } from "@/components/case-studies/CaseStudyListCard";
import { ListPager } from "@/components/list/ListPager";
import { SearchNotice } from "@/components/list/SearchNotice";
import {
  PendingFade,
  ResultsPendingProvider,
} from "@/components/list/ResultsPending";

const TITLE = "Case Studies — CMC Global";
const DESCRIPTION =
  "How CMC Global engineers help clients modernize, automate and grow — real products, real challenges, real results.";

export async function generateMetadata(
  props: PageProps<"/[locale]/case-studies">,
): Promise<Metadata> {
  const { locale } = await props.params;
  return buildPageMetadata({
    locale,
    path: "case-studies",
    title: TITLE,
    description: DESCRIPTION,
  });
}

const one = (v: string | string[] | undefined) => (Array.isArray(v) ? v[0] : v);

export default async function CaseStudiesPage(
  props: PageProps<"/[locale]/case-studies">,
) {
  const { locale } = await props.params;
  if (locale !== "en") notFound();
  setRequestLocale(locale);

  const sp = await props.searchParams;
  const category = one(sp.category)?.trim() || undefined;
  const q = one(sp.q)?.trim() || undefined;
  // "Search instead for what I typed": no spelling correction (set by the notice's link).
  const exact = q ? one(sp.exact) === "1" : false;
  const requested = Number.parseInt(one(sp.page) ?? "1", 10);

  const [result, resolution] = await Promise.all([
    getCaseStudyPage({
      page: Number.isFinite(requested) ? requested : 1,
      category,
      q,
      exact,
    }),
    q && !exact ? getSearchResolution({ q, type: "case_study" }) : null,
  ]);

  return (
    <section
      id={RESULTS_ID}
      aria-label="Case studies"
      className="design-canvas"
    >
      <div className="csl-body">
        <ResultsPendingProvider>
          <CaseStudyFilterBar
            categories={result.categories}
            category={category}
            q={q}
            exact={exact}
          />
          <hr className="csl-rule" />
          <PendingFade>
            <p className="csl-count" aria-live="polite">
              Showing {result.totalItems}{" "}
              {result.totalItems === 1 ? "result" : "results"}
            </p>
            <SearchNotice
              resolution={resolution}
              exactHref={caseStudiesHref({ category, q, exact: true })}
              suggestedHref={caseStudiesHref({
                category,
                q: resolution?.didYouMean ?? q,
              })}
            />
            {result.items.length === 0 ? (
              <p className="csl-empty">
                No case studies match — try another filter.
              </p>
            ) : (
              <div className="csl-grid">
                {result.items.map((study) => (
                  <CaseStudyListCard key={study.id} study={study} />
                ))}
              </div>
            )}
            <ListPager
              page={result.page}
              totalPages={result.totalPages}
              label="Case studies pages"
              href={(p) => caseStudiesHref({ page: p, category, q })}
            />
          </PendingFade>
        </ResultsPendingProvider>
      </div>
    </section>
  );
}
