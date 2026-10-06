import type { Metadata } from "next";
import Image from "next/image";
import { notFound } from "next/navigation";
import { setRequestLocale } from "next-intl/server";
import { getTranslations } from "next-intl/server";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { SITE_URL } from "@/lib/seo/constants";
import { Link } from "@/i18n/navigation";
import { getPost, getPreviewPost } from "@/lib/cms/posts";
import { getPreviewToken } from "@/lib/cms/previewSession";
import { DraftBanner } from "@/components/preview/DraftBanner";
import { Placeholder } from "@/components/ui/Placeholder";
import { ReadingProgress } from "@/components/insights/ReadingProgress";

function initials(name: string): string {
  return name
    .split(" ")
    .map((w) => w[0])
    .join("")
    .slice(0, 2)
    .toUpperCase();
}

/**
 * Injects an id into every top-level <h2> in the (already server-sanitized) body HTML and
 * returns the matching table-of-contents entries — mirrors the design mockup's
 * "+ built from body H2s" note. Regex-based rather than a full DOM parse since body is
 * trusted CMS-authored content, not user input.
 */
function prepareBody(body: string): { html: string; toc: { id: string; label: string }[] } {
  const toc: { id: string; label: string }[] = [];
  let index = 0;
  const html = body.replace(/<h2([^>]*)>([\s\S]*?)<\/h2>/gi, (_match, attrs: string, inner: string) => {
    const label = inner.replace(/<[^>]*>/g, "").trim();
    const base = label
      .toLowerCase()
      .replace(/[^a-z0-9]+/g, "-")
      .replace(/^-+|-+$/g, "");
    const id = base ? `s-${base}` : `s-${index}`;
    index += 1;
    toc.push({ id, label });
    const cleanAttrs = attrs.replace(/\sid="[^"]*"/i, "");
    return `<h2${cleanAttrs} id="${id}">${inner}</h2>`;
  });
  return { html, toc };
}

export async function generateMetadata(
  props: PageProps<"/[locale]/insights/[slug]">,
): Promise<Metadata> {
  const { locale, slug } = await props.params;
  const token = await getPreviewToken();
  const post = token ? await getPreviewPost(slug, token) : await getPost(slug);
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
    description: post.excerpt,
    ogImage: post.image,
    noIndex: token !== null,
  });
}

