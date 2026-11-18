import { useState } from "react";
import { Input, Modal, message } from "antd";

/** Asks for a name (and a line of description) and saves; the caller does the saving. */
export default function SaveToLibraryModal({
  open,
  title,
  hint,
  defaultName,
  onSave,
  onClose,
}: {
  open: boolean;
  title: string;
  hint: string;
  defaultName?: string;
  onSave: (name: string, description: string) => Promise<void>;
  onClose: () => void;
}) {
  const [name, setName] = useState(defaultName ?? "");
  const [description, setDescription] = useState("");
  const [saving, setSaving] = useState(false);
  const submit = async () => {
    if (!name.trim()) return;
    setSaving(true);
    try {
      await onSave(name.trim(), description.trim());
      message.success("Saved to the library");
      onClose();
    } catch {
      message.error("Could not save to the library");
    } finally {
      setSaving(false);
    }
  };
  return (
    <Modal title={title} open={open} onOk={submit} okText="Save" okButtonProps={{ disabled: !name.trim(), loading: saving }} onCancel={onClose} destroyOnHidden>
      <Input autoFocus value={name} onChange={(e) => setName(e.target.value)} onPressEnter={submit} placeholder="Name" maxLength={120} />
      <Input style={{ marginTop: 8 }} value={description} onChange={(e) => setDescription(e.target.value)} placeholder="Description (optional)" maxLength={500} />
      <p style={{ marginTop: 10, fontSize: 12, color: "#7b8aa5" }}>{hint}</p>
    </Modal>
  );
}
