import { useState, type CSSProperties } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Collapse, Empty, Form, Switch, message } from "antd";
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
import { RESOURCE_PREVIEWS } from "../components/resourcePreviews";

type Values = Record<string, unknown>;
type Status = "DRAFT" | "PUBLISHED";

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
      return (resource!.contentType ? flattenItem(data.data) : data.data) as Values;
    },
    enabled: !!resource && !isNew && !!site && !!id,
  });

  if (!resource?.editor) return <Empty description="Unknown content type" />;
  if (!isNew && !detailQuery.data) {
    return <div style={{ padding: 48, textAlign: "center", color: "#7b8aa5" }}>Loading…</div>;
  }

  const initial: Values | null = isNew
    ? prefill
      ? { ...prefillValues(resource, prefill), displayOrder: prefill.displayOrder, active: prefill.active, status: "DRAFT" }
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
  const [active, setActive] = useState((initial?.active as boolean | undefined) ?? true);
  const [status, setStatus] = useState<Status>((initial?.status as Status | undefined) ?? "DRAFT");
  // The row version this form was loaded with; the server rejects a save made against an older one.
  const [version, setVersion] = useState(initial?.version as number | undefined);
  const [dirty, setDirty] = useState(false);
  const [historyOpen, setHistoryOpen] = useState(false);
  const [pickerField, setPickerField] = useState<NamePath | null>(null);
  const [previewValues, setPreviewValues] = useState<Values | null>(null);

  const Preview = RESOURCE_PREVIEWS[resource.key];
  const mainFields = resource.fields.filter((f) => f.area !== "side" && !f.tableOnly);
  const groups = resource.groups ?? [];
  const plainFields = mainFields.filter((f) => !f.group || !groups.some((g) => g.key === f.group));
  const [openGroups, setOpenGroups] = useState<string[]>(groups.filter((g) => g.defaultOpen).map((g) => g.key));
  const [hiddenSections, setHiddenSections] = useState<string[]>((initial?.hiddenSections as string[] | undefined) ?? []);
  const sideFields = resource.fields.filter((f) => f.area === "side");
  const siteSub = site ? SITES.find((s) => s.code === site)?.subdomain : undefined;
  const titleValue = (Form.useWatch(resource.titleField, form) as string | undefined) ?? "";
  const pub = status === "PUBLISHED";
  const touch = () => setDirty(true);

  const saveMutation = useMutation({
    mutationFn: async (nextStatus: Status) => {
      const fieldValues = await form.validateFields();
      // A loaded row also carries its metadata (siteId, createdAt, ...): only declared fields go into `data`.
      const loaded = { ...form.getFieldsValue(true), ...fieldValues };
      const content: Values = resource.contentType
        ? Object.fromEntries(Object.entries(loaded).filter(([key]) => resource.fields.some((f) => f.name === key)))
        : loaded;
      const values: Values = {
        ...content,
        ...(isNew ? {} : { id: Number(id), version }),
        ...(groups.length ? { hiddenSections } : {}),
        slug: slug || undefined,
        displayOrder,
        active,
        status: nextStatus,
      };
      const body = resource.contentType ? toItemPayload(resource.contentType, values) : values;
      return isNew
        ? contentApi.post(resource.apiPath, body, { params: { site } })
        : contentApi.put(resource.apiPath, body, { params: { site } });
    },
    onSuccess: (res, nextStatus) => {
      setVersion((res.data.data as { version?: number }).version);
      setStatus(nextStatus);
      setDirty(false);
      message.success(nextStatus === "PUBLISHED" ? "Published" : "Saved as draft");
      queryClient.invalidateQueries({ queryKey: ["content", resource.key, site] });
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
            <button
              type="button"
              aria-label={`Back to ${resource.label.toLowerCase()}`}
              onClick={() => navigate(editor.backPath)}
              style={{ width: 38, height: 38, flex: "none", borderRadius: 10, border: "1px solid #dde4f0", background: "transparent", cursor: "pointer", color: "#26354f" }}
            >
              ←
            </button>
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
              {pub ? "Published" : "Draft"}
            </span>
          </div>
          <div style={{ display: "flex", alignItems: "center", gap: 10, flexWrap: "wrap" }}>
            <span style={{ fontSize: 12.5, color: "#7b8aa5", display: "flex", alignItems: "center", gap: 6 }}>
              <span style={{ width: 7, height: 7, borderRadius: "50%", background: dirty ? "#fa8c16" : "#00bbff" }} />
              {dirty ? "Unsaved changes" : isNew ? "Not saved yet" : "Saved"}
            </span>
            {Preview && site && (
              <button type="button" onClick={() => setPreviewValues({ ...form.getFieldsValue(true) })} style={outlineButton}>
                Preview
              </button>
            )}
            {!isNew && (
              <button type="button" onClick={() => setHistoryOpen(true)} style={outlineButton}>
                History
              </button>
            )}
            <button
              type="button"
              onClick={() => saveMutation.mutate("DRAFT")}
              disabled={saveMutation.isPending}
              style={{ ...outlineButton, border: "1px solid #bcd9fb", color: "#0083ff" }}
            >
              Save draft
            </button>
            <button
              type="button"
              onClick={() => saveMutation.mutate("PUBLISHED")}
              disabled={saveMutation.isPending}
              style={{ ...outlineButton, padding: "0 20px", border: "none", background: "#0083ff", color: "#ffffff" }}
            >
              {pub ? "Update" : "Publish"}
            </button>
          </div>
        </div>
      </div>

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
                  {(["DRAFT", "PUBLISHED"] as const).map((s) => (
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
                      {s === "DRAFT" ? "Draft" : "Published"}
                    </button>
                  ))}
                </div>

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
                </div>

                <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
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
                  <div style={{ display: "flex", flexDirection: "column", gap: 6 }}>
                    <span style={labelStyle}>Active</span>
                    <button
                      type="button"
                      aria-label="Toggle active"
                      onClick={() => {
                        setActive(!active);
                        touch();
                      }}
                      style={{ position: "relative", width: 44, height: 24, marginTop: 6, borderRadius: 999, border: "none", cursor: "pointer", padding: 0, background: active ? "#0083ff" : "#cfd2d6", transition: "background .2s" }}
                    >
                      <span style={{ position: "absolute", top: 3, left: active ? 23 : 3, width: 18, height: 18, borderRadius: "50%", background: "#ffffff", boxShadow: "0 1px 3px rgba(10,37,64,.25)", transition: "left .2s" }} />
                    </button>
                  </div>
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
