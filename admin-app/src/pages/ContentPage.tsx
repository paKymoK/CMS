import { useState } from "react";
import { useParams } from "react-router-dom";
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
import { findResource, type FieldConfig } from "../config/resources";
import MediaPickerModal from "../components/MediaPickerModal";

type Row = Record<string, unknown> & { id: number };

const STATUS_OPTIONS = [
  { label: "Draft", value: "DRAFT" },
  { label: "Published", value: "PUBLISHED" },
];

export default function ContentPage() {
  const { resourceKey } = useParams<{ resourceKey: string }>();
  const resource = resourceKey ? findResource(resourceKey) : undefined;
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
      queryClient.invalidateQueries({ queryKey: ["content", resource?.key, site] });
    },
  });

  const deleteMutation = useMutation({
    mutationFn: async (id: number) =>
      contentApi.delete(`${resource!.apiPath}/${id}`, { params: { site } }),
    onSuccess: () => {
      message.success("Deleted");
      queryClient.invalidateQueries({ queryKey: ["content", resource?.key, site] });
    },
  });

  if (!resource) {
    return <Empty description="Unknown content type" />;
  }

  const openCreate = () => {
    setEditing(null);
    form.resetFields();
    form.setFieldsValue({ active: true, status: "DRAFT", displayOrder: 0 });
    setDrawerOpen(true);
  };

  const openEdit = (row: Row) => {
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

  const columns = [
    { title: "ID", dataIndex: "id", width: 70 },
    { title: resource.fields.find((f) => f.name === resource.titleField)?.label ?? "Title", dataIndex: resource.titleField },
    ...resource.fields
      .filter((f) => f.showInTable && f.name !== resource.titleField)
      .map((f) => ({
        title: f.label,
        dataIndex: f.name,
        render: (value: unknown) =>
          f.type === "boolean" ? (value ? "Yes" : "No") : String(value ?? ""),
      })),
    { title: "Status", dataIndex: "status" },
    { title: "Active", dataIndex: "active", render: (v: boolean) => (v ? "Yes" : "No") },
    { title: "Order", dataIndex: "displayOrder", width: 80 },
    {
      title: "",
      key: "actions",
      width: 120,
      render: (_: unknown, row: Row) => (
        <div className="flex gap-2">
          <Button size="small" icon={<EditOutlined />} onClick={() => openEdit(row)} />
          <Popconfirm
            title="Delete this item?"
            onConfirm={() => deleteMutation.mutate(row.id)}
          >
            <Button size="small" danger icon={<DeleteOutlined />} />
          </Popconfirm>
        </div>
      ),
    },
  ];

  return (
    <div>
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-semibold m-0">{resource.label}</h2>
        <div className="flex gap-2">
          {resource.supportsTypeFilter && (
            <Select
              allowClear
              placeholder="Filter by type"
              style={{ width: 180 }}
              value={typeFilter}
              onChange={setTypeFilter}
              options={resource.typeFilterOptions}
            />
          )}
          <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>
            New
          </Button>
        </div>
      </div>

      <Table
        rowKey="id"
        columns={columns}
        dataSource={listQuery.data ?? []}
        loading={listQuery.isLoading}
        pagination={false}
      />

      <Drawer
        title={editing ? `Edit ${resource.label.slice(0, -1)}` : `New ${resource.label.slice(0, -1)}`}
        open={drawerOpen}
        onClose={() => setDrawerOpen(false)}
        width={480}
        extra={
          <Button type="primary" loading={saveMutation.isPending} onClick={handleSubmit}>
            Save
          </Button>
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
