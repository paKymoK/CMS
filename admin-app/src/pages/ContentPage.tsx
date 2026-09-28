import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  Table,
  Button,
  Drawer,
  Form,
  Input,
  InputNumber,
  Switch,
  Select,
  DatePicker,
  Popconfirm,
  message,
  Empty,
  Image,
  type FormInstance,
} from "antd";
import { PlusOutlined, EditOutlined, DeleteOutlined, PictureOutlined } from "@ant-design/icons";
import dayjs from "dayjs";
import { contentApi } from "../lib/api";
import { useSite } from "../lib/useSite";
import { siteLabel, SITES } from "../config/sites";
import { findResource, type FieldConfig } from "../config/resources";
import { NAVIGATION } from "../config/navigation";
import MediaPickerModal from "../components/MediaPickerModal";

type Row = Record<string, unknown> & { id: number; active: boolean; status: string };

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
  background: "repeating-linear-gradient(135deg, #0c2447 0 6px, #143c6e 6px 12px)",
};

// Falls back to the design's diagonal-stripe placeholder on load failure too, not just a missing
// value — seed data can point at paths (e.g. website's own /images/...) this app never serves.
function Thumb({ src }: { src: string | null }) {
  const [broken, setBroken] = useState(false);
  if (!src || broken) return <div style={placeholderThumbStyle} />;
  return (
    <img
      src={src}
      alt=""
      style={{ width: 44, height: 44, flex: "none", objectFit: "cover" }}
      onError={() => setBroken(true)}
    />
  );
}

// Which nav group this resource lives under (e.g. "Home Page") — derived from NAVIGATION rather
// than hardcoded, so a future second page's resources get the right eyebrow automatically.
function groupLabelFor(resourceKey: string): string {
  const path = `/content/${resourceKey}`;
  return NAVIGATION.find((g) => g.items?.some((i) => i.key === path))?.label ?? "";
}

