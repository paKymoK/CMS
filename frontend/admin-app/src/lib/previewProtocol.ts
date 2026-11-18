import type {
  AboutPageData,
  FooterNavColumn,
  SiteFooterData,
  MobileBottomBarItem,
  SiteHeaderBarItem,
  CaseStudyArticleData,
  CaseStudyArticleLabels,
  IndustryCardData,
  IndustryPageData,
  InsightItemData,
  IndustryStoryData,
  PostArticleData,
  PostArticleLabels,
  SectionIntroData,
  SectionLinkIntroData,
  ServicePageData,
} from "@cms/content-renderer";

// Kept apart from contentPreview.ts: the preview frame imports this, and must not pull in the admin's API/gateway config.

/** Where the preview iframe lives (see preview-frame.html), and the messages it and the admin exchange. */
export const PREVIEW_FRAME_URL = "/preview-frame.html";
export const MSG_READY = "cms-preview:ready";
export const MSG_RENDER = "cms-preview:render";
/** Frame → admin: the height of the rendered page, so the iframe can grow to fit and only the admin scrolls. */
export const MSG_HEIGHT = "cms-preview:height";

/** What the frame should draw. */
export type PreviewPage =
  | { kind: "post"; post: PostArticleData; pageUrl: string }
  | { kind: "case-study"; caseStudy: CaseStudyArticleData }
  | { kind: "about"; page: AboutPageData; stories: IndustryStoryData[]; storiesIntro?: SectionIntroData }
  | { kind: "industry"; page: IndustryPageData; stories: IndustryStoryData[]; storiesIntro?: SectionIntroData }
  | { kind: "service"; page: ServicePageData; categoryName: string; stories: IndustryStoryData[]; storiesIntro?: SectionIntroData };

/** The site-wide footer content the frame draws around the page (the admin fetches it; the frame has no access to the API). */
export interface PreviewChrome {
  footerNav: FooterNavColumn[];
  footer?: SiteFooterData;
  /** The newest articles (the insights accordion, the latest-highlights cards). */
  insights: InsightItemData[];
  /** The photo cards of a service page's industries strip. */
  industryCards: IndustryCardData[];
  /** Section texts by their anchor id ("contact", "service-industries", "service-highlights"). */
  intros: Record<string, SectionLinkIntroData>;
}

export interface PreviewMessage {
  type: typeof MSG_RENDER;
  page: PreviewPage;
  chrome?: PreviewChrome;
}

/** The labels of the website's header, in its order (website/content/nav.ts HEADER_NAV): the preview draws them as a still picture. */
export const HEADER_ITEMS: SiteHeaderBarItem[] = [
  { label: "About Us" },
  { label: "Services", hasDropdown: true },
  { label: "Industries", hasDropdown: true },
  { label: "Insights" },
  { label: "Career" },
  { label: "Contact" },
];

/** The labels of the website's mobile bottom bar (website/content/nav.ts PRIMARY_NAV), drawn at phone width. */
export const MOBILE_NAV_ITEMS: MobileBottomBarItem[] = [
  { label: "Services" },
  { label: "Case Studies" },
  { label: "Insights" },
  { label: "About Us" },
  { label: "Resources" },
  { label: "Careers" },
];

/** The public pages are English-only today, so the preview uses the site's English strings. */
export const POST_LABELS: PostArticleLabels = {
  eyebrow: "Insights",
  minRead: "min read",
  onThisPage: "On this page",
  share: "Share",
  keepReading: "Keep reading",
  relatedInsights: "Related insights",
  viewAllInsights: "View all insights",
};

export const CASE_STUDY_LABELS: CaseStudyArticleLabels = {
  home: "Home",
  eyebrow: "Case Studies",
  discussProject: "Discuss a similar project",
  eventPhotography: "Event photography",
  results: "Results",
  moreWork: "More work",
  relatedCaseStudies: "Related case studies",
};
