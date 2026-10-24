import { useRef, useState, type CSSProperties } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { DatePicker, Input, Modal, message } from "antd";
import dayjs, { type Dayjs } from "dayjs";
import { contentApi } from "../lib/api";
import { useSite } from "../lib/useSite";
import { SITES } from "../config/sites";
import { mediaSrc } from "../lib/media";
import MediaPickerModal from "../components/MediaPickerModal";
import RichTextEditor, { type RichTextEditorHandle } from "../components/RichTextEditor";
import { wordsAndOutline } from "../lib/richText";
import PostPreview from "../components/PostPreview";
import PostTemplatePicker, { TEMPLATES_PATH } from "../components/PostTemplatePicker";
import HistoryDrawer from "../components/HistoryDrawer";
import { SeoChecklist } from "../components/SeoChecklist";
import SearchSnippetModal from "../components/SearchSnippetModal";
import { publishUnlessSeoMissing, seoChecks } from "../lib/seo";
import type { ItemPrefill } from "../lib/audit";
import type { PostTemplate } from "../lib/postTemplates";

const API_PATH = "/v1/admin/posts";

/** Draft: being written. Scheduled: goes live by itself at a set time. Published: on the site. Archived: taken off the site but kept. */
type PostStatus = "DRAFT" | "SCHEDULED" | "PUBLISHED" | "ARCHIVED";
const STATUS_LABELS: Record<PostStatus, string> = { DRAFT: "Draft", SCHEDULED: "Scheduled", PUBLISHED: "Published", ARCHIVED: "Archived" };
const TIME_FORMAT = "D MMM YYYY, HH:mm";
/** The editor's own time zone, shown beside the pickers: times are picked in it and stored as UTC. */
const ZONE = `${Intl.DateTimeFormat().resolvedOptions().timeZone} (UTC${dayjs().format("Z")})`;

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
  publishAt: string | null;
  unpublishAt: string | null;
  body: string | null;
  authorName: string | null;
  authorRole: string | null;
  tags: string[] | null;
  featured: boolean | null;
  layout: string | null;
  seoTitle: string | null;
  seoDescription: string | null;
  seoImage: string | null;
  seoNoindex: boolean | null;
}

const LAYOUTS = [
  { value: "default", label: "Standard", hint: "Header with cover, contents sidebar, article" },
  { value: "focused", label: "Focused", hint: "Centred article, no sidebar" },
  { value: "wide", label: "Wide", hint: "Full-width article, no sidebar" },
  { value: "landing", label: "Landing", hint: "Just the content: no header, author, share or related posts" },
] as const;

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
  border: "1px solid #dde4f0",
  borderRadius: 10,
  padding: "10px 12px",
  fontFamily: "inherit",
  fontSize: 14,
  color: "#0b1b3a",
  outline: "none",
  width: "100%",
};

