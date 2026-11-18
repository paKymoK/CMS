import { useState, type CSSProperties } from "react";
import dayjs, { type Dayjs } from "dayjs";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Collapse, DatePicker, Empty, Form, Modal, Select, Switch, message, type FormInstance } from "antd";
import type { NamePath } from "antd/es/form/interface";
import { contentApi } from "../lib/api";
import { useSite } from "../lib/useSite";
import { SITES } from "../config/sites";
import { findFieldByPath, findResource, type FieldConfig, type ResourceConfig } from "../config/resources";
import { checkImageSpec } from "../lib/media";
import { flattenItem, toItemPayload } from "../lib/contentItem";
import type { ItemPrefill } from "../lib/audit";
import { FieldInput } from "../components/FieldInput";
import MediaPickerModal from "../components/MediaPickerModal";
import HistoryDrawer from "../components/HistoryDrawer";
import PreviousUrls from "../components/PreviousUrls";
import PageTemplateBar from "../components/PageTemplateBar";
import { RESOURCE_PREVIEWS } from "../components/resourcePreviews";
import { SeoChecklist } from "../components/SeoChecklist";
import SearchSnippetModal from "../components/SearchSnippetModal";
import { publishUnlessSeoMissing, seoChecks, type SeoCheck, type SeoValues } from "../lib/seo";
import { confirmMovePagesToTrash } from "../components/pageTrash";
import { allPages, duplicatePage, PAGE_TYPE, type PageType, parentChain, selfAndDescendants } from "../lib/pageList";

type Values = Record<string, unknown>;
/** A page can also be Scheduled (goes live by itself at a set time) and Archived (taken off the site but kept). */
type Status = "DRAFT" | "SCHEDULED" | "PUBLISHED" | "ARCHIVED";
const STATUS_LABELS: Record<Status, string> = { DRAFT: "Draft", SCHEDULED: "Scheduled", PUBLISHED: "Published", ARCHIVED: "Archived" };
/** The editor's own time zone, shown beside the pickers: times are picked in it and stored as UTC. */
const ZONE = `${Intl.DateTimeFormat().resolvedOptions().timeZone} (UTC${dayjs().format("Z")})`;
const TIME_FORMAT = "MMM D, YYYY HH:mm";

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

/** The search-engine values of the form, and what the website falls back to for each, for a resource with an SEO section. */
function seoValuesFor(resource: ResourceConfig, values: Values): SeoValues {
  const text = (v: unknown) => (typeof v === "string" ? v : "");
  return {
    seoTitle: text(values.seoTitle),
    seoDescription: text(values.seoDescription),
    seoImage: text(values.seoImage),
    fallbackTitle: resource.seo?.fallbackTitle(values) ?? "",
    fallbackDescription: resource.seo?.fallbackDescription?.(values) ?? "",
    fallbackImage: resource.seo?.fallbackImage?.(values) ?? "",
    noindex: values.seoNoindex === true,
    canonical: text(values.seoCanonical),
    nofollow: values.seoNofollow === true,
    jsonLd: text(values.seoJsonLd),
  };
}

/** The SEO checklist for the values in the form, or none when the resource has no SEO section. */
function seoChecksFor(resource: ResourceConfig, values: Values, slug: string): SeoCheck[] {
  const seo = resource.seo;
  if (!seo) return [];
  return seoChecks({
    ...seoValuesFor(resource, values),
    slug: seo.url && !resource.editor?.noSlug ? { value: slug, url: seo.url(slug || "…", values) } : undefined,
  });
}

/** The checklist under the SEO fields, and the search-result preview it opens: reads the form as it is being edited. */
function SeoPanel({ resource, form, slug, host }: { resource: ResourceConfig; form: FormInstance<Values>; slug: string; host: string }) {
  const values = (Form.useWatch([], form) as Values | undefined) ?? {};
  const [open, setOpen] = useState(false);
  return (
    <>
      <SeoChecklist checks={seoChecksFor(resource, values, slug)} onPreview={() => setOpen(true)} />
      <SearchSnippetModal
        open={open}
        onClose={() => setOpen(false)}
        input={{ ...seoValuesFor(resource, values), host, path: resource.seo?.url?.(slug || "…", values) ?? "" }}
      />
    </>
  );
}