export default function ContentPage() {
  const { resourceKey } = useParams<{ resourceKey: string }>();
  const resource = resourceKey ? findResource(resourceKey) : undefined;
  const navigate = useNavigate();
  const { site } = useSite();
  const queryClient = useQueryClient();
  const [form] = Form.useForm();
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [editing, setEditing] = useState<Row | null>(null);
  const [typeFilter, setTypeFilter] = useState<string | undefined>(undefined);
  const [pickerField, setPickerField] = useState<string | null>(null);

  const listKey = ["content", resource?.key, site, typeFilter];

  const listQuery = useQuery({
    queryKey: listKey,
    queryFn: async () => {
      const { data } = await contentApi.get(resource!.apiPath, {
        params: { site, ...(typeFilter ? { type: typeFilter } : {}) },
      });
      return data.data as Row[];
    },
    enabled: !!resource && !!site,
  });

  const invalidate = () => queryClient.invalidateQueries({ queryKey: ["content", resource?.key, site] });

  const saveMutation = useMutation({
    mutationFn: async (values: Record<string, unknown>) => {
      if (editing) {
        return contentApi.put(resource!.apiPath, values, { params: { site } });
      }
      return contentApi.post(resource!.apiPath, values, { params: { site } });
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
    mutationFn: async (row: Row) =>
      contentApi.put(resource!.apiPath, { ...row, active: !row.active }, { params: { site } }),
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

  const rows = (listQuery.data ?? []).filter((r) => (typeFilter ? r.type === typeFilter : true));
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
        <span style={{ fontFamily: "var(--font-mono)", fontSize: 12, color: "#6a7c90" }}>{v}</span>
      ),
    },
    {
      title: <ColHeader>{resource.fields.find((f) => f.name === resource.titleField)?.label ?? "Title"}</ColHeader>,
      dataIndex: resource.titleField,
      render: (title: string, row: Row) => (
        <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
          {imgField ? <Thumb src={(row[imgField.name] as string) ?? null} /> : null}
          <span style={{ fontSize: 14, fontWeight: 600, color: "#10141c" }}>{title}</span>
        </div>
      ),
    },
    ...resource.fields
      .filter((f) => f.showInTable && f.name !== resource.titleField)
      .map((f) => ({
        title: <ColHeader>{f.label}</ColHeader>,
        dataIndex: f.name,
        render: (value: unknown) => (
          <span style={{ fontSize: 14, color: "#55585f" }}>
            {f.type === "boolean" ? (value ? "Yes" : "No") : String(value ?? "—")}
          </span>
        ),
      })),
    {
      title: <ColHeader>Status</ColHeader>,
      dataIndex: "status",
      render: (status: string) => {
        const pub = status === "PUBLISHED";
        return (
          <span
            style={{
              display: "inline-block",
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
        );
      },
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
        <span style={{ fontFamily: "var(--font-mono)", fontSize: 12, color: "#6a7c90" }}>{v}</span>
      ),
    },
    {
      title: "",
      key: "actions",
      width: 150,
      render: (_: unknown, row: Row) => (
        <div className="flex justify-end gap-2">
          <Button
            size="small"
            icon={<EditOutlined />}
            onClick={() => openEdit(row)}
            style={{
              fontFamily: "var(--font-mono)",
              fontSize: 10,
              letterSpacing: ".08em",
              color: "#189fe0",
              borderColor: "rgba(24,159,224,.4)",
            }}
          >
            EDIT
          </Button>
          <Popconfirm title="Delete this item?" onConfirm={() => deleteMutation.mutate(row.id)}>
            <Button
              size="small"
              danger
              icon={<DeleteOutlined />}
              style={{ fontFamily: "var(--font-mono)", fontSize: 10, letterSpacing: ".08em" }}
            >
              DELETE
            </Button>
          </Popconfirm>
        </div>
      ),
    },
  ];

  return (
    <div>
      <div className="flex flex-wrap items-end justify-between gap-5 mb-6">
        <div className="min-w-0">
          <div className="cms-eyebrow mb-3">
            — {groupLabelFor(resource.key)} · {site ? siteLabel(site) : ""}
          </div>
          <h1 className="m-0" style={{ fontSize: "clamp(24px,3vw,32px)", fontWeight: 700, letterSpacing: "-.02em", color: "#10314f" }}>
            {resource.label}
          </h1>
          <p className="mt-2 mb-0" style={{ fontSize: 15, lineHeight: 1.6, color: "#55585f" }}>
            {rows.length} items · <span style={{ color: "#189fe0" }}>{publishedCount} published</span>
            {siteSub ? ` on ${siteSub}` : ""}
          </p>
        </div>
        <div className="flex flex-wrap items-center gap-3">
          {resource.supportsTypeFilter && (
            <div className="flex gap-1.5 p-1 rounded-full" style={{ border: "1px solid #dfe3e8", background: "#ffffff" }}>
              {[{ label: "All", value: undefined }, ...(resource.typeFilterOptions ?? [])].map((o) => {
                const on = typeFilter === o.value;
                return (
                  <button
                    key={o.label}
                    type="button"
                    onClick={() => setTypeFilter(o.value)}
                    style={{
                      border: "none",
                      borderRadius: 999,
                      padding: "8px 14px",
                      cursor: "pointer",
                      fontFamily: "inherit",
                      fontSize: 12.5,
                      fontWeight: 600,
                      background: on ? "#0a2540" : "transparent",
                      color: on ? "#ffffff" : "#3c4858",
                    }}
                  >
                    {o.label}
                  </button>
                );
              })}
            </div>
          )}
          <Button
            icon={<PlusOutlined />}
            onClick={openCreate}
            style={{
              minHeight: 44,
              padding: "0 22px",
              border: "none",
              background: "#189fe0",
              fontFamily: "var(--font-mono)",
              fontSize: 12,
              letterSpacing: ".1em",
              color: "#ffffff",
            }}
          >
            NEW {singular.toUpperCase()}
          </Button>
        </div>
      </div>

      <Table
        rowKey="id"
        columns={columns}
        dataSource={rows}
        loading={listQuery.isLoading}
        pagination={false}
        bordered
        scroll={{ x: "max-content" }}
        locale={{
          emptyText: (
            <div className="py-14 text-center">
              <div className="cms-eyebrow">— Nothing here yet</div>
              <p className="mt-2 mb-0" style={{ fontSize: 14, color: "#55585f" }}>
                No items for {site ? siteLabel(site) : "this site"}. Create the first one.
              </p>
            </div>
          ),
        }}
      />

      <Drawer
        title={
          <div>
            <div className="cms-eyebrow mb-2">
              — {editing ? `Edit · ID ${editing.id}` : `New · ${site ? siteLabel(site) : ""}`}
            </div>
            <div style={{ fontSize: 22, fontWeight: 700, letterSpacing: "-.01em", color: "#10314f" }}>
              {editing ? (editing[resource.titleField] as string) : `New ${singular.toLowerCase()}`}
            </div>
          </div>
        }
        open={drawerOpen}
        onClose={() => setDrawerOpen(false)}
        width={480}
        footer={
          <div className="flex gap-2.5">
            <Button onClick={() => setDrawerOpen(false)}>Cancel</Button>
            <Button type="primary" block loading={saveMutation.isPending} onClick={handleSubmit}>
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
              onOpenPicker={() => setPickerField(f.name)}
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

      <MediaPickerModal
        open={!!pickerField}
        onClose={() => setPickerField(null)}
        onSelect={(url) => {
          if (pickerField) form.setFieldValue(pickerField, url);
          setPickerField(null);
        }}
      />
    </div>
  );
}

function FieldInput({
  field: f,
  form,
  onOpenPicker,
}: {
  field: FieldConfig;
  form: FormInstance;
  onOpenPicker: () => void;
}) {
  const rules = f.required ? [{ required: true, message: `${f.label} is required` }] : [];
  const currentValue = Form.useWatch(f.name, form) as string | undefined;

  if (f.type === "boolean") {
    return (
      <Form.Item name={f.name} label={f.label} valuePropName="checked">
        <Switch />
      </Form.Item>
    );
  }
  if (f.type === "number") {
    return (
      <Form.Item name={f.name} label={f.label} rules={rules}>
        <InputNumber style={{ width: "100%" }} />
      </Form.Item>
    );
  }
  if (f.type === "textarea" || f.type === "json") {
    return (
      <Form.Item name={f.name} label={f.label} rules={rules}>
        <Input.TextArea rows={f.type === "json" ? 4 : 3} />
      </Form.Item>
    );
  }
  if (f.type === "select") {
    return (
      <Form.Item name={f.name} label={f.label} rules={rules}>
        <Select options={f.options} />
      </Form.Item>
    );
  }
  if (f.type === "datetime") {
    return (
      <Form.Item name={f.name} label={f.label} rules={rules}>
        <DatePicker showTime style={{ width: "100%" }} />
      </Form.Item>
    );
  }
  if (f.type === "string-list") {
    return (
      <Form.Item name={f.name} label={f.label} rules={rules}>
        <Select mode="tags" style={{ width: "100%" }} open={false} tokenSeparators={[","]} />
      </Form.Item>
    );
  }
  if (f.type === "image") {
    return (
      <Form.Item label={f.label}>
        <div className="flex items-center gap-3">
          {currentValue ? (
            <Image src={currentValue} width={56} height={56} className="object-cover rounded" />
          ) : null}
          <Form.Item name={f.name} rules={rules} noStyle>
            <Input placeholder="No image selected" readOnly style={{ flex: 1 }} />
          </Form.Item>
          <Button icon={<PictureOutlined />} onClick={onOpenPicker}>
            Choose
          </Button>
        </div>
      </Form.Item>
    );
  }
  return (
    <Form.Item name={f.name} label={f.label} rules={rules}>
      <Input />
    </Form.Item>
  );
}
