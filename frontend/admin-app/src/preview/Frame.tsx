import { useEffect, useState } from "react";
import { AboutPage, CaseStudyArticle, IndustryPage, PostArticle, ServicePage } from "@cms/content-renderer";
import { CASE_STUDY_LABELS, MSG_HEIGHT, MSG_READY, MSG_RENDER, POST_LABELS, type PreviewMessage, type PreviewPage } from "../lib/previewProtocol";

/** Stand-in for a section that reads site-wide content rather than the page being edited. */
function SiteSection({ label }: { label: string }) {
  return (
    <div className="flex h-40 items-center justify-center border-y border-dashed border-[#c5d3e8] bg-[#f4f7fc] text-sm text-[#7b8aa5]">
      {label}
    </div>
  );
}

/**
 * The document the admin's preview shows in an iframe. It holds no data of its own: the admin
 * sends the current editor state with postMessage and this draws it with the same components the
 * website uses. Being its own document, the breakpoints follow the frame's width (desktop / tablet /
 * mobile) and the website's styles never touch the admin UI.
 */
export default function Frame() {
  const [page, setPage] = useState<PreviewPage | null>(null);

  useEffect(() => {
    const onMessage = (e: MessageEvent) => {
      if (e.origin !== window.location.origin) return;
      if ((e.data as PreviewMessage | undefined)?.type === MSG_RENDER) setPage((e.data as PreviewMessage).page);
    };
    // Links in the preview must not navigate this frame away (a related-article link would load the admin SPA in it).
    const onClick = (e: MouseEvent) => {
      const a = (e.target as Element).closest("a");
      if (a && !a.getAttribute("href")?.startsWith("#")) e.preventDefault();
    };
    window.addEventListener("message", onMessage);
    document.addEventListener("click", onClick);
    // The frame never scrolls itself: it reports its content height and the admin sizes the iframe to it.
    let lastHeight = 0;
    const reportHeight = () => {
      const height = document.documentElement.scrollHeight;
      if (height === lastHeight) return;
      lastHeight = height;
      window.parent.postMessage({ type: MSG_HEIGHT, height }, window.location.origin);
    };
    const observer = new ResizeObserver(reportHeight);
    observer.observe(document.body);
    window.parent.postMessage({ type: MSG_READY }, window.location.origin);
    return () => {
      observer.disconnect();
      window.removeEventListener("message", onMessage);
      document.removeEventListener("click", onClick);
    };
  }, []);

  if (!page) return null;
  if (page.kind === "about") return <AboutPage page={page.page} />;
  if (page.kind === "industry") {
    return (
      <IndustryPage
        page={page.page}
        insights={<SiteSection label="Latest insights (site-wide, not previewed)" />}
        contact={<SiteSection label="Contact form (site-wide, not previewed)" />}
      />
    );
  }
  if (page.kind === "service") {
    return (
      <ServicePage
        page={page.page}
        categoryName={page.categoryName}
        stories={page.stories}
        storiesIntro={page.storiesIntro}
        industries={<SiteSection label="Industries strip (site-wide, not previewed)" />}
        highlights={<SiteSection label="Latest highlights (site-wide, not previewed)" />}
        contact={<SiteSection label="Contact form (site-wide, not previewed)" />}
      />
    );
  }
  if (page.kind === "case-study") return <CaseStudyArticle caseStudy={page.caseStudy} labels={CASE_STUDY_LABELS} />;
  return <PostArticle post={page.post} pageUrl={page.pageUrl} labels={POST_LABELS} />;
}
