import type { Metadata } from "next";
import Image from "next/image";
import { notFound } from "next/navigation";
import { setRequestLocale } from "next-intl/server";
import { getTranslations } from "next-intl/server";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { Link } from "@/i18n/navigation";
import { getPosts } from "@/lib/cms/posts";
import { Placeholder } from "@/components/ui/Placeholder";

const TITLE = "Insights — CMC Global";
const DESCRIPTION =
  "Perspectives on AI, cloud and software delivery from CMC Global engineers, advisors and partners.";

// Distinct tag values across the currently-loaded page of posts — the public API returns
// all published posts unpaged (see lib/cms/posts.ts), so this is derived client-side from
// whatever's already on the page rather than a separate "list all tags" endpoint.
function distinctTags(tags: string[][]): string[] {
  return Array.from(new Set(tags.flat())).sort();
}

function initials(name: string): string {
  return name
    .split(" ")
    .map((w) => w[0])
    .join("")
    .slice(0, 2)
    .toUpperCase();
}

export async function generateMetadata(
  props: PageProps<"/[locale]/insights">,
): Promise<Metadata> {
  const { locale } = await props.params;
  return buildPageMetadata({ locale, path: "insights", title: TITLE, description: DESCRIPTION });
}

export default async function InsightsPage(props: PageProps<"/[locale]/insights">) {
  const { locale } = await props.params;
  const searchParams = await props.searchParams;

  // Same Phase 0 gating as the homepage — see app/[locale]/page.tsx and
  // lib/seo/constants.ts's LOCALES_WITH_CONTENT.
  if (locale !== "en") {
    notFound();
  }
  setRequestLocale(locale);

  const tag = typeof searchParams.tag === "string" ? searchParams.tag : undefined;
  const q = typeof searchParams.q === "string" ? searchParams.q : undefined;
  const filtering = !!(tag || q);

  const t = await getTranslations("insights");
  const posts = await getPosts({ tag, q });
  const allTags = distinctTags(posts.map((p) => p.tags));

  const featured = !filtering ? (posts.find((p) => p.featured) ?? posts[0]) : undefined;
  const rest = featured ? posts.filter((p) => p.id !== featured.id) : posts;

  const chipHref = (nextTag?: string) => {
    const params = new URLSearchParams();
    if (nextTag) params.set("tag", nextTag);
    if (q) params.set("q", q);
    const qs = params.toString();
    return `/insights${qs ? `?${qs}` : ""}`;
  };

  return (
    <div className="font-wave-sans text-[#0f172a]">
      <section className="mx-auto max-w-[1180px] px-6 pt-[150px] pb-10">
        <div className="flex flex-wrap items-end justify-between gap-7">
          <div className="max-w-[640px]">
            <div className="font-mono-wave mb-4 text-xs tracking-[0.16em] text-brand-primary uppercase">
              — {t("eyebrow")}
            </div>
            <h1 className="font-sans text-[clamp(34px,4.6vw,56px)] leading-[1.1] font-bold tracking-[-0.025em] text-[#10314f]">
              {t("titleLead")} <span className="text-brand-primary">{t("titleHighlight")}</span>
            </h1>
            <p className="mt-4.5 text-lg leading-[1.6] text-[#55585f]">{t("subtitle")}</p>
          </div>
          <form action="/insights" className="relative w-full flex-[0_1_320px] min-w-[240px]">
            {tag && <input type="hidden" name="tag" value={tag} />}
            <svg
              viewBox="0 0 24 24"
              className="pointer-events-none absolute top-1/2 left-4 h-4 w-4 -translate-y-1/2 fill-none stroke-[#6a7c90] stroke-2"
              aria-hidden
            >
              <circle cx="11" cy="11" r="7" />
              <path d="M20 20l-3.5-3.5" strokeLinecap="round" />
            </svg>
            <input
              type="text"
              name="q"
              defaultValue={q}
              placeholder={t("searchPlaceholder")}
              className="h-12 w-full rounded-full border border-[#dfe3e8] bg-white pr-4.5 pl-[42px] text-[15px] text-[#10141c] outline-none focus:border-brand-primary"
            />
          </form>
        </div>

        <div className="mt-9 flex flex-wrap items-center gap-2 border-t border-[#e0e4e9] pt-6">
          <span className="font-mono-wave mr-1.5 text-[11px] tracking-[0.12em] text-[#5a5d64] uppercase">
            {t("topics")}
          </span>
          <Link
            href={chipHref(undefined)}
            className={`font-mono-wave rounded-full border px-3.5 py-2 text-[11px] tracking-[0.04em] transition-colors ${
              !tag ? "border-[#0a2540] bg-[#0a2540] text-white" : "border-[#dfe3e8] bg-white text-[#3d4046]"
            }`}
          >
            {t("allTopics")}
          </Link>
          {allTags.map((tg) => (
            <Link
              key={tg}
              href={chipHref(tg)}
              className={`font-mono-wave rounded-full border px-3.5 py-2 text-[11px] tracking-[0.04em] transition-colors ${
                tag === tg ? "border-[#0a2540] bg-[#0a2540] text-white" : "border-[#dfe3e8] bg-white text-[#3d4046]"
              }`}
            >
              #{tg}
            </Link>
          ))}
        </div>
      </section>

      {featured && (
        <section className="mx-auto max-w-[1180px] px-6 pt-4 pb-14">
          <Link
            href={`/insights/${featured.slug}`}
            className="grid grid-cols-[repeat(auto-fit,minmax(min(100%,400px),1fr))] overflow-hidden bg-[#0a2540] text-white"
          >
            <div className="relative min-h-[340px]">
              {featured.image ? (
                <Image src={featured.image} alt="" fill className="object-cover" sizes="(min-width: 1180px) 590px, 100vw" />
              ) : (
                <Placeholder tone="dark" label={t("eventPhotography")} className="absolute inset-0" />
              )}
              <span className="font-mono-wave absolute top-4.5 left-4.5 rounded-full bg-white px-3 py-1.5 text-[10px] tracking-[0.12em] text-[#0b63c5]">
                {t("featuredBadge")}
              </span>
            </div>
            <div className="relative flex flex-col justify-between gap-7 bg-gradient-to-b from-[#0b3a6e] to-[#0a2540] p-[clamp(28px,4vw,48px)]">
              <div className="absolute inset-x-0 top-0 h-px bg-gradient-to-r from-transparent via-[#aad4ff]/34 to-transparent" />
              <div>
                <div className="font-mono-wave text-[11px] tracking-[0.12em] text-[#c9d9ee]">
                  {featured.date} · {featured.readMinutes} {t("minRead")}
                </div>
                <h2 className="mt-4 font-sans text-[clamp(22px,2.6vw,32px)] leading-[1.25] font-bold tracking-[-0.015em] text-white text-pretty">
                  {featured.title}
                </h2>
                <p className="mt-3.5 text-base leading-[1.6] text-[#dce7f5]">{featured.excerpt}</p>
              </div>
              <div className="flex items-center justify-between gap-4">
                <div className="flex items-center gap-2.5">
                  <div className="flex h-9 w-9 items-center justify-center rounded-full bg-[radial-gradient(circle_at_34%_30%,#7cc4f7_0%,#1a6fc4_60%,#0c3f7d_100%)] text-xs font-bold text-white">
                    {initials(featured.authorName)}
                  </div>
                  <span className="text-sm font-semibold text-white">{featured.authorName}</span>
                </div>
                <span className="flex h-11 w-11 items-center justify-center rounded-full bg-white text-[#0b63c5]">
                  <svg viewBox="0 0 24 24" className="h-[18px] w-[18px] fill-none stroke-current stroke-2" aria-hidden>
                    <path d="M5 12h14M13 6l6 6-6 6" strokeLinecap="round" strokeLinejoin="round" />
                  </svg>
                </span>
              </div>
            </div>
          </Link>
        </section>
      )}

      <section className="mx-auto max-w-[1180px] px-6 pb-24">
        <div className="mb-6 flex items-baseline justify-between gap-4">
          <h2 className="font-sans text-[22px] font-bold text-[#10314f]">
            {filtering ? t("results") : t("latest")}
          </h2>
          <span className="font-mono-wave text-[11px] tracking-[0.08em] text-[#6a7c90]">
            {rest.length} {rest.length === 1 ? t("article") : t("articles")}
          </span>
        </div>

        {rest.length === 0 ? (
          <div className="border border-[#e0e4e9] bg-[#f6f9fc] px-6 py-16 text-center">
            <div className="font-mono-wave text-[11px] tracking-[0.16em] text-[#5a5d64] uppercase">
              — {t("noMatches")}
            </div>
            <p className="mt-2.5 mb-4.5 text-[15px] text-[#55585f]">{t("noMatchesBody")}</p>
            <Link
              href="/insights"
              className="font-mono-wave inline-block border border-brand-primary/40 bg-white px-4 py-2.5 text-[11px] tracking-[0.08em] text-brand-primary"
            >
              {t("clearFilters")}
            </Link>
          </div>
        ) : (
          <div className="grid grid-cols-[repeat(auto-fill,minmax(280px,1fr))] gap-x-[22px] gap-y-10">
            {rest.map((p) => (
              <Link
                key={p.id}
                href={`/insights/${p.slug}`}
                className="group flex flex-col gap-3.5 text-[#10141c] transition-transform duration-300 ease-out hover:-translate-y-1.5"
              >
                <div className="relative overflow-hidden">
                  {p.image ? (
                    <div className="relative aspect-4/3 w-full">
                      <Image src={p.image} alt="" fill className="object-cover" sizes="(min-width: 1180px) 280px, 45vw" />
                    </div>
                  ) : (
                    <Placeholder tone="navy" label={t("eventPhotography")} className="aspect-4/3 w-full" />
                  )}
                </div>
                <div className="flex flex-wrap gap-1.5">
                  {p.tags.map((tg) => (
                    <span
                      key={tg}
                      className="font-mono-wave rounded-full bg-[#eaf4fb] px-2.5 py-1 text-[10px] tracking-[0.04em] text-[#0b63c5]"
                    >
                      #{tg}
                    </span>
                  ))}
                </div>
                <span className="text-lg leading-[1.4] font-bold text-[#10141c] text-pretty">{p.title}</span>
                <span className="text-[15px] leading-[1.55] text-[#55585f]">{p.excerpt}</span>
                <span className="font-mono-wave mt-auto text-[11px] tracking-[0.06em] text-[#5a5d64]">
                  {p.date} · {p.readMinutes} {t("min")} · {p.authorName}
                </span>
              </Link>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
