import type { Metadata } from "next";
import Image from "next/image";
import { notFound } from "next/navigation";
import { setRequestLocale } from "next-intl/server";
import { getTranslations } from "next-intl/server";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { Link } from "@/i18n/navigation";
import { getCaseStudy, getPreviewCaseStudy } from "@/lib/cms/caseStudies";
import { getPreviewToken } from "@/lib/cms/previewSession";
import { DraftBanner } from "@/components/preview/DraftBanner";
import { Placeholder } from "@/components/ui/Placeholder";

export async function generateMetadata(
  props: PageProps<"/[locale]/case-studies/[slug]">,
): Promise<Metadata> {
  const { locale, slug } = await props.params;
  const token = await getPreviewToken();
  const caseStudy = token ? await getPreviewCaseStudy(slug, token) : await getCaseStudy(slug);
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
    title: `${caseStudy.title} — CMC Global`,
    description: caseStudy.summary,
    ogImage: caseStudy.image,
    noIndex: token !== null,
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
  const token = await getPreviewToken();
  const caseStudy = token ? await getPreviewCaseStudy(slug, token) : await getCaseStudy(slug);
  if (!caseStudy) {
    notFound();
  }

  return (
    <div className="font-wave-sans text-[#0f172a]">
      {token !== null && <DraftBanner />}
      <section className="mx-auto max-w-[1180px] px-6 pt-[150px] pb-16">
        <div className="grid grid-cols-[repeat(auto-fit,minmax(min(100%,420px),1fr))] items-center gap-[clamp(32px,5vw,64px)]">
          <div className="min-w-0">
            <nav className="font-mono-wave mb-5.5 flex flex-wrap items-center gap-2 text-[11px] tracking-[0.08em] text-[#6a7c90] uppercase">
              <Link href="/" className="text-[#6a7c90]">
                {t("home")}
              </Link>
              <span>/</span>
              <Link href="/#cases" className="text-[#6a7c90]">
                {t("eyebrow")}
              </Link>
            </nav>
            <div className="font-mono-wave mb-4 text-xs tracking-[0.16em] text-brand-primary uppercase">
              — {caseStudy.category}
            </div>
            <h1 className="font-sans text-[clamp(30px,3.8vw,48px)] leading-[1.14] font-bold tracking-[-0.02em] text-[#10314f] text-balance">
              {caseStudy.title}
            </h1>
            <p className="mt-5 max-w-[560px] text-lg leading-[1.6] text-[#55585f] text-pretty">
              {caseStudy.summary}
            </p>
            <div className="mt-7 flex flex-wrap items-center gap-2.5">
              <a
                href="#contact"
                className="inline-flex items-center gap-3 rounded-full bg-[#0a2540] py-1.5 pr-1.5 pl-6 text-sm font-semibold text-white transition-colors hover:bg-brand-primary"
              >
                {t("discussProject")}
                <span className="flex h-9 w-9 items-center justify-center rounded-full bg-white text-[#0b63c5]">
                  <svg viewBox="0 0 24 24" className="h-4 w-4 fill-none stroke-current stroke-2" aria-hidden>
                    <path d="M5 12h14M13 6l6 6-6 6" strokeLinecap="round" strokeLinejoin="round" />
                  </svg>
                </span>
              </a>
              <span className="font-mono-wave inline-flex items-center px-4 text-xs tracking-[0.04em] text-[#5a5d64]">
                {caseStudy.date}
              </span>
            </div>
          </div>
          <div className="relative min-w-0">
            {caseStudy.image ? (
              <div className="relative aspect-4/3 w-full">
                <Image src={caseStudy.image} alt="" fill className="object-cover" sizes="(min-width: 1180px) 590px, 100vw" priority />
              </div>
            ) : (
              <Placeholder tone="cool" label={t("eventPhotography")} className="aspect-4/3 w-full" />
            )}
          </div>
        </div>
      </section>

      {caseStudy.results.length > 0 && (
        <section className="relative overflow-hidden bg-gradient-to-b from-[#03091a] via-[#04102a] to-[#061634] px-6 py-[clamp(44px,6vw,72px)]">
          <div className="absolute inset-x-0 top-0 h-px bg-gradient-to-r from-transparent via-[#aad4ff]/34 to-transparent" />
          <div className="relative mx-auto max-w-[1132px]">
            <div className="mb-7 flex items-center gap-2.5">
              <span className="font-mono-wave text-xs tracking-[0.16em] text-[#c9d9ee] uppercase">
                — {t("results")}
              </span>
            </div>
            <div className="grid grid-cols-[repeat(auto-fit,minmax(200px,1fr))] gap-4.5">
              {caseStudy.results.map((r, i) => (
                <div
                  key={i}
                  className="rounded-[10px] border border-white/22 bg-gradient-to-[105deg] from-[#d6e4f3]/14 to-[#96b4d7]/6 p-6"
                >
                  <div className="text-[clamp(34px,4vw,48px)] leading-none font-bold tracking-[-0.03em] text-white">
                    {r.value}
                  </div>
                  <div className="mt-3 text-sm leading-[1.45] text-[#dce7f5]">{r.label}</div>
                </div>
              ))}
            </div>
          </div>
        </section>
      )}

      <section className="mx-auto max-w-[1180px] px-6 py-[clamp(56px,7vw,88px)]">
        <article
          className="mx-auto max-w-[700px] text-lg leading-[1.75] text-[#3d4046] [&_a]:text-brand-primary [&_figcaption]:font-mono-wave [&_figcaption]:mt-2.5 [&_figcaption]:text-[11px] [&_figcaption]:text-[#6a7c90] [&_h2]:font-sans [&_h2]:mt-13 [&_h2]:mb-2.5 [&_h2]:text-[clamp(22px,2.4vw,28px)] [&_h2]:font-bold [&_h2]:tracking-[-0.01em] [&_h2]:text-[#10314f] [&_img]:w-full [&_img]:object-cover [&_li]:flex [&_li]:gap-3 [&_p]:mb-5.5 [&_ul]:mb-5.5 [&_ul]:list-none [&_ul]:pl-0"
          dangerouslySetInnerHTML={{ __html: caseStudy.body }}
        />

        {caseStudy.testimonial && (
          <figure className="relative mx-auto mt-11 max-w-[700px] overflow-hidden bg-[#0a2540] p-8 text-white">
            <div className="absolute inset-x-0 top-0 h-px bg-gradient-to-r from-transparent via-[#aad4ff]/34 to-transparent" />
            {caseStudy.testimonial.quote && (
              <blockquote className="font-sans text-[clamp(19px,2.1vw,23px)] leading-[1.5] font-medium text-white">
                &ldquo;{caseStudy.testimonial.quote}&rdquo;
              </blockquote>
            )}
            <figcaption className="mt-5.5 flex items-center gap-3">
              {caseStudy.testimonial.photo ? (
                <div className="relative h-11 w-11 flex-none overflow-hidden rounded-full">
                  <Image src={caseStudy.testimonial.photo} alt="" fill className="object-cover" />
                </div>
              ) : (
                <div className="h-11 w-11 flex-none rounded-full bg-white/15" />
              )}
              <span className="flex flex-col">
                <span className="font-sans text-sm font-bold text-white">{caseStudy.testimonial.name}</span>
                <span className="text-[13px] text-[#c9d9ee]">
                  {caseStudy.testimonial.title}
                  {caseStudy.testimonial.company ? `, ${caseStudy.testimonial.company}` : ""}
                </span>
              </span>
            </figcaption>
          </figure>
        )}
      </section>

      {caseStudy.related.length > 0 && (
        <section className="bg-[#f6f9fc] py-[clamp(56px,7vw,96px)]">
          <div className="mx-auto max-w-[1120px] px-6">
            <div className="mb-8 flex flex-wrap items-end justify-between gap-4">
              <div>
                <div className="font-mono-wave mb-3 text-xs tracking-[0.16em] text-brand-primary uppercase">
                  — {t("moreWork")}
                </div>
                <h2 className="font-sans text-[clamp(24px,3vw,34px)] font-bold tracking-[-0.02em] text-[#10314f]">
                  {t("relatedCaseStudies")}
                </h2>
              </div>
            </div>
            <div className="grid grid-cols-[repeat(auto-fill,minmax(260px,1fr))] gap-4.5">
              {caseStudy.related.map((r) => (
                <Link
                  key={r.id}
                  href={`/case-studies/${r.slug}`}
                  className="flex flex-col border border-[#e0e4e9] bg-white text-[#10141c] transition-[transform,box-shadow] duration-300 ease-out hover:-translate-y-1.5 hover:shadow-[0_18px_44px_rgba(10,37,64,0.12)]"
                >
                  {r.image ? (
                    <div className="relative aspect-[16/10] w-full">
                      <Image src={r.image} alt="" fill className="object-cover" sizes="(min-width: 1120px) 260px, 33vw" />
                    </div>
                  ) : (
                    <Placeholder tone="warm" label="" className="aspect-[16/10] w-full" />
                  )}
                  <div className="flex flex-col gap-2.5 px-5 pt-4.5 pb-5.5">
                    <span className="font-mono-wave text-[10px] tracking-[0.1em] text-brand-primary uppercase">
                      {r.category}
                    </span>
                    <span className="text-[16.5px] leading-[1.4] font-bold text-[#10141c] text-pretty">
                      {r.title}
                    </span>
                  </div>
                </Link>
              ))}
            </div>
          </div>
        </section>
      )}
    </div>
  );
}
