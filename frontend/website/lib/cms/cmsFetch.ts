/**
 * fetch() for server-side CMS requests, with a hard timeout.
 *
 * Without one, a CMS host that is unreachable or very slow (e.g. a tunnel that is down while Vercel
 * builds) stalls every page that fetches it until the OS gives up — long past Next's 60-second
 * static-generation limit, which fails the whole build. With the timeout the request aborts, the
 * caller's existing catch/fallback runs (bundled content for the homepage, null for detail pages)
 * and ISR fills in the real content on the next revalidation.
 */
const CMS_TIMEOUT_MS = Number(process.env.CMS_FETCH_TIMEOUT_MS) || 8000;

export function cmsFetch(url: string, init: RequestInit & { next?: { revalidate?: number } } = {}) {
  return fetch(url, { ...init, signal: init.signal ?? AbortSignal.timeout(CMS_TIMEOUT_MS) });
}
