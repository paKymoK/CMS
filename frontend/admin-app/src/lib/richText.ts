export interface RichTextOutline {
  n: string;
  text: string;
}

/** Split out of RichTextEditor.tsx so that file only exports the component — mixing component and
 * non-component exports from one file breaks Vite's fast refresh. */
export function wordsAndOutline(html: string): { words: number; outline: RichTextOutline[] } {
  const container = document.createElement("div");
  container.innerHTML = html;
  const text = container.innerText ?? container.textContent ?? "";
  const words = (text.trim().match(/\S+/g) ?? []).length;
  const outline = [...container.querySelectorAll("h2")]
    .map((h) => h.textContent?.trim() ?? "")
    .filter(Boolean)
    .map((t, i) => ({ n: String(i + 1).padStart(2, "0"), text: t }));
  return { words, outline };
}
