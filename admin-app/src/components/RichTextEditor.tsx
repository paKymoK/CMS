import { forwardRef, useImperativeHandle, useState, type CSSProperties, type ReactNode } from "react";
import { EditorContent, useEditor, useEditorState } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import Image from "@tiptap/extension-image";
import { Table, TableRow, TableHeader, TableCell } from "@tiptap/extension-table";
import { Input, Modal, message } from "antd";
import { relativizeMediaHtml, resolveMediaHtml } from "../lib/media";
import { parseEmbedUrl } from "../lib/embeds";
import {
  Accordion,
  AccordionSummary,
  Callout,
  Column,
  Columns,
  Embed,
  Figcaption,
  Figure,
  LinkButton,
  TEXT_COLORS,
  TextAlignment,
  TextColor,
  type FigureAlign,
  type TextAlign,
  type TextColorName,
} from "./rte/extensions";

export interface RichTextEditorHandle {
  /** Inserts HTML at the editor's last known selection (kept across the media-picker modal);
   * appended to the end if the editor never had one. Parsed through the editor schema, so
   * anything the schema doesn't allow is dropped. */
  insertHtml: (html: string) => void;
}

interface RichTextEditorProps {
  value: string;
  onChange: (html: string) => void;
  onInsertImage: () => void;
}

const toolStyle = (on: boolean, disabled = false): CSSProperties => ({
  minWidth: 32,
  height: 32,
  padding: "0 9px",
  border: "none",
  cursor: disabled ? "default" : "pointer",
  opacity: disabled ? 0.35 : 1,
  background: on ? "#0a2540" : "transparent",
  color: on ? "#ffffff" : "#3d4046",
  fontFamily: "var(--font-mono)",
  fontSize: 12,
  borderRadius: 4,
});

const Sep = () => <span style={{ width: 1, height: 20, background: "#e0e4e9", margin: "0 4px" }} />;

function ToolBtn({ label, title, on = false, disabled = false, onRun }: { label: ReactNode; title: string; on?: boolean; disabled?: boolean; onRun: () => void }) {
  return (
    <button
      type="button"
      title={title}
      aria-label={title}
      disabled={disabled}
      onMouseDown={(e) => {
        e.preventDefault(); // keep the editor's selection
        onRun();
      }}
      style={toolStyle(on, disabled)}
    >
      {label}
    </button>
  );
}

