export type Row = Record<string, unknown> & { id: number; status: string };

const ENVELOPE_KEYS = ["id", "displayOrder", "status", "version", "publishAt", "unpublishAt", "saveAsDraft"] as const;

// Generic content_item rows keep their editable fields inside `data`; the list/drawer work on the
// flattened shape every typed resource already has, so these two convert at the API boundary only.
// A page's editor shows its pending draft (saved edits to a live page) rather than the live content.
export function flattenItem(row: Record<string, unknown>, preferDraft = false): Row {
  const content = preferDraft && row.draft ? row.draft : row.data;
  return { ...(content as Record<string, unknown>), ...row } as Row;
}

export function toItemPayload(
  contentType: string,
  values: Record<string, unknown>,
): Record<string, unknown> {
  const envelope: Record<string, unknown> = { contentType };
  const data: Record<string, unknown> = {};
  for (const [k, v] of Object.entries(values)) {
    if ((ENVELOPE_KEYS as readonly string[]).includes(k)) envelope[k] = v;
    else if (v !== undefined && v !== null && v !== "") data[k] = v;
  }
  return { ...envelope, data };
}

