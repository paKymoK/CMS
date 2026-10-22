/**
 * Injects an id into every top-level <h2> in the (already server-sanitized by content-service on save) body HTML and
 * returns the matching table-of-contents entries — mirrors the design mockup's
 * "+ built from body H2s" note. Regex-based rather than a full DOM parse since body is
 * sanitized on write.
 */
export function prepareBody(body: string): {
  html: string;
  toc: { id: string; label: string }[];
} {
  const toc: { id: string; label: string }[] = [];
  let index = 0;
  const html = body.replace(/<h2([^>]*)>([\s\S]*?)<\/h2>/gi, (_match, attrs: string, inner: string) => {
    const label = inner.replace(/<[^>]*>/g, "").trim();
    const base = label
      .toLowerCase()
      .replace(/[^a-z0-9]+/g, "-")
      .replace(/^-+|-+$/g, "");
    const id = base ? `s-${base}` : `s-${index}`;
    index += 1;
    toc.push({ id, label });
    const cleanAttrs = attrs.replace(/\sid="[^"]*"/i, "");
    return `<h2${cleanAttrs} id="${id}">${inner}</h2>`;
  });
  return { html, toc };
}