const cardLabelStyle: CSSProperties = {
  fontSize: 12,
  fontWeight: 600,
  color: "#5b6b86",
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
/**
 * A deleted post's content as the start of a new one (Activity → "Create a new item from this
 * version"): same content, but no id, no slug (the server picks a free one), a draft.
 */
function prefillAsPost(prefill: ItemPrefill): PostRecord {
  return {
    ...(prefill.data as unknown as PostRecord),
    id: 0,
    slug: null,
    displayOrder: prefill.displayOrder,
    active: prefill.active,
    status: "DRAFT",
  };
}

export default function PostEditorPage() {
  const { id } = useParams<{ id: string }>();
  const isNew = id === "new";
  const { site } = useSite();
  const location = useLocation();
  // Set when a new item starts from a deleted item's content (see ItemPrefill).
  const prefill = isNew ? ((location.state as { prefill?: ItemPrefill } | null)?.prefill ?? null) : null;
  // A new post starts from a template (Blank is one of them); existing posts open directly.
  const [seed, setSeed] = useState<PostTemplate | null>(null);

  const queryClient = useQueryClient();
  // A restore changes the item behind the form: refetch it, then remount the form on the new copy.
  const [restoreCount, setRestoreCount] = useState(0);
  const detailQuery = useQuery({
    queryKey: ["post-editor", id, site],
    queryFn: async () => {
      const { data } = await contentApi.get(`${API_PATH}/${id}`, { params: { site } });
      return data.data as PostRecord;
    },
    enabled: !isNew && !!site && !!id,
  });

  if (!isNew && !detailQuery.data) {
    return <div style={{ padding: 48, textAlign: "center", color: "#7b8aa5" }}>Loading…</div>;
  }

  if (isNew && !seed && !prefill) return <PostTemplatePicker onPick={setSeed} />;

  return <PostEditorForm onRestored={async () => { await queryClient.refetchQueries({ queryKey: ["post-editor", id, site] }); setRestoreCount((n) => n + 1); }} key={`${id}-${restoreCount}`} id={id!} isNew={isNew} initial={isNew ? (prefill ? prefillAsPost(prefill) : null) : (detailQuery.data ?? null)} seed={seed} />;
}

function PostEditorForm({ id, isNew, initial, seed, onRestored }: { id: string; isNew: boolean; initial: PostRecord | null; seed: PostTemplate | null; onRestored?: () => void }) {
  const navigate = useNavigate();
  const { site } = useSite();
  const queryClient = useQueryClient();
  const [historyOpen, setHistoryOpen] = useState(false);

  const [title, setTitle] = useState(initial?.title ?? "");
  const [slug, setSlug] = useState(initial?.slug ?? "");
  const [excerpt, setExcerpt] = useState(initial?.excerpt ?? "");
  const [image, setImage] = useState<string | null>(initial?.image ?? null);
  const [date, setDate] = useState(initial?.date ?? "");
  const [displayOrder, setDisplayOrder] = useState(initial?.displayOrder ?? 0);
  const [active, setActive] = useState(initial?.active ?? true);
  const [category, setCategory] = useState(initial?.category ?? seed?.category ?? "insight");
  const [status, setStatus] = useState<PostStatus>((initial?.status as PostStatus) ?? "DRAFT");
  // Picked in the editor's time zone; sent as UTC. The job that acts on them runs once a minute.
  const [publishAt, setPublishAt] = useState<Dayjs | null>(initial?.publishAt ? dayjs(initial.publishAt) : null);
  const [unpublishAt, setUnpublishAt] = useState<Dayjs | null>(initial?.unpublishAt ? dayjs(initial.unpublishAt) : null);
  const [body, setBody] = useState(initial?.body ?? seed?.body ?? "");
  const [authorName, setAuthorName] = useState(initial?.authorName ?? "");
  const [authorRole, setAuthorRole] = useState(initial?.authorRole ?? "");
  const [tags, setTags] = useState<string[]>(initial?.tags ?? seed?.tags ?? []);
  const [tagDraft, setTagDraft] = useState("");
  const [featured, setFeatured] = useState(initial?.featured ?? false);
  const [layout, setLayout] = useState(initial?.layout ?? seed?.layout ?? "default");
  const [seoTitle, setSeoTitle] = useState(initial?.seoTitle ?? "");
  const [seoDescription, setSeoDescription] = useState(initial?.seoDescription ?? "");
  const [seoImage, setSeoImage] = useState(initial?.seoImage ?? "");
  const [seoNoindex, setSeoNoindex] = useState(initial?.seoNoindex ?? false);
  const initialScan = wordsAndOutline(initial?.body ?? seed?.body ?? "");
  const [words, setWords] = useState(initialScan.words);
  const [outline, setOutline] = useState<{ n: string; text: string }[]>(initialScan.outline);
  const [dirty, setDirty] = useState(false);
  const [pickerOpen, setPickerOpen] = useState(false);
  const [pickerTarget, setPickerTarget] = useState<"cover" | "body" | "social">("cover");
  const rteRef = useRef<RichTextEditorHandle>(null);

  const onBodyChange = (html: string) => {
    setBody(html);
    setDirty(true);
    const scanned = wordsAndOutline(html);
    setWords(scanned.words);
    setOutline(scanned.outline);
  };

  const seoValues = {
    seoTitle,
    seoDescription,
    seoImage,
    fallbackTitle: `${title || "Untitled post"} — CMC Global Insights`,
    // Left blank, the excerpt is taken from the start of the post, as the server does.
    fallbackDescription: excerpt.trim() || body.replace(/<[^>]*>/g, " ").replace(/\s+/g, " ").trim(),
    fallbackImage: image ?? "",
    noindex: seoNoindex,
  };
  const seoCheckList = seoChecks({ ...seoValues, slug: { value: slug, url: `/insights/${slug || "…"}` } });
  const [snippetOpen, setSnippetOpen] = useState(false);

  const saveMutation = useMutation({
    mutationFn: async (nextStatus: PostStatus) => {
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
        publishAt: nextStatus === "SCHEDULED" && publishAt ? publishAt.toISOString() : null,
        unpublishAt: (nextStatus === "SCHEDULED" || nextStatus === "PUBLISHED") && unpublishAt ? unpublishAt.toISOString() : null,
        body,
        authorName,
        authorRole,
        tags,
        featured,
        layout,
        seoTitle,
        seoDescription,
        seoImage,
        seoNoindex,
      };
      if (isNew) return contentApi.post(API_PATH, payload, { params: { site } });
      return contentApi.put(API_PATH, payload, { params: { site } });
    },
    onSuccess: (res, nextStatus) => {
      setStatus(nextStatus);
      setDirty(false);
      if (nextStatus !== "SCHEDULED") setPublishAt(null);
      if (nextStatus === "DRAFT" || nextStatus === "ARCHIVED") setUnpublishAt(null);
      message.success(
        nextStatus === "PUBLISHED" ? "Published" : nextStatus === "SCHEDULED" ? "Scheduled" : nextStatus === "ARCHIVED" ? "Archived" : "Saved as draft",
      );
      queryClient.invalidateQueries({ queryKey: ["content", "posts", site] });
      if (isNew) {
        const created = res.data.data as PostRecord;
        navigate(`/posts/${created.id}`, { replace: true });
      } else {
        queryClient.invalidateQueries({ queryKey: ["post-editor", id, site] });
      }
    },
  });

  const [previewOpen, setPreviewOpen] = useState(false);

  const [templateOpen, setTemplateOpen] = useState(false);
  const [templateName, setTemplateName] = useState("");
  const templateMutation = useMutation({
    mutationFn: () =>
      contentApi.post(TEMPLATES_PATH, { name: templateName.trim(), description: title ? `Saved from “${title}”` : null, layout, category, tags, body }, { params: { site } }),
    onSuccess: () => {
      message.success("Saved as a template");
      queryClient.invalidateQueries({ queryKey: ["post-templates", site] });
      setTemplateOpen(false);
      setTemplateName("");
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
  const scheduled = status === "SCHEDULED";
  // Checked here for a quick answer; the server checks again.
  const scheduleProblem = !scheduled
    ? null
    : !publishAt
      ? "Choose when the post should be published."
      : !publishAt.isAfter(dayjs()) && !(initial?.publishAt && dayjs(initial.publishAt).isSame(publishAt))
        ? "The publish time must be in the future."
        : unpublishAt && !unpublishAt.isAfter(publishAt)
          ? "The unpublish time must be after the publish time."
          : null;

  return (
    <div style={{ minHeight: "100vh" }}>
      {/* Not sticky — AppShell's own header already owns the
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
            borderRadius: 16,
            border: "1px solid #e3e9f4",
            background: "#ffffff",
            boxShadow: "0 8px 24px rgba(10,30,70,.04), 0 1px 2px rgba(10,30,70,.04)",
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
                borderRadius: 10,
                border: "1px solid #dde4f0",
                background: "transparent",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                color: "#26354f",
                cursor: "pointer",
              }}
            >
              ←
            </button>
            <div style={{ display: "flex", flexDirection: "column", minWidth: 0 }}>
              <span style={{ ...cardLabelStyle, fontSize: 10 }}>
                Perspectives / Posts / {isNew ? "New" : `ID ${id}`} · {site ? site.toUpperCase() : ""}
              </span>
              <span
                style={{
                  fontSize: 15,
                  fontWeight: 600,
                  color: "#0b1b3a",
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
                fontFamily: "inherit", fontSize: 12, fontWeight: 600,
                background: pub ? "#e3f7ee" : scheduled ? "#e6f1ff" : status === "ARCHIVED" ? "#eceff4" : "#fff4e0",
                color: pub ? "#0b7a55" : scheduled ? "#0058b3" : status === "ARCHIVED" ? "#5b6b86" : "#9a5b00",
              }}
            >
              {STATUS_LABELS[status]}
            </span>
          </div>
          <div style={{ display: "flex", alignItems: "center", gap: 10, flexWrap: "wrap" }}>
            <span style={{ fontSize: 12.5, color: "#7b8aa5", display: "flex", alignItems: "center", gap: 6 }}>
              <span
                style={{
                  width: 7,
                  height: 7,
                  borderRadius: "50%",
                  background: dirty ? "#fa8c16" : "#00bbff",
                }}
              />
              {dirty ? "Unsaved changes" : isNew ? "Not saved yet" : "Saved"}
            </span>
            {site && (
              <button
                type="button"
                onClick={() => setPreviewOpen(true)}
                style={{
                  minHeight: 40,
                  display: "flex",
                  alignItems: "center",
                  padding: "0 16px",
                  border: "1px solid #dde4f0", borderRadius: 10,
                  background: "#ffffff",
                  fontFamily: "inherit", fontSize: 13, fontWeight: 600,
                  color: "#26354f",
                  cursor: "pointer",
                }}
              >
                Preview
              </button>
            )}
            {!isNew && (
              <button
                type="button"
                onClick={() => setHistoryOpen(true)}
                style={{
                  minHeight: 40,
                  display: "flex",
                  alignItems: "center",
                  padding: "0 16px",
                  border: "1px solid #dde4f0", borderRadius: 10,
                  background: "#ffffff",
                  fontFamily: "inherit", fontSize: 13, fontWeight: 600,
                  color: "#26354f",
                  cursor: "pointer",
                }}
              >
                History
              </button>
            )}
            <button
              type="button"
              onClick={() => setTemplateOpen(true)}
              style={{ minHeight: 40, padding: "0 16px", border: "1px solid #dde4f0", borderRadius: 10, background: "#ffffff", fontFamily: "inherit", fontSize: 13, fontWeight: 600, color: "#26354f", cursor: "pointer" }}
            >
              Save as template
            </button>
            <button
              type="button"
              onClick={() => saveMutation.mutate("DRAFT")}
              disabled={saveMutation.isPending}
              style={{
                minHeight: 40,
                padding: "0 16px",
                border: "1px solid #bcd9fb", borderRadius: 10,
                background: "#ffffff",
                fontFamily: "inherit", fontSize: 13, fontWeight: 600,
                color: "#0083ff",
                cursor: "pointer",
              }}
            >
              {status === "ARCHIVED" ? "Move to draft" : "Save draft"}
            </button>
            {status === "ARCHIVED" ? (
              <button
                type="button"
                onClick={() => saveMutation.mutate("ARCHIVED")}
                disabled={saveMutation.isPending}
                style={{ minHeight: 40, padding: "0 16px", border: "1px solid #dde4f0", borderRadius: 10, background: "#ffffff", fontFamily: "inherit", fontSize: 13, fontWeight: 600, color: "#26354f", cursor: "pointer" }}
              >
                Save
              </button>
            ) : !isNew ? (
              <button
                type="button"
                onClick={() =>
                  status === "PUBLISHED"
                    ? Modal.confirm({
                        title: "Archive this post?",
                        content: "It is taken off the site and its address stops working, but the post is kept. You can publish it again or move it back to draft.",
                        okText: "Archive",
                        cancelText: "Cancel",
                        onOk: () => saveMutation.mutate("ARCHIVED"),
                      })
                    : saveMutation.mutate("ARCHIVED")
                }
                disabled={saveMutation.isPending}
                style={{ minHeight: 40, padding: "0 16px", border: "1px solid #dde4f0", borderRadius: 10, background: "#ffffff", fontFamily: "inherit", fontSize: 13, fontWeight: 600, color: "#26354f", cursor: "pointer" }}
              >
                Archive
              </button>
            ) : null}
            <button
              type="button"
              onClick={() => {
                if (scheduleProblem) {
                  message.error(scheduleProblem);
                  return;
                }
                const goal: PostStatus = scheduled ? "SCHEDULED" : "PUBLISHED";
                // Only a post that is not live yet gets the SEO prompt; updating one never asks.
                return status === "PUBLISHED" || initial?.status === goal
                  ? saveMutation.mutate(goal)
                  : publishUnlessSeoMissing(seoCheckList, () => saveMutation.mutate(goal));
              }}
              disabled={saveMutation.isPending}
              style={{
                minHeight: 40,
                padding: "0 20px",
                border: "none",
                borderRadius: 10,
                background: "#0083ff",
                fontFamily: "inherit", fontSize: 13, fontWeight: 600,
                color: "#ffffff",
                cursor: "pointer",
              }}
            >
              {scheduled ? (initial?.status === "SCHEDULED" ? "Update schedule" : "Schedule") : pub ? "Update" : "Publish"}
            </button>
          </div>
        </div>
      </div>

      <div
        className="cms-editor-body"
        style={{
          margin: "0 auto",
          padding: "28px clamp(16px,3vw,32px) 80px",
          display: "flex",
          flexWrap: "wrap",
          alignItems: "flex-start",
          gap: 24,
        }}
      >
        <main style={{ flex: "1 1 640px", minWidth: 0, background: "#ffffff", border: "1px solid #e3e9f4", borderRadius: 16, overflow: "clip", boxShadow: "0 1px 2px rgba(10,30,70,.04)" }}>
          {image ? (
            <div style={{ position: "relative" }}>
              <img src={mediaSrc(image)} alt="" style={{ display: "block", width: "100%", aspectRatio: "21/8", objectFit: "cover" }} />
              <div style={{ position: "absolute", right: 14, bottom: 14, display: "flex", gap: 8 }}>
                {(["CHANGE COVER", "REMOVE"] as const).map((l) => (
                  <button
                    key={l}
                    type="button"
                    onClick={() => {
                      if (l === "REMOVE") {
                        setImage(null);
                        setDirty(true);
                      } else {
                        setPickerTarget("cover");
                        setPickerOpen(true);
                      }
                    }}
                    style={{ padding: "9px 14px", border: "none", background: "rgba(255,255,255,.94)", fontFamily: "inherit", fontSize: 13, fontWeight: 600, color: "#0b1b3a", cursor: "pointer" }}
                  >
                    {l === "REMOVE" ? "Remove" : "Change cover"}
                  </button>
                ))}
              </div>
            </div>
          ) : (
            <div style={{ padding: "14px clamp(20px,5vw,56px) 0" }}>
              <button
                type="button"
                onClick={() => {
                  setPickerTarget("cover");
                  setPickerOpen(true);
                }}
                style={{ padding: "8px 12px", border: "1px dashed #b7cbe8", borderRadius: 10, background: "transparent", fontFamily: "inherit", fontSize: 13, fontWeight: 600, color: "#7b8aa5", cursor: "pointer" }}
              >
                + ADD COVER IMAGE (OPTIONAL)
              </button>
            </div>
          )}

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
                fontFamily: "Inter, 'Helvetica Neue', Helvetica, Arial, sans-serif",
                fontSize: "clamp(24px,3vw,34px)",
                lineHeight: 1.2,
                fontWeight: 700,
                letterSpacing: "-.02em",
                color: "#0b1b3a",
                outline: "none",
                background: "transparent",
              }}
            />

            <div style={{ display: "flex", flexWrap: "wrap", alignItems: "center", gap: 10 }}>
              <div style={{ flex: 1, minWidth: 260, display: "flex", alignItems: "center", border: "1px solid #dde4f0", background: "#f4f7fc" }}>
                <span style={{ padding: "0 0 0 12px", fontFamily: "var(--font-mono)", fontSize: 12, color: "#7b8aa5", whiteSpace: "nowrap" }}>
                  {siteSub ?? "site"}/insights/
                </span>
                <input
                  value={slug}
                  onChange={(e) => {
                    setSlug(slugify(e.target.value));
                    setDirty(true);
                  }}
                  style={{ flex: 1, minWidth: 80, border: "none", background: "transparent", padding: "10px 12px 10px 2px", fontFamily: "var(--font-mono)", fontSize: 12, color: "#0b1b3a", outline: "none" }}
                />
              </div>
              <button
                type="button"
                onClick={() => {
                  setSlug(slugify(title));
                  setDirty(true);
                }}
                style={{ padding: "10px 12px", border: "1px solid #dde4f0", borderRadius: 10, background: "#ffffff", fontFamily: "inherit", fontSize: 13, fontWeight: 600, color: "#26354f", cursor: "pointer" }}
              >
                From title
              </button>
            </div>

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
              borderTop: "1px solid #e3e9f4",
              background: "#f4f7fc",
              fontFamily: "var(--font-mono)",
              fontSize: 11,
              letterSpacing: ".06em",
              color: "#7b8aa5",
            }}
          >
            <span>
              {words} WORDS · {Math.max(1, Math.round(words / 220))} MIN READ
            </span>
          </div>
        </main>

        <aside style={{ flex: "0 1 330px", minWidth: 280, display: "flex", flexDirection: "column", gap: 16, position: "sticky", top: 88 }}>
          <div style={{ background: "#ffffff", border: "1px solid #e3e9f4", borderRadius: 16, overflow: "clip", boxShadow: "0 1px 2px rgba(10,30,70,.04)" }}>
            <div style={{ padding: "14px 18px", borderBottom: "1px solid #e3e9f4", ...cardLabelStyle }}>Publishing</div>
            <div style={{ padding: "16px 18px", display: "flex", flexDirection: "column", gap: 14 }}>
              <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 6, padding: 4, border: "1px solid #dde4f0", borderRadius: 12, background: "#e8eef8" }}>
                {(["DRAFT", "SCHEDULED", "PUBLISHED", "ARCHIVED"] as const).map((s) => (
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
                      borderRadius: 9,
                      cursor: "pointer",
                      fontFamily: "inherit",
                      fontSize: 12.5,
                      fontWeight: 600,
                      background: status === s ? "#ffffff" : "transparent",
                      boxShadow: status === s ? "0 1px 3px rgba(10,30,70,.12)" : "none",
                      color: status === s ? "#0b1b3a" : "#5b6b86",
                    }}
                  >
                    {STATUS_LABELS[s]}
                  </button>
                ))}
              </div>
              {status === "ARCHIVED" && (
                <span style={{ fontSize: 12.5, color: "#7b8aa5", lineHeight: 1.45 }}>
                  Archived: not on the site, but kept. Use Save to keep it archived, or Publish / Move to draft to bring it back.
                </span>
              )}
              {scheduled && (
                <label style={{ display: "flex", flexDirection: "column", gap: 6 }}>
                  <span style={cardLabelStyle}>Publish at</span>
                  <DatePicker
                    showTime={{ format: "HH:mm" }}
                    format={TIME_FORMAT}
                    value={publishAt}
                    minDate={dayjs()}
                    onChange={(v) => {
                      setPublishAt(v ? v.startOf("minute") : null);
                      setDirty(true);
                    }}
                    style={{ width: "100%" }}
                  />
                </label>
              )}
              {(scheduled || pub) && (
                <label style={{ display: "flex", flexDirection: "column", gap: 6 }}>
                  <span style={cardLabelStyle}>Take offline at (optional)</span>
                  <DatePicker
                    showTime={{ format: "HH:mm" }}
                    format={TIME_FORMAT}
                    value={unpublishAt}
                    minDate={dayjs()}
                    allowClear
                    onChange={(v) => {
                      setUnpublishAt(v ? v.startOf("minute") : null);
                      setDirty(true);
                    }}
                    style={{ width: "100%" }}
                  />
                </label>
              )}
              {(scheduled || (pub && unpublishAt)) && (
                <span style={{ fontSize: 12.5, color: scheduleProblem ? "#c4341c" : "#7b8aa5", lineHeight: 1.45 }}>
                  {scheduleProblem ??
                    `Times are in your time zone, ${ZONE}. The site checks once a minute, so a change can take up to a minute to show.`}
                </span>
              )}
              <label style={{ display: "flex", flexDirection: "column", gap: 6 }}>
                <span style={cardLabelStyle}>Display date</span>
                <input
                  value={date}
                  onChange={(e) => {
                    setDate(e.target.value);
                    setDirty(true);
                  }}
                  placeholder="Blank = today, when published"
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
                      background: active ? "#0083ff" : "#cfd2d6",
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

          <div style={{ background: "#ffffff", border: "1px solid #e3e9f4", borderRadius: 16, overflow: "clip", boxShadow: "0 1px 2px rgba(10,30,70,.04)" }}>
            <div style={{ padding: "14px 18px", borderBottom: "1px solid #e3e9f4", ...cardLabelStyle }}>Page layout</div>
            <div style={{ padding: "12px 18px", display: "flex", flexDirection: "column", gap: 8 }}>
              {LAYOUTS.map((l) => (
                <label key={l.value} style={{ display: "flex", gap: 10, alignItems: "flex-start", cursor: "pointer" }}>
                  <input
                    type="radio"
                    name="layout"
                    checked={layout === l.value}
                    onChange={() => {
                      setLayout(l.value);
                      setDirty(true);
                    }}
                    style={{ marginTop: 3 }}
                  />
                  <span style={{ display: "flex", flexDirection: "column" }}>
                    <span style={{ fontSize: 13.5, fontWeight: 600, color: "#0b1b3a" }}>{l.label}</span>
                    <span style={{ fontSize: 12, color: "#7b8aa5" }}>{l.hint}</span>
                  </span>
                </label>
              ))}
            </div>
          </div>

          <div style={{ background: "#ffffff", border: "1px solid #e3e9f4", borderRadius: 16, overflow: "clip", boxShadow: "0 1px 2px rgba(10,30,70,.04)" }}>
            <div style={{ padding: "14px 18px", borderBottom: "1px solid #e3e9f4", ...cardLabelStyle }}>Summary (optional)</div>
            <div style={{ padding: "14px 18px" }}>
            <label style={{ display: "flex", flexDirection: "column", gap: 8 }}>
              <span style={{ display: "flex", justifyContent: "space-between", ...cardLabelStyle }}>
                <span>Excerpt</span>
                <span style={{ color: excerpt.length > 200 ? "#d4380d" : "#7b8aa5" }}>{excerpt.length} / 200</span>
              </span>
              <textarea
                rows={3}
                value={excerpt}
                onChange={(e) => {
                  setExcerpt(e.target.value);
                  setDirty(true);
                }}
                placeholder="Left blank, it is taken from the start of the post"
                style={{ border: "1px solid #dde4f0", padding: "12px 14px", resize: "vertical", fontFamily: "'Helvetica Neue', Helvetica, Arial, sans-serif", fontSize: 15, lineHeight: 1.55, color: "#0b1b3a", outline: "none" }}
              />
            </label>
            </div>
          </div>

          <div style={{ background: "#ffffff", border: "1px solid #e3e9f4", borderRadius: 16, overflow: "clip", boxShadow: "0 1px 2px rgba(10,30,70,.04)" }}>
            <div style={{ padding: "14px 18px", borderBottom: "1px solid #e3e9f4", ...cardLabelStyle }}>SEO (optional)</div>
            <div style={{ padding: "14px 18px", display: "flex", flexDirection: "column", gap: 12 }}>
              <label style={{ display: "flex", flexDirection: "column", gap: 6 }}>
                <span style={{ display: "flex", justifyContent: "space-between", ...cardLabelStyle }}>
                  <span>Meta title</span>
                  <span style={{ color: seoTitle.length > 60 ? "#d4380d" : "#7b8aa5" }}>{seoTitle.length} / 60</span>
                </span>
                <input
                  value={seoTitle}
                  onChange={(e) => {
                    setSeoTitle(e.target.value);
                    setDirty(true);
                  }}
                  placeholder="The whole page title in search results"
                  style={inputStyle}
                />
              </label>
              <label style={{ display: "flex", flexDirection: "column", gap: 6 }}>
                <span style={{ display: "flex", justifyContent: "space-between", ...cardLabelStyle }}>
                  <span>Meta description</span>
                  <span style={{ color: seoDescription.length > 160 ? "#d4380d" : "#7b8aa5" }}>{seoDescription.length} / 160</span>
                </span>
                <textarea
                  rows={3}
                  value={seoDescription}
                  onChange={(e) => {
                    setSeoDescription(e.target.value);
                    setDirty(true);
                  }}
                  placeholder="Left blank, the excerpt is used"
                  style={{ border: "1px solid #dde4f0", padding: "10px 12px", resize: "vertical", fontFamily: "inherit", fontSize: 14, lineHeight: 1.5, color: "#0b1b3a", outline: "none" }}
                />
              </label>
              <div style={{ display: "flex", flexDirection: "column", gap: 6 }}>
                <span style={cardLabelStyle}>Share image</span>
                <div style={{ display: "flex", alignItems: "center", gap: 10 }}>
                  {seoImage ? <img src={mediaSrc(seoImage)} alt="" style={{ width: 64, height: 34, objectFit: "cover", borderRadius: 4 }} /> : null}
                  <button
                    type="button"
                    onClick={() => {
                      setPickerTarget("social");
                      setPickerOpen(true);
                    }}
                    style={{ padding: "7px 12px", border: "1px solid #dde4f0", borderRadius: 8, background: "#ffffff", fontFamily: "inherit", fontSize: 12.5, fontWeight: 600, color: "#26354f", cursor: "pointer" }}
                  >
                    {seoImage ? "Change" : "Choose"}
                  </button>
                  {seoImage ? (
                    <button
                      type="button"
                      onClick={() => {
                        setSeoImage("");
                        setDirty(true);
                      }}
                      style={{ padding: "7px 12px", border: "none", background: "transparent", fontFamily: "inherit", fontSize: 12.5, color: "#7b8aa5", cursor: "pointer" }}
                    >
                      Remove
                    </button>
                  ) : null}
                </div>
                <span style={{ fontSize: 12, color: "#7b8aa5" }}>Left empty, the cover image is used. Around 1200 × 630 works best.</span>
              </div>
              <label style={{ display: "flex", alignItems: "center", gap: 8 }}>
                <input
                  type="checkbox"
                  checked={seoNoindex}
                  onChange={(e) => {
                    setSeoNoindex(e.target.checked);
                    setDirty(true);
                  }}
                />
                <span style={cardLabelStyle}>Hide from search engines</span>
              </label>
              <SeoChecklist checks={seoCheckList} onPreview={() => setSnippetOpen(true)} />
            </div>
          </div>

          <div style={{ background: "#ffffff", border: "1px solid #e3e9f4", borderRadius: 16, overflow: "clip", boxShadow: "0 1px 2px rgba(10,30,70,.04)" }}>
            <div style={{ padding: "14px 18px", borderBottom: "1px solid #e3e9f4", ...cardLabelStyle }}>Author (optional)</div>
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
                    background: "radial-gradient(circle at 34% 30%, #00bbff 0%, #0083ff 60%, #002d68 100%)",
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

          <div style={{ background: "#ffffff", border: "1px solid #e3e9f4", borderRadius: 16, overflow: "clip", boxShadow: "0 1px 2px rgba(10,30,70,.04)" }}>
            <div style={{ padding: "14px 18px", borderBottom: "1px solid #e3e9f4", ...cardLabelStyle }}>Tags</div>
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
                    color: "#0159be",
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
                    style={{ width: 18, height: 18, border: "none", borderRadius: "50%", background: "rgba(1,89,190,.12)", color: "#0159be", fontSize: 12, lineHeight: 1, cursor: "pointer" }}
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
                style={{ flex: 1, minWidth: 120, border: "none", padding: "6px 4px", fontFamily: "var(--font-mono)", fontSize: 11, color: "#0b1b3a", outline: "none" }}
              />
            </div>
          </div>

          <div style={{ background: "#ffffff", border: "1px solid #e3e9f4", borderRadius: 16, overflow: "clip", boxShadow: "0 1px 2px rgba(10,30,70,.04)" }}>
            <div style={{ padding: "14px 18px", borderBottom: "1px solid #e3e9f4", display: "flex", justifyContent: "space-between", alignItems: "center", gap: 8 }}>
              <span style={cardLabelStyle}>Outline</span>
              <span style={{ fontFamily: "var(--font-mono)", fontSize: 10, color: "#7b8aa5" }}>From H2 headings · builds the TOC</span>
            </div>
            <div style={{ padding: "10px 18px 16px", display: "flex", flexDirection: "column" }}>
              {outline.map((o) => (
                <div key={o.n} style={{ display: "flex", gap: 10, padding: "7px 0", borderBottom: "1px solid #eef1f4", fontSize: 13, color: "#0b1b3a" }}>
                  <span style={{ fontFamily: "var(--font-mono)", fontSize: 10, lineHeight: "19px", color: "#0083ff" }}>{o.n}</span>
                  {o.text}
                </div>
              ))}
              {outline.length === 0 && <span style={{ fontSize: 13, color: "#7b8aa5", paddingTop: 6 }}>Add H2 headings to build the table of contents.</span>}
            </div>
          </div>
        </aside>
      </div>

      <Modal
        title="Save as template"
        open={templateOpen}
        onOk={() => templateName.trim() && templateMutation.mutate()}
        okButtonProps={{ disabled: !templateName.trim(), loading: templateMutation.isPending }}
        onCancel={() => setTemplateOpen(false)}
        okText="Save"
        destroyOnHidden
      >
        <Input autoFocus value={templateName} onChange={(e) => setTemplateName(e.target.value)} onPressEnter={() => templateName.trim() && templateMutation.mutate()} placeholder="Template name, e.g. Monthly newsletter" maxLength={80} />
        <p style={{ marginTop: 10, fontSize: 12, color: "#7b8aa5" }}>
          Saves the current content, layout, category and tags as a starting point for new posts. Title, cover, author and dates are not included.
        </p>
      </Modal>

      {!isNew && (
        <HistoryDrawer
          open={historyOpen}
          onClose={() => setHistoryOpen(false)}
          itemId={Number(id)}
          onRestored={onRestored}
        />
      )}
      <SearchSnippetModal
        open={snippetOpen}
        onClose={() => setSnippetOpen(false)}
        input={{ ...seoValues, host: siteSub ?? "site", path: `/insights/${slug || "…"}` }}
      />
      {previewOpen && site && (
        <PostPreview
          site={site}
          slug={slug}
          draft={{ id: isNew ? undefined : Number(id), title, image, date, category, body, authorName, authorRole, layout }}
          onClose={() => setPreviewOpen(false)}
        />
      )}
      <MediaPickerModal
        open={pickerOpen}
        onClose={() => setPickerOpen(false)}
        onSelect={(url) => {
          if (pickerTarget === "cover") {
            setImage(url);
          } else if (pickerTarget === "social") {
            setSeoImage(url);
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
