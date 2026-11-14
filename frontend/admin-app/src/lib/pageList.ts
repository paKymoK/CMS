import { contentApi } from "./api";
import type { BulkResult, LinkCheck } from "./postList";

export type PageType = "service_page" | "industry_page" | "about_page";
export type PageStatus = "DRAFT" | "SCHEDULED" | "PUBLISHED" | "ARCHIVED";
export type PageSort = "updated_desc" | "updated_asc" | "title_asc" | "title_desc" | "published_desc" | "published_asc";
export type PageBulkAction = "PUBLISH" | "UNPUBLISH" | "ARCHIVE" | "TRASH" | "RESTORE" | "DELETE_FOREVER";

/** One page as a list row (BRD PAGE-01). */
export interface PageRow {
  id: number;
  type: PageType;
  title: string | null;
  slug: string | null;
  /** The public address, e.g. /services/digital-engineering/web-app. */
  path: string;
  status: PageStatus;
  publishAt: string | null;
  /** Who created it and who last changed it ("Display Name (sub)"). */
  author: string | null;
  /** When it last went live. */
  publishedAt: string | null;
  updatedAt: string | null;
  updatedBy: string | null;
  version: number;
}

export interface PageList {
  items: PageRow[];
  page: number;
  /** 0 when everything is on one page. */
  size: number;
  totalItems: number;
  totalPages: number;
}

export interface PageFacets {
  statusCounts: Record<PageStatus, number>;
  trashCount: number;
  authors: { sub: string; name: string }[];
}

export interface PageQuery {
  q?: string;
  status?: PageStatus;
  type?: PageType;
  author?: string;
  /** Dates (yyyy-mm-dd) bounding when the page last went live. */
  publishedFrom?: string;
  publishedTo?: string;
  sort?: PageSort;
  page?: number;
  /** 0 asks for all. */
  size?: number;
}

export interface PageTrashRow {
  id: number;
  type: PageType;
  title: string | null;
  slug: string | null;
  path: string;
  previousStatus: PageStatus | null;
  trashedAt: string;
  trashedBy: string | null;
}

export interface PageTrashPage {
  items: PageTrashRow[];
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
}

export const PAGE_SIZES = [20, 40, 60, 0] as const;
export const PAGE_TRASH_DAYS = 30;

export const PAGE_TYPE: Record<PageType, { label: string; editor: (id: number | string) => string; newPath: string | null }> = {
  service_page: { label: "Service", editor: (id) => `/service-pages/${id}`, newPath: "/service-pages/new" },
  industry_page: { label: "Industry", editor: (id) => `/industry-pages/${id}`, newPath: "/industry-pages/new" },
  // One About page per site: it is edited, not created.
  about_page: { label: "About", editor: (id) => `/about-page/${id}`, newPath: null },
};

export const PAGE_STATUS_LABELS: Record<PageStatus, string> = {
  DRAFT: "Draft",
  SCHEDULED: "Scheduled",
  PUBLISHED: "Published",
  ARCHIVED: "Archived",
};

export const PAGE_SORT_LABELS: Record<PageSort, string> = {
  updated_desc: "Updated: newest first",
  updated_asc: "Updated: oldest first",
  title_asc: "Name: A to Z",
  title_desc: "Name: Z to A",
  published_desc: "Published: newest first",
  published_asc: "Published: oldest first",
};

export async function listPages(site: string, query: PageQuery): Promise<PageList> {
  const params: Record<string, unknown> = { site, size: 20 };
  for (const [key, value] of Object.entries(query)) {
    if (value !== undefined && value !== "") params[key] = value;
  }
  const { data } = await contentApi.get("/v1/admin/pages/page", { params });
  return data.data as PageList;
}

export async function pageFacets(site: string): Promise<PageFacets> {
  const { data } = await contentApi.get("/v1/admin/pages/facets", { params: { site } });
  return data.data as PageFacets;
}

/** `slugs` (page id to address) is for RESTORE only: restore a page under another address. */
export async function bulkPages(
  site: string,
  ids: number[],
  action: PageBulkAction,
  slugs?: Record<number, string>,
): Promise<BulkResult> {
  const { data } = await contentApi.post("/v1/admin/pages/bulk", { ids, action, slugs }, { params: { site } });
  return data.data as BulkResult;
}

export async function quickEditPage(
  site: string,
  id: number,
  edit: { title?: string; slug?: string; status?: PageStatus; version: number },
): Promise<PageRow> {
  const { data } = await contentApi.put(`/v1/admin/pages/${id}`, edit, { params: { site } });
  return data.data as PageRow;
}

export async function listPageTrash(site: string, q: string | undefined, page: number): Promise<PageTrashPage> {
  const params: Record<string, unknown> = { site, size: 20, page };
  if (q) params.q = q;
  const { data } = await contentApi.get("/v1/admin/pages/trash", { params });
  return data.data as PageTrashPage;
}

/** Before trashing: what links to these pages (and whether they are in a menu). Empty means nothing to warn about. */
export async function pageTrashLinks(site: string, ids: number[]): Promise<LinkCheck[]> {
  const { data } = await contentApi.post("/v1/admin/pages/trash/links", { ids }, { params: { site } });
  return data.data as LinkCheck[];
}

/** The public address of a page on the site: https://<site domain>/<locale><path>. Without a domain, the path alone. */
export function pageUrl(subdomain: string | undefined, siteCode: string, path: string): string {
  const full = `/${siteCode}${path}`;
  return subdomain ? `https://${subdomain}${full}` : full;
}
