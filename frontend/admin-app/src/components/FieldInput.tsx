import { Button, ColorPicker, DatePicker, Form, Image, Input, InputNumber, Select, Switch, type FormInstance } from "antd";
import { useQuery } from "@tanstack/react-query";
import type { NamePath } from "antd/es/form/interface";
import { DeleteIcon, PictureIcon, PlusIcon } from "./icons";
import { contentApi } from "../lib/api";
import { useSite } from "../lib/useSite";
import { mediaSrc } from "../lib/media";
import type { FieldConfig } from "../config/resources";
import RichTextField from "./RichTextField";

/** Hex colour picker bound to a Form.Item (stores "#rrggbb", or nothing when cleared). */
function HexColorInput({
  value,
  onChange,
}: {
  value?: string;
  onChange?: (v: string | undefined) => void;
}) {
  return (
    <ColorPicker
      value={value || undefined}
      format="hex"
      disabledAlpha
      allowClear
      showText={(c) => (value ? c.toHexString().toUpperCase() : "Default")}
      onChange={(c) => onChange?.(c.toHexString())}
      onClear={() => onChange?.(undefined)}
    />
  );
}

type Path = (string | number)[];

/**
 * One form field of any type. object-list entries render their `itemFields` through this same
 * component, so lists nest to any depth. `prefix` is the field's path relative to the enclosing
 * Form.List (what Form.Item names need); `abs` is the enclosing path from the form root (what
 * useWatch, setFieldValue and the media picker need).
 */
