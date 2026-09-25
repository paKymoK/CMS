// Sidebar structure, decoupled from RESOURCES' field-shape config (config/resources.ts) so a new
// page's nav entry doesn't require touching AppShell, and AppShell doesn't need to know anything
// about content-service's field contracts. Add a new page as one more top-level NavItem with
// `items` (rendered as a submenu) — nothing else in AppShell changes.
import type { ReactNode } from "react";
import { FileTextOutlined, PictureOutlined, RobotOutlined } from "@ant-design/icons";
import { RESOURCES } from "./resources";

export interface NavItem {
  /** Route path for a leaf item; a stable, route-less id for a group (has `items`). */
  key: string;
  label: string;
  icon?: ReactNode;
  /** Present => rendered as a collapsible submenu instead of a direct link. */
  items?: NavItem[];
}

export const NAVIGATION: NavItem[] = [
  {
    key: "home-page",
    label: "Home Page",
    icon: <FileTextOutlined />,
    // Derived from RESOURCES rather than duplicated here, so adding/renaming a content type
    // in resources.ts is the only edit needed — this list can't drift out of sync with it.
    items: RESOURCES.map((r) => ({ key: `/content/${r.key}`, label: r.label })),
  },
  // Next page ships as one more entry here, e.g.:
  // { key: "about-page", label: "About Page", icon: <FileTextOutlined />, items: [...] },
  { key: "/media", label: "Media Library", icon: <PictureOutlined /> },
  { key: "/assistant", label: "Assistant", icon: <RobotOutlined /> },
];
