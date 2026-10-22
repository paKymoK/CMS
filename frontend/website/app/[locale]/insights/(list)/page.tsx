import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { setRequestLocale } from "next-intl/server";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { getPosts } from "@/lib/cms/posts";
import { getSearchResolution } from "@/lib/cms/search";
import {
  PERSPECTIVES_RESULTS_ID,
  perspectivesHref,
} from "@/lib/cms/perspectivesNav";
import { PerspectiveCard } from "@/components/insights/PerspectiveCard";
import { PerspectivesFilterBar } from "@/components/insights/PerspectivesFilterBar";
import { PerspectivesSort } from "@/components/insights/PerspectivesSort";
import { ListPager } from "@/components/list/ListPager";
import { SearchNotice } from "@/components/list/SearchNotice";
import {
  PendingFade,
  ResultsPendingProvider,
} from "@/components/list/ResultsPending";

const TITLE = "Insights — CMC Global";
const DESCRIPTION =
  "Perspectives on AI, cloud and software delivery from CMC Global engineers, advisors and partners.";
const PAGE_SIZE = 6;

export async function generateMetadata(
  props: PageProps<"/[locale]/insights">,
): Promise<Metadata> {
  const { locale } = await props.params;
  return buildPageMetadata({
    locale,
    path: "insights",
    title: TITLE,
    description: DESCRIPTION,
  });
}

const one = (v: string | string[] | undefined) => (Array.isArray(v) ? v[0] : v);
const time = (date: string) => Date.parse(date) || 0;

export default async function PerspectivesPage(
  props: PageProps<"/[locale]/insights">,
) {
  const { locale } = await props.params;
  if (locale !== "en") notFound();
  setRequestLocale(locale);

  const sp = await props.searchParams;
  const category = one(sp.category)?.trim() || undefined;
  const tag = one(sp.tag)?.trim() || undefined;
  const q = one(sp.q)?.trim() || undefined;
  // "Search instead for what I typed": no spelling correction (set by the notice's link).
  const exact = q ? one(sp.exact) === "1" : false;
  const sort = one(sp.sort) === "oldest" ? ("oldest" as const) : undefined;
  const requested = Number.parseInt(one(sp.page) ?? "1", 10);

  // Tag and text search are filtered by content-service; the content type, order and paging are
  // applied here — the set is small and the facets (below) come from the unfiltered list anyway.
  const [all, found, resolution] = await Promise.all([
    getPosts(),
    getPosts({ tag, q, exact }),
    q && !exact ? getSearchResolution({ q, type: "post" }) : null,
  ]);
  const categories = Array.from(
    new Set(all.map((p) => p.category).filter(Boolean)),
  ).sort();
  const tags = Array.from(new Set(all.flatMap((p) => p.tags))).sort();

  const matches = found
    .filter((p) => !category || p.category === category)
    .sort((a, b) =>
      sort ? time(a.date) - time(b.date) : time(b.date) - time(a.date),
    );
  const totalPages = Math.max(1, Math.ceil(matches.length / PAGE_SIZE));
  const page = Math.min(
    Math.max(1, Number.isFinite(requested) ? requested : 1),
    totalPages,
  );
  const items = matches.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE);
  const current = { category, tag, q, exact, sort };

  return (
    <section
      id={PERSPECTIVES_RESULTS_ID}
      aria-label="Perspectives"
      className="design-canvas"
    >
      <div className="csl-body psp-body">
        <ResultsPendingProvider>
          <PerspectivesFilterBar
            categories={categories}
            tags={tags}
            current={current}
          />
          <hr className="csl-rule" />
          <PendingFade>
            <div className="psp-bar">
              <p className="csl-count" aria-live="polite">
                Showing {matches.length}{" "}
                {matches.length === 1 ? "result" : "results"}
              </p>
              <PerspectivesSort current={current} />
            </div>
            <SearchNotice
              resolution={resolution}
              exactHref={perspectivesHref({ ...current, exact: true })}
              suggestedHref={perspectivesHref({
                ...current,
                q: resolution?.didYouMean ?? q,
                exact: undefined,
              })}
            />
            {items.length === 0 ? (
              <p className="csl-empty">
                No perspectives match — try another filter.
              </p>
            ) : (
              <ul className="psp-grid">
                {items.map((post) => (
                  <PerspectiveCard key={post.id} post={post} />
                ))}
              </ul>
            )}
            <ListPager
              page={page}
              totalPages={totalPages}
              label="Perspectives pages"
              href={(p) => perspectivesHref({ ...current, page: p })}
            />
          </PendingFade>
        </ResultsPendingProvider>
      </div>
    </section>
  );
}
