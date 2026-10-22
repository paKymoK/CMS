import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { setRequestLocale } from "next-intl/server";
import { getTranslations } from "next-intl/server";
import { buildPageMetadata } from "@/lib/seo/metadata";
import { SITE_URL } from "@/lib/seo/constants";
import { Link } from "@/i18n/navigation";
import { getPost, getPreviewPost } from "@/lib/cms/posts";
import { getPreviewToken } from "@/lib/cms/previewSession";
import { DraftBanner } from "@/components/preview/DraftBanner";
import { InsightCard } from "@/components/insights/InsightCard";
import { InsightsHero } from "@/components/insights/InsightsHero";
import { ReadingProgress } from "@/components/insights/ReadingProgress";

/**
 * Injects an id into every top-level <h2> in the (already server-sanitized by content-service on save) body HTML and
 * returns the matching table-of-contents entries — mirrors the design mockup's
 * "+ built from body H2s" note. Regex-based rather than a full DOM parse since body is
 * sanitized on write.
 */
function prepareBody(body: string): {
  html: string;
  toc: { id: string; label: string }[];
} {
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

export async function generateMetadata(props: PageProps<"/[locale]/insights/[slug]">): Promise<Metadata> {
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
    description: post.excerpt ?? "",
    ogImage: post.image ?? undefined,
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

  const { html, toc } = prepareBody(post.body ?? "");
  const layout = post.layout ?? "default";
  const landing = layout === "landing";
  const sidebar = layout === "default";
  const articleWidth = layout === "wide" ? "max-w-[1000px]" : landing ? "max-w-[1100px]" : "max-w-[700px]";
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

  const shareBlock = (
    <div>
      <div className="font-mono-wave mb-3.5 text-[11px] tracking-[0.16em] text-[#5a5d64] uppercase">— {t("share")}</div>
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
  );

  return (
    <div className="font-wave-sans text-[#0f172a]">
      {token !== null && <DraftBanner />}
      <ReadingProgress targetId="post-article" />

      {landing ? (
        <h1 className="sr-only">{post.title}</h1>
      ) : (
        <InsightsHero eyebrow={t("eyebrow")} title={post.title} image={post.image} small>
          <div className="ins-hero-meta">
            {post.authorName && (
              <span>
                <b>{post.authorName}</b>
                {post.authorRole ? `, ${post.authorRole}` : ""}
              </span>
            )}
            {post.date && <span>{post.date}</span>}
            <span>
              {post.readMinutes} {t("minRead")}
            </span>
          </div>
        </InsightsHero>
      )}

      <section className={`mx-auto flex max-w-[1180px] flex-wrap justify-center gap-[clamp(32px,6vw,80px)] px-6 pb-24 ${landing ? "pt-[130px]" : "pt-16"}`}>
        {sidebar && (
          <aside className="flex-[0_1_220px] min-w-[200px]">
            <div className="sticky top-[112px] flex flex-col gap-8.5">
              {toc.length > 0 && (
                <div>
                  <div className="font-mono-wave mb-3.5 text-[11px] tracking-[0.16em] text-[#5a5d64] uppercase">— {t("onThisPage")}</div>
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
              {shareBlock}
            </div>
          </aside>
        )}

        <article
          id="post-article"
          className={`rich-body min-w-0 ${articleWidth} flex-[1_1_560px] text-lg leading-[1.75] text-[#3d4046] [&_a]:text-brand-primary [&_blockquote]:my-9 [&_blockquote]:border-l-2 [&_blockquote]:border-brand-primary [&_blockquote]:py-7 [&_blockquote]:pl-7 [&_blockquote_p]:font-sans [&_blockquote_p]:text-[clamp(20px,2.2vw,24px)] [&_blockquote_p]:leading-[1.45] [&_blockquote_p]:font-semibold [&_blockquote_p]:text-[#10314f] [&_figcaption]:font-mono-wave [&_figcaption]:mt-2.5 [&_figcaption]:text-[11px] [&_figcaption]:text-[#6a7c90] [&_h2]:font-sans [&_h2]:mt-13 [&_h2]:mb-4 [&_h2]:scroll-mt-[110px] [&_h2]:text-[clamp(22px,2.4vw,28px)] [&_h2]:leading-[1.3] [&_h2]:font-bold [&_h2]:tracking-[-0.01em] [&_h2]:text-[#10314f] [&_h3]:font-sans [&_h3]:mt-9 [&_h3]:mb-3 [&_h3]:text-xl [&_h3]:font-bold [&_h3]:text-[#10141c] [&_img]:w-full [&_img]:object-cover [&_p]:mb-5.5`}
          dangerouslySetInnerHTML={{ __html: html }}
        />
        {!sidebar && !landing && <div className="w-full max-w-[700px]">{shareBlock}</div>}
      </section>

      {!landing && post.related.length > 0 && (
        <section className="bg-[#f6f9fc] py-[clamp(56px,7vw,96px)]">
          <div className="mx-auto max-w-[1120px] px-6">
            <div className="mb-8 flex flex-wrap items-end justify-between gap-4">
              <div>
                <div className="font-mono-wave mb-3 text-xs tracking-[0.16em] text-brand-primary uppercase">— {t("keepReading")}</div>
                <h2 className="font-sans text-[clamp(24px,3vw,34px)] font-bold tracking-[-0.02em] text-[#10314f]">{t("relatedInsights")}</h2>
              </div>
              <Link
                href="/insights"
                className="font-mono-wave rounded-sm border border-brand-primary/40 px-4 py-2.5 text-[11px] tracking-[0.08em] text-brand-primary uppercase transition-colors hover:bg-brand-primary hover:text-white"
              >
                {t("viewAllInsights")}
              </Link>
            </div>
            <div className="ins-grid ins-related">
              {post.related.map((r) => (
                <InsightCard key={r.id} post={r} />
              ))}
            </div>
          </div>
        </section>
      )}
    </div>
  );
}
