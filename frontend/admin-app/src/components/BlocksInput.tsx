import { useState, type DragEvent } from "react";
import { Button, Dropdown, Form, Input, Popconfirm, Switch, type FormInstance } from "antd";
import type { NamePath } from "antd/es/form/interface";
import { DeleteIcon, PlusIcon } from "./icons";
import { FieldInput } from "./FieldInput";
import type { BlockTypeConfig, FieldConfig } from "../config/resources";

type Path = (string | number)[];
interface BlockValue {
  id?: string;
  type?: string;
  hidden?: boolean;
  props?: Record<string, unknown>;
}

const newId = (type: string) => `${type}-${Math.random().toString(36).slice(2, 8)}`;

/** A few words that say which block this is when it is folded: its heading, else its first entry's title. */
function summaryOf(props: Record<string, unknown> | undefined): string {
  if (!props) return "";
  for (const key of ["title", "heading"]) {
    const v = props[key];
    if (typeof v === "string" && v.trim()) return v.trim();
  }
  for (const list of Object.values(props)) {
    if (Array.isArray(list) && list.length && list[0] && typeof list[0] === "object") {
      const t = (list[0] as Record<string, unknown>).title;
      if (typeof t === "string" && t.trim()) return t.trim();
    }
  }
  return "";
}

/**
 * The page's blocks: an ordered list the editor adds to, duplicates, drags (or nudges with the arrows),
 * hides and removes. Each block's form is drawn from its type's `fields` by the same recursive
 * FieldInput as everything else, with the block's `props` as the base path. The form keeps every block
 * mounted while it is folded, so saving validates and sends folded blocks too.
 */
