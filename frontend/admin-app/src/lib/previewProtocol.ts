import type { PostArticleData, PostArticleLabels } from "@cms/post-renderer";

// Kept apart from postPreview.ts: the preview frame imports this, and must not pull in the admin's API/gateway config.

/** Where the preview iframe lives (see preview-frame.html), and the messages it and the admin exchange. */
export const PREVIEW_FRAME_URL = "/preview-frame.html";
export const MSG_READY = "cms-preview:ready";
export const MSG_POST = "cms-preview:post";

export interface PreviewMessage {
  type: typeof MSG_POST;
  post: PostArticleData;
  pageUrl: string;
}

/** The public post page is English-only today, so the preview uses the site's English strings. */
export const PREVIEW_LABELS: PostArticleLabels = {
  eyebrow: "Insights",
  minRead: "min read",
  onThisPage: "On this page",
  share: "Share",
  keepReading: "Keep reading",
  relatedInsights: "Related insights",
  viewAllInsights: "View all insights",
};