export default async function PostDetailPage(props: PageProps<"/[locale]/insights/[slug]">) {
  const { locale, slug } = await props.params;

  if (locale !== "en") {
    notFound();
  }
  setRequestLocale(locale);

  const t = await getTranslations("insights");
  const token = await getPreviewToken();
  const post = token ? await getPreviewPost(slug, token) : await getPost(slug);
  if (!post) {
    notFound();
  }

  const { html, toc } = prepareBody(post.body);
  const pageUrl = `${SITE_URL}/${locale}/insights/${slug}`;
  const shareLinks = [
    {
      name: "LinkedIn",
      href: `https://www.linkedin.com/sharing/share-offsite/?url=${encodeURIComponent(pageUrl)}`,
      path: "M20.45 20.45h-3.55v-5.57c0-1.33-.02-3.04-1.85-3.04-1.86 0-2.15 1.45-2.15 2.94v5.67H9.35V9h3.41v1.56h.05c.47-.9 1.63-1.85 3.36-1.85 3.6 0 4.27 2.37 4.27 5.45v6.29ZM5.34 7.43a2.06 2.06 0 1 1 0-4.12 2.06 2.06 0 0 1 0 4.12ZM7.12 20.45H3.56V9h3.56v11.45Z",
    },
    {
      name: "Facebook",
      href: `https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(pageUrl)}`,
      path: "M22 12a10 10 0 1 0-11.56 9.88v-6.99H7.9V12h2.54V9.8c0-2.5 1.49-3.89 3.78-3.89 1.1 0 2.24.2 2.24.2v2.46h-1.26c-1.24 0-1.63.77-1.63 1.56V12h2.78l-.44 2.89h-2.34v6.99A10 10 0 0 0 22 12Z",
    },
    {
      name: "X",
      href: `https://twitter.com/intent/tweet?url=${encodeURIComponent(pageUrl)}&text=${encodeURIComponent(post.title)}`,
      path: "M17.75 3h3.07l-6.7 7.66L22 21h-6.17l-4.83-6.32L5.47 21H2.4l7.17-8.2L2 3h6.33l4.37 5.78L17.75 3Zm-1.08 16.2h1.7L7.4 4.72H5.58L16.67 19.2Z",
    },
  ];

  return (
    <div className="font-wave-sans text-[#0f172a]">
      {token !== null && <DraftBanner />}
      <ReadingProgress targetId="post-article" />

      <section className="mx-auto max-w-[1180px] px-6 pt-[150px] pb-16">
        <div className="grid grid-cols-[repeat(auto-fit,minmax(min(100%,420px),1fr))] items-center gap-[clamp(32px,5vw,64px)]">
          <div className="min-w-0">
            <nav className="font-mono-wave mb-5.5 flex flex-wrap items-center gap-2 text-[11px] tracking-[0.08em] text-[#6a7c90] uppercase">
              <Link href="/" className="text-[#6a7c90]">
                {t("home")}
              </Link>
              <span>/</span>
              <Link href="/insights" className="text-[#6a7c90]">
                {t("eyebrow")}
              </Link>
            </nav>
            <div className="font-mono-wave mb-4 text-xs tracking-[0.16em] text-brand-primary uppercase">
              — {t("insightBadge")}
            </div>
            <h1 className="font-sans text-[clamp(30px,3.8vw,46px)] leading-[1.16] font-bold tracking-[-0.02em] text-[#10314f] text-balance">
              {post.title}
            </h1>
            <p className="mt-5 max-w-[560px] text-lg leading-[1.6] text-[#55585f] text-pretty">{post.excerpt}</p>
            <div className="mt-7.5 flex flex-wrap items-center gap-4.5 border-t border-[#e0e4e9] pt-5.5">
              <div className="flex items-center gap-3">
                {post.authorAvatar ? (
                  <div className="relative h-11 w-11 flex-none overflow-hidden rounded-full">
                    <Image src={post.authorAvatar} alt="" fill className="object-cover" />
                  </div>
                ) : (
                  <div className="flex h-11 w-11 flex-none items-center justify-center rounded-full bg-[radial-gradient(circle_at_34%_30%,#7cc4f7_0%,#1a6fc4_60%,#0c3f7d_100%)] text-sm font-bold text-white">
                    {initials(post.authorName)}
                  </div>
                )}
                <div className="flex flex-col gap-0.5">
                  <span className="text-sm font-bold text-[#10141c]">{post.authorName}</span>
                  {post.authorRole && <span className="text-[13px] text-[#6a7c90]">{post.authorRole}</span>}
                </div>
              </div>
              <div className="h-7 w-px bg-[#e0e4e9]" />
              <div className="font-mono-wave flex flex-wrap items-center gap-3.5 text-xs tracking-[0.04em] text-[#5a5d64]">
                <span>{post.date}</span>
                <span className="h-1 w-1 rounded-full bg-[#cfd2d6]" />
                <span>
                  {post.readMinutes} {t("minRead")}
                </span>
              </div>
            </div>
          </div>
          <div className="relative min-w-0">
            {post.image ? (
              <div className="relative aspect-4/3 w-full">
                <Image src={post.image} alt="" fill className="object-cover" sizes="(min-width: 1180px) 590px, 100vw" priority />
              </div>
            ) : (
              <Placeholder tone="navy" label={t("eventPhotography")} className="aspect-4/3 w-full" />
            )}
          </div>
        </div>
      </section>

      <section className="mx-auto flex max-w-[1180px] flex-wrap justify-center gap-[clamp(32px,6vw,80px)] px-6 pt-6 pb-24">
        <aside className="flex-[0_1_220px] min-w-[200px]">
          <div className="sticky top-[112px] flex flex-col gap-8.5">
            {toc.length > 0 && (
              <div>
                <div className="font-mono-wave mb-3.5 text-[11px] tracking-[0.16em] text-[#5a5d64] uppercase">
                  — {t("onThisPage")}
                </div>
                <nav className="flex flex-col border-l border-[#e0e4e9]">
                  {toc.map((item) => (
                    <a
                      key={item.id}
                      href={`#${item.id}`}
                      className="-ml-px border-l-2 border-transparent py-2 pl-4 text-[13.5px] leading-[1.4] font-medium text-[#6a7c90] transition-colors hover:border-brand-primary hover:text-[#10314f]"
                    >
                      {item.label}
                    </a>
                  ))}
                </nav>
              </div>
            )}
            <div>
              <div className="font-mono-wave mb-3.5 text-[11px] tracking-[0.16em] text-[#5a5d64] uppercase">
                — {t("share")}
              </div>
              <div className="flex gap-2">
                {shareLinks.map((s) => (
                  <a
                    key={s.name}
                    href={s.href}
                    target="_blank"
                    rel="noreferrer"
                    aria-label={`Share on ${s.name}`}
                    title={`Share on ${s.name}`}
                    className="flex h-9.5 w-9.5 items-center justify-center rounded-full border border-[#cfd2d6] text-[#3d4046] transition-colors hover:border-brand-primary hover:bg-brand-primary hover:text-white"
                  >
                    <svg viewBox="0 0 24 24" className="h-[15px] w-[15px] fill-current" aria-hidden>
                      <path d={s.path} />
                    </svg>
                  </a>
                ))}
              </div>
            </div>
          </div>
        </aside>

        <article
          id="post-article"
          className="min-w-0 max-w-[700px] flex-[1_1_560px] text-lg leading-[1.75] text-[#3d4046] [&_a]:text-brand-primary [&_blockquote]:my-9 [&_blockquote]:border-l-2 [&_blockquote]:border-brand-primary [&_blockquote]:py-7 [&_blockquote]:pl-7 [&_blockquote_p]:font-sans [&_blockquote_p]:text-[clamp(20px,2.2vw,24px)] [&_blockquote_p]:leading-[1.45] [&_blockquote_p]:font-semibold [&_blockquote_p]:text-[#10314f] [&_figcaption]:font-mono-wave [&_figcaption]:mt-2.5 [&_figcaption]:text-[11px] [&_figcaption]:text-[#6a7c90] [&_h2]:font-sans [&_h2]:mt-13 [&_h2]:mb-4 [&_h2]:scroll-mt-[110px] [&_h2]:text-[clamp(22px,2.4vw,28px)] [&_h2]:leading-[1.3] [&_h2]:font-bold [&_h2]:tracking-[-0.01em] [&_h2]:text-[#10314f] [&_h3]:font-sans [&_h3]:mt-9 [&_h3]:mb-3 [&_h3]:text-xl [&_h3]:font-bold [&_h3]:text-[#10141c] [&_img]:w-full [&_img]:object-cover [&_p]:mb-5.5"
          dangerouslySetInnerHTML={{ __html: html }}
        />
      </section>

      {post.related.length > 0 && (
        <section className="bg-[#f6f9fc] py-[clamp(56px,7vw,96px)]">
          <div className="mx-auto max-w-[1120px] px-6">
            <div className="mb-8 flex flex-wrap items-end justify-between gap-4">
              <div>
                <div className="font-mono-wave mb-3 text-xs tracking-[0.16em] text-brand-primary uppercase">
                  — {t("keepReading")}
                </div>
                <h2 className="font-sans text-[clamp(24px,3vw,34px)] font-bold tracking-[-0.02em] text-[#10314f]">
                  {t("relatedInsights")}
                </h2>
              </div>
              <Link
                href="/insights"
                className="font-mono-wave rounded-sm border border-brand-primary/40 px-4 py-2.5 text-[11px] tracking-[0.08em] text-brand-primary uppercase transition-colors hover:bg-brand-primary hover:text-white"
              >
                {t("viewAllInsights")}
              </Link>
            </div>
            <div className="grid grid-cols-[repeat(auto-fill,minmax(260px,1fr))] gap-4.5">
              {post.related.map((r) => (
                <Link
                  key={r.id}
                  href={`/insights/${r.slug}`}
                  className="flex flex-col gap-3.5 text-[#10141c] transition-transform duration-300 ease-out hover:-translate-y-1.5"
                >
                  {r.image ? (
                    <div className="relative aspect-4/3 w-full">
                      <Image src={r.image} alt="" fill className="object-cover" sizes="(min-width: 1120px) 260px, 33vw" />
                    </div>
                  ) : (
                    <Placeholder tone="cool" label={t("eventPhotography")} className="aspect-4/3 w-full" />
                  )}
                  <span className="font-mono-wave text-[11px] tracking-[0.06em] text-[#5a5d64]">
                    {r.date} · {r.readMinutes} {t("min")}
                  </span>
                  <span className="text-[17px] leading-[1.4] font-bold text-[#10141c] text-pretty">{r.title}</span>
                </Link>
              ))}
            </div>
          </div>
        </section>
      )}
    </div>
  );
}
