/** Embeds authors may place in a post/case-study body. Only these providers are allowed: the
 * editor refuses everything else, and the website's render-time sanitizer re-checks the same list
 * (website/lib/richText.ts) — keep the two in sync. */
export type EmbedKind = "video" | "form";

export interface EmbedSpec {
  src: string;
  kind: EmbedKind;
}

/** hostname -> kind, for the final iframe src. */
export const EMBED_HOSTS: Record<string, EmbedKind> = {
  "www.youtube-nocookie.com": "video",
  "player.vimeo.com": "video",
  "docs.google.com": "form",
  "tally.so": "form",
  "form.typeform.com": "form",
  "form.jotform.com": "form",
  "forms.office.com": "form",
};

/** True when `src` is an https URL on an allowed embed host (Google only under /forms/). */
export function isAllowedEmbedSrc(src: string): boolean {
  try {
    const u = new URL(src);
    if (u.protocol !== "https:" || !(u.hostname in EMBED_HOSTS)) return false;
    if (u.hostname === "docs.google.com") return u.pathname.startsWith("/forms/");
    return true;
  } catch {
    return false;
  }
}

/** Turns what an author pastes (a watch/share link or an embed URL) into the canonical iframe src,
 * or null when the provider isn't allowed. */
export function parseEmbedUrl(input: string): EmbedSpec | null {
  let u: URL;
  try {
    u = new URL(input.trim());
  } catch {
    return null;
  }
  if (u.protocol !== "https:") return null;
  const host = u.hostname.replace(/^www\./, "");

  let id: string | null = null;
  if (host === "youtu.be") id = u.pathname.slice(1);
  else if (host === "youtube.com" || host === "youtube-nocookie.com") {
    id = u.searchParams.get("v") ?? u.pathname.match(/^\/(?:embed|shorts)\/([\w-]+)/)?.[1] ?? null;
  }
  if (id && /^[\w-]{6,20}$/.test(id)) {
    return { src: `https://www.youtube-nocookie.com/embed/${id}`, kind: "video" };
  }

  if (host === "vimeo.com" || host === "player.vimeo.com") {
    const vid = u.pathname.match(/(\d{5,})/)?.[1];
    if (vid) return { src: `https://player.vimeo.com/video/${vid}`, kind: "video" };
  }

  if (host === "tally.so") {
    const tid = u.pathname.match(/^\/(?:r|embed)\/([\w-]+)/)?.[1];
    if (tid) return { src: `https://tally.so/embed/${tid}`, kind: "form" };
  }

  if (host === "docs.google.com" && u.pathname.startsWith("/forms/")) {
    const path = u.pathname.replace(/\/viewform$/, "") + "/viewform";
    return { src: `https://docs.google.com${path}?embedded=true`, kind: "form" };
  }

  if (host === "form.typeform.com" || host === "form.jotform.com" || host === "forms.office.com") {
    const clean = `https://${host}${u.pathname}${u.search}`;
    if (isAllowedEmbedSrc(clean)) return { src: clean, kind: "form" };
  }
  return null;
}
