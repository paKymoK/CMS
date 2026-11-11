import { contentApi } from "./api";

export type PostStatus = "DRAFT" | "PUBLISHED" | "SCHEDULED" | "ARCHIVED";
export type PostSort = "updated_desc" | "updated_asc" | "title_asc" | "title_desc";
export type BulkAction = "PUBLISH" | "UNPUBLISH" | "ARCHIVE" | "TRASH" | "RESTORE" | "DELETE_FOREVER";

/** One post as a list row (BRD POST-01). */
export interface PostRow {
  id: number;
  title: string | null;
  slug: string | null;
  category: string | null;
  tags: string[];
  authorName: string | null;
  status: PostStatus;
  publishAt: string | null;
  /** When it last changed, and who did it ("Display Name (sub)"). */
  updatedAt: string | null;
  updatedBy: string | null;
  version: number;
}

export interface PostPage {
  items: PostRow[];
  /** Counts from 1. */
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
}

export interface PostFacets {
  statusCounts: Record<PostStatus, number>;
  /** Posts in the Trash. They are not in the status counts. */
  trashCount: number;
  categories: { name: string; count: number }[];
  years: number[];
}

export interface BulkResult {
  updated: number[];
  failed: { id: number; title: string | null; reason: string }[];
}

/** What the list is showing; every field is optional and empty means "no condition". */
export interface PostQuery {
  q?: string;
  status?: PostStatus;
  category?: string;
  /** Month and year of the last update. A month needs a year. */
  year?: number;
  month?: number;
  sort?: PostSort;
  page?: number;
}

export const PAGE_SIZE = 20;
/** The most posts one bulk action takes (the server refuses more). */
export const BULK_LIMIT = 100;

export async function listPosts(site: string, query: PostQuery): Promise<PostPage> {
  const params: Record<string, unknown> = { site, size: PAGE_SIZE };
  for (const [key, value] of Object.entries(query)) {
    if (value !== undefined && value !== "") params[key] = value;
  }
  const { data } = await contentApi.get("/v1/admin/posts/page", { params });
  return data.data as PostPage;
}

export async function postFacets(site: string): Promise<PostFacets> {
  const { data } = await contentApi.get("/v1/admin/posts/facets", { params: { site } });
  return data.data as PostFacets;
}

/** `slugs` (post id to address) is for RESTORE only: restore a post under another address. */
export async function bulkPosts(
  site: string,
  ids: number[],
  action: BulkAction,
  slugs?: Record<number, string>,
): Promise<BulkResult> {
  const { data } = await contentApi.post("/v1/admin/posts/bulk", { ids, action, slugs }, { params: { site } });
  return data.data as BulkResult;
}

/** A post in the Trash. Restoring always brings it back as a draft. */
export interface TrashRow {
  id: number;
  title: string | null;
  slug: string | null;
  category: string | null;
  authorName: string | null;
  /** What it was before it was trashed. */
  previousStatus: PostStatus | null;
  trashedAt: string;
  /** Who trashed it ("Display Name (sub)"). */
  trashedBy: string | null;
  /** Whole days until it is deleted for good. */
  daysLeft: number;
}

export interface TrashPage {
  items: TrashRow[];
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
}

/** Days a post stays in the Trash. */
export const TRASH_DAYS = 30;

export async function listTrash(site: string, q: string | undefined, page: number): Promise<TrashPage> {
  const params: Record<string, unknown> = { site, size: PAGE_SIZE, page };
  if (q) params.q = q;
  const { data } = await contentApi.get("/v1/admin/posts/trash", { params });
  return data.data as TrashPage;
}

export interface LinkCheck {
  id: number;
  title: string | null;
  referrerCount: number;
  referrers: { id: number; contentType: string; status: string; title: string | null }[];
}

/** Before trashing: which of these posts other items link to. Empty means nothing to warn about. */
export async function trashLinks(site: string, ids: number[]): Promise<LinkCheck[]> {
  const { data } = await contentApi.post("/v1/admin/posts/trash/links", { ids }, { params: { site } });
  return data.data as LinkCheck[];
}

export interface RestoreCheck {
  id: number;
  title: string | null;
  slug: string;
  /** Set when the address is taken: what holds it. */
  holder: { id: number; title: string | null } | null;
  /** A free address to offer instead. */
  suggestion: string | null;
}

/** Before restoring: whether each post's address is still free. */
export async function restoreCheck(site: string, ids: number[]): Promise<RestoreCheck[]> {
  const { data } = await contentApi.post("/v1/admin/posts/trash/restore-check", { ids }, { params: { site } });
  return data.data as RestoreCheck[];
}

export const STATUS_LABELS: Record<PostStatus, string> = {
  DRAFT: "Draft",
  PUBLISHED: "Published",
  SCHEDULED: "Scheduled",
  ARCHIVED: "Archived",
};

export const SORT_LABELS: Record<PostSort, string> = {
  updated_desc: "Updated: newest first",
  updated_asc: "Updated: oldest first",
  title_asc: "Title: A to Z",
  title_desc: "Title: Z to A",
};

/**
 * The public address of a post: https://<site domain>/<locale>/insights/<slug>. Without a known
 * domain, the path alone.
 */
export function postUrl(subdomain: string | undefined, siteCode: string, slug: string): string {
  const path = `/${siteCode}/insights/${slug}`;
  return subdomain ? `https://${subdomain}${path}` : path;
}