/** Link targets we accept; everything else (javascript:, data:, …) is refused. */
function normalizeLink(raw: string): string | null {
  const v = raw.trim();
  if (!v) return null;
  if (/^(https?:\/\/|mailto:|tel:|\/|#)/i.test(v)) return v;
  if (/^[\w-]+(\.[\w-]+)+(\/.*)?$/.test(v)) return `https://${v}`;
  return null;
}

const RichTextEditor = forwardRef<RichTextEditorHandle, RichTextEditorProps>(function RichTextEditor(
  { value, onChange, onInsertImage },
  ref,
) {
  const [linkOpen, setLinkOpen] = useState(false);
  const [linkUrl, setLinkUrl] = useState("");
  const [linkButton, setLinkButton] = useState(false);
  const [embedOpen, setEmbedOpen] = useState(false);
  const [embedUrl, setEmbedUrl] = useState("");
  const [sourceMode, setSourceMode] = useState(false);
  const [sourceHtml, setSourceHtml] = useState("");

  const editor = useEditor({
    extensions: [
      StarterKit.configure({
        heading: { levels: [2, 3, 4] }, // H1 is the post title
        link: {
          openOnClick: false,
          protocols: ["http", "https", "mailto", "tel"],
          HTMLAttributes: { rel: "noopener noreferrer" },
        },
      }),
      Image.configure({ inline: false }),
      Figure,
      Figcaption,
      Callout,
      Embed,
      Columns,
      Column,
      Accordion,
      AccordionSummary,
      TextAlignment,
      TextColor,
      LinkButton,
      Table.configure({ resizable: false }),
      TableRow,
      TableHeader,
      TableCell,
    ],
    // The stored value carries origin-less media paths; the DOM needs loadable srcs.
    content: resolveMediaHtml(value),
    onUpdate: ({ editor: e }) => onChange(relativizeMediaHtml(e.getHTML())),
    editorProps: {
      attributes: { class: "cms-rte", "data-rte": "1" },
      handleDOMEvents: {
        // A click on a <summary> would collapse the section and hide its body from the editor.
        click: (_view, e) => {
          if ((e.target as HTMLElement | null)?.closest("summary")) e.preventDefault();
          return false;
        },
      },
    },
  });

  const s = useEditorState({
    editor,
    selector: ({ editor: e }) => ({
      h2: !!e?.isActive("heading", { level: 2 }),
      h3: !!e?.isActive("heading", { level: 3 }),
      h4: !!e?.isActive("heading", { level: 4 }),
      textAlign: (e?.getAttributes("paragraph").textAlign ?? e?.getAttributes("heading").textAlign ?? "left") as TextAlign,
      color: (e?.isActive("textColor") ? e.getAttributes("textColor").color : null) as TextColorName | null,
      button: !!e?.getAttributes("link").button,
      columns: !!e?.isActive("columns"),
      accordion: !!e?.isActive("accordion"),
      bold: !!e?.isActive("bold"),
      italic: !!e?.isActive("italic"),
      underline: !!e?.isActive("underline"),
      strike: !!e?.isActive("strike"),
      code: !!e?.isActive("code"),
      link: !!e?.isActive("link"),
      ul: !!e?.isActive("bulletList"),
      ol: !!e?.isActive("orderedList"),
      quote: !!e?.isActive("blockquote"),
      codeBlock: !!e?.isActive("codeBlock"),
      callout: !!e?.isActive("callout"),
      table: !!e?.isActive("table"),
      figure: !!e?.isActive("figure"),
      align: (e?.getAttributes("figure").align as FigureAlign | undefined) ?? "full",
      canUndo: !!e?.can().undo(),
      canRedo: !!e?.can().redo(),
    }),
  });

  useImperativeHandle(ref, () => ({
    insertHtml: (html: string) => {
      editor?.chain().focus().insertContent(resolveMediaHtml(html)).run();
    },
  }));

  if (!editor || !s) return <div style={{ minHeight: 420 }} />;

  const chain = () => editor.chain().focus();

  const openLink = () => {
    setLinkUrl((editor.getAttributes("link").href as string | undefined) ?? "https://");
    setLinkButton(!!editor.getAttributes("link").button);
    setLinkOpen(true);
  };
  const insertColumns = () =>
    chain()
      .insertContent({
        type: "columns",
        content: [
          { type: "column", content: [{ type: "paragraph" }] },
          { type: "column", content: [{ type: "paragraph" }] },
        ],
      })
      .run();
  const insertAccordion = () =>
    chain()
      .insertContent({
        type: "accordion",
        content: [
          { type: "accordionSummary", content: [{ type: "text", text: "Question or title" }] },
          { type: "paragraph" },
        ],
      })
      .run();

  const applyLink = () => {
    if (!linkUrl.trim()) {
      chain().extendMarkRange("link").unsetLink().run();
    } else {
      const href = normalizeLink(linkUrl);
      if (!href) {
        message.error("Links must be http(s), mailto, tel, or a site path");
        return;
      }
      chain().extendMarkRange("link").setLink({ href }).updateAttributes("link", { button: linkButton }).run();
    }
    setLinkOpen(false);
  };

  const applyEmbed = () => {
    const spec = parseEmbedUrl(embedUrl);
    if (!spec) {
      message.error("Unsupported link. Use YouTube, Vimeo, Loom, Spotify, Google Forms, Tally, Typeform, Jotform or Microsoft Forms.");
      return;
    }
    chain().insertContent({ type: "embed", attrs: spec }).run();
    setEmbedOpen(false);
    setEmbedUrl("");
  };

  const toggleSource = () => {
    if (!sourceMode) {
      setSourceHtml(editor.getHTML());
      setSourceMode(true);
    } else {
      // Re-parsed through the schema: whatever it doesn't allow is dropped here.
      editor.commands.setContent(resolveMediaHtml(sourceHtml), { emitUpdate: true });
      setSourceMode(false);
    }
  };

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
        <ToolBtn label="↶" title="Undo (⌘Z)" disabled={!s.canUndo} onRun={() => chain().undo().run()} />
        <ToolBtn label="↷" title="Redo (⇧⌘Z)" disabled={!s.canRedo} onRun={() => chain().redo().run()} />
        <Sep />
        <ToolBtn label="P" title="Paragraph" on={!s.h2 && !s.h3 && !s.h4} onRun={() => chain().setParagraph().run()} />
        <ToolBtn label="H2" title="Heading 2" on={s.h2} onRun={() => chain().toggleHeading({ level: 2 }).run()} />
        <ToolBtn label="H3" title="Heading 3" on={s.h3} onRun={() => chain().toggleHeading({ level: 3 }).run()} />
        <ToolBtn label="H4" title="Heading 4" on={s.h4} onRun={() => chain().toggleHeading({ level: 4 }).run()} />
        <Sep />
        {(["left", "center", "right"] as const).map((a) => (
          <ToolBtn key={a} label={a === "left" ? "⇤" : a === "center" ? "↔" : "⇥"} title={`Align ${a}`} on={s.textAlign === a} onRun={() => chain().setTextAlign(a).run()} />
        ))}
        <Sep />
        <ToolBtn label={<b>B</b>} title="Bold (⌘B)" on={s.bold} onRun={() => chain().toggleBold().run()} />
        <ToolBtn label={<i>I</i>} title="Italic (⌘I)" on={s.italic} onRun={() => chain().toggleItalic().run()} />
        <ToolBtn label={<u>U</u>} title="Underline (⌘U)" on={s.underline} onRun={() => chain().toggleUnderline().run()} />
        <ToolBtn label={<s>S</s>} title="Strikethrough" on={s.strike} onRun={() => chain().toggleStrike().run()} />
        <ToolBtn label="</>" title="Inline code" on={s.code} onRun={() => chain().toggleCode().run()} />
        {(Object.keys(TEXT_COLORS) as TextColorName[]).map((c) => (
          <ToolBtn
            key={c}
            label={<span style={{ display: "inline-block", width: 12, height: 12, borderRadius: "50%", background: TEXT_COLORS[c], outline: s.color === c ? "2px solid #0a2540" : "none", outlineOffset: 1 }} />}
            title={`Text colour: ${c}`}
            on={false}
            onRun={() => (s.color === c ? chain().unsetTextColor().run() : chain().setTextColor(c).run())}
          />
        ))}
        <ToolBtn label="Link" title="Insert / edit link" on={s.link} onRun={openLink} />
        <ToolBtn label="Clear" title="Clear formatting" onRun={() => chain().unsetAllMarks().clearNodes().run()} />
        <Sep />
        <ToolBtn label="• List" title="Bullet list" on={s.ul} onRun={() => chain().toggleBulletList().run()} />
        <ToolBtn label="1. List" title="Numbered list" on={s.ol} onRun={() => chain().toggleOrderedList().run()} />
        <ToolBtn label={'" Quote'} title="Pull quote" on={s.quote} onRun={() => chain().toggleBlockquote().run()} />
        <ToolBtn label="Callout" title="Callout box" on={s.callout} onRun={() => (s.callout ? chain().lift("callout").run() : chain().wrapIn("callout").run())} />
        <ToolBtn label="Code" title="Code block" on={s.codeBlock} onRun={() => chain().toggleCodeBlock().run()} />
        <ToolBtn label="Columns" title="Two columns" on={s.columns} onRun={insertColumns} />
        <ToolBtn label="Accordion" title="Collapsible section" on={s.accordion} onRun={insertAccordion} />
        <ToolBtn label="―" title="Divider" onRun={() => chain().setHorizontalRule().run()} />
        <Sep />
        <ToolBtn label="+ Image" title="Insert image from library" onRun={onInsertImage} />
        <ToolBtn label="+ Table" title="Insert table" onRun={() => chain().insertTable({ rows: 3, cols: 3, withHeaderRow: true }).run()} />
        <ToolBtn label="+ Embed" title="Embed video, audio or form" onRun={() => setEmbedOpen(true)} />
        <span style={{ flex: 1 }} />
        <ToolBtn label="HTML" title="Edit HTML source" on={sourceMode} onRun={toggleSource} />
      </div>

      {(s.figure || s.table || s.columns || s.accordion) && !sourceMode && (
        <div style={{ display: "flex", flexWrap: "wrap", alignItems: "center", gap: 2, padding: "4px 6px", border: "1px solid #e0e4e9", borderTop: "none", background: "#f6f9fc" }}>
          {s.figure && (
            <>
              <span style={{ fontFamily: "var(--font-mono)", fontSize: 10, color: "#6a7c90", padding: "0 8px" }}>IMAGE</span>
              {(["full", "left", "right"] as const).map((a) => (
                <ToolBtn key={a} label={a === "full" ? "Full width" : a === "left" ? "Float left" : "Float right"} title={`Align ${a}`} on={s.align === a} onRun={() => chain().updateAttributes("figure", { align: a }).run()} />
              ))}
              <ToolBtn label="Remove" title="Remove image" onRun={() => chain().deleteNode("figure").run()} />
            </>
          )}
          {s.columns && (
            <>
              <span style={{ fontFamily: "var(--font-mono)", fontSize: 10, color: "#6a7c90", padding: "0 8px" }}>COLUMNS</span>
              <ToolBtn label="Delete" title="Delete columns" onRun={() => chain().deleteNode("columns").run()} />
            </>
          )}
          {s.accordion && (
            <>
              <span style={{ fontFamily: "var(--font-mono)", fontSize: 10, color: "#6a7c90", padding: "0 8px" }}>ACCORDION</span>
              <ToolBtn label="Delete" title="Delete accordion" onRun={() => chain().deleteNode("accordion").run()} />
            </>
          )}
          {s.table && (
            <>
              <span style={{ fontFamily: "var(--font-mono)", fontSize: 10, color: "#6a7c90", padding: "0 8px" }}>TABLE</span>
              <ToolBtn label="+ Row" title="Add row below" onRun={() => chain().addRowAfter().run()} />
              <ToolBtn label="+ Col" title="Add column right" onRun={() => chain().addColumnAfter().run()} />
              <ToolBtn label="− Row" title="Delete row" onRun={() => chain().deleteRow().run()} />
              <ToolBtn label="− Col" title="Delete column" onRun={() => chain().deleteColumn().run()} />
              <ToolBtn label="Header" title="Toggle header row" onRun={() => chain().toggleHeaderRow().run()} />
              <ToolBtn label="Merge" title="Merge / split cells" onRun={() => chain().mergeOrSplit().run()} />
              <ToolBtn label="Delete" title="Delete table" onRun={() => chain().deleteTable().run()} />
            </>
          )}
        </div>
      )}

      {sourceMode ? (
        <textarea
          value={sourceHtml}
          onChange={(e) => setSourceHtml(e.target.value)}
          spellCheck={false}
          style={{ display: "block", width: "100%", minHeight: 420, padding: 20, border: "none", outline: "none", fontFamily: "var(--font-mono)", fontSize: 12.5, lineHeight: 1.6, color: "#10141c", background: "#f6f9fc", resize: "vertical" }}
        />
      ) : (
        <EditorContent editor={editor} className="cms-rte-wrap" />
      )}

      <Modal title="Link" open={linkOpen} onOk={applyLink} onCancel={() => setLinkOpen(false)} okText="Apply" destroyOnHidden>
        <Input autoFocus value={linkUrl} onChange={(e) => setLinkUrl(e.target.value)} onPressEnter={applyLink} placeholder="https://… (empty removes the link)" />
        <label style={{ display: "flex", alignItems: "center", gap: 8, marginTop: 12, fontSize: 13 }}>
          <input type="checkbox" checked={linkButton} onChange={(e) => setLinkButton(e.target.checked)} />
          Show as a button
        </label>
        {s.link && (
          <button type="button" onClick={() => { chain().extendMarkRange("link").unsetLink().run(); setLinkOpen(false); }} style={{ marginTop: 10, border: "none", background: "none", color: "#d4380d", cursor: "pointer", padding: 0 }}>
            Remove link
          </button>
        )}
      </Modal>

      <Modal title="Embed video, audio or form" open={embedOpen} onOk={applyEmbed} onCancel={() => setEmbedOpen(false)} okText="Insert" destroyOnHidden>
        <Input autoFocus value={embedUrl} onChange={(e) => setEmbedUrl(e.target.value)} onPressEnter={applyEmbed} placeholder="Paste a YouTube, Vimeo, Loom, Spotify or form link" />
        <p style={{ marginTop: 10, fontSize: 12, color: "#6a7c90" }}>
          Allowed: YouTube, Vimeo, Loom, Spotify, Google Forms, Tally, Typeform, Jotform, Microsoft Forms.
        </p>
      </Modal>
    </div>
  );
});

export default RichTextEditor;
