const CMS_BASE = process.env.NEXT_PUBLIC_CMS_API_BASE_URL!;
const MEDIA_PATH = "/media-service/";
const RELATIVE_SRC = /(\ssrc=["'])(\/media-service\/)/g;

/**
 * CMS content stores media as ORIGIN-LESS paths (/media-service/images/<id><ext>) so the data
 * carries no host and means the same thing in every environment. This resolves every such path in
 * a content-service response against the gateway this site already talks to, once, at the fetch
 * boundary — so components keep receiving ready-to-use URLs, and next/image keeps loading straight
 * from the gateway rather than proxying bytes through the Next server. Rich-text bodies get their
 * <img src="/media-service/..."> rewritten the same way. Anything else (absolute URLs from before
 * this convention, the site's own /images/... statics) passes through untouched.
 */
export function resolveMediaUrls<T>(value: T): T {
  if (typeof value === "string") {
    if (value.startsWith(MEDIA_PATH)) return `${CMS_BASE}${value}` as T;
    if (value.includes("/media-service/")) {
      return value.replace(RELATIVE_SRC, `$1${CMS_BASE}$2`) as T;
    }
    return value;
  }
  if (Array.isArray(value)) return value.map(resolveMediaUrls) as T;
  if (value && typeof value === "object") {
    return Object.fromEntries(
      Object.entries(value).map(([k, v]) => [k, resolveMediaUrls(v)]),
    ) as T;
  }
  return value;
}
