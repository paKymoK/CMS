import { cookies, draftMode } from "next/headers";
import { getHomeContent, getPreviewHomeContent } from "@/lib/cms/homeContent";
import { PREVIEW_TOKEN_COOKIE } from "@/lib/cms/previewCookie";

/**
 * Draft mode is entered exclusively through /api/preview, which validates the token before
 * enabling it — so a real visitor's request always has draftMode().isEnabled === false and this
 * always takes the getHomeContent() branch below, same as before this feature existed. The
 * missing-cookie case (draft mode on, but no cms_preview_token — e.g. it expired/was cleared out
 * from under an already-rendered page) degrades to published content with a console.error rather
 * than throwing, since this is a page render, not the explicit /api/preview entry point.
 */
export async function loadContent(): Promise<{
  content: Awaited<ReturnType<typeof getHomeContent>>;
  isPreview: boolean;
}> {
  const draft = await draftMode();
  if (!draft.isEnabled) {
    return { content: await getHomeContent(), isPreview: false };
  }

  const token = (await cookies()).get(PREVIEW_TOKEN_COOKIE)?.value;
  if (!token) {
    console.error(
      "Draft mode enabled with no preview token cookie — falling back to published content",
    );
    return { content: await getHomeContent(), isPreview: false };
  }

  try {
    return { content: await getPreviewHomeContent(token), isPreview: true };
  } catch (err) {
    console.error("Failed to load preview content, falling back to published content", err);
    return { content: await getHomeContent(), isPreview: false };
  }
}