export function FieldInput({
  field: f,
  form,
  onOpenPicker,
  prefix = [],
  abs = [],
}: {
  field: FieldConfig;
  form: FormInstance;
  onOpenPicker: (path?: NamePath) => void;
  prefix?: Path;
  abs?: Path;
}) {
  const rules = f.required ? [{ required: true, message: `${f.label} is required` }] : [];
  const name = [...prefix, f.name];
  const absName = [...abs, f.name];
  const currentValue = Form.useWatch(absName, form) as string | undefined;
  const gate = Form.useWatch(f.showWhen ? [...abs, f.showWhen.field] : absName, form) as string | undefined;

  if (f.showWhen && gate !== f.showWhen.equals) return null;

  if (f.type === "boolean") {
    return (
      <Form.Item name={name} label={f.label} valuePropName="checked">
        <Switch />
      </Form.Item>
    );
  }
  if (f.type === "number") {
    return (
      <Form.Item name={name} label={f.label} rules={rules}>
        <InputNumber style={{ width: "100%" }} />
      </Form.Item>
    );
  }
  if (f.type === "textarea" || f.type === "json") {
    return (
      <Form.Item name={name} label={f.label} rules={rules}>
        <Input.TextArea rows={f.type === "json" ? 4 : 3} />
      </Form.Item>
    );
  }
  if (f.type === "select") {
    return (
      <Form.Item name={name} label={f.label} rules={rules}>
        <Select options={f.options} />
      </Form.Item>
    );
  }
  if (f.type === "datetime") {
    return (
      <Form.Item name={name} label={f.label} rules={rules}>
        <DatePicker showTime style={{ width: "100%" }} />
      </Form.Item>
    );
  }
  if (f.type === "multi-select") {
    return (
      <Form.Item name={name} label={f.label}>
        <Select mode="multiple" options={f.options} allowClear />
      </Form.Item>
    );
  }
  if (f.type === "content-ref") {
    return <ContentRefSelect field={f} name={name} rules={rules} />;
  }
  if (f.type === "richtext") {
    return (
      <Form.Item name={name} label={f.label} rules={rules} valuePropName="value" trigger="onChange">
        <RichTextField />
      </Form.Item>
    );
  }
  if (f.type === "object-list") {
    return (
      <Form.Item label={f.label}>
        <Form.List name={name}>
          {(entries, { add, remove, move }) => (
            <div className="flex flex-col gap-3">
              {entries.map((entry, index) => (
                <div
                  key={entry.key}
                  style={{ border: "1px solid #dde4f0", borderRadius: 6, padding: 12, background: "#fafbfc" }}
                >
                  <div className="flex items-center justify-between mb-2">
                    <span className="cms-eyebrow">
                      {f.label.split("(")[0].trim()} · {index + 1}
                    </span>
                    <div className="flex gap-1">
                      <Button size="small" disabled={index === 0} onClick={() => move(index, index - 1)}>
                        ↑
                      </Button>
                      <Button
                        size="small"
                        disabled={index === entries.length - 1}
                        onClick={() => move(index, index + 1)}
                      >
                        ↓
                      </Button>
                      <Button size="small" danger icon={<DeleteIcon />} onClick={() => remove(entry.name)} aria-label="Remove" />
                    </div>
                  </div>
                  <div className="cms-nested-field">
                    {(f.itemFields ?? []).map((sub) => (
                      <FieldInput
                        key={sub.name}
                        field={sub}
                        form={form}
                        onOpenPicker={onOpenPicker}
                        prefix={[entry.name]}
                        abs={[...absName, entry.name]}
                      />
                    ))}
                  </div>
                </div>
              ))}
              <Button type="dashed" icon={<PlusIcon />} onClick={() => add({})}>
                {f.addLabel ?? "Add"}
              </Button>
            </div>
          )}
        </Form.List>
      </Form.Item>
    );
  }
  if (f.type === "stat-list") {
    return (
      <Form.Item label={f.label}>
        <Form.List name={name}>
          {(fields, { add, remove }) => (
            <div className="flex flex-col gap-2">
              {fields.map((field) => (
                <div key={field.key} className="flex items-start gap-2">
                  <Form.Item
                    name={[field.name, "value"]}
                    rules={[{ required: true, message: "Value" }]}
                    style={{ width: 110, marginBottom: 0 }}
                  >
                    <Input placeholder="33+" />
                  </Form.Item>
                  <Form.Item
                    name={[field.name, "label"]}
                    rules={[{ required: true, message: "Label" }]}
                    style={{ flex: 1, marginBottom: 0 }}
                  >
                    <Input placeholder="Years of engineering excellence" />
                  </Form.Item>
                  <Button icon={<DeleteIcon />} onClick={() => remove(field.name)} aria-label="Remove" />
                </div>
              ))}
              <Button type="dashed" icon={<PlusIcon />} onClick={() => add({ value: "", label: "" })}>
                Add figure
              </Button>
            </div>
          )}
        </Form.List>
      </Form.Item>
    );
  }
  if (f.type === "string-list") {
    return (
      <Form.Item name={name} label={f.label} rules={rules}>
        <Select mode="tags" style={{ width: "100%" }} open={false} tokenSeparators={[","]} />
      </Form.Item>
    );
  }
  if (f.type === "image") {
    return (
      <Form.Item label={f.label}>
        <div className="flex items-center gap-3">
          {currentValue ? (
            <Image src={mediaSrc(currentValue as string)} width={56} height={56} className="object-cover rounded" />
          ) : null}
          <Form.Item name={name} rules={rules} noStyle>
            <Input placeholder="No image selected" readOnly style={{ flex: 1 }} />
          </Form.Item>
          <Button icon={<PictureIcon />} onClick={() => onOpenPicker(absName)}>
            Choose
          </Button>
        </div>
      </Form.Item>
    );
  }
  if (f.type === "color") {
    return (
      <Form.Item name={name} label={f.label} rules={rules}>
        <HexColorInput />
      </Form.Item>
    );
  }
  if (f.type === "video") {
    return (
      <Form.Item label={f.label}>
        <div className="flex items-center gap-3">
          <Form.Item name={name} rules={rules} noStyle>
            <Input placeholder="No video selected" readOnly style={{ flex: 1 }} />
          </Form.Item>
          <Button icon={<PictureIcon />} onClick={() => onOpenPicker(absName)}>
            Choose
          </Button>
          {currentValue ? (
            <Button onClick={() => form.setFieldValue(absName, undefined)}>Clear</Button>
          ) : null}
        </div>
      </Form.Item>
    );
  }
  return (
    <Form.Item name={name} label={f.label} rules={rules}>
      <Input />
    </Form.Item>
  );
}

// Dropdown of another content type's items (e.g. a service page's category), listing the item's
// label but storing its `valueField` (the slug).
function ContentRefSelect({
  field: f,
  name,
  rules,
}: {
  field: FieldConfig;
  name: Path;
  rules: { required: boolean; message: string }[];
}) {
  const { site } = useSite();
  const q = useQuery({
    queryKey: ["content-ref", f.ref?.contentType, site],
    queryFn: async () => {
      const { data } = await contentApi.get("/v1/admin/content-items", {
        params: { site, type: f.ref!.contentType },
      });
      // valueField may name a column of the row itself (e.g. "id") rather than a key inside its data.
      return (data.data as ({ data: Record<string, string> } & Record<string, unknown>)[]).map((i) => {
        const ref = f.ref!;
        const extra = ref.labelSuffixField ? i.data[ref.labelSuffixField] : undefined;
        return {
          value: i.data[ref.valueField] ?? (i[ref.valueField] as string | number),
          label: extra ? `${i.data[ref.labelField]} — ${extra}` : i.data[ref.labelField],
        };
      });
    },
    enabled: !!site && !!f.ref,
  });
  return (
    <Form.Item name={name} label={f.label} rules={rules}>
      <Select options={q.data ?? []} loading={q.isLoading} showSearch optionFilterProp="label" />
    </Form.Item>
  );
}
