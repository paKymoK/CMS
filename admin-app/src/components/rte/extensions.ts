import { Extension, Mark, Node, mergeAttributes } from "@tiptap/core";
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
          title: node.attrs.kind === "form" ? "Embedded form" : node.attrs.kind === "audio" ? "Embedded audio" : "Embedded video",
          loading: "lazy",
          allowfullscreen: "true",
          referrerpolicy: "strict-origin-when-cross-origin",
        },
      ],
    ];
  },
});

export type TextAlign = "left" | "center" | "right";
const ALIGNED_BLOCKS = ["paragraph", "heading", "blockquote"];

/** Block alignment, stored as data-text-align on p / headings / blockquote (left = no attribute). */
export const TextAlignment = Extension.create({
  name: "textAlignment",
  addGlobalAttributes() {
    return [
      {
        types: ALIGNED_BLOCKS,
        attributes: {
          textAlign: {
            default: "left" as TextAlign,
            parseHTML: (el) => {
              const a = el.getAttribute("data-text-align");
              return a === "center" || a === "right" ? a : "left";
            },
            renderHTML: (attrs) => (attrs.textAlign === "left" ? {} : { "data-text-align": attrs.textAlign }),
          },
        },
      },
    ];
  },
  addCommands() {
    return {
      setTextAlign:
        (align: TextAlign) =>
        ({ commands }) =>
          ALIGNED_BLOCKS.map((t) => commands.updateAttributes(t, { textAlign: align })).some(Boolean),
    };
  },
});

/** Named palette, not free colour: the website styles these names, and the sanitizer never has to
 * accept inline CSS. */
export const TEXT_COLORS = {
  blue: "#0b63c5",
  green: "#1f8a4c",
  orange: "#d46b08",
  red: "#d4380d",
  gray: "#6a7c90",
} as const;
export type TextColorName = keyof typeof TEXT_COLORS;

export const TextColor = Mark.create({
  name: "textColor",
  addAttributes() {
    return { color: { default: "blue" } };
  },
  parseHTML() {
    return [
      {
        tag: "span[data-color]",
        getAttrs: (el) => {
          const c = (el as HTMLElement).getAttribute("data-color") ?? "";
          return c in TEXT_COLORS ? { color: c } : false;
        },
      },
    ];
  },
  renderHTML: ({ HTMLAttributes }) => ["span", { "data-color": HTMLAttributes.color }, 0],
  addCommands() {
    return {
      setTextColor:
        (color: TextColorName) =>
        ({ commands }) =>
          commands.setMark(this.name, { color }),
      unsetTextColor:
        () =>
        ({ commands }) =>
          commands.unsetMark(this.name),
    };
  },
});

/** Lets a link render as a call-to-action button: <a data-button>. */
export const LinkButton = Extension.create({
  name: "linkButton",
  addGlobalAttributes() {
    return [
      {
        types: ["link"],
        attributes: {
          button: {
            default: false,
            parseHTML: (el) => el.hasAttribute("data-button"),
            renderHTML: (attrs) => (attrs.button ? { "data-button": "" } : {}),
          },
        },
      },
    ];
  },
});

/** Two or three side-by-side columns: <div data-columns="2"><div data-column>…</div>…</div>. */
export const Column = Node.create({
  name: "column",
  content: "block+",
  isolating: true,
  parseHTML: () => [{ tag: "div[data-column]" }],
  renderHTML: ({ HTMLAttributes }) => ["div", mergeAttributes(HTMLAttributes, { "data-column": "" }), 0],
});

export const Columns = Node.create({
  name: "columns",
  group: "block",
  content: "column{2,3}",
  isolating: true,
  draggable: true,
  parseHTML: () => [{ tag: "div[data-columns]" }],
  renderHTML: ({ node }) => ["div", { "data-columns": String(node.childCount) }, 0],
});

/** Collapsible section: <details><summary>title</summary>…blocks…</details>. `open` is forced on
 * while editing so the body is reachable; the sanitizer strips it, so published posts start closed. */
export const AccordionSummary = Node.create({
  name: "accordionSummary",
  content: "inline*",
  defining: true,
  parseHTML: () => [{ tag: "summary" }],
  renderHTML: ({ HTMLAttributes }) => ["summary", HTMLAttributes, 0],
});

export const Accordion = Node.create({
  name: "accordion",
  group: "block",
  content: "accordionSummary block+",
  isolating: true,
  draggable: true,
  parseHTML: () => [{ tag: "details" }],
  renderHTML: ({ HTMLAttributes }) => ["details", mergeAttributes(HTMLAttributes, { open: "" }), 0],
});

declare module "@tiptap/core" {
  interface Commands<ReturnType> {
    textAlignment: { setTextAlign: (align: TextAlign) => ReturnType };
    textColor: { setTextColor: (color: TextColorName) => ReturnType; unsetTextColor: () => ReturnType };
  }
}
