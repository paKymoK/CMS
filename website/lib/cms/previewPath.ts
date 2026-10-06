import { routing } from "@/i18n/routing";

// Strict allowlist rather than "any relative path": /api/preview redirects here after enabling
// draft mode, so anything looser (e.g. "//evil.example", "/\evil.example", "/en/../x") is an
// open-redirect risk. Only the pages that actually support preview are reachable.
const LOCALES = routing.locales.join("|");
const PREVIEW_PATH = new RegExp(
  `^/(?:${LOCALES})(?:/(?:insights|case-studies)/[a-z0-9]+(?:-[a-z0-9]+)*)?$`,
);

export const DEFAULT_PREVIEW_PATH = "/en";

/** Returns the path if it's a previewable page, otherwise null. */
export function parsePreviewPath(path: string | null): string | null {
  if (!path) return null;
  return PREVIEW_PATH.test(path) ? path : null;
}
