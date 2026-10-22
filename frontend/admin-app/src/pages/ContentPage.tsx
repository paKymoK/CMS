import { useEffect, useRef, useState } from "react";
import HistoryDrawer from "../components/HistoryDrawer";
import type { ItemPrefill } from "../lib/audit";
import { useLocation, useNavigate, useParams, useSearchParams } from "react-router-dom";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  Table,
  Button,
  Drawer,
  Form,
  InputNumber,
  Switch,
  Select,
  Popconfirm,
  message,
  Empty,
} from "antd";
import { PlusIcon, EditIcon, DeleteIcon, ClockIcon } from "../components/icons";
import dayjs from "dayjs";
import { contentApi } from "../lib/api";
import { useSite } from "../lib/useSite";
import { siteLabel, SITES } from "../config/sites";
import { findResource } from "../config/resources";
import type { NamePath } from "antd/es/form/interface";
import MediaPickerModal from "../components/MediaPickerModal";
import { checkImageSpec, mediaSrc } from "../lib/media";
import { flattenItem, toItemPayload, type Row } from "../lib/contentItem";
import { FieldInput } from "../components/FieldInput";

const STATUS_OPTIONS = [
  { label: "Draft", value: "DRAFT" },
  { label: "Published", value: "PUBLISHED" },
];

// "Case Studies" -> "Case Study", "Testimonials" -> "Testimonial", "Stats" -> "Stat".
function singularize(label: string): string {
  return label.replace(/ies$/, "y").replace(/s$/, "");
}

function ColHeader({ children }: { children: string }) {
  return <span className="cms-eyebrow">{children}</span>;
}

const placeholderThumbStyle = {
  width: 44,
  height: 44,
  flex: "none" as const,
  background: "repeating-linear-gradient(135deg, #001c51 0 6px, #003f88 6px 12px)",
};

// Falls back to the design's diagonal-stripe placeholder on load failure too, not just a missing
// value — seed data can point at paths (e.g. website's own /images/...) this app never serves.
function Thumb({ src }: { src: string | null }) {
  const [broken, setBroken] = useState(false);
  if (!src || broken) return <div style={placeholderThumbStyle} />;
  return (
    <img
      src={mediaSrc(src)}
      alt=""
      style={{ width: 44, height: 44, flex: "none", objectFit: "cover" }}
      onError={() => setBroken(true)}
    />
  );
}

/**
 * Opens the edit drawer for the row named by ?edit=<id> (a search result links here), once the list
 * has loaded, then drops the parameter so a refresh or "back" doesn't reopen it.
 */
function OpenEditFromQuery({ rows, open }: { rows?: Row[]; open: (row: Row) => void }) {
  const [params, setParams] = useSearchParams();
  const wanted = Number(params.get("edit")) || null;
  const latestOpen = useRef(open);
  useEffect(() => {
    latestOpen.current = open;
  });
  useEffect(() => {
    if (!wanted || !rows) return;
    const row = rows.find((r) => r.id === wanted);
    if (row) latestOpen.current(row);
    setParams(
      (current) => {
        current.delete("edit");
        return current;
      },
      { replace: true },
    );
  }, [wanted, rows, setParams]);
  return null;
}

/**
 * Opens the "new item" drawer filled with a deleted item's content when the screen was reached with
 * a prefill in router state (Activity → "Create a new item from this version"), then drops the state
 * so a refresh or "back" doesn't reopen it. It is a new item: nothing of the old one but its content.
 */
function OpenNewFromState({ open }: { open: (prefill: ItemPrefill) => void }) {
  const location = useLocation();
  const navigate = useNavigate();
  const prefill = (location.state as { prefill?: ItemPrefill } | null)?.prefill;
  const latestOpen = useRef(open);
  useEffect(() => {
    latestOpen.current = open;
  });
  useEffect(() => {
    if (!prefill) return;
    latestOpen.current(prefill);
    navigate(`${location.pathname}${location.search}`, { replace: true, state: null });
  }, [prefill, location.pathname, location.search, navigate]);
  return null;
}

