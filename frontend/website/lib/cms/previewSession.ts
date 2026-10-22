import { cookies, draftMode } from "next/headers";
import { PREVIEW_TOKEN_COOKIE } from "./previewCookie";

/**
 * The active preview token, or null for a normal visitor. Draft mode is only ever entered through
 * /api/preview (which validates the token first), so a real visitor's request always gets null
 * here and takes the unchanged published/ISR path. Draft mode on but no cookie (expired/cleared
 * mid-session) also returns null — callers degrade to published content rather than throwing.
 */
export async function getPreviewToken(): Promise<string | null> {
  const draft = await draftMode();
  if (!draft.isEnabled) return null;
  return (await cookies()).get(PREVIEW_TOKEN_COOKIE)?.value ?? null;
}