export function BlocksInput({
  field: f,
  form,
  onOpenPicker,
  prefix,
  abs,
}: {
  field: FieldConfig;
  form: FormInstance;
  onOpenPicker: (path?: NamePath) => void;
  prefix: Path;
  abs: Path;
}) {
  const types = f.blockTypes ?? [];
  const name = [...prefix, f.name];
  const absName = [...abs, f.name];
  const blocks = (Form.useWatch(absName, form) as BlockValue[] | undefined) ?? [];
  const [open, setOpen] = useState<Record<string, boolean>>({});
  const [dragFrom, setDragFrom] = useState<number | null>(null);
  const [dragOver, setDragOver] = useState<number | null>(null);
  const typeOf = (t?: string): BlockTypeConfig | undefined => types.find((x) => x.type === t);
  const used = (t: string) => blocks.some((b) => b.type === t);

  return (
    <Form.Item label={f.label}>
      <Form.List name={name}>
        {(entries, { add, remove, move }) => (
          <div className="flex flex-col gap-3">
            {entries.map((entry, index) => {
              const value = blocks[index];
              const cfg = typeOf(value?.type);
              const id = value?.id ?? String(entry.key);
              const folded = !open[id];
              const hidden = value?.hidden === true;
              const summary = summaryOf(value?.props);
              const dragging = dragFrom === index;
              const onDragStart = (e: DragEvent<HTMLElement>) => {
                setDragFrom(index);
                e.dataTransfer.effectAllowed = "move";
                const card = e.currentTarget.closest("[data-block-card]");
                if (card) e.dataTransfer.setDragImage(card, 16, 16);
              };
              return (
                <div
                  key={entry.key}
                  data-block-card
                  onDragOver={(e) => {
                    if (dragFrom === null) return;
                    e.preventDefault();
                    if (dragOver !== index) setDragOver(index);
                  }}
                  onDrop={(e) => {
                    e.preventDefault();
                    if (dragFrom !== null && dragFrom !== index) move(dragFrom, index);
                    setDragFrom(null);
                    setDragOver(null);
                  }}
                  style={{
                    border: `1px solid ${dragOver === index && dragFrom !== null && !dragging ? "#0083ff" : "#dde4f0"}`,
                    borderRadius: 8,
                    background: "#fafbfc",
                    opacity: dragging ? 0.45 : 1,
                  }}
                >
                  <Form.Item name={[entry.name, "id"]} hidden noStyle>
                    <Input />
                  </Form.Item>
                  <Form.Item name={[entry.name, "type"]} hidden noStyle>
                    <Input />
                  </Form.Item>
                  <div className="flex items-center gap-2" style={{ padding: "8px 10px", flexWrap: "wrap" }}>
                    <span
                      draggable
                      onDragStart={onDragStart}
                      onDragEnd={() => {
                        setDragFrom(null);
                        setDragOver(null);
                      }}
                      title="Drag to reorder"
                      aria-label="Drag to reorder"
                      style={{ cursor: "grab", padding: "2px 6px", color: "#7b8aa5", userSelect: "none", letterSpacing: 1 }}
                    >
                      ⋮⋮
                    </span>
                    <button
                      type="button"
                      onClick={() => setOpen((o) => ({ ...o, [id]: folded }))}
                      aria-expanded={!folded}
                      style={{ flex: 1, minWidth: 140, textAlign: "left", border: "none", background: "transparent", cursor: "pointer", padding: 0, fontFamily: "inherit" }}
                    >
                      <span style={{ fontWeight: 600, color: hidden ? "#7b8aa5" : "#0b1b3a" }}>{cfg?.label ?? value?.type ?? "Block"}</span>
                      {summary && <span style={{ marginLeft: 8, color: "#7b8aa5", fontSize: 13 }}>{summary}</span>}
                      {hidden && <span style={{ marginLeft: 8, color: "#9a5b00", fontSize: 12 }}>hidden on the website</span>}
                    </button>
                    <Form.Item name={[entry.name, "hidden"]} valuePropName="checked" noStyle getValueProps={(v) => ({ checked: !v })} normalize={(c) => !c}>
                      <Switch size="small" aria-label={`Show ${cfg?.label ?? "block"} on the website`} />
                    </Form.Item>
                    <Button size="small" disabled={index === 0} onClick={() => move(index, index - 1)} aria-label="Move up">
                      ↑
                    </Button>
                    <Button size="small" disabled={index === entries.length - 1} onClick={() => move(index, index + 1)} aria-label="Move down">
                      ↓
                    </Button>
                    <Button
                      size="small"
                      disabled={!cfg || cfg.single}
                      title={cfg?.single ? "This section can appear only once" : "Duplicate"}
                      onClick={() => {
                        const copy = JSON.parse(JSON.stringify(value)) as BlockValue;
                        copy.id = newId(value?.type ?? "block");
                        add(copy, index + 1);
                        setOpen((o) => ({ ...o, [copy.id!]: true }));
                      }}
                    >
                      Duplicate
                    </Button>
                    <Popconfirm title="Remove this section?" okText="Remove" onConfirm={() => remove(entry.name)}>
                      <Button size="small" danger icon={<DeleteIcon />} aria-label="Remove section" />
                    </Popconfirm>
                  </div>
                  <div style={{ display: folded ? "none" : "block", padding: "4px 12px 12px", opacity: hidden ? 0.55 : 1 }}>
                    {cfg && cfg.fields.length === 0 && <p style={{ margin: 0, color: "#5b6b86", fontSize: 13 }}>{cfg.description}</p>}
                    <div className="cms-nested-field">
                      {(cfg?.fields ?? []).map((sub) => (
                        <FieldInput
                          key={sub.name}
                          field={sub}
                          form={form}
                          onOpenPicker={onOpenPicker}
                          prefix={[entry.name, "props"]}
                          abs={[...absName, entry.name, "props"]}
                        />
                      ))}
                    </div>
                  </div>
                </div>
              );
            })}
            <Dropdown
              trigger={["click"]}
              menu={{
                items: types.map((t) => ({
                  key: t.type,
                  disabled: !!t.single && used(t.type),
                  label: (
                    <div style={{ maxWidth: 320, whiteSpace: "normal" }}>
                      <div style={{ fontWeight: 600 }}>{t.label}</div>
                      {t.description && <div style={{ fontSize: 12, color: "#7b8aa5" }}>{t.description}</div>}
                    </div>
                  ),
                })),
                onClick: ({ key }) => {
                  const id = newId(key);
                  add({ id, type: key, props: {} });
                  setOpen((o) => ({ ...o, [id]: true }));
                },
              }}
            >
              <Button type="dashed" icon={<PlusIcon />}>
                {f.addLabel ?? "Add section"}
              </Button>
            </Dropdown>
          </div>
        )}
      </Form.List>
    </Form.Item>
  );
}