const labelStyle: CSSProperties = { fontSize: 12, fontWeight: 600, color: "#5b6b86" };
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
const cardStyle: CSSProperties = {
  background: "#ffffff",
  border: "1px solid #e3e9f4",
  borderRadius: 16,
  overflow: "clip",
  boxShadow: "0 1px 2px rgba(10,30,70,.04)",
};
const outlineButton: CSSProperties = {
  minHeight: 40,
  display: "flex",
  alignItems: "center",
  padding: "0 16px",
  border: "1px solid #dde4f0",
  borderRadius: 10,
  background: "#ffffff",
  fontFamily: "inherit",
  fontSize: 13,
  fontWeight: 600,
  color: "#26354f",
  cursor: "pointer",
};

/**
 * A deleted item's content as the start of a new one (Activity → "Create a new item from this version"):
 * same content, but no id, no slug (the server picks a free one), a draft, and none of the fields that
 * link to other rows.
 */
function prefillValues(resource: ResourceConfig, prefill: ItemPrefill): Values {
  const values: Values = { ...(prefill.data as Values) };
  delete values.id;
  delete values.slug;
  for (const f of resource.fields) if (f.dropOnCopy) delete values[f.name];
  return values;
}

/**
 * Generic full-page editor for a resource that sets `editor` in config/resources.ts: the form is
 * drawn from the resource's `fields`, so a new page type needs a config entry (and a preview), not a new page.
 * Thin data-fetching shell; the form is id-keyed so server data is the form's start state, not synced into it by an effect.
 */
export default function ResourceEditorPage({ resourceKey }: { resourceKey: string }) {
  const resource = findResource(resourceKey);
  const { id } = useParams<{ id: string }>();
  const isNew = id === "new";
  const { site } = useSite();
  const location = useLocation();
  const queryClient = useQueryClient();
  // A restore changes the item behind the form: refetch it, then remount the form on the new copy.
  const [restoreCount, setRestoreCount] = useState(0);
  const prefill = isNew ? ((location.state as { prefill?: ItemPrefill } | null)?.prefill ?? null) : null;

  const detailQuery = useQuery({
    queryKey: ["resource-editor", resourceKey, id, site],
    queryFn: async () => {
      const { data } = await contentApi.get(`${resource!.apiPath}/${id}`, { params: { site } });
      return (resource!.contentType ? flattenItem(data.data, true) : data.data) as Values;
    },
    enabled: !!resource && !isNew && !!site && !!id,
  });

  if (!resource?.editor) return <Empty description="Unknown content type" />;
  if (!isNew && !detailQuery.data) {
    return <div style={{ padding: 48, textAlign: "center", color: "#7b8aa5" }}>Loading…</div>;
  }

  const initial: Values | null = isNew
    ? prefill
      ? { ...prefillValues(resource, prefill), displayOrder: prefill.displayOrder, status: "DRAFT" }
      : null
    : (detailQuery.data ?? null);

  return (
    <ResourceEditorForm
      key={`${id}-${restoreCount}`}
      resource={resource}
      id={id!}
      isNew={isNew}
      initial={initial}
      onRestored={async () => {
        await queryClient.refetchQueries({ queryKey: ["resource-editor", resourceKey, id, site] });
        setRestoreCount((n) => n + 1);
      }}
    />
  );
}

