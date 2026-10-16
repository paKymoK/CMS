// Primary nav and in-page section nav never change per-region, so unlike the rest of
// content/home/*, this is hardcoded here rather than fetched from the CMS — it used to come
// from content-service's primary-nav-item/nav-section tables, which were removed.

export type PrimaryNavItem = {
  label: string;
  hasDropdown?: boolean;
  /** Real destination, if this item has one yet — falls back to "#" (unwired) when omitted. */
  href?: string;
  /** Sub-pages listed in the header's dropdown panel. */
  children?: { label: string; href: string }[];
  /** Grouped sub-pages (a heading with its links under it), for the Services dropdown. */
  groups?: { label: string; links: { label: string; href: string }[] }[];
};

export type NavSection = {
  /** Anchor id, matches the section's id attribute. */
  id: string;
  label: string;
};

export const PRIMARY_NAV: PrimaryNavItem[] = [
  { label: "Services", hasDropdown: true },
  { label: "Case Studies", hasDropdown: true, href: "/case-studies" },
  { label: "Insights", href: "/insights" },
  { label: "About Us", href: "/about" },
  { label: "Resources", hasDropdown: true },
  { label: "Careers" },
];

/** Desktop header order, per the Figma "Header" component. (PRIMARY_NAV above still drives the
 * mobile bottom bar until that is redesigned.) */
export const HEADER_NAV: PrimaryNavItem[] = [
  { label: "About Us", href: "/about" },
  { label: "Services", hasDropdown: true, href: "/#whatwedo" },
  {
    label: "Industries",
    hasDropdown: true,
    // Healthcare and Automotive have no design yet, so their pages don't exist (the links 404).
    children: [
      { label: "Banking, Finance & Insurance", href: "/industries/bfsi" },
      { label: "Healthcare", href: "/industries/healthcare" },
      { label: "Automotive", href: "/industries/automotive" },
    ],
  },
  { label: "Insights", href: "/insights" },
  { label: "Career" },
  { label: "Contact", href: "/#contact" },
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
