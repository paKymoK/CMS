import type { CSSProperties, ReactNode } from "react";
import { fieldLabel, type Snapshot } from "../lib/audit";
import { diffWords, htmlToText } from "../lib/textDiff";

const removed: CSSProperties = { background: "#ffe3e0", color: "#8a1c10", textDecoration: "line-through" };
const added: CSSProperties = { background: "#d8f5e1", color: "#0b5a26" };
const blockStyle: CSSProperties = {
  margin: 0,
  padding: "10px 12px",
  borderRadius: 8,
  fontFamily: "inherit",
  fontSize: 13.5,
  lineHeight: 1.55,
  whiteSpace: "pre-wrap",
  overflowWrap: "anywhere",
};

const ENVELOPE: { key: keyof Snapshot; label: string }[] = [
  { key: "status", label: "Status" },
  { key: "active", label: "Active" },
  { key: "displayOrder", label: "Display order" },
];

/** A value as text: strings as they are (rich text without its markup), the rest as compact JSON. */
function asText(value: unknown): string {
  if (value === undefined || value === null) return "";
  if (typeof value === "string") return value.includes("<") ? htmlToText(value) : value;
  if (typeof value === "object") return JSON.stringify(value, null, 2);
  return String(value);
}

function isTextual(value: unknown): boolean {
  return value === undefined || value === null || typeof value === "string";
}

/** Where a field's old and new value are shown: inline word diff for text, side by side otherwise. */
function ValueDiff({ before, after }: { before: unknown; after: unknown }) {
  if (isTextual(before) && isTextual(after) && before != null && after != null) {
    const parts = diffWords(asText(before), asText(after));
    return (
      <p style={{ ...blockStyle, background: "#f7f9fb", border: "1px solid #e6ebf0" }}>
        {parts.map((part, i) =>
          part.kind === "same" ? (
            <span key={i}>{part.text}</span>
          ) : part.kind === "del" ? (
            <del key={i} style={removed}>
              {part.text}
            </del>
          ) : (
            <ins key={i} style={{ ...added, textDecoration: "none" }}>
              {part.text}
            </ins>
          ),
        )}
      </p>
    );
  }
  const was = asText(before);
  const is = asText(after);
  return (
    <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))", gap: 8 }}>
      {before !== undefined && before !== null && (
        <pre style={{ ...blockStyle, ...removed, textDecoration: "none" }}>{was || "(empty)"}</pre>
      )}
      {after !== undefined && after !== null && (
        <pre style={{ ...blockStyle, ...added }}>{is || "(empty)"}</pre>
      )}
    </div>
  );
}

function Field({ label, children }: { label: string; children: ReactNode }) {
  return (
    <div style={{ marginBottom: 16 }}>
      <div className="cms-eyebrow" style={{ marginBottom: 6 }}>
        {label}
      </div>
      {children}
    </div>
  );
}

/**
 * What changed between two recorded states of an item: only the fields that differ, each with its
 * old and new value — text as an inline word diff, anything else side by side. A create shows
 * everything the item started with; a delete everything it had.
 */
export default function ContentDiff({
  before,
  after,
  contentType,
}: {
  before: Snapshot | null;
  after: Snapshot | null;
  contentType: string;
}) {
  const beforeData = before?.data ?? {};
  const afterData = after?.data ?? {};
  const keys = Array.from(new Set([...Object.keys(beforeData), ...Object.keys(afterData)]));
  const changedKeys = keys.filter(
    (key) => JSON.stringify(beforeData[key]) !== JSON.stringify(afterData[key]),
  );
  const changedEnvelope = ENVELOPE.filter(
    ({ key }) => !before || !after || JSON.stringify(before[key]) !== JSON.stringify(after[key]),
  );

  if (changedKeys.length === 0 && changedEnvelope.length === 0) {
    return <p style={{ color: "#6a7c90", margin: 0 }}>Nothing the editor can see changed.</p>;
  }

  return (
    <div>
      {changedKeys.map((key) => (
        <Field key={key} label={fieldLabel(contentType, key)}>
          <ValueDiff before={beforeData[key]} after={afterData[key]} />
        </Field>
      ))}
      {changedEnvelope.map(({ key, label }) => (
        <Field key={key} label={label}>
          <ValueDiff before={before ? String(before[key]) : undefined} after={after ? String(after[key]) : undefined} />
        </Field>
      ))}
    </div>
  );
}
