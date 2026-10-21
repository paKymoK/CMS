// Media references are stored in content as ORIGIN-LESS paths — /media-service/images/<id><ext> —
// so the data carries no host and means the same thing in every environment (dev, staging, prod).
// Each app resolves them against its own gateway base URL at render time. Absolute URLs (anything
// an editor stored before this convention) pass through untouched.
const GATEWAY = (import.meta.env.VITE_BASE_URL as string).replace(/\/$/, "");
const MEDIA_PATH = "/media-service/";

/** What to put in an <img src>/<Image src>: resolves a stored media path against the gateway. */
export function mediaSrc(url: string): string;
export function mediaSrc(url: string | null | undefined): string | undefined;
export function mediaSrc(url: string | null | undefined): string | undefined {
  if (!url) return undefined;
  return url.startsWith(MEDIA_PATH) ? `${GATEWAY}${url}` : url;
}

// src="..." / src='...' pointing at media-service, either form. Rich-text bodies are HTML strings,
// so they get the same resolve-on-display / strip-on-save treatment as a plain image field.
const RELATIVE_SRC = /(\ssrc=["'])(\/media-service\/)/g;
const ABSOLUTE_SRC = new RegExp(`(\\ssrc=["'])${GATEWAY.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")}(/media-service/)`, "g");

/** Rich-text HTML as stored -> HTML the editor can display. */
export function resolveMediaHtml(html: string): string {
  return html.replace(RELATIVE_SRC, `$1${GATEWAY}$2`);
}

/** Rich-text HTML read back from the editor -> the origin-less form that gets stored. */
export function relativizeMediaHtml(html: string): string {
  return html.replace(ABSOLUTE_SRC, "$1$2");
}

/**
 * Checks a library image against the proportions a field needs. Returns a message describing the
 * mismatch, or null when it fits. The ratio may be off by 1% at most; the width must reach the spec
 * (so a 2x export of the same proportions passes).
 */
export function checkImageSpec(
  url: string,
  spec: { width: number; height: number },
): Promise<string | null> {
  return new Promise((resolve) => {
    const img = new Image();
    img.onload = () => {
      const ratioOff = Math.abs(img.naturalWidth / img.naturalHeight / (spec.width / spec.height) - 1);
      if (ratioOff > 0.01 || img.naturalWidth < spec.width) {
        resolve(
          `This image is ${img.naturalWidth}×${img.naturalHeight}; it must be ${spec.width}×${spec.height} (or a larger file with the same proportions).`,
        );
      } else {
        resolve(null);
      }
    };
    img.onerror = () => resolve("The image could not be loaded to check its size.");
    img.src = mediaSrc(url)!;
  });
}
