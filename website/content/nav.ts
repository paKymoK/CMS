// Primary nav and in-page section nav never change per-region, so unlike the rest of
// content/home/*, this is hardcoded here rather than fetched from the CMS — it used to come
// from content-service's primary-nav-item/nav-section tables, which were removed.

export type PrimaryNavItem = {
  label: string;
  hasDropdown?: boolean;
  /** Real destination, if this item has one yet — falls back to "#" (unwired) when omitted. */
  href?: string;
};

export type NavSection = {
  /** Anchor id, matches the section's id attribute. */
  id: string;
  label: string;
};

export const PRIMARY_NAV: PrimaryNavItem[] = [
  { label: "Services", hasDropdown: true },
  { label: "Case Studies", hasDropdown: true },
  { label: "Insights", href: "/insights" },
  { label: "About Us", hasDropdown: true },
  { label: "Resources", hasDropdown: true },
  { label: "Careers" },
];

export const NAV_SECTIONS: NavSection[] = [
  { id: "overview", label: "Overview" },
  { id: "whatwedo", label: "What We Do" },
  { id: "testimonials", label: "Testimonials" },
  { id: "global", label: "Global Delivery" },
  { id: "about", label: "About Us" },
  { id: "cases", label: "Case Studies" },
  { id: "insights", label: "Insights" },
];