function ResourceEditorForm({
  resource,
  id,
  isNew,
  initial,
  onRestored,
}: {
  resource: ResourceConfig;
  id: string;
  isNew: boolean;
  initial: Values | null;
  onRestored: () => void;
}) {
  const editor = resource.editor!;
  const navigate = useNavigate();
  const { site } = useSite();
  const queryClient = useQueryClient();
  const [form] = Form.useForm<Values>();
  const [slug, setSlug] = useState((initial?.slug as string | null | undefined) ?? "");
  const [displayOrder, setDisplayOrder] = useState((initial?.displayOrder as number | undefined) ?? 0);
  const [status, setStatus] = useState<Status>((initial?.status as Status | undefined) ?? "DRAFT");
  // What the server has: a published page with saved edits that are not live yet (a pending draft) shows the edits here.
  const [storedStatus, setStoredStatus] = useState<Status | undefined>(initial?.status as Status | undefined);
  const [hasDraft, setHasDraft] = useState(!!initial?.draft);
  // Pages sit in a structure (a parent page, structure only: the address does not change).
  const [parentId, setParentId] = useState<number | null>((initial?.parentId as number | undefined) ?? null);
  const everyPage = useQuery({ queryKey: ["pages-all", site], queryFn: () => allPages(site!), enabled: !!editor.pageStatuses && !!site });
  // The row version this form was loaded with; the server rejects a save made against an older one.
  const [version, setVersion] = useState(initial?.version as number | undefined);
  const [dirty, setDirty] = useState(false);
  const [historyOpen, setHistoryOpen] = useState(false);
  const [pickerField, setPickerField] = useState<NamePath | null>(null);
  const [previewValues, setPreviewValues] = useState<Values | null>(null);

  const Preview = RESOURCE_PREVIEWS[resource.key];
  const mainFields = resource.fields.filter((f) => f.area !== "side" && !f.tableOnly);
  const groups = resource.groups ?? [];
  const hasToggles = groups.some((g) => g.toggleable);
  // Service and Industry pages share one block list and its library of saved layouts and sections.
  const libraryBlocksField = resource.fields.find((f) => f.type === "blocks" && f.sharedLibrary);
  const plainFields = mainFields.filter((f) => !f.group || !groups.some((g) => g.key === f.group));
  const [openGroups, setOpenGroups] = useState<string[]>(groups.filter((g) => g.defaultOpen).map((g) => g.key));
  const [hiddenSections, setHiddenSections] = useState<string[]>((initial?.hiddenSections as string[] | undefined) ?? []);
  const sideFields = resource.fields.filter((f) => f.area === "side");
  const siteSub = site ? SITES.find((s) => s.code === site)?.subdomain : undefined;
  const titleValue = (Form.useWatch(resource.titleField, form) as string | undefined) ?? "";
  const pub = status === "PUBLISHED";
  const scheduled = status === "SCHEDULED";
  const pageStatuses = !!editor.pageStatuses;
  const [publishAt, setPublishAt] = useState<Dayjs | null>(initial?.publishAt ? dayjs(initial.publishAt as string) : null);
  const [unpublishAt, setUnpublishAt] = useState<Dayjs | null>(initial?.unpublishAt ? dayjs(initial.unpublishAt as string) : null);
  // Checked here for a quick answer; the server checks again.
  const scheduleProblem = !scheduled
    ? null
    : !publishAt
      ? "Choose when the page should be published."
      : !publishAt.isAfter(dayjs()) && !(initial?.publishAt && dayjs(initial.publishAt as string).isSame(publishAt))
        ? "The publish time must be in the future."
        : unpublishAt && !unpublishAt.isAfter(publishAt)
          ? "The unpublish time must be after the publish time."
          : null;
  // A live page's edits can be saved without going live: "Save draft" then keeps the page published as it is.
  const livePending = pageStatuses && storedStatus === "PUBLISHED" && status === "PUBLISHED";
  const touch = () => setDirty(true);
  const discard = useMutation({
    mutationFn: () => contentApi.delete(`${resource.apiPath}/${id}/draft`, { params: { site } }),
    onSuccess: () => {
      message.success("Draft discarded: the editor shows the live page again");
      queryClient.invalidateQueries({ queryKey: ["pages-list", site] });
      onRestored();
    },
    onError: () => message.error("Could not discard the draft"),
  });

  const saveMutation = useMutation({
    mutationFn: async ({ status: nextStatus, asDraft = false }: { status: Status; asDraft?: boolean }) => {
      const fieldValues = await form.validateFields();
      // A loaded row also carries its metadata (siteId, createdAt, ...): only declared fields go into `data`.
      const loaded = { ...form.getFieldsValue(true), ...fieldValues };
      // A block with no fields of its own (e.g. the industries strip) registers nothing in the form, so
      // its empty `props` is gone by now; the server needs it to know the block's type.
      for (const f of resource.fields) {
        if (f.type === "blocks" && Array.isArray(loaded[f.name])) {
          loaded[f.name] = (loaded[f.name] as Values[]).map((b) => ({ ...b, props: b.props ?? {} }));
        }
      }
      const content: Values = resource.contentType
        ? Object.fromEntries(Object.entries(loaded).filter(([key]) => resource.fields.some((f) => f.name === key)))
        : loaded;
      const values: Values = {
        ...content,
        ...(isNew ? {} : { id: Number(id), version }),
        ...(hasToggles ? { hiddenSections } : {}),
        slug: slug || undefined,
        displayOrder,
        status: nextStatus,
        ...(asDraft ? { saveAsDraft: true } : {}),
        ...(editor.pageStatuses ? { parentId: parentId ?? undefined } : {}),
        ...(pageStatuses
          ? {
              publishAt: nextStatus === "SCHEDULED" && publishAt ? publishAt.toISOString() : null,
              unpublishAt: (nextStatus === "SCHEDULED" || nextStatus === "PUBLISHED") && unpublishAt ? unpublishAt.toISOString() : null,
            }
          : {}),
      };
      const body = resource.contentType ? toItemPayload(resource.contentType, values) : values;
      return isNew
        ? contentApi.post(resource.apiPath, body, { params: { site } })
        : contentApi.put(resource.apiPath, body, { params: { site } });
    },
    onSuccess: (res, { status: nextStatus, asDraft = false }) => {
      setVersion((res.data.data as { version?: number }).version);
      setStatus(nextStatus);
      setStoredStatus(nextStatus);
      setHasDraft(asDraft);
      if (nextStatus === "DRAFT" || nextStatus === "ARCHIVED") setUnpublishAt(null);
      setDirty(false);
      message.success(
        asDraft ? "Saved as a draft: the live page is unchanged until you press Update" : nextStatus === "PUBLISHED" ? "Published" : nextStatus === "SCHEDULED" ? "Scheduled" : nextStatus === "ARCHIVED" ? "Archived" : "Saved as draft",
      );
      queryClient.invalidateQueries({ queryKey: ["content", resource.key, site] });
      queryClient.invalidateQueries({ queryKey: ["slugs", site] });
      if (isNew) {
        navigate(resource.editorRoute!((res.data.data as { id: number }).id), { replace: true });
      } else {
        queryClient.invalidateQueries({ queryKey: ["resource-editor", resource.key, id, site] });
      }
    },
  });

  const setSectionVisible = (key: string, visible: boolean) => {
    const rest = hiddenSections.filter((k) => k !== key);
    setHiddenSections(visible ? rest : [...rest, key]);
    touch();
  };
  const jumpTo = (key: string) => {
    setOpenGroups((open) => (open.includes(key) ? open : [...open, key]));
    requestAnimationFrame(() => document.getElementById(`group-${key}`)?.scrollIntoView({ behavior: "smooth", block: "start" }));
  };

  const renderField = (f: FieldConfig) => <FieldInput key={f.name} field={f} form={form} onOpenPicker={(path) => setPickerField(path ?? f.name)} />;

  return (
    <div style={{ minHeight: "100vh" }}>
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
            {!editor.singleton && (
              <button
                type="button"
                aria-label={`Back to ${resource.label.toLowerCase()}`}
                onClick={() => navigate(editor.backPath)}
                style={{ width: 38, height: 38, flex: "none", borderRadius: 10, border: "1px solid #dde4f0", background: "transparent", cursor: "pointer", color: "#26354f" }}
              >
                ←
              </button>
            )}
            <div style={{ display: "flex", flexDirection: "column", minWidth: 0 }}>
              <span style={{ ...labelStyle, fontSize: 10 }}>
                {resource.label} / {isNew ? "New" : `ID ${id}`} · {site ? site.toUpperCase() : ""}
              </span>
              <span style={{ fontSize: 15, fontWeight: 600, color: "#0b1b3a", whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis", maxWidth: 320 }}>
                {titleValue || `Untitled ${editor.singular}`}
              </span>
            </div>
            <span
              style={{ flex: "none", padding: "4px 10px", borderRadius: 999, fontSize: 12, fontWeight: 600, background: pub ? "#e3f7ee" : "#fff4e0", color: pub ? "#0b7a55" : "#9a5b00" }}
            >
              {STATUS_LABELS[status]}
            </span>
            {hasDraft && (
              <span style={{ flex: "none", padding: "4px 10px", borderRadius: 999, fontSize: 12, fontWeight: 600, background: "#e6f2ff", color: "#0159be" }}>
                Unpublished changes
              </span>
            )}
          </div>
          <div style={{ display: "flex", alignItems: "center", gap: 10, flexWrap: "wrap" }}>
            <span style={{ fontSize: 12.5, color: "#7b8aa5", display: "flex", alignItems: "center", gap: 6 }}>
              <span style={{ width: 7, height: 7, borderRadius: "50%", background: dirty ? "#fa8c16" : "#00bbff" }} />
              {dirty ? "Unsaved changes" : isNew ? "Not saved yet" : "Saved"}
            </span>
            {libraryBlocksField && site && <PageTemplateBar form={form} fieldName={libraryBlocksField.name} onApplied={touch} />}
            {Preview && site && (
              <button type="button" onClick={() => setPreviewValues({ ...form.getFieldsValue(true), slug, ...(hasToggles ? { hiddenSections } : {}) })} style={outlineButton}>
                Preview
              </button>
            )}
            {!isNew && (
              <button type="button" onClick={() => setHistoryOpen(true)} style={outlineButton}>
                History
              </button>
            )}
            {pageStatuses && !isNew && !editor.singleton && site && resource.contentType && (
              <button
                type="button"
                onClick={async () => {
                  try {
                    const copy = await duplicatePage(site, Number(id));
                    message.success(`Created “${copy.title}” as a draft${dirty ? " (from the last saved version)" : ""}`);
                    navigate(PAGE_TYPE[copy.type as PageType].editor(copy.id));
                  } catch {
                    message.error("Could not copy the page");
                  }
                }}
                style={outlineButton}
              >
                Duplicate
              </button>
            )}
            {hasDraft && (
              <button
                type="button"
                onClick={() =>
                  Modal.confirm({
                    title: "Discard the unpublished changes?",
                    content: "The saved edits are thrown away and the editor goes back to the live page. The live page itself is not changed.",
                    okText: "Discard changes",
                    okButtonProps: { danger: true },
                    onOk: () => discard.mutateAsync(),
                  })
                }
                disabled={saveMutation.isPending}
                style={outlineButton}
              >
                Discard changes
              </button>
            )}
            {pageStatuses && !isNew && !editor.singleton && site && (
              <button
                type="button"
                onClick={() => confirmMovePagesToTrash(site, [{ id: Number(id), title: titleValue || null }], () => navigate(editor.backPath, { replace: true }))}
                style={{ ...outlineButton, color: "#b42318" }}
              >
                Move to Trash
              </button>
            )}
            <button
              type="button"
              onClick={() => (livePending ? saveMutation.mutate({ status: "PUBLISHED", asDraft: true }) : saveMutation.mutate({ status: "DRAFT" }))}
              disabled={saveMutation.isPending}
              style={{ ...outlineButton, border: "1px solid #bcd9fb", color: "#0083ff" }}
              title={livePending ? "Keeps the page live as it is: your edits wait here until you press Update" : undefined}
            >
              {status === "ARCHIVED" || (storedStatus === "PUBLISHED" && pageStatuses && status === "DRAFT") ? "Move to draft" : "Save draft"}
            </button>
            {pageStatuses && !isNew && status !== "ARCHIVED" && (
              <button
                type="button"
                onClick={() =>
                  status === "PUBLISHED"
                    ? Modal.confirm({
                        title: `Archive this ${editor.singular}?`,
                        content: "It is taken off the site and its address stops working, but the page is kept. You can publish it again or move it back to draft.",
                        okText: "Archive",
                        onOk: () => saveMutation.mutate({ status: "ARCHIVED" }),
                      })
                    : saveMutation.mutate({ status: "ARCHIVED" })
                }
                disabled={saveMutation.isPending}
                style={outlineButton}
              >
                Archive
              </button>
            )}
            {status === "ARCHIVED" && (
              <button type="button" onClick={() => saveMutation.mutate({ status: "ARCHIVED" })} disabled={saveMutation.isPending} style={outlineButton}>
                Save
              </button>
            )}
            <button
              type="button"
              onClick={() => {
                if (scheduleProblem) {
                  message.error(scheduleProblem);
                  return;
                }
                const goal: Status = scheduled ? "SCHEDULED" : "PUBLISHED";
                // Only a page that is not live yet gets the SEO prompt; updating one never asks.
                return status === "PUBLISHED" || initial?.status === goal || !resource.seo
                  ? saveMutation.mutate({ status: goal })
                  : publishUnlessSeoMissing(seoChecksFor(resource, form.getFieldsValue(true), slug), () => saveMutation.mutate({ status: goal }));
              }}
              disabled={saveMutation.isPending}
              style={{ ...outlineButton, padding: "0 20px", border: "none", background: "#0083ff", color: "#ffffff" }}
            >
              {scheduled ? (initial?.status === "SCHEDULED" ? "Update schedule" : "Schedule") : pub ? "Update" : "Publish"}
            </button>
          </div>
        </div>
      </div>

      {hasDraft && (
        <div style={{ margin: "0 0 16px", padding: "10px 16px", borderRadius: 12, background: "#eaf4ff", border: "1px solid #bcd9fb", fontSize: 13.5, color: "#0b1b3a" }}>
          This page has <b>saved changes that are not live yet</b>. The website still shows the last published version; press <b>Update</b> to publish these changes.
        </div>
      )}

      <Form form={form} layout="vertical" initialValues={initial ?? undefined} onValuesChange={touch} requiredMark={false}>
        <div className="cms-editor-body" style={{ margin: "0 auto", padding: "0 0 80px", display: "flex", flexWrap: "wrap", alignItems: "flex-start", gap: 24 }}>
          <main style={{ ...cardStyle, flex: "1 1 640px", minWidth: 0, padding: "clamp(20px,3vw,32px)" }}>
            {plainFields.map(renderField)}
            {groups.map((g) => {
              const off = g.toggleable && hiddenSections.includes(g.key);
              return (
                <div key={g.key} id={`group-${g.key}`} style={{ marginTop: 16, scrollMarginTop: 88 }}>
                  <Collapse
                    activeKey={openGroups.includes(g.key) ? [g.key] : []}
                    onChange={(keys) =>
                      setOpenGroups((open) => [...open.filter((k) => k !== g.key), ...(keys.length ? [g.key] : [])])
                    }
                    items={[
                      {
                        key: g.key,
                        forceRender: true,
                        label: (
                          <span style={{ fontWeight: 600, color: off ? "#7b8aa5" : "#0b1b3a" }}>
                            {g.label}
                            {off ? " · hidden on the website" : ""}
                          </span>
                        ),
                        extra: g.toggleable ? (
                          <Switch
                            size="small"
                            checked={!off}
                            aria-label={`Show ${g.label} on the website`}
                            onClick={(checked, e) => {
                              e.stopPropagation();
                              setSectionVisible(g.key, checked);
                            }}
                          />
                        ) : undefined,
                        children: (
                          <div style={{ opacity: off ? 0.55 : 1 }}>
                            {g.description && <p style={{ margin: 0, color: "#5b6b86", fontSize: 13 }}>{g.description}</p>}
                            {mainFields.filter((f) => f.group === g.key).map(renderField)}
                            {g.key === "seo" && resource.seo && <SeoPanel resource={resource} form={form} slug={slug} host={siteSub ?? "site"} />}
                          </div>
                        ),
                      },
                    ]}
                  />
                </div>
              );
            })}
          </main>

          <aside style={{ flex: "0 1 330px", minWidth: 280, display: "flex", flexDirection: "column", gap: 16, position: "sticky", top: 88 }}>
            <div style={cardStyle}>
              <div style={{ padding: "14px 18px", borderBottom: "1px solid #e3e9f4", ...labelStyle }}>Publishing</div>
              <div style={{ padding: "16px 18px", display: "flex", flexDirection: "column", gap: 14 }}>
                <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 6, padding: 4, border: "1px solid #dde4f0", borderRadius: 12, background: "#e8eef8" }}>
                  {(pageStatuses ? (["DRAFT", "SCHEDULED", "PUBLISHED", "ARCHIVED"] as const) : (["DRAFT", "PUBLISHED"] as const)).map((s) => (
                    <button
                      key={s}
                      type="button"
                      onClick={() => {
                        setStatus(s);
                        touch();
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
                {pageStatuses && scheduled && (
                  <label style={{ display: "flex", flexDirection: "column", gap: 6 }}>
                    <span style={labelStyle}>Publish at</span>
                    <DatePicker
                      showTime={{ format: "HH:mm" }}
                      format={TIME_FORMAT}
                      value={publishAt}
                      minDate={dayjs()}
                      onChange={(v) => {
                        setPublishAt(v ? v.startOf("minute") : null);
                        touch();
                      }}
                      style={{ width: "100%" }}
                    />
                  </label>
                )}
                {pageStatuses && (scheduled || pub) && (
                  <label style={{ display: "flex", flexDirection: "column", gap: 6 }}>
                    <span style={labelStyle}>Take offline at (optional)</span>
                    <DatePicker
                      showTime={{ format: "HH:mm" }}
                      format={TIME_FORMAT}
                      value={unpublishAt}
                      minDate={dayjs()}
                      allowClear
                      onChange={(v) => {
                        setUnpublishAt(v ? v.startOf("minute") : null);
                        touch();
                      }}
                      style={{ width: "100%" }}
                    />
                  </label>
                )}
                {pageStatuses && (scheduled || (pub && unpublishAt)) && (
                  <span style={{ fontSize: 12.5, color: scheduleProblem ? "#c4341c" : "#7b8aa5", lineHeight: 1.45 }}>
                    {scheduleProblem ?? `Times are in your time zone, ${ZONE}. The site checks once a minute, so a change can take up to a minute to show.`}
                  </span>
                )}

                {!editor.noSlug && (
                  <div style={{ display: "flex", flexDirection: "column", gap: 6 }}>
                    <span style={labelStyle}>URL</span>
                    <div style={{ display: "flex", alignItems: "center", border: "1px solid #dde4f0", background: "#f4f7fc", borderRadius: 10 }}>
                      <span style={{ padding: "0 0 0 12px", fontFamily: "var(--font-mono)", fontSize: 12, color: "#7b8aa5", whiteSpace: "nowrap" }}>
                        {siteSub ?? "site"}/{editor.slugPrefix}/
                      </span>
                      <input
                        value={slug}
                        onChange={(e) => {
                          setSlug(slugify(e.target.value));
                          touch();
                        }}
                        style={{ flex: 1, minWidth: 60, border: "none", background: "transparent", padding: "10px 12px 10px 2px", fontFamily: "var(--font-mono)", fontSize: 12, color: "#0b1b3a", outline: "none" }}
                      />
                    </div>
                    <button
                      type="button"
                      onClick={() => {
                        setSlug(slugify(titleValue));
                        touch();
                      }}
                      style={{ ...outlineButton, minHeight: 34, justifyContent: "center" }}
                    >
                      From title
                    </button>
                    <PreviousUrls itemId={isNew ? undefined : id} slug={slug} live={status !== "DRAFT"} prefix={`${siteSub ?? "site"}/${editor.slugPrefix.split("/")[0]}/`} />
                  </div>
                )}

                {pageStatuses && (
                  <div style={{ display: "flex", flexDirection: "column", gap: 6 }}>
                    <span style={labelStyle}>Parent page</span>
                    <Select
                      allowClear
                      showSearch
                      optionFilterProp="label"
                      placeholder="None (top level)"
                      value={parentId ?? undefined}
                      onChange={(v) => {
                        setParentId(v ?? null);
                        touch();
                      }}
                      options={(everyPage.data ?? [])
                        .filter((p) => isNew || !selfAndDescendants(everyPage.data ?? [], Number(id)).has(p.id))
                        .map((p) => ({ value: p.id, label: p.title ?? "(no title)" }))}
                    />
                    <span style={{ fontSize: 12, color: "#7b8aa5", lineHeight: 1.45 }}>
                      {parentId !== null && everyPage.data ? `Sits under: ${parentChain(everyPage.data, parentId)}. ` : ""}
                      Structure only: the page's address does not change. If the parent is moved to the Trash, this page leaves the menus.
                    </span>
                  </div>
                )}

                <div style={{ display: "grid", gridTemplateColumns: "1fr", gap: 10 }}>
                  <label style={{ display: "flex", flexDirection: "column", gap: 6 }}>
                    <span style={labelStyle}>Order</span>
                    <input
                      type="number"
                      value={displayOrder}
                      onChange={(e) => {
                        setDisplayOrder(Number(e.target.value) || 0);
                        touch();
                      }}
                      style={inputStyle}
                    />
                  </label>
                </div>
              </div>
            </div>

            {groups.length > 0 && (
              <div style={cardStyle}>
                <div style={{ padding: "14px 18px", borderBottom: "1px solid #e3e9f4", ...labelStyle }}>Sections</div>
                <div style={{ padding: "8px 10px", display: "flex", flexDirection: "column" }}>
                  {groups.map((g) => (
                    <button
                      key={g.key}
                      type="button"
                      onClick={() => jumpTo(g.key)}
                      style={{ display: "flex", justifyContent: "space-between", gap: 8, padding: "7px 8px", border: "none", background: "transparent", borderRadius: 8, cursor: "pointer", fontFamily: "inherit", fontSize: 13, color: g.toggleable && hiddenSections.includes(g.key) ? "#a3afc4" : "#26354f", textAlign: "left" }}
                    >
                      {g.label}
                      {g.toggleable && hiddenSections.includes(g.key) ? <span style={{ fontSize: 11 }}>hidden</span> : null}
                    </button>
                  ))}
                </div>
              </div>
            )}

            {sideFields.length > 0 && (
              <div style={cardStyle}>
                <div style={{ padding: "14px 18px", borderBottom: "1px solid #e3e9f4", ...labelStyle }}>Details</div>
                <div style={{ padding: "16px 18px" }}>{sideFields.map(renderField)}</div>
              </div>
            )}
          </aside>
        </div>
      </Form>

      {Preview && previewValues && site && (
        <Preview site={site} id={isNew ? undefined : Number(id)} values={previewValues} onClose={() => setPreviewValues(null)} />
      )}
      {!isNew && <HistoryDrawer open={historyOpen} onClose={() => setHistoryOpen(false)} itemId={Number(id)} onRestored={onRestored} />}
      <MediaPickerModal
        open={!!pickerField}
        onClose={() => setPickerField(null)}
        onSelect={async (url) => {
          const target = pickerField;
          const spec = target ? findFieldByPath(resource.fields, target)?.imageSpec : undefined;
          if (spec) {
            const problem = await checkImageSpec(url, spec);
            if (problem) {
              message.error(problem);
              return; // keep the picker open so another image can be chosen
            }
          }
          if (target) {
            form.setFieldValue(target, url);
            touch();
          }
          setPickerField(null);
        }}
      />
    </div>
  );
}
