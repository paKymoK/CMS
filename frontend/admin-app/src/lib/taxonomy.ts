import { contentApi } from "./api";
import type { BulkResult } from "./postList";

/** Categories and tags are two lists with the same screens and rules; the kind picks the API path. */
export type TaxonomyKind = "categories" | "tags";

export const KIND_PATH: Record<TaxonomyKind, string> = {
  categories: "post-categories",
  tags: "post-tags",
};

export const KIND_TEXT: Record<TaxonomyKind, { plural: string; singular: string; Singular: string; Plural: string }> = {
  categories: { plural: "categories", singular: "category", Singular: "Category", Plural: "Categories" },
  tags: { plural: "tags", singular: "tag", Singular: "Tag", Plural: "Tags" },
};

export type TaxonomyStatus = "ACTIVE" | "INACTIVE";
export type TaxonomySort = "name_asc" | "name_desc" | "updated_desc" | "updated_asc" | "posts_desc" | "posts_asc";

export interface TaxonomyRow {
  id: number;
  name: string;
  slug: string;
  description: string | null;
  parentId: number | null;
  parentName: string | null;
  status: TaxonomyStatus;
  /** Published posts that carry it. */
  publishedCount: number;
  /** The default "Uncategorized": it cannot be deleted, deactivated or renamed to another address. */
  isDefault: boolean;
  updatedAt: string | null;
  updatedBy: string | null;
  version: number;
}

export interface TaxonomyPage {
  items: TaxonomyRow[];
  page: number;
  /** 0 when everything is on one page. */
  size: number;
  totalItems: number;
  totalPages: number;
  statusCounts: Record<TaxonomyStatus, number>;
  trashCount: number;
}

export interface TaxonomyQuery {
  q?: string;
  status?: TaxonomyStatus;
  parentId?: number;
  sort?: TaxonomySort;
  page?: number;
  /** 0 asks for everything. */
  size?: number;
}

export interface TaxonomyTrashRow {
  id: number;
  name: string;
  slug: string | null;
  trashedAt: string;
  trashedBy: string | null;
  /** Posts that get it back on restore. */
  postCount: number;
}

export interface TaxonomyTrashPage {
  items: TaxonomyTrashRow[];
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
}

export type TaxonomyBulkAction = "ACTIVATE" | "DEACTIVATE" | "DELETE" | "REMOVE_FROM_POSTS" | "RESTORE" | "DELETE_FOREVER";

export interface TaxonomyInput {
  name: string;
  slug?: string;
  description?: string;
  parentId?: number | null;
  status?: TaxonomyStatus;
  version?: number;
}

export const TAXONOMY_PAGE_SIZES = [20, 40, 60, 0] as const;
export const TRASH_DAYS = 30;
/** A post carries at most this many categories and tags together. */
export const MAX_PER_POST = 15;

const base = (kind: TaxonomyKind) => `/v1/admin/${KIND_PATH[kind]}`;

export async function listTaxonomy(site: string, kind: TaxonomyKind, query: TaxonomyQuery): Promise<TaxonomyPage> {
  const params: Record<string, unknown> = { site };
  for (const [key, value] of Object.entries(query)) {
    if (value !== undefined && value !== "") params[key] = value;
  }
  const { data } = await contentApi.get(base(kind), { params });
  return data.data as TaxonomyPage;
}

export async function createTaxonomy(site: string, kind: TaxonomyKind, input: TaxonomyInput): Promise<TaxonomyRow> {
  const { data } = await contentApi.post(base(kind), input, { params: { site } });
  return data.data as TaxonomyRow;
}

export async function updateTaxonomy(site: string, kind: TaxonomyKind, id: number, input: TaxonomyInput): Promise<TaxonomyRow> {
  const { data } = await contentApi.put(`${base(kind)}/${id}`, input, { params: { site } });
  return data.data as TaxonomyRow;
}

export async function bulkTaxonomy(
  site: string,
  kind: TaxonomyKind,
  ids: number[],
  action: TaxonomyBulkAction,
): Promise<BulkResult> {
  const { data } = await contentApi.post(`${base(kind)}/bulk`, { ids, action }, { params: { site } });
  return data.data as BulkResult;
}

export async function listTaxonomyTrash(
  site: string,
  kind: TaxonomyKind,
  q: string | undefined,
  page: number,
): Promise<TaxonomyTrashPage> {
  const params: Record<string, unknown> = { site, size: 20, page };
  if (q) params.q = q;
  const { data } = await contentApi.get(`${base(kind)}/trash`, { params });
  return data.data as TaxonomyTrashPage;
}

/** Every category or tag of the site, for pickers and filters (a few hundred at most). */
export async function allTaxonomy(site: string, kind: TaxonomyKind): Promise<TaxonomyRow[]> {
  return (await listTaxonomy(site, kind, { size: 0, sort: "name_asc" })).items;
}

/** The address segment for a name: lower case, accents folded, words joined by hyphens. */
export function slugify(text: string): string {
  return text
    .normalize("NFD")
    .replace(/\p{M}/gu, "")
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/^-+|-+$/g, "");
}
