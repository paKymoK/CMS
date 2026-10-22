import { Button, ColorPicker, DatePicker, Form, Image, Input, InputNumber, Segmented, Select, Switch, type FormInstance } from "antd";
import { useEffect } from "react";
import { useQuery } from "@tanstack/react-query";
import { Link } from "react-router-dom";
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
  if (f.type === "stat-list" && f.library) {
    return <LibraryStatList field={f} form={form} name={name} absName={absName} prefix={prefix} abs={abs} />;
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

/**
 * A stat-list that can come from a library (the published rows of another content type) instead of
 * being typed into the page. The switch is a real form control, so changing it marks the form
 * unsaved; "Convert to custom" copies the library's figures into the page and switches to custom.
 */
function LibraryStatList({
  field: f,
  form,
  name,
  absName,
  prefix,
  abs,
}: {
  field: FieldConfig;
  form: FormInstance;
  name: Path;
  absName: Path;
  prefix: Path;
  abs: Path;
}) {
  const library = f.library!;
  const { site } = useSite();
  const source = (Form.useWatch([...abs, library.sourceField], form) as string | undefined) ?? "custom";
  const rows = useQuery({
    queryKey: ["library", library.contentType, site],
    queryFn: async () => {
      const { data } = await contentApi.get("/v1/admin/content-items", { params: { site, type: library.contentType } });
      return (data.data as { status: string; active: boolean; data: { value: string; label: string } }[])
        .filter((row) => row.status === "PUBLISHED" && row.active)
        .map((row) => ({ value: row.data.value, label: row.data.label }));
    },
    enabled: !!site && source === "library",
  });
  // The library can change while this page is open (another tab, the Stats screen): show it fresh whenever it is in use.
  const refetchLibrary = rows.refetch;
  useEffect(() => {
    if (source === "library") void refetchLibrary();
  }, [source, refetchLibrary]);

  return (
    <Form.Item label={f.label}>
      <Form.Item name={[...prefix, library.sourceField]} noStyle>
        <SourceSwitch
          manageLabel={library.manageLabel}
          managePath={library.managePath}
          rows={rows.data}
          loading={rows.isLoading}
          onConvert={async () => {
            // Read the library at click time, not from the cache: a stale copy is the one thing this must not make.
            const fresh = await refetchLibrary();
            form.setFieldValue(absName, fresh.data ?? []);
          }}
        />
      </Form.Item>
      {source !== "library" && (
        <Form.List name={name}>
          {(figures, { add, remove }) => (
            <div className="flex flex-col gap-2" style={{ marginTop: 12 }}>
              {figures.map((figure) => (
                <div key={figure.key} className="flex items-start gap-2">
                  <Form.Item name={[figure.name, "value"]} rules={[{ required: true, message: "Value" }]} style={{ width: 110, marginBottom: 0 }}>
                    <Input placeholder="33+" />
                  </Form.Item>
                  <Form.Item name={[figure.name, "label"]} rules={[{ required: true, message: "Label" }]} style={{ flex: 1, marginBottom: 0 }}>
                    <Input placeholder="Years of engineering excellence" />
                  </Form.Item>
                  <Button icon={<DeleteIcon />} onClick={() => remove(figure.name)} aria-label="Remove" />
                </div>
              ))}
              <Button type="dashed" icon={<PlusIcon />} onClick={() => add({ value: "", label: "" })}>
                Add figure
              </Button>
            </div>
          )}
        </Form.List>
      )}
    </Form.Item>
  );
}

/** The Custom / From library switch (a Form.Item control), with the library's figures and the convert button shown in library mode. */
function SourceSwitch({
  value = "custom",
  onChange,
  manageLabel,
  managePath,
  rows,
  loading,
  onConvert,
}: {
  value?: string;
  onChange?: (value: string) => void;
  manageLabel: string;
  managePath: string;
  rows?: { value: string; label: string }[];
  loading: boolean;
  onConvert: () => Promise<void>;
}) {
  return (
    <div>
      <Segmented
        value={value}
        onChange={(v) => onChange?.(String(v))}
        options={[
          { label: "Custom", value: "custom" },
          { label: `From the ${manageLabel} library`, value: "library" },
        ]}
      />
      {value === "library" && (
        <div style={{ marginTop: 12, border: "1px solid #dde4f0", borderRadius: 8, padding: 12, background: "#fafbfc" }}>
          <div style={{ fontSize: 12.5, color: "#5b6b86", marginBottom: 8 }}>
            Shows the published figures of the {manageLabel} screen, in its order. Edit them there,{" "}
            <Link to={managePath}>open {manageLabel}</Link>.
          </div>
          {loading ? (
            <div style={{ color: "#7b8aa5" }}>Loading…</div>
          ) : (rows ?? []).length === 0 ? (
            <div style={{ color: "#7b8aa5" }}>No published figures yet.</div>
          ) : (
            <ul style={{ margin: 0, paddingLeft: 18 }}>
              {(rows ?? []).map((row) => (
                <li key={`${row.value}-${row.label}`}>
                  <strong>{row.value}</strong> {row.label}
                </li>
              ))}
            </ul>
          )}
          <Button
            size="small"
            style={{ marginTop: 10 }}
            disabled={!rows || rows.length === 0}
            onClick={async () => {
              await onConvert();
              onChange?.("custom");
            }}
          >
            Convert to custom
          </Button>
        </div>
      )}
    </div>
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
