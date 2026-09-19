// Every content-service admin controller follows the identical CRUD contract (GET list/by-id,
// POST, PUT with id in the body, DELETE/{id}, all under ?site=<code>) — see
// docs/cms-platform-plan.md's Phase 2 status. Driving all 9 screens off one config here, rather
// than hand-writing 9 near-identical page files, is a deliberate deviation from the backend's
// "no generic abstraction" convention: these ARE genuinely identical shapes on the frontend, and
// hand-duplicating them buys nothing but drift risk across 9 files.
// Primary nav items and nav sections used to live here too — removed along with their
// content-service tables/endpoints since that nav structure never changes per-region and is
// now hardcoded directly in the Next.js site.
export type FieldType =
  | "text"
  | "textarea"
  | "number"
  | "boolean"
  | "select"
  | "image"
  | "datetime"
  | "string-list"
  | "json";

export interface FieldOption {
  label: string;
  value: string;
}

export interface FieldConfig {
  name: string;
  label: string;
  type: FieldType;
  required?: boolean;
  options?: FieldOption[];
  /** Shown as a column in the list table. Defaults to true for scalar field types. */
  showInTable?: boolean;
}

export interface ResourceConfig {
  key: string;
  label: string;
  apiPath: string;
  titleField: string;
  fields: FieldConfig[];
  supportsTypeFilter?: boolean;
  typeFilterOptions?: FieldOption[];
}

const scalarDefault = (type: FieldType) =>
  !["textarea", "json", "string-list"].includes(type);

function field(config: FieldConfig): FieldConfig {
  return { showInTable: scalarDefault(config.type), ...config };
}

export const RESOURCES: ResourceConfig[] = [
  {
    key: "stats",
    label: "Stats",
    apiPath: "/v1/admin/stats",
    titleField: "label",
    fields: [
      field({ name: "value", label: "Value", type: "text", required: true }),
      field({ name: "label", label: "Label", type: "text", required: true }),
      field({ name: "note", label: "Note", type: "text" }),
    ],
  },
  {
    key: "services",
    label: "Services",
    apiPath: "/v1/admin/services",
    titleField: "name",
    fields: [field({ name: "name", label: "Name", type: "text", required: true })],
  },
  {
    key: "offices",
    label: "Offices",
    apiPath: "/v1/admin/offices",
    titleField: "city",
    fields: [
      field({ name: "city", label: "City", type: "text", required: true }),
      field({ name: "address", label: "Address", type: "text", required: true }),
      field({ name: "lat", label: "Latitude", type: "number", required: true }),
      field({ name: "lon", label: "Longitude", type: "number", required: true }),
      field({ name: "flagColor", label: "Flag color", type: "text", required: true }),
      field({ name: "big", label: "Big marker", type: "boolean" }),
      field({ name: "image", label: "Office photo", type: "image" }),
    ],
  },
  {
    key: "case-studies",
    label: "Case Studies",
    apiPath: "/v1/admin/case-studies",
    titleField: "title",
    fields: [
      field({ name: "title", label: "Title", type: "text", required: true }),
      field({ name: "date", label: "Date", type: "text" }),
      field({ name: "category", label: "Category", type: "text" }),
      field({ name: "image", label: "Image", type: "image" }),
    ],
  },
  {
    key: "posts",
    label: "Posts",
    apiPath: "/v1/admin/posts",
    titleField: "title",
    fields: [
      field({ name: "title", label: "Title", type: "text", required: true }),
      field({ name: "category", label: "Category", type: "text" }),
      field({ name: "date", label: "Date", type: "text" }),
      field({ name: "image", label: "Image", type: "image" }),
      field({ name: "excerpt", label: "Excerpt", type: "textarea" }),
    ],
  },
  {
    key: "logo-badges",
    label: "Logo Badges",
    apiPath: "/v1/admin/logo-badges",
    titleField: "name",
    supportsTypeFilter: true,
    typeFilterOptions: [
      { label: "Award", value: "AWARD" },
      { label: "Certification", value: "CERTIFICATION" },
      { label: "Partner", value: "PARTNER" },
    ],
    fields: [
      field({
        name: "type",
        label: "Type",
        type: "select",
        required: true,
        options: [
          { label: "Award", value: "AWARD" },
          { label: "Certification", value: "CERTIFICATION" },
          { label: "Partner", value: "PARTNER" },
        ],
      }),
      field({ name: "name", label: "Name", type: "text", required: true }),
      field({ name: "logo", label: "Logo", type: "image", required: true }),
    ],
  },
  {
    key: "testimonials",
    label: "Testimonials",
    apiPath: "/v1/admin/testimonials",
    titleField: "name",
    fields: [
      field({ name: "name", label: "Name", type: "text", required: true }),
      field({ name: "company", label: "Company", type: "text" }),
      field({ name: "title", label: "Title", type: "text" }),
      field({ name: "photo", label: "Photo", type: "image" }),
      field({ name: "quote", label: "Quote", type: "textarea" }),
      field({
        name: "flankLogos",
        label: "Flank logos (JSON array of {name, color})",
        type: "json",
      }),
    ],
  },
  {
    key: "footer-nav-categories",
    label: "Footer Nav Categories",
    apiPath: "/v1/admin/footer-nav-categories",
    titleField: "label",
    fields: [
      field({ name: "label", label: "Label", type: "text", required: true }),
      field({ name: "links", label: "Links", type: "string-list" }),
    ],
  },
  {
    key: "banners",
    label: "Banners",
    apiPath: "/v1/admin/banners",
    titleField: "title",
    fields: [
      field({ name: "title", label: "Title", type: "text", required: true }),
      field({ name: "body", label: "Body", type: "textarea" }),
      field({ name: "image", label: "Image", type: "image" }),
      field({ name: "ctaLabel", label: "CTA label", type: "text" }),
      field({ name: "ctaUrl", label: "CTA URL", type: "text" }),
      field({ name: "activeFrom", label: "Active from", type: "datetime" }),
      field({ name: "activeUntil", label: "Active until", type: "datetime" }),
    ],
  },
];

export function findResource(key: string): ResourceConfig | undefined {
  return RESOURCES.find((r) => r.key === key);
}
