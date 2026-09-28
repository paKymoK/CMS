import { useRef, useState, type CSSProperties } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { message } from "antd";
import { contentApi } from "../lib/api";
import { useSite } from "../lib/useSite";
import { SITES } from "../config/sites";
import MediaPickerModal from "../components/MediaPickerModal";
import RichTextEditor, { type RichTextEditorHandle } from "../components/RichTextEditor";
import { wordsAndOutline } from "../lib/richText";

const API_PATH = "/v1/admin/posts";

interface PostRecord {
  id: number;
  title: string;
  slug: string | null;
  excerpt: string | null;
  image: string | null;
  date: string | null;
  category: string | null;
  displayOrder: number;
  active: boolean;
  status: string;
  body: string | null;
  authorName: string | null;
  authorRole: string | null;
  tags: string[] | null;
  featured: boolean | null;
}

function slugify(text: string): string {
  return text
    .toLowerCase()
    .normalize("NFD")
    .replace(/[̀-ͯ]/g, "")
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/^-|-$/g, "")
    .split("-")
    .slice(0, 7)
    .join("-");
}

const inputStyle: CSSProperties = {
  border: "1px solid #dfe3e8",
  padding: "10px 12px",
  fontFamily: "'Helvetica Neue', Helvetica, Arial, sans-serif",
  fontSize: 14,
  color: "#10141c",
  outline: "none",
  width: "100%",
};

const cardLabelStyle: CSSProperties = {
  fontFamily: "var(--font-mono)",
  fontSize: 10,
  letterSpacing: ".12em",
  textTransform: "uppercase",
  color: "#5a5d64",
};

function initials(name: string): string {
  const parts = name.trim().split(/\s+/).filter(Boolean);
  if (parts.length >= 2) return (parts[0][0] + parts[1][0]).toUpperCase();
  return name.slice(0, 2).toUpperCase() || "—";
}

/** Thin data-fetching shell. The actual form is a separate component, keyed by id, so its state
 * is lazily initialized straight from server data on mount rather than copied in via an effect
 * (React's set-state-in-effect lint rule flags the latter as cascading-render-prone — and keying
 * by id also gives us a clean remount for free when navigating from one post's editor to another's). */
export default function PostEditorPage() {
  const { id } = useParams<{ id: string }>();
  const isNew = id === "new";
  const { site } = useSite();

  const detailQuery = useQuery({
    queryKey: ["post-editor", id, site],
    queryFn: async () => {
      const { data } = await contentApi.get(`${API_PATH}/${id}`, { params: { site } });
      return data.data as PostRecord;
    },
    enabled: !isNew && !!site && !!id,
  });

  if (!isNew && !detailQuery.data) {
    return <div style={{ padding: 48, textAlign: "center", color: "#6a7c90" }}>Loading…</div>;
  }

  return <PostEditorForm key={id} id={id!} isNew={isNew} initial={detailQuery.data ?? null} />;
}

