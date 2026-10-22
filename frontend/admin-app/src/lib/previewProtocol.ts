import type {
  CaseStudyArticleData,
  CaseStudyArticleLabels,
  IndustryStoryData,
  PostArticleData,
  PostArticleLabels,
  SectionIntroData,
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
  | { kind: "service"; page: ServicePageData; categoryName: string; stories: IndustryStoryData[]; storiesIntro?: SectionIntroData };

export interface PreviewMessage {
  type: typeof MSG_RENDER;
  page: PreviewPage;
}

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
