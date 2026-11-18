import { useState } from "react";
import type { FormInstance } from "antd";
import { Modal, Popconfirm, message } from "antd";
import { useSite } from "../lib/useSite";
import { toPageBlocks, useLibrary, useLibraryActions, type LibraryItem } from "../lib/blockLibrary";
import SaveToLibraryModal from "./SaveToLibraryModal";

const outline = { height: 32, padding: "0 12px", border: "1px solid #dde4f0", borderRadius: 6, background: "#fff", color: "#0b1b3a", fontSize: 13, fontWeight: 600, cursor: "pointer", fontFamily: "inherit" } as const;

/**
 * "Use a template" and "Save as template" for a Service or Industry page: a template is the page's
 * ordered list of blocks. Using one replaces the sections below the hero with a copy; nothing links
 * the page back to it.
 */
export default function PageTemplateBar({ form, fieldName, onApplied }: { form: FormInstance; fieldName: string; onApplied: () => void }) {
  const { site } = useSite();
  const [pickOpen, setPickOpen] = useState(false);
  const [saveOpen, setSaveOpen] = useState(false);
  const templates = useLibrary(site, "PAGE", pickOpen);
  const { save, remove } = useLibraryActions(site);

  const apply = (item: LibraryItem) => {
    form.setFieldValue(fieldName, toPageBlocks(item));
    setPickOpen(false);
    onApplied();
    message.success(`Applied “${item.name}”. Save the page to keep it.`);
  };
  const choose = (item: LibraryItem) => {
    const current = form.getFieldValue(fieldName) as unknown[] | undefined;
    if (current && current.length > 0) {
      Modal.confirm({ title: "Replace the sections below the hero?", content: "The page's current sections are replaced by a copy of the template's.", okText: "Replace", onOk: () => apply(item) });
    } else apply(item);
  };

  return (
    <>
      <button type="button" style={outline} onClick={() => setPickOpen(true)}>
        Use a template
      </button>
      <button
        type="button"
        style={outline}
        onClick={() => {
          const blocks = form.getFieldValue(fieldName) as unknown[] | undefined;
          if (!blocks || blocks.length === 0) message.info("Add at least one section first.");
          else setSaveOpen(true);
        }}
      >
        Save as template
      </button>

      <Modal title="Use a template" open={pickOpen} onCancel={() => setPickOpen(false)} footer={null} destroyOnHidden width={560}>
        {templates.isLoading && <p style={{ color: "#7b8aa5" }}>Loading…</p>}
        {templates.data?.length === 0 && <p style={{ color: "#7b8aa5" }}>Nothing yet. Use “Save as template” in any Service or Industry page’s editor.</p>}
        <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
          {templates.data?.map((t) => (
            <div key={t.id} className="cms-card" style={{ display: "flex", alignItems: "center", gap: 8, padding: "10px 12px" }}>
              <button type="button" onClick={() => choose(t)} style={{ flex: 1, textAlign: "left", border: "none", background: "transparent", cursor: "pointer", fontFamily: "inherit", padding: 0 }}>
                <div style={{ fontWeight: 600, color: "#0b1b3a" }}>{t.name}</div>
                <div style={{ fontSize: 12.5, color: "#7b8aa5" }}>
                  {t.blocks.length} section{t.blocks.length === 1 ? "" : "s"}
                  {t.description ? ` · ${t.description}` : ""}
                </div>
              </button>
              <Popconfirm title="Delete this template?" description="Pages already made from it are not affected." okText="Delete" okButtonProps={{ danger: true }} onConfirm={() => remove("PAGE", t.id).catch(() => message.error("Could not delete the template"))}>
                <button type="button" aria-label={`Delete template ${t.name}`} style={{ width: 24, height: 24, border: "none", borderRadius: "50%", background: "#eef3fb", color: "#5b6b86", cursor: "pointer" }}>
                  ×
                </button>
              </Popconfirm>
            </div>
          ))}
        </div>
      </Modal>

      <SaveToLibraryModal
        open={saveOpen}
        title="Save as template"
        hint="Saves the page's sections (not the hero, address or SEO) as a starting point for new Service and Industry pages."
        onSave={(name, description) => save("PAGE", name, description, (form.getFieldValue(fieldName) as unknown[]) ?? [])}
        onClose={() => setSaveOpen(false)}
      />
    </>
  );
}
