import { useEffect, useState } from "react";
import type { PageBlockContext } from "@cms/content-renderer";
import { AboutPage, CaseStudyArticle, ContactForm, IndustryPage, InsightsAccordion, PostArticle, ServiceHighlights, ServiceIndustries, ServicePage, SiteFooterView, SiteHeaderBar } from "@cms/content-renderer";
import { CASE_STUDY_LABELS, HEADER_ITEMS, MSG_HEIGHT, MSG_READY, MSG_RENDER, POST_LABELS, type PreviewChrome, type PreviewMessage, type PreviewPage } from "../lib/previewProtocol";

/**
 * The document the admin's preview shows in an iframe. It holds no data of its own: the admin
 * sends the current editor state with postMessage and this draws it with the same components the
 * website uses. Being its own document, the breakpoints follow the frame's width (desktop / tablet /
 * mobile) and the website's styles never touch the admin UI.
 */
export default function Frame() {
  const [page, setPage] = useState<PreviewPage | null>(null);
  const [chrome, setChrome] = useState<PreviewChrome | undefined>(undefined);

  useEffect(() => {
    const onMessage = (e: MessageEvent) => {
      if (e.origin !== window.location.origin) return;
      if ((e.data as PreviewMessage | undefined)?.type === MSG_RENDER) {
        setPage((e.data as PreviewMessage).page);
        setChrome((e.data as PreviewMessage).chrome);
      }
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
  // The website's header sits over the top of the page and its footer closes it; the page itself is the part being edited.
  return (
    <div className="relative">
      <SiteHeaderBar items={HEADER_ITEMS} logoSrc="/preview-logo.svg" contactLabel="Contact Us" />
      <Page page={page} chrome={chrome} />
      {chrome && <SiteFooterView footerNav={chrome.footerNav} footer={chrome.footer} defaultCopyright={`Copyright ©${new Date().getFullYear()} CMC Global. All rights reserved.`} />}
    </div>
  );
}

function Page({ page, chrome }: { page: PreviewPage; chrome?: PreviewChrome }) {
  const intros = chrome?.intros ?? {};
  const contact = <ContactForm intro={intros.contact} />;
  if (page.kind === "about" || page.kind === "industry" || page.kind === "service") {
    // The blocks that read site-wide content: the same for both page types, as on the website.
    const context: PageBlockContext = {
      stories: page.stories,
      storiesIntro: page.storiesIntro,
      industries: <ServiceIndustries cards={chrome?.industryCards ?? []} intro={intros["service-industries"]} />,
      highlights: <ServiceHighlights insights={chrome?.insights ?? []} intro={intros["service-highlights"]} />,
      insights: (block) => (
        <InsightsAccordion
          insights={chrome?.insights ?? []}
          intro={block.title ? { eyebrow: block.eyebrow, title: block.title, body: block.body, linkLabel: block.linkLabel, linkUrl: block.linkUrl } : undefined}
        />
      ),
    };
    if (page.kind === "about") return <AboutPage page={page.page} context={context} />;
    return page.kind === "industry" ? (
      <IndustryPage page={page.page} context={context} contact={contact} />
    ) : (
      <ServicePage page={page.page} categoryName={page.categoryName} context={context} contact={contact} />
    );
  }
  if (page.kind === "case-study") return <CaseStudyArticle caseStudy={page.caseStudy} labels={CASE_STUDY_LABELS} />;
  return <PostArticle post={page.post} pageUrl={page.pageUrl} labels={POST_LABELS} />;
}
