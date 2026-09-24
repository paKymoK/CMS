// Shared between app/api/preview/*'s route handlers and [locale]/page.tsx's draftMode() branch —
// carries the actual preview_token value. draftMode()'s own cookie only signals on/off, it can't
// carry arbitrary data, so this is a second, separate cookie alongside it.
export const PREVIEW_TOKEN_COOKIE = "cms_preview_token";

export const PREVIEW_TOKEN_COOKIE_OPTIONS = {
  httpOnly: true,
  secure: process.env.NODE_ENV === "production",
  sameSite: "lax" as const,
  maxAge: 15 * 60, // matches content-service's preview_token TTL
  path: "/",
};
