import { forwardRef, useEffect, useImperativeHandle, useRef, useState, type CSSProperties } from "react";

export interface RichTextEditorHandle {
  /** Inserts at the live cursor position if the editor still has an active selection there
   * (e.g. called synchronously from a toolbar action); otherwise appends to the end — the
   * selection is long gone once a modal (the media picker) has taken focus in between. */
  insertHtml: (html: string) => void;
}

interface RichTextEditorProps {
  value: string;
  onChange: (html: string) => void;
  onInsertImage: () => void;
}

type Block = "p" | "h2" | "h3" | "blockquote" | "ul" | "ol";

const toolStyle = (on: boolean, extra?: CSSProperties): CSSProperties => ({
  minWidth: 34,
  height: 32,
  padding: "0 9px",
  border: "none",
  cursor: "pointer",
  background: on ? "#0a2540" : "transparent",
  color: on ? "#ffffff" : "#3d4046",
  fontFamily: "var(--font-mono)",
  fontSize: 12,
  borderRadius: 4,
  ...extra,
});

function currentBlock(root: HTMLElement): Block {
  const sel = window.getSelection();
  if (!sel || !sel.anchorNode || !root.contains(sel.anchorNode)) return "p";
  const node: Node = sel.anchorNode;
  let el = node.nodeType === 3 ? node.parentElement : (node as HTMLElement);
  while (el && el !== root) {
    const tag = el.tagName?.toLowerCase();
    if (tag === "h2" || tag === "h3" || tag === "blockquote" || tag === "ul" || tag === "ol") {
      return tag as Block;
    }
    el = el.parentElement;
  }
  return "p";
}

/** Same technique the mockup's own prototype uses (design/project/CMS Post Editor.dc.html): a
 * plain contentEditable div driven by document.execCommand, not a rich-text library. innerHTML is
 * only pushed into the DOM once on mount — after that the DOM is the source of truth and we just
 * read it back on input, so typing never fights a controlled re-render / cursor jump. */
const RichTextEditor = forwardRef<RichTextEditorHandle, RichTextEditorProps>(function RichTextEditor(
  { value, onChange, onInsertImage },
  ref,
) {
  const rootRef = useRef<HTMLDivElement>(null);
  const mountedValue = useRef(value);
  const [block, setBlock] = useState<Block>("p");

  useEffect(() => {
    if (rootRef.current) rootRef.current.innerHTML = mountedValue.current;
  }, []);

  const scan = () => {
    const el = rootRef.current;
    if (!el) return;
    setBlock(currentBlock(el));
    onChange(el.innerHTML);
  };

  useImperativeHandle(ref, () => ({
    insertHtml: (html: string) => {
      const el = rootRef.current;
      if (!el) return;
      const sel = window.getSelection();
      const hasLiveSelection = !!sel && sel.rangeCount > 0 && el.contains(sel.anchorNode);
      if (hasLiveSelection) {
        el.focus();
        document.execCommand("insertHTML", false, html);
      } else {
        el.innerHTML += html;
      }
      scan();
    },
  }));

  const withFocus = (fn: () => void) => (e: React.MouseEvent) => {
    e.preventDefault();
    rootRef.current?.focus();
    fn();
    scan();
  };

  const exec = (command: string, arg?: string) => document.execCommand(command, false, arg);
  const formatBlock = (tag: Block) => () => exec("formatBlock", block === tag ? "p" : tag);

  const tools: { label: string; title: string; on: boolean; run: (e: React.MouseEvent) => void; extra?: CSSProperties }[] = [
    { label: "P", title: "Paragraph", on: block === "p", run: withFocus(() => exec("formatBlock", "p")) },
    { label: "H2", title: "Heading 2", on: block === "h2", run: withFocus(formatBlock("h2")) },
    { label: "H3", title: "Heading 3", on: block === "h3", run: withFocus(formatBlock("h3")) },
    { label: "B", title: "Bold", on: false, run: withFocus(() => exec("bold")), extra: { fontWeight: 700 } },
    { label: "I", title: "Italic", on: false, run: withFocus(() => exec("italic")), extra: { fontStyle: "italic" } },
    {
      label: "Link",
      title: "Insert link",
      on: false,
      run: withFocus(() => {
        const url = window.prompt("Link URL", "https://");
        if (url) exec("createLink", url);
      }),
    },
    { label: "• List", title: "Bullet list", on: block === "ul", run: withFocus(() => exec("insertUnorderedList")) },
    { label: "1. List", title: "Numbered list", on: block === "ol", run: withFocus(() => exec("insertOrderedList")) },
    { label: '" Quote', title: "Pull quote", on: block === "blockquote", run: withFocus(formatBlock("blockquote")) },
    {
      label: "+ Image",
      title: "Insert image from library",
      on: false,
      run: withFocus(() => onInsertImage()),
    },
  ];

  return (
    <div>
      <div
        style={{
          position: "sticky",
          top: 96,
          zIndex: 5,
          display: "flex",
          flexWrap: "wrap",
          alignItems: "center",
          gap: 2,
          padding: 6,
          border: "1px solid #e0e4e9",
          background: "#ffffff",
          boxShadow: "0 6px 18px rgba(10,37,64,.06)",
        }}
      >
        {tools.map((t) => (
          <button
            key={t.label}
            type="button"
            onMouseDown={t.run}
            title={t.title}
            style={toolStyle(t.on, t.extra)}
          >
            {t.label}
          </button>
        ))}
      </div>
      <div
        ref={rootRef}
        data-rte="1"
        contentEditable
        suppressContentEditableWarning
        onInput={scan}
        onKeyUp={scan}
        onMouseUp={scan}
        className="cms-rte"
        style={{
          minHeight: 420,
          padding: "28px clamp(20px,5vw,56px) 56px",
          fontFamily: "'Helvetica Neue', Helvetica, Arial, sans-serif",
          fontSize: 17,
          lineHeight: 1.75,
          color: "#3d4046",
          outline: "none",
        }}
      />
    </div>
  );
});

export default RichTextEditor;
