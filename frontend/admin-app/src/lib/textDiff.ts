/** One run of a word-level diff: text kept, text that was removed, or text that was added. */
export interface DiffPart {
  kind: "same" | "del" | "add";
  text: string;
}

/** Above this many words on a side (after trimming what both share) a precise diff is skipped. */
const MAX_WORDS = 2000;

/**
 * A text as alternating words and whitespace runs, so joining the tokens restores it. Whitespace is
 * its own token (not glued to the word before it) so a word that gains a trailing space or sits at
 * the end of the text still matches itself.
 */
function tokens(text: string): string[] {
  return text.match(/\S+|\s+/g) ?? [];
}

/**
 * A word-level diff of two texts: which words stayed, which were removed and which were added.
 * Words that both texts start or end with are peeled off first, so a small edit inside a long
 * article costs little; a rewrite too large to compare precisely comes back as "all removed, all
 * added" rather than freezing the page.
 */
export function diffWords(before: string, after: string): DiffPart[] {
  const a = tokens(before);
  const b = tokens(after);
  let start = 0;
  while (start < a.length && start < b.length && a[start] === b[start]) start++;
  let endA = a.length;
  let endB = b.length;
  while (endA > start && endB > start && a[endA - 1] === b[endB - 1]) {
    endA--;
    endB--;
  }
  const middleA = a.slice(start, endA);
  const middleB = b.slice(start, endB);

  const parts: DiffPart[] = [];
  const push = (kind: DiffPart["kind"], text: string) => {
    if (!text) return;
    const last = parts[parts.length - 1];
    if (last && last.kind === kind) last.text += text;
    else parts.push({ kind, text });
  };

  push("same", a.slice(0, start).join(""));
  if (middleA.length > MAX_WORDS || middleB.length > MAX_WORDS) {
    push("del", middleA.join(""));
    push("add", middleB.join(""));
  } else {
    // Longest common subsequence of the differing middle.
    const rows = middleA.length + 1;
    const cols = middleB.length + 1;
    const table = new Uint16Array(rows * cols);
    for (let i = middleA.length - 1; i >= 0; i--) {
      for (let j = middleB.length - 1; j >= 0; j--) {
        table[i * cols + j] =
          middleA[i] === middleB[j]
            ? table[(i + 1) * cols + j + 1] + 1
            : Math.max(table[(i + 1) * cols + j], table[i * cols + j + 1]);
      }
    }
    let i = 0;
    let j = 0;
    while (i < middleA.length && j < middleB.length) {
      if (middleA[i] === middleB[j]) {
        push("same", middleA[i]);
        i++;
        j++;
      } else if (table[(i + 1) * cols + j] >= table[i * cols + j + 1]) {
        push("del", middleA[i++]);
      } else {
        push("add", middleB[j++]);
      }
    }
    while (i < middleA.length) push("del", middleA[i++]);
    while (j < middleB.length) push("add", middleB[j++]);
  }
  push("same", a.slice(endA).join(""));
  return parts;
}

/** Plain text of an HTML fragment (for diffing rich text without its markup). */
export function htmlToText(html: string): string {
  if (typeof DOMParser === "undefined") return html.replace(/<[^>]*>/g, " ");
  const doc = new DOMParser().parseFromString(html, "text/html");
  // Block boundaries become newlines so paragraphs don't run together.
  doc.querySelectorAll("p,div,li,h1,h2,h3,h4,h5,h6,br,tr").forEach((el) => el.append("\n"));
  return (doc.body.textContent ?? "").replace(/\n{3,}/g, "\n\n").trim();
}