function PostEditorForm({ id, isNew, initial }: { id: string; isNew: boolean; initial: PostRecord | null }) {
  const navigate = useNavigate();
  const { site } = useSite();
  const queryClient = useQueryClient();

  const [title, setTitle] = useState(initial?.title ?? "");
  const [slug, setSlug] = useState(initial?.slug ?? "");
  const [excerpt, setExcerpt] = useState(initial?.excerpt ?? "");
  const [image, setImage] = useState<string | null>(initial?.image ?? null);
  const [date, setDate] = useState(initial?.date ?? "");
  const [displayOrder, setDisplayOrder] = useState(initial?.displayOrder ?? 0);
  const [active, setActive] = useState(initial?.active ?? true);
  const [category, setCategory] = useState(initial?.category ?? "insight");
  const [status, setStatus] = useState<"DRAFT" | "PUBLISHED">((initial?.status as "DRAFT" | "PUBLISHED") ?? "DRAFT");
  const [body, setBody] = useState(initial?.body ?? "");
  const [authorName, setAuthorName] = useState(initial?.authorName ?? "");
  const [authorRole, setAuthorRole] = useState(initial?.authorRole ?? "");
  const [tags, setTags] = useState<string[]>(initial?.tags ?? []);
  const [tagDraft, setTagDraft] = useState("");
  const [featured, setFeatured] = useState(initial?.featured ?? false);
  const initialScan = wordsAndOutline(initial?.body ?? "");
  const [words, setWords] = useState(initialScan.words);
  const [outline, setOutline] = useState<{ n: string; text: string }[]>(initialScan.outline);
  const [dirty, setDirty] = useState(false);
  const [pickerOpen, setPickerOpen] = useState(false);
  const [pickerTarget, setPickerTarget] = useState<"cover" | "body">("cover");
  const rteRef = useRef<RichTextEditorHandle>(null);

  const onBodyChange = (html: string) => {
    setBody(html);
    setDirty(true);
    const scanned = wordsAndOutline(html);
    setWords(scanned.words);
    setOutline(scanned.outline);
  };

  const saveMutation = useMutation({
    mutationFn: async (nextStatus: "DRAFT" | "PUBLISHED") => {
      const payload = {
        ...(isNew ? {} : { id: Number(id) }),
        title,
        slug: slug || undefined,
        excerpt,
        image,
        date,
        displayOrder,
        active,
        category,
        status: nextStatus,
        body,
        authorName,
        authorRole,
        tags,
        featured,
      };
      if (isNew) return contentApi.post(API_PATH, payload, { params: { site } });
      return contentApi.put(API_PATH, payload, { params: { site } });
    },
    onSuccess: (res, nextStatus) => {
      setStatus(nextStatus);
      setDirty(false);
      message.success(nextStatus === "PUBLISHED" ? "Published" : "Saved as draft");
      queryClient.invalidateQueries({ queryKey: ["content", "posts", site] });
      if (isNew) {
        const created = res.data.data as PostRecord;
        navigate(`/posts/${created.id}`, { replace: true });
      } else {
        queryClient.invalidateQueries({ queryKey: ["post-editor", id, site] });
      }
    },
  });

  const addTag = () => {
    const t = slugify(tagDraft);
    if (t && !tags.includes(t)) setTags([...tags, t]);
    setTagDraft("");
    setDirty(true);
  };

  const siteSub = site ? SITES.find((s) => s.code === site)?.subdomain : undefined;
  const pub = status === "PUBLISHED";

  return (
    <div style={{ minHeight: "100vh", background: "#f6f9fc" }}>
      {/* Not sticky, unlike the standalone mockup — AppShell's own header already owns the
          viewport's top:0 sticky slot, and stacking a second one here would fight it. */}
      <div style={{ padding: "0 0 16px" }}>
        <div
          style={{
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            gap: 16,
            minHeight: 64,
            padding: "10px 12px 10px 18px",
            flexWrap: "wrap",
            borderRadius: 18,
            border: "1px solid rgba(24,159,224,.2)",
            background: "rgba(255,255,255,.88)",
            backdropFilter: "blur(24px)",
            boxShadow: "0 10px 34px rgba(10,37,64,.1), 0 2px 6px rgba(10,37,64,.05)",
          }}
        >
          <div style={{ display: "flex", alignItems: "center", gap: 14, minWidth: 0 }}>
            <button
              type="button"
              aria-label="Back to posts"
              onClick={() => navigate("/content/posts")}
              style={{
                width: 38,
                height: 38,
                flex: "none",
                borderRadius: "50%",
                border: "1px solid #cfd2d6",
                background: "transparent",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                color: "#3d4046",
                cursor: "pointer",
              }}
            >
              ←
            </button>
            <div style={{ display: "flex", flexDirection: "column", minWidth: 0 }}>
              <span style={{ ...cardLabelStyle, fontSize: 10 }}>
                Home Page / Posts / {isNew ? "New" : `ID ${id}`} · {site ? site.toUpperCase() : ""}
              </span>
              <span
                style={{
                  fontSize: 15,
                  fontWeight: 600,
                  color: "#10314f",
                  whiteSpace: "nowrap",
                  overflow: "hidden",
                  textOverflow: "ellipsis",
                  maxWidth: 320,
                }}
              >
                {title || "Untitled post"}
              </span>
            </div>
            <span
              style={{
                flex: "none",
                padding: "4px 10px",
                borderRadius: 999,
                fontFamily: "var(--font-mono)",
                fontSize: 10,
                letterSpacing: ".08em",
                background: pub ? "rgba(24,159,224,.12)" : "#eef1f4",
                color: pub ? "#0b63c5" : "#5a5d64",
              }}
            >
              {status}
            </span>
          </div>
          <div style={{ display: "flex", alignItems: "center", gap: 10, flexWrap: "wrap" }}>
            <span style={{ fontSize: 12.5, color: "#6a7c90", display: "flex", alignItems: "center", gap: 6 }}>
              <span
                style={{
                  width: 7,
                  height: 7,
                  borderRadius: "50%",
                  background: dirty ? "#fa8c16" : "#00bbe4",
                }}
              />
              {dirty ? "Unsaved changes" : isNew ? "Not saved yet" : "Saved"}
            </span>
            {siteSub && !isNew && slug && (
              <a
                href={`https://${siteSub}/insights/${slug}`}
                target="_blank"
                rel="noreferrer"
                style={{
                  minHeight: 40,
                  display: "flex",
                  alignItems: "center",
                  padding: "0 16px",
                  border: "1px solid #cfd2d6",
                  background: "#ffffff",
                  fontFamily: "var(--font-mono)",
                  fontSize: 11,
                  letterSpacing: ".1em",
                  color: "#3d4046",
                }}
              >
                PREVIEW ↗
              </a>
            )}
            <button
              type="button"
              onClick={() => saveMutation.mutate("DRAFT")}
              disabled={saveMutation.isPending}
              style={{
                minHeight: 40,
                padding: "0 16px",
                border: "1px solid rgba(24,159,224,.4)",
                background: "#ffffff",
                fontFamily: "var(--font-mono)",
                fontSize: 11,
                letterSpacing: ".1em",
                color: "#189fe0",
                cursor: "pointer",
              }}
            >
              SAVE DRAFT
            </button>
            <button
              type="button"
              onClick={() => saveMutation.mutate("PUBLISHED")}
              disabled={saveMutation.isPending}
              style={{
                minHeight: 40,
                padding: "0 20px",
                border: "none",
                background: "#189fe0",
                fontFamily: "var(--font-mono)",
                fontSize: 11,
                letterSpacing: ".1em",
                color: "#ffffff",
                cursor: "pointer",
              }}
            >
              {pub ? "UPDATE" : "PUBLISH"}
            </button>
          </div>
        </div>
      </div>

      <div
        style={{
          maxWidth: 1240,
          margin: "0 auto",
          padding: "28px clamp(16px,3vw,32px) 80px",
          display: "flex",
          flexWrap: "wrap",
          alignItems: "flex-start",
          gap: 24,
        }}
      >
        <main style={{ flex: "1 1 640px", minWidth: 0, background: "#ffffff", border: "1px solid #e0e4e9" }}>
          <div style={{ position: "relative" }}>
            {image ? (
              <img src={image} alt="" style={{ display: "block", width: "100%", aspectRatio: "21/8", objectFit: "cover" }} />
            ) : (
              <div style={{ width: "100%", aspectRatio: "21/8", background: "repeating-linear-gradient(135deg, #0c2447 0 6px, #143c6e 6px 12px)" }} />
            )}
            <div style={{ position: "absolute", right: 14, bottom: 14, display: "flex", gap: 8 }}>
              <button
                type="button"
                onClick={() => {
                  setPickerTarget("cover");
                  setPickerOpen(true);
                }}
                style={{
                  padding: "9px 14px",
                  border: "none",
                  background: "rgba(255,255,255,.94)",
                  fontFamily: "var(--font-mono)",
                  fontSize: 10,
                  letterSpacing: ".1em",
                  color: "#10314f",
                  cursor: "pointer",
                }}
              >
                CHANGE COVER
              </button>
            </div>
          </div>

          <div style={{ padding: "clamp(22px,4vw,44px) clamp(20px,5vw,56px) 12px", display: "flex", flexDirection: "column", gap: 18 }}>
            <textarea
              rows={2}
              value={title}
              onChange={(e) => {
                setTitle(e.target.value);
                setDirty(true);
              }}
              placeholder="Post title"
              style={{
                width: "100%",
                border: "none",
                resize: "none",
                padding: 0,
                fontFamily: "Montserrat, Arial, sans-serif",
                fontSize: "clamp(24px,3vw,34px)",
                lineHeight: 1.2,
                fontWeight: 700,
                letterSpacing: "-.02em",
                color: "#10314f",
                outline: "none",
                background: "transparent",
              }}
            />

            <div style={{ display: "flex", flexWrap: "wrap", alignItems: "center", gap: 10 }}>
              <div style={{ flex: 1, minWidth: 260, display: "flex", alignItems: "center", border: "1px solid #dfe3e8", background: "#f6f9fc" }}>
                <span style={{ padding: "0 0 0 12px", fontFamily: "var(--font-mono)", fontSize: 12, color: "#6a7c90", whiteSpace: "nowrap" }}>
                  {siteSub ?? "site"}/insights/
                </span>
                <input
                  value={slug}
                  onChange={(e) => {
                    setSlug(slugify(e.target.value));
                    setDirty(true);
                  }}
                  style={{ flex: 1, minWidth: 80, border: "none", background: "transparent", padding: "10px 12px 10px 2px", fontFamily: "var(--font-mono)", fontSize: 12, color: "#10141c", outline: "none" }}
                />
              </div>
              <button
                type="button"
                onClick={() => {
                  setSlug(slugify(title));
                  setDirty(true);
                }}
                style={{ padding: "10px 12px", border: "1px solid #cfd2d6", background: "#ffffff", fontFamily: "var(--font-mono)", fontSize: 10, letterSpacing: ".08em", color: "#3d4046", cursor: "pointer" }}
              >
                FROM TITLE
              </button>
            </div>

            <label style={{ display: "flex", flexDirection: "column", gap: 8 }}>
              <span style={{ display: "flex", justifyContent: "space-between", ...cardLabelStyle }}>
                <span>Excerpt</span>
                <span style={{ color: excerpt.length > 200 ? "#d4380d" : "#9aa3ad" }}>{excerpt.length} / 200</span>
              </span>
              <textarea
                rows={3}
                value={excerpt}
                onChange={(e) => {
                  setExcerpt(e.target.value);
                  setDirty(true);
                }}
                placeholder="One or two sentences shown on cards and under the title"
                style={{ border: "1px solid #dfe3e8", padding: "12px 14px", resize: "vertical", fontFamily: "'Helvetica Neue', Helvetica, Arial, sans-serif", fontSize: 15, lineHeight: 1.55, color: "#10141c", outline: "none" }}
              />
            </label>
          </div>

          <RichTextEditor
            ref={rteRef}
            value={body}
            onChange={onBodyChange}
            onInsertImage={() => {
              setPickerTarget("body");
              setPickerOpen(true);
            }}
          />

          <div
            style={{
              display: "flex",
              flexWrap: "wrap",
              justifyContent: "space-between",
              gap: 12,
              padding: "14px clamp(20px,5vw,56px)",
              borderTop: "1px solid #e0e4e9",
              background: "#f6f9fc",
              fontFamily: "var(--font-mono)",
              fontSize: 11,
              letterSpacing: ".06em",
              color: "#6a7c90",
            }}
          >
            <span>
              {words} WORDS · {Math.max(1, Math.round(words / 220))} MIN READ
            </span>
          </div>
        </main>

        <aside style={{ flex: "0 1 330px", minWidth: 280, display: "flex", flexDirection: "column", gap: 16, position: "sticky", top: 104 }}>
          <div style={{ background: "#ffffff", border: "1px solid #e0e4e9" }}>
            <div style={{ padding: "14px 18px", borderBottom: "1px solid #e0e4e9", ...cardLabelStyle }}>— Publishing</div>
            <div style={{ padding: "16px 18px", display: "flex", flexDirection: "column", gap: 14 }}>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 6, padding: 4, border: "1px solid #dfe3e8", borderRadius: 999, background: "#f6f9fc" }}>
                {(["DRAFT", "PUBLISHED"] as const).map((s) => (
                  <button
                    key={s}
                    type="button"
                    onClick={() => {
                      setStatus(s);
                      setDirty(true);
                    }}
                    style={{
                      height: 32,
                      border: "none",
                      borderRadius: 999,
                      cursor: "pointer",
                      fontFamily: "inherit",
                      fontSize: 12.5,
                      fontWeight: 600,
                      background: status === s ? "#0a2540" : "transparent",
                      color: status === s ? "#ffffff" : "#3c4858",
                    }}
                  >
                    {s === "DRAFT" ? "Draft" : "Published"}
                  </button>
                ))}
              </div>
              <label style={{ display: "flex", flexDirection: "column", gap: 6 }}>
                <span style={cardLabelStyle}>Display date</span>
                <input
                  value={date}
                  onChange={(e) => {
                    setDate(e.target.value);
                    setDirty(true);
                  }}
                  placeholder="Jun 2, 2026"
                  style={inputStyle}
                />
              </label>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
                <label style={{ display: "flex", flexDirection: "column", gap: 6 }}>
                  <span style={cardLabelStyle}>Order</span>
                  <input
                    type="number"
                    value={displayOrder}
                    onChange={(e) => {
                      setDisplayOrder(Number(e.target.value) || 0);
                      setDirty(true);
                    }}
                    style={inputStyle}
                  />
                </label>
                <div style={{ display: "flex", flexDirection: "column", gap: 6 }}>
                  <span style={cardLabelStyle}>Active</span>
                  <button
                    type="button"
                    aria-label="Toggle active"
                    onClick={() => {
                      setActive(!active);
                      setDirty(true);
                    }}
                    style={{
                      position: "relative",
                      width: 44,
                      height: 24,
                      marginTop: 6,
                      borderRadius: 999,
                      border: "none",
                      cursor: "pointer",
                      padding: 0,
                      background: active ? "#189fe0" : "#cfd2d6",
                      transition: "background .2s",
                    }}
                  >
                    <span
                      style={{
                        position: "absolute",
                        top: 3,
                        left: active ? 23 : 3,
                        width: 18,
                        height: 18,
                        borderRadius: "50%",
                        background: "#ffffff",
                        boxShadow: "0 1px 3px rgba(10,37,64,.25)",
                        transition: "left .2s",
                      }}
                    />
                  </button>
                </div>
              </div>
              <label style={{ display: "flex", flexDirection: "column", gap: 6 }}>
                <span style={cardLabelStyle}>Category</span>
                <input
                  value={category}
                  onChange={(e) => {
                    setCategory(e.target.value);
                    setDirty(true);
                  }}
                  style={inputStyle}
                />
              </label>
              <label style={{ display: "flex", alignItems: "center", gap: 8 }}>
                <input
                  type="checkbox"
                  checked={featured}
                  onChange={(e) => {
                    setFeatured(e.target.checked);
                    setDirty(true);
                  }}
                />
                <span style={cardLabelStyle}>Featured on Insights</span>
              </label>
            </div>
          </div>

          <div style={{ background: "#ffffff", border: "1px solid #e0e4e9" }}>
            <div style={{ padding: "14px 18px", borderBottom: "1px solid #e0e4e9", ...cardLabelStyle }}>— Author</div>
            <div style={{ padding: "14px 18px", display: "flex", flexDirection: "column", gap: 10 }}>
              <div style={{ display: "flex", alignItems: "center", gap: 10 }}>
                <span
                  style={{
                    width: 32,
                    height: 32,
                    flex: "none",
                    borderRadius: "50%",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    fontSize: 11,
                    fontWeight: 700,
                    color: "#ffffff",
                    background: "radial-gradient(circle at 34% 30%, #7cc4f7 0%, #1a6fc4 60%, #0c3f7d 100%)",
                  }}
                >
                  {authorName ? initials(authorName) : "—"}
                </span>
                <input
                  value={authorName}
                  onChange={(e) => {
                    setAuthorName(e.target.value);
                    setDirty(true);
                  }}
                  placeholder="Author name"
                  style={{ ...inputStyle, flex: 1 }}
                />
              </div>
              <input
                value={authorRole}
                onChange={(e) => {
                  setAuthorRole(e.target.value);
                  setDirty(true);
                }}
                placeholder="Role (e.g. Head of AI Advisory)"
                style={inputStyle}
              />
            </div>
          </div>

          <div style={{ background: "#ffffff", border: "1px solid #e0e4e9" }}>
            <div style={{ padding: "14px 18px", borderBottom: "1px solid #e0e4e9", ...cardLabelStyle }}>— Tags</div>
            <div style={{ padding: "14px 18px", display: "flex", flexWrap: "wrap", gap: 6 }}>
              {tags.map((t) => (
                <span
                  key={t}
                  style={{
                    display: "inline-flex",
                    alignItems: "center",
                    gap: 6,
                    padding: "5px 6px 5px 10px",
                    borderRadius: 999,
                    background: "#eaf4fb",
                    fontFamily: "var(--font-mono)",
                    fontSize: 11,
                    color: "#0b63c5",
                  }}
                >
                  #{t}
                  <button
                    type="button"
                    aria-label="Remove tag"
                    onClick={() => {
                      setTags(tags.filter((x) => x !== t));
                      setDirty(true);
                    }}
                    style={{ width: 18, height: 18, border: "none", borderRadius: "50%", background: "rgba(11,99,197,.12)", color: "#0b63c5", fontSize: 12, lineHeight: 1, cursor: "pointer" }}
                  >
                    ×
                  </button>
                </span>
              ))}
              <input
                value={tagDraft}
                onChange={(e) => setTagDraft(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === "Enter" || e.key === ",") {
                    e.preventDefault();
                    addTag();
                  }
                }}
                placeholder="Add tag + Enter"
                style={{ flex: 1, minWidth: 120, border: "none", padding: "6px 4px", fontFamily: "var(--font-mono)", fontSize: 11, color: "#10141c", outline: "none" }}
              />
            </div>
          </div>

          <div style={{ background: "#ffffff", border: "1px solid #e0e4e9" }}>
            <div style={{ padding: "14px 18px", borderBottom: "1px solid #e0e4e9", display: "flex", justifyContent: "space-between", alignItems: "center", gap: 8 }}>
              <span style={cardLabelStyle}>— Outline</span>
              <span style={{ fontFamily: "var(--font-mono)", fontSize: 10, color: "#9aa3ad" }}>FROM H2 · BUILDS TOC</span>
            </div>
            <div style={{ padding: "10px 18px 16px", display: "flex", flexDirection: "column" }}>
              {outline.map((o) => (
                <div key={o.n} style={{ display: "flex", gap: 10, padding: "7px 0", borderBottom: "1px solid #eef1f4", fontSize: 13, color: "#10314f" }}>
                  <span style={{ fontFamily: "var(--font-mono)", fontSize: 10, lineHeight: "19px", color: "#189fe0" }}>{o.n}</span>
                  {o.text}
                </div>
              ))}
              {outline.length === 0 && <span style={{ fontSize: 13, color: "#9aa3ad", paddingTop: 6 }}>Add H2 headings to build the table of contents.</span>}
            </div>
          </div>
        </aside>
      </div>

      <MediaPickerModal
        open={pickerOpen}
        onClose={() => setPickerOpen(false)}
        onSelect={(url) => {
          if (pickerTarget === "cover") {
            setImage(url);
          } else {
            rteRef.current?.insertHtml(
              `<figure><img src="${url}" alt=""><figcaption>Add a caption</figcaption></figure><p><br></p>`,
            );
          }
          setDirty(true);
          setPickerOpen(false);
        }}
      />
    </div>
  );
}
