import { contentApi } from "./api";
import { RESOURCES } from "../config/resources";

export type AuditAction = "CREATE" | "UPDATE" | "PUBLISH" | "UNPUBLISH" | "ARCHIVE" | "DELETE" | "RESTORE";

/** One line of an item's history or of the site's activity feed. */
export interface AuditEntry {
  id: number;
  itemId: number;
  contentType: string;
  action: AuditAction;
  title: string | null;
  actorName: string | null;
  actor: { sub: string; name?: string; email?: string } | null;
  occurredAt: string;
  /** Names of the fields that changed. */
  changed: string[];
}

/** An item as the audit trail recorded it: everything an editor can change. */
export interface Snapshot {
  contentType: string;
  data: Record<string, unknown>;
  status: string;
  active: boolean;
  displayOrder: number;
}

export interface AuditDetail {
  entry: AuditEntry;
  before: Snapshot | null;
  after: Snapshot | null;
}

export interface AuditPage {
  items: AuditEntry[];
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
}

export interface AuditQuery {
  itemId?: number;
  type?: string;
  action?: AuditAction;
  actor?: string;
  /** Inclusive dates, YYYY-MM-DD. */
  from?: string;
  to?: string;
  page?: number;
  size?: number;
}

export interface Restored {
  itemId: number;
  contentType: string;
}

/**
 * What a deleted item's last recorded state hands to the "new item" form: its content, and the two
 * envelope settings an editor can choose. Nothing else comes along (no id, no links to other items,
 * no status): it starts a new item rather than bringing the old one back.
 */
export interface ItemPrefill {
  data: Record<string, unknown>;
  displayOrder: number;
  active: boolean;
}

/** The prefill a snapshot of a deleted item offers. */
export function prefillFrom(snapshot: Snapshot): ItemPrefill {
  return { data: snapshot.data, displayOrder: snapshot.displayOrder, active: snapshot.active };
}

export async function listAudit(site: string, query: AuditQuery): Promise<AuditPage> {
  const params: Record<string, unknown> = { site };
  for (const [key, value] of Object.entries(query)) {
    if (value !== undefined && value !== "") params[key] = value;
  }
  const { data } = await contentApi.get("/v1/admin/audit", { params });
  return data.data as AuditPage;
}

export async function getAuditEntry(site: string, id: number): Promise<AuditDetail> {
  const { data } = await contentApi.get(`/v1/admin/audit/${id}`, { params: { site } });
  return data.data as AuditDetail;
}

export async function restoreAudit(site: string, id: number): Promise<Restored> {
  const { data } = await contentApi.post(`/v1/admin/audit/${id}/restore`, null, { params: { site } });
  return data.data as Restored;
}

export const ACTION_LABELS: Record<AuditAction, string> = {
  CREATE: "Created",
  UPDATE: "Updated",
  PUBLISH: "Published",
  UNPUBLISH: "Unpublished",
  ARCHIVE: "Archived",
  DELETE: "Deleted",
  RESTORE: "Restored",
};

/** antd Tag colours, one per kind of change. */
export const ACTION_COLORS: Record<AuditAction, string> = {
  CREATE: "green",
  UPDATE: "blue",
  PUBLISH: "cyan",
  UNPUBLISH: "orange",
  ARCHIVE: "gold",
  DELETE: "red",
  RESTORE: "purple",
};

/** The content type behind a resource screen: posts and case studies have their own type keys. */
function typeOf(resource: (typeof RESOURCES)[number]): string | null {
  if (resource.contentType) return resource.contentType;
  if (resource.key === "posts") return "post";
  if (resource.key === "case-studies") return "case_study";
  return null;
}

/** Every content type the admin has a screen for, with that screen's name — for filters. */
export const AUDIT_TYPE_OPTIONS = RESOURCES.flatMap((r) => {
  const type = typeOf(r);
  return type ? [{ value: type, label: r.label }] : [];
});

/** "heroTitle" → "Hero title"; the configured label for fields the admin has a form field for. */
export function fieldLabel(contentType: string, name: string): string {
  const resource = RESOURCES.find((r) => typeOf(r) === contentType);
  const configured = resource?.fields.find((f) => f.name === name)?.label;
  if (configured) return configured;
  const spaced = name.replace(/([a-z0-9])([A-Z])/g, "$1 $2").toLowerCase();
  return spaced.charAt(0).toUpperCase() + spaced.slice(1);
}
