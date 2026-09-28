// Sidebar structure, decoupled from RESOURCES' field-shape config (config/resources.ts) so a new
// page's nav entry doesn't require touching AppShell, and AppShell doesn't need to know anything
// about content-service's field contracts. Add a new page as one more top-level NavItem with
// `items` — nothing else in AppShell changes.
//
// No per-item icons: the sidebar numbers each group's items (01, 02, ...) instead, per the CMS
// Admin design (see design/project/CMS Admin.dc.html) — AppShell computes that numbering.
import { RESOURCES } from "./resources";

export interface NavItem {
  /** Route path for a leaf item; a stable, route-less id for a group (has `items`). */
  key: string;
  label: string;
  /** Present => rendered as a group: a plain section label above its (always-visible) items. */
  items?: NavItem[];
}

export const NAVIGATION: NavItem[] = [
  {
    key: "home-page",
    label: "Home Page",
    // Derived from RESOURCES rather than duplicated here, so adding/renaming a content type
    // in resources.ts is the only edit needed — this list can't drift out of sync with it.
    items: RESOURCES.map((r) => ({ key: `/content/${r.key}`, label: r.label })),
  },
  // Next page ships as one more entry here, e.g.:
  // { key: "about-page", label: "About Page", items: [...] },
  {
    key: "library",
    label: "Library",
    items: [
      { key: "/media", label: "Media Library" },
      { key: "/assistant", label: "Assistant" },
    ],
  },
];
