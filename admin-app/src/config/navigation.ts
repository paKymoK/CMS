// Sidebar structure, decoupled from RESOURCES' field-shape config (config/resources.ts) so a new
// page's nav entry doesn't require touching AppShell, and AppShell doesn't need to know anything
// about content-service's field contracts. Add a new page as one more top-level NavItem with
// `items` — nothing else in AppShell changes.
//
// No per-item icons: the sidebar numbers each group's items (01, 02, ...) instead, per the CMS
// Admin design (see design/project/CMS Admin.dc.html) — AppShell computes that numbering.
import { RESOURCES, type ResourceGroup } from "./resources";

export interface NavItem {
  /** Route path for a leaf item; a stable, route-less id for a group (has `items`). */
  key: string;
  label: string;
  /** Present => rendered as a group: a plain section label above its (always-visible) items. */
  items?: NavItem[];
}

const inGroup = (group: ResourceGroup) =>
  RESOURCES.filter((r) => (r.group ?? "home") === group).map((r) => ({
    key: `/content/${r.key}`,
    label: r.label,
  }));

export const NAVIGATION: NavItem[] = [
  // Derived from RESOURCES rather than duplicated here, so adding/renaming a content type in
  // resources.ts is the only edit needed — these lists can't drift out of sync with it.
  { key: "home-page", label: "Home Page", items: inGroup("home") },
  { key: "about-page", label: "About Page", items: inGroup("about") },
  { key: "industry-page", label: "Industry Page", items: inGroup("industry") },
  { key: "site-wide", label: "Site-wide", items: inGroup("site") },
  {
    key: "library",
    label: "Library",
    items: [
      { key: "/media", label: "Media Library" },
      { key: "/assistant", label: "Assistant" },
    ],
  },
];
