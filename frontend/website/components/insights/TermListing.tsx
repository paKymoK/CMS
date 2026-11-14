import { notFound } from "next/navigation";
import { getTerm, type TaxonomyKind } from "@/lib/cms/taxonomy";
import { getHomeContent } from "@/lib/cms/homeContent";
import { sectionIntro } from "@/lib/sectionIntro";
import { ListHero } from "@/components/list/ListHero";
import { ListPager } from "@/components/list/ListPager";
import { ContactForm } from "@/components/home/ContactForm";
import { PerspectiveCard } from "./PerspectiveCard";

const PAGE_SIZE = 6;

/**
 * /category/<slug> and /tag/<slug>: the published posts that carry one category or tag, newest first,
 * on the same cards as the Insights listing. An Inactive or unknown one is a 404.
 */
export async function TermListing({
  kind,
  slug,
  page: requested,
}: {
  kind: TaxonomyKind;
  slug: string;
  page: number;
}) {
  const found = await getTerm(kind, slug);
  if (!found) notFound();
  const { term, posts } = found;
  const content = await getHomeContent();

  const time = (d: string) => Date.parse(d) || 0;
  const sorted = [...posts].sort((a, b) => time(b.date) - time(a.date));
  const totalPages = Math.max(1, Math.ceil(sorted.length / PAGE_SIZE));
  const page = Math.min(Math.max(1, Number.isFinite(requested) ? requested : 1), totalPages);
  const items = sorted.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE);
  const eyebrow = kind === "category" ? "Category" : "Tag";

  return (
    <>
      <ListHero
        crumbs={[{ label: "Insights" }, ...(term.parentName ? [{ label: term.parentName }] : []), { label: term.name }]}
        title={term.name}
        subtitle={term.description || `Published insights ${kind === "category" ? "in the category" : "tagged"} “${term.name}”.`}
      />
      <section aria-label={`${eyebrow}: ${term.name}`} className="design-canvas">
        <div className="csl-body psp-body">
          <p className="csl-count" aria-live="polite">
            {sorted.length} {sorted.length === 1 ? "result" : "results"}
          </p>
          {items.length === 0 ? (
            <p className="csl-empty">Nothing has been published here yet.</p>
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
            label={`${term.name} pages`}
            href={(p) => `/${kind}/${term.slug}${p > 1 ? `?page=${p}` : ""}`}
          />
        </div>
      </section>
      <ContactForm intro={sectionIntro(content, "contact")} />
    </>
  );
}