export default function ContentPage() {
  const { resourceKey } = useParams<{ resourceKey: string }>();
  const resource = resourceKey ? findResource(resourceKey) : undefined;
  const navigate = useNavigate();
  const { site } = useSite();
  const queryClient = useQueryClient();
  const [form] = Form.useForm();
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [historyOpen, setHistoryOpen] = useState(false);
  const [editing, setEditing] = useState<Row | null>(null);
  const [typeFilter, setTypeFilter] = useState<string | undefined>(undefined);
  const [pickerField, setPickerField] = useState<NamePath | null>(null);

  const listKey = ["content", resource?.key, site, typeFilter];

  const listQuery = useQuery({
    queryKey: listKey,
    queryFn: async () => {
      const { data } = await contentApi.get(resource!.apiPath, {
        params: {
          site,
          ...(resource!.contentType
            ? { type: resource!.contentType }
            : typeFilter
              ? { type: typeFilter }
              : {}),
        },
      });
      const items = data.data as Record<string, unknown>[];
      return (resource!.contentType ? items.map(flattenItem) : items) as Row[];
    },
    enabled: !!resource && !!site,
  });

  const invalidate = () => queryClient.invalidateQueries({ queryKey: ["content", resource?.key, site] });

  const saveMutation = useMutation({
    mutationFn: async (values: Record<string, unknown>) => {
      const body = resource!.contentType ? toItemPayload(resource!.contentType, values) : values;
      if (editing) {
        return contentApi.put(resource!.apiPath, body, { params: { site } });
      }
      return contentApi.post(resource!.apiPath, body, { params: { site } });
    },
    onSuccess: () => {
      message.success(editing ? "Updated" : "Created");
      setDrawerOpen(false);
      invalidate();
    },
  });

  const deleteMutation = useMutation({
    mutationFn: async (id: number) =>
      contentApi.delete(`${resource!.apiPath}/${id}`, { params: { site } }),
    onSuccess: () => {
      message.success("Deleted");
      invalidate();
    },
  });

  // Flips just `active`, resending the row's other fields unchanged — same PUT contract the
  // drawer's full save uses, just triggered from the list instead of opening it.
  const toggleActiveMutation = useMutation({
    mutationFn: async (row: Row) => {
      const next = { ...row, active: !row.active };
      const body = resource!.contentType
        ? {
            id: row.id,
            contentType: resource!.contentType,
            data: row.data,
            displayOrder: row.displayOrder,
            active: next.active,
            status: row.status,
          }
        : next;
      return contentApi.put(resource!.apiPath, body, { params: { site } });
    },
    onSuccess: invalidate,
  });

  if (!resource) {
    return <Empty description="Unknown content type" />;
  }

  const openCreate = () => {
    if (resource.editorRoute) {
      navigate(resource.editorRoute("new"));
      return;
    }
    setEditing(null);
    form.resetFields();
    form.setFieldsValue({ active: true, status: "DRAFT", displayOrder: 0 });
    setDrawerOpen(true);
  };

  const openEdit = (row: Row) => {
    if (resource.editorRoute) {
      navigate(resource.editorRoute(row.id));
      return;
    }
    setEditing(row);
    const values: Record<string, unknown> = { ...row };
    for (const f of resource.fields) {
      if (f.type === "json" && values[f.name] != null) {
        values[f.name] = JSON.stringify(values[f.name], null, 2);
      }
      if (f.type === "datetime" && values[f.name]) {
        values[f.name] = dayjs(values[f.name] as string);
      }
    }
    form.setFieldsValue(values);
    setDrawerOpen(true);
  };

  // Opens the "new" form prefilled from an existing row: same content, a "-copy" slug, draft status.
  const openDuplicate = (row: Row) => {
    if (resource.editorRoute) {
      const content: Record<string, unknown> = { ...row };
      delete content.id;
      delete content.data;
      if (typeof content.navLabel === "string") content.navLabel = `${content.navLabel} (copy)`;
      navigate(resource.editorRoute("new"), {
        state: { prefill: { data: content, displayOrder: row.displayOrder ?? 0, active: row.active } },
      });
      return;
    }
    setEditing(null);
    form.resetFields();
    const values: Record<string, unknown> = { ...row };
    delete values.id;
    delete values.data;
    if (typeof values.slug === "string") values.slug = `${values.slug}-copy`;
    if (typeof values.navLabel === "string") values.navLabel = `${values.navLabel} (copy)`;
    values.status = "DRAFT";
    form.setFieldsValue(values);
    setDrawerOpen(true);
  };

  const handleSubmit = async () => {
    const values = await form.validateFields();
    for (const f of resource.fields) {
      if (f.type === "json" && typeof values[f.name] === "string") {
        const raw = (values[f.name] as string).trim();
        if (!raw) {
          values[f.name] = null;
          continue;
        }
        try {
          values[f.name] = JSON.parse(raw);
        } catch {
          message.error(`${f.label} must be valid JSON`);
          throw new Error("invalid json");
        }
      }
      if (f.type === "datetime" && values[f.name]) {
        values[f.name] = (values[f.name] as dayjs.Dayjs).toISOString();
      }
    }
    if (editing) values.id = editing.id;
    saveMutation.mutate(values);
  };

  const rows = (listQuery.data ?? []).filter((r) =>
    typeFilter && !resource.contentType ? r.type === typeFilter : true,
  );
  const publishedCount = rows.filter((r) => r.status === "PUBLISHED").length;
  const imgField = resource.fields.find((f) => f.type === "image");
  const singular = singularize(resource.label);
  const siteSub = site ? SITES.find((s) => s.code === site)?.subdomain : undefined;

  const columns = [
    {
      title: <ColHeader>ID</ColHeader>,
      dataIndex: "id",
      width: 56,
      render: (v: number) => (
        <span style={{ fontFamily: "var(--font-mono)", fontSize: 12, color: "#7b8aa5" }}>{v}</span>
      ),
    },
    {
      title: <ColHeader>{resource.fields.find((f) => f.name === resource.titleField)?.label ?? "Title"}</ColHeader>,
      dataIndex: resource.titleField,
      render: (title: string, row: Row) => (
        <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
          {imgField ? <Thumb src={(row[imgField.name] as string) ?? null} /> : null}
          <span style={{ fontSize: 14, fontWeight: 600, color: "#0b1b3a" }}>{title}</span>
        </div>
      ),
    },
    ...resource.fields
      .filter((f) => f.showInTable && f.name !== resource.titleField)
      .map((f) => ({
        title: <ColHeader>{f.label}</ColHeader>,
        dataIndex: f.name,
        render: (value: unknown) => (
          <span style={{ fontSize: 14, color: "#5b6b86" }}>
            {f.type === "boolean" ? (value ? "Yes" : "No") : String(value ?? "—")}
          </span>
        ),
      })),
    {
      title: <ColHeader>Status</ColHeader>,
      dataIndex: "status",
      render: (status: string) => (
        <span className={`cms-status${status === "PUBLISHED" ? " is-published" : ""}`}>
          {status === "PUBLISHED" ? "Published" : "Draft"}
        </span>
      ),
    },
    {
      title: <ColHeader>Active</ColHeader>,
      dataIndex: "active",
      render: (active: boolean, row: Row) => (
        <Switch
          size="small"
          checked={active}
          loading={toggleActiveMutation.isPending && toggleActiveMutation.variables?.id === row.id}
          onChange={() => toggleActiveMutation.mutate(row)}
        />
      ),
    },
    {
      title: <ColHeader>Order</ColHeader>,
      dataIndex: "displayOrder",
      width: 64,
      render: (v: number) => (
        <span style={{ fontFamily: "var(--font-mono)", fontSize: 12, color: "#7b8aa5" }}>{v}</span>
      ),
    },
    {
      title: "",
      key: "actions",
      width: resource.duplicable ? 260 : 150,
      render: (_: unknown, row: Row) => (
        <div className="cms-row-actions">
          <Button
            size="small"
            className="cms-mini-btn"
            icon={<EditIcon />}
            onClick={() => openEdit(row)}
            style={{ color: "#0069cc", borderColor: "#bcd9fb" }}
          >
            Edit
          </Button>
          {resource.duplicable && (
            <Button size="small" className="cms-mini-btn" onClick={() => openDuplicate(row)}>
              Duplicate
            </Button>
          )}
          <Popconfirm title="Delete this item?" onConfirm={() => deleteMutation.mutate(row.id)}>
            <Button size="small" className="cms-mini-btn" danger icon={<DeleteIcon />}>
              Delete
            </Button>
          </Popconfirm>
        </div>
      ),
    },
  ];

  return (
    <div>
      <OpenEditFromQuery rows={listQuery.data} open={openEdit} />
      <OpenNewFromState
        open={(prefill) =>
          // openDuplicate drops the id and sets the status itself; those two are placeholders.
          openDuplicate({
            ...prefill.data,
            id: 0,
            status: "DRAFT",
            displayOrder: prefill.displayOrder,
            active: prefill.active,
          })
        }
      />
      <div className="flex flex-wrap items-end justify-between gap-5 mb-6">
        <div className="min-w-0">
          <h1 className="cms-page-title">{resource.label}</h1>
          <p className="cms-page-sub">
            {rows.length} items · <span style={{ color: "#0b7a55", fontWeight: 600 }}>{publishedCount} published</span>
            {siteSub ? ` on ${siteSub}` : ""}
          </p>
        </div>
        <div className="flex flex-wrap items-center gap-3">
          {resource.supportsTypeFilter && (
            <div className="cms-segmented">
              {[{ label: "All", value: undefined }, ...(resource.typeFilterOptions ?? [])].map((o) => {
                const on = typeFilter === o.value;
                return (
                  <button
                    key={o.label}
                    type="button"
                    className={on ? "is-on" : undefined}
                    aria-pressed={on}
                    onClick={() => setTypeFilter(o.value)}
                  >
                    {o.label}
                  </button>
                );
              })}
            </div>
          )}
          <Button className="cms-cta" icon={<PlusIcon />} onClick={openCreate}>
            New {singular.toLowerCase()}
          </Button>
        </div>
      </div>

      {listQuery.isLoading ? (
        <div className="cms-skel" aria-busy="true" aria-label="Loading">
          {[0, 1, 2, 3, 4].map((i) => (
            <div key={i} className="cms-skel-row">
              <div className="cms-skel-bar" style={{ width: 28 }} />
              <div className="cms-skel-bar" style={{ width: `${28 - i * 2}%` }} />
              <div className="cms-skel-bar" style={{ width: "18%", marginLeft: "auto" }} />
              <div className="cms-skel-bar" style={{ width: 64 }} />
            </div>
          ))}
        </div>
      ) : (
      <Table
        className="cms-table"
        rowKey="id"
        columns={columns}
        dataSource={rows}
        pagination={false}
        scroll={{ x: "max-content" }}
        locale={{
          emptyText: (
            <div className="py-14 text-center">
              <div style={{ fontSize: 16, fontWeight: 700, color: "#0b1b3a" }}>Nothing here yet</div>
              <p className="mt-2 mb-0" style={{ fontSize: 14, color: "#5b6b86" }}>
                No items for {site ? siteLabel(site) : "this site"}. Create the first one.
              </p>
            </div>
          ),
        }}
      />
      )}

      <Drawer
        title={
          <div style={{ minWidth: 0 }}>
            <div className="cms-sheet-kicker">
              {resource.label} · {editing ? `Edit · ID ${editing.id}` : `New · ${site ? siteLabel(site) : ""}`}
            </div>
            <div className="cms-sheet-title">
              {editing ? (editing[resource.titleField] as string) : `New ${singular.toLowerCase()}`}
            </div>
          </div>
        }
        extra={
          editing && (
            <Button icon={<ClockIcon />} onClick={() => setHistoryOpen(true)}>
              History
            </Button>
          )
        }
        open={drawerOpen}
        closable={{ placement: "end" }}
        onClose={() => setDrawerOpen(false)}
        width={resource.fields.some((f) => f.type === "object-list") ? 720 : 540}
        footer={
          <div className="flex justify-end gap-2.5">
            <Button onClick={() => setDrawerOpen(false)}>Cancel</Button>
            <Button type="primary" loading={saveMutation.isPending} onClick={handleSubmit}>
              Save changes
            </Button>
          </div>
        }
      >
        <Form form={form} layout="vertical">
          {resource.fields.map((f) => (
            <FieldInput
              key={f.name}
              field={f}
              form={form}
              onOpenPicker={(path) => setPickerField(path ?? f.name)}
            />
          ))}
          <Form.Item name="displayOrder" label="Display order">
            <InputNumber style={{ width: "100%" }} />
          </Form.Item>
          <Form.Item name="active" label="Active" valuePropName="checked">
            <Switch />
          </Form.Item>
          <Form.Item name="status" label="Status">
            <Select options={STATUS_OPTIONS} />
          </Form.Item>
        </Form>
      </Drawer>

      <HistoryDrawer
        open={historyOpen}
        onClose={() => setHistoryOpen(false)}
        itemId={editing?.id ?? null}
        onRestored={() => {
          invalidate();
          setDrawerOpen(false);
        }}
      />
      <MediaPickerModal
        open={!!pickerField}
        mode={
          typeof pickerField === "string" &&
          resource.fields.find((f) => f.name === pickerField)?.type === "video"
            ? "video"
            : "image"
        }
        onClose={() => setPickerField(null)}
        onSelect={async (url) => {
          const target = pickerField;
          const spec =
            typeof target === "string"
              ? resource.fields.find((f) => f.name === target)?.imageSpec
              : undefined;
          if (spec) {
            const problem = await checkImageSpec(url, spec);
            if (problem) {
              message.error(problem);
              return; // keep the picker open so another image can be chosen
            }
          }
          if (target) form.setFieldValue(target, url);
          setPickerField(null);
        }}
      />
    </div>
  );
}
