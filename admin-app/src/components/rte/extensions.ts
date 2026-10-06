import { Node, mergeAttributes } from "@tiptap/core";
import { isAllowedEmbedSrc, EMBED_HOSTS } from "../../lib/embeds";

export type FigureAlign = "full" | "left" | "right";

/** <figure data-align><img><figcaption>…</figcaption></figure> — matches the markup the old editor
 * already stored, so existing posts load unchanged. */
export const Figcaption = Node.create({
  name: "figcaption",
  content: "inline*",
  defining: true,
  parseHTML: () => [{ tag: "figcaption" }],
  renderHTML: ({ HTMLAttributes }) => ["figcaption", HTMLAttributes, 0],
});

export const Figure = Node.create({
  name: "figure",
  group: "block",
  content: "image figcaption",
  isolating: true,
  draggable: true,

  addAttributes() {
    return {
      align: {
        default: "full" as FigureAlign,
        parseHTML: (el) => {
          const a = el.getAttribute("data-align");
          return a === "left" || a === "right" ? a : "full";
        },
        renderHTML: (attrs) => ({ "data-align": attrs.align }),
      },
    };
  },

  parseHTML: () => [{ tag: "figure" }],
  renderHTML: ({ HTMLAttributes }) => ["figure", HTMLAttributes, 0],

  addKeyboardShortcuts() {
    return {
      // Enter in a caption leaves the figure instead of trying to split it.
      Enter: ({ editor }) => {
        const { $from } = editor.state.selection;
        if ($from.parent.type.name !== "figcaption") return false;
        const after = $from.after($from.depth - 1);
        return editor
          .chain()
          .insertContentAt(after, { type: "paragraph" })
          .focus(after + 1)
          .run();
      },
    };
  },
});

/** Highlighted note box: <div data-callout>…blocks…</div> */
export const Callout = Node.create({
  name: "callout",
  group: "block",
  content: "block+",
  defining: true,
  parseHTML: () => [{ tag: "div[data-callout]" }],
  renderHTML: ({ HTMLAttributes }) => ["div", mergeAttributes(HTMLAttributes, { "data-callout": "" }), 0],
});

/** Video/form iframe from an allowlisted provider only. Stored as
 * <div data-embed="video|form"><iframe src=… ></iframe></div>. */
export const Embed = Node.create({
  name: "embed",
  group: "block",
  atom: true,
  draggable: true,

  addAttributes() {
    return {
      src: { default: "" },
      kind: { default: "video" },
    };
  },

  parseHTML() {
    return [
      {
        tag: "div[data-embed]",
        getAttrs: (el) => {
          const src = (el as HTMLElement).querySelector("iframe")?.getAttribute("src") ?? "";
          if (!isAllowedEmbedSrc(src)) return false; // drop anything off the allowlist
          return { src, kind: EMBED_HOSTS[new URL(src).hostname] };
        },
      },
    ];
  },

  renderHTML({ node }) {
    return [
      "div",
      { "data-embed": node.attrs.kind },
      [
        "iframe",
        {
          src: node.attrs.src,
          title: node.attrs.kind === "form" ? "Embedded form" : "Embedded video",
          loading: "lazy",
          allowfullscreen: "true",
          referrerpolicy: "strict-origin-when-cross-origin",
        },
      ],
    ];
  },
});
