import { useRef, useState } from "react";
import RichTextEditor, { type RichTextEditorHandle } from "./RichTextEditor";
import MediaPickerModal from "./MediaPickerModal";

/** The rich-text editor as a form control (value / onChange), with its own image picker, so a form can use it like any input. */
export default function RichTextField({ value, onChange }: { value?: string; onChange?: (html: string) => void }) {
  const ref = useRef<RichTextEditorHandle>(null);
  const [pickerOpen, setPickerOpen] = useState(false);

  return (
    <div style={{ border: "1px solid #dde4f0", borderRadius: 10, overflow: "clip" }}>
      <RichTextEditor ref={ref} value={value ?? ""} onChange={(html) => onChange?.(html)} onInsertImage={() => setPickerOpen(true)} />
      <MediaPickerModal
        open={pickerOpen}
        onClose={() => setPickerOpen(false)}
        onSelect={(url) => {
          ref.current?.insertHtml(`<figure><img src="${url}" alt=""><figcaption>Add a caption</figcaption></figure><p><br></p>`);
          setPickerOpen(false);
        }}
      />
    </div>
  );
}
