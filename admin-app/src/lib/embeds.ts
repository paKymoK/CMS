import hosts from "../../../backend/content-service/src/main/resources/rich-text-embed-hosts.json";

/** Embeds authors may place in a post/case-study body. Only the providers in
 * rich-text-embed-hosts.json are allowed. That file belongs to content-service, which enforces it
 * on save (RichTextSanitizer) — the editor imports the same file, so the two cannot drift. */
export type EmbedKind = "video" | "form" | "audio";

export interface EmbedSpec {
  src: string;
  kind: EmbedKind;
}

const HOSTS = hosts as Record<string, { kind: EmbedKind; pathPrefix: string }>;

/** hostname -> kind, for the final iframe src. */
export const EMBED_HOSTS: Record<string, EmbedKind> = Object.fromEntries(
  Object.entries(HOSTS).map(([host, h]) => [host, h.kind]),
);

/** True when `src` is an https URL on an allowed embed host and under that host's path prefix. */
export function isAllowedEmbedSrc(src: string): boolean {
  try {
    const u = new URL(src);
    const h = HOSTS[u.hostname];
    return u.protocol === "https:" && !!h && u.pathname.startsWith(h.pathPrefix);
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

  if (host === "loom.com") {
    const lid = u.pathname.match(/^\/(?:share|embed)\/([\w-]+)/)?.[1];
    if (lid) return { src: `https://www.loom.com/embed/${lid}`, kind: "video" };
  }

  if (host === "open.spotify.com") {
    const m = u.pathname.match(/^\/(?:embed\/)?(track|episode|playlist|album|show)\/([\w]+)/);
    if (m) return { src: `https://open.spotify.com/embed/${m[1]}/${m[2]}`, kind: "audio" };
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
