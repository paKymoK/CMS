// Sidebar structure, decoupled from RESOURCES' field-shape config (config/resources.ts) so a new
// page's nav entry doesn't require touching AppShell, and AppShell doesn't need to know anything
// about content-service's field contracts. Add a new page as one more top-level NavItem with
// `items` — nothing else in AppShell changes.
//
// The sidebar renders each group as a collapsible section with one glyph (`icon`, an SVG path on a
// 24px grid) — the same glyph is what remains when the sidebar is collapsed to its icon rail.
import { RESOURCES, type ResourceGroup } from "./resources";

export interface NavItem {
  /** Route path for a leaf item; a stable, route-less id for a group (has `items`). */
  key: string;
  label: string;
  /** SVG path data (24x24 viewBox, stroked) for the group / top-level item glyph. */
  icon?: string;
  /** Present => rendered as a collapsible group of leaf links. */
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
  { key: "home-page", label: "Home Page", icon: "M3 11l9-8 9 8M5 10v10h14V10", items: inGroup("home") },
  {
    key: "perspectives",
    label: "Perspectives",
    icon: "M5 4h14v16H5zM9 8h6M9 12h6M9 16h3",
    items: inGroup("perspectives"),
  },
  {
    key: "company",
    label: "Company",
    icon: "M4 21V6l8-3 8 3v15M9 21v-5h6v5M8 10h.01M12 10h.01M16 10h.01M8 13h.01M12 13h.01M16 13h.01",
    items: inGroup("company"),
  },
  {
    key: "about-page",
    label: "About Page",
    icon: "M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18M12 8h.01M11 12h1v5h1",
    items: inGroup("about"),
  },
  {
    key: "services-pages",
    label: "Service Pages",
    icon: "M4 4h7v7H4zM13 4h7v7h-7zM4 13h7v7H4zM13 13h7v7h-7z",
    items: inGroup("services"),
  },
  {
    key: "industry-page",
    label: "Industry Page",
    icon: "M3 21h18M5 21V8l7-4 7 4v13M9 21v-6h6v6",
    items: inGroup("industry"),
  },
  {
    key: "site-wide",
    label: "Site-wide",
    icon: "M12 21a9 9 0 1 0 0-18 9 9 0 0 0 0 18M3 12h18M12 3c3 3 3 15 0 18M12 3c-3 3-3 15 0 18",
    items: inGroup("site"),
  },
  {
    key: "library",
    label: "Library",
    icon: "M3 7a2 2 0 0 1 2-2h4l2 2h8a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z",
    items: [
      { key: "/media", label: "Media Library" },
      { key: "/activity", label: "Activity" },
      { key: "/assistant", label: "Assistant" },
    ],
  },
];
