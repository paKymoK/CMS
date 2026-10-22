// Every content-service admin controller follows the identical CRUD contract (GET list/by-id,
// POST, PUT with id in the body, DELETE/{id}, all under ?site=<code>). Driving all 9 screens off
// one config here, rather than hand-writing 9 near-identical page files, avoids drift risk across
// 9 files: the shapes are genuinely identical on the frontend.
export type FieldType =
  | "text"
  | "textarea"
  | "number"
  | "boolean"
  | "select"
  | "image"
  | "video"
  | "color"
  | "datetime"
  | "string-list"
  | "stat-list"
  | "object-list"
  | "multi-select"
  | "content-ref"
  | "richtext"
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
  /** object-list: the fields of each entry. Any field type, including another object-list (any depth). */
  itemFields?: FieldConfig[];
  /** object-list: label of the "add entry" button. */
  addLabel?: string;
  /** content-ref: pick one item of another content type; the stored value is its `valueField`. */
  ref?: { contentType: string; valueField: string; labelField: string; labelSuffixField?: string };
  /** image: the exact proportions the website expects. The picker rejects an image whose aspect
   * ratio is off by more than 1% or whose width is below `width` (larger, same-ratio files such as
   * 2× versions are fine). */
  imageSpec?: { width: number; height: number };
  /** Only render this field while another field of the same form has the given value. */
  showWhen?: { field: string; equals: string };
  /** Full-page editor only: where the field sits. "side" fields go in the sidebar cards; default is the main column. */
  area?: "main" | "side";
  /** Full-page editor only: cleared when a new item starts from a deleted item's content (e.g. a link to another row). */
  dropOnCopy?: boolean;
  /** Shown as a column in the list table. Defaults to true for scalar field types. */
  showInTable?: boolean;
  /** Full-page editor only: key of a `ResourceConfig.groups` entry; the field is drawn inside that collapsible section. */
  group?: string;
  /** Full-page editor only: not drawn there because the editor already has a control for it (e.g. `slug`); still a list column. */
  tableOnly?: boolean;
  /**
   * stat-list: the figures can instead be the published rows of another content type (a "library").
   * `sourceField` is the sibling field that holds "custom" or "library"; the server swaps the library
   * rows in before answering, so a page renders the same either way.
   */
  library?: { contentType: string; sourceField: string; manageLabel: string; managePath: string };
}

/** A collapsible section of the full-page editor's main column. */
export interface FieldGroup {
  key: string;
  label: string;
  /** Shows an on/off switch; off sections are listed in the page's `hiddenSections` and not rendered by the website. */
  toggleable?: boolean;
  /** Open when the editor loads. Defaults to closed. */
  defaultOpen?: boolean;
  /** Text shown inside the section, for one that has no fields of its own (content edited elsewhere). */
  description?: string;
}

/** Which sidebar group a screen lives in. */
export type ResourceGroup =
  | "home"
  | "perspectives"
  | "company"
  | "about"
  | "industry"
  | "services"
  | "site";

export interface ResourceConfig {
  key: string;
  /** Sidebar group; defaults to "home". */
  group?: ResourceGroup;
  label: string;
  apiPath: string;
  titleField: string;
  fields: FieldConfig[];
  /** Present => backed by the generic content_item table (apiPath is /v1/admin/content-items):
   * `fields` are stored inside the item's `data`, and this is the content_type key. */
  contentType?: string;
  supportsTypeFilter?: boolean;
  typeFilterOptions?: FieldOption[];
  /** Adds a "Duplicate" row action that opens the form prefilled from the row (slug suffixed). */
  duplicable?: boolean;
  /** Present => this resource has a dedicated editor route (rich body, slug, etc.) instead of the
   * generic drawer form; ContentPage navigates here on row-click/"New" rather than opening the
   * drawer. `id` is the row id, or "new" for create. */
  editorRoute?: (id: number | "new") => string;
  /**
   * Full-page editor only: what the SEO checklist needs to know, i.e. what the website falls back to
   * when a search-engine field is empty, and the page's address for a slug (omit `url` for a page at a
   * fixed address).
   */
  seo?: {
    fallbackTitle: (values: Record<string, unknown>) => string;
    fallbackDescription?: (values: Record<string, unknown>) => string | undefined;
    fallbackImage?: (values: Record<string, unknown>) => string | undefined;
    url?: (slug: string, values: Record<string, unknown>) => string;
  };
  /** Full-page editor only: sections for fields that set `group`. Ungrouped fields come first, then the sections in this order. */
  groups?: FieldGroup[];
  /** Present => the editorRoute page is the generic full-page form (ResourceEditorPage) driven by `fields`. */
  editor?: {
    slugPrefix: string;
    singular: string;
    backPath: string;
    /** The site has exactly one: the sidebar opens the editor directly (no list, no back button). */
    singleton?: boolean;
    /** No URL of its own (the page lives at a fixed address), so the editor shows no URL box. */
    noSlug?: boolean;
  };
}

/** The field config a form path points at, e.g. ["results", 0, "value"] -> the `value` itemField of `results`. */
export function findFieldByPath(fields: FieldConfig[], path: string | number | (string | number)[]): FieldConfig | undefined {
  const names = (Array.isArray(path) ? path : [path]).filter((p): p is string => typeof p === "string");
  let list = fields;
  let found: FieldConfig | undefined;
  for (const n of names) {
    found = list.find((f) => f.name === n);
    if (!found) return undefined;
    list = found.itemFields ?? [];
  }
  return found;
}

/** A form value as plain text, or undefined when it is empty or not text. */
const str = (v: unknown) => (typeof v === "string" && v.trim() ? v : undefined);

const scalarDefault = (type: FieldType) =>
  !["textarea", "richtext", "json", "string-list", "stat-list", "object-list", "multi-select"].includes(type);

function field(config: FieldConfig): FieldConfig {
  return { showInTable: scalarDefault(config.type), ...config };
}

export const RESOURCES: ResourceConfig[] = [
  {
    key: "stats",
    label: "Stats",
    apiPath: "/v1/admin/content-items",
    contentType: "stat",
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
    apiPath: "/v1/admin/content-items",
    contentType: "service_card",
    titleField: "name",
    fields: [
      field({ name: "name", label: "Name", type: "text", required: true }),
      field({ name: "description", label: "Description (shown on hover)", type: "textarea" }),
      field({ name: "image", label: "Service image (portrait, ~2:3)", type: "image" }),
    ],
  },
  {
    key: "offices",
    group: "company",
    label: "Offices",
    apiPath: "/v1/admin/content-items",
    contentType: "office",
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
    group: "perspectives",
    label: "Case Studies",
    apiPath: "/v1/admin/case-studies",
    titleField: "title",
    editorRoute: (id) => `/case-studies/${id}`,
    editor: { slugPrefix: "case-studies", singular: "case study", backPath: "/content/case-studies" },
    groups: [{ key: "seo", label: "SEO" }],
    seo: {
      fallbackTitle: (v) => `${str(v.title) || "Untitled case study"} — CMC Global`,
      fallbackDescription: (v) => str(v.summary),
      fallbackImage: (v) => str(v.image),
      url: (slug) => `/case-studies/${slug}`,
    },
    fields: [
      field({ name: "seoTitle", label: "Meta title (the whole page title in search results)", type: "text", showInTable: false, group: "seo" }),
      field({ name: "seoDescription", label: "Meta description (falls back to the summary)", type: "textarea", showInTable: false, group: "seo" }),
      field({ name: "seoImage", label: "Share image (falls back to the cover image)", type: "image", showInTable: false, group: "seo" }),
      field({ name: "seoNoindex", label: "Hide from search engines", type: "boolean", showInTable: false, group: "seo" }),
      field({ name: "title", label: "Title", type: "text", required: true }),
      field({ name: "summary", label: "Summary (shown on cards and under the title)", type: "textarea", showInTable: false }),
      field({ name: "image", label: "Cover image", type: "image" }),
      field({ name: "body", label: "Body (use H2 Challenge / Solution / Outcome)", type: "richtext" }),
      field({ name: "date", label: "Display date", type: "text", area: "side" }),
      field({ name: "category", label: "Category", type: "text", area: "side" }),
      field({
        name: "results",
        label: "Results (e.g. 40% / Faster deployment)",
        type: "object-list",
        addLabel: "Add result",
        area: "side",
        itemFields: [
          field({ name: "value", label: "Value", type: "text", required: true }),
          field({ name: "label", label: "Label", type: "text", required: true }),
        ],
      }),
      field({
        name: "testimonialId",
        label: "Testimonial",
        type: "content-ref",
        ref: { contentType: "testimonial", valueField: "id", labelField: "name", labelSuffixField: "company" },
        area: "side",
        dropOnCopy: true,
        showInTable: false,
      }),
    ],
  },
  {
    key: "posts",
    group: "perspectives",
    label: "Posts",
    apiPath: "/v1/admin/posts",
    titleField: "title",
    editorRoute: (id) => `/posts/${id}`,
    fields: [
      field({ name: "title", label: "Title", type: "text", required: true }),
      field({ name: "category", label: "Category", type: "text" }),
      field({ name: "date", label: "Date", type: "text" }),
      field({ name: "image", label: "Image", type: "image" }),
      field({ name: "excerpt", label: "Excerpt", type: "textarea" }),
      field({ name: "seoTitle", label: "Meta title", type: "text", showInTable: false }),
      field({ name: "seoDescription", label: "Meta description", type: "textarea", showInTable: false }),
      field({ name: "seoImage", label: "Share image", type: "image", showInTable: false }),
      field({ name: "seoNoindex", label: "Hide from search engines", type: "boolean", showInTable: false }),
    ],
  },
  {
    key: "awards",
    group: "company",
    label: "Awards",
    apiPath: "/v1/admin/content-items",
    contentType: "award",
    titleField: "name",
    fields: [
      field({ name: "name", label: "Name", type: "text", required: true }),
      field({ name: "logo", label: "Logo", type: "image", required: true }),
    ],
  },
  {
    key: "certifications",
    group: "company",
    label: "Certifications",
    apiPath: "/v1/admin/content-items",
    contentType: "certification",
    titleField: "name",
    fields: [
      field({ name: "name", label: "Name", type: "text", required: true }),
      field({ name: "logo", label: "Logo", type: "image", required: true }),
    ],
  },
  {
    key: "partners",
    group: "company",
    label: "Partners",
    apiPath: "/v1/admin/content-items",
    contentType: "partner",
    titleField: "name",
    fields: [
      field({ name: "name", label: "Name", type: "text", required: true }),
      field({ name: "logo", label: "Logo", type: "image", required: true }),
    ],
  },
  {
    key: "testimonials",
    label: "Testimonials",
    apiPath: "/v1/admin/content-items",
    contentType: "testimonial",
    titleField: "name",
    fields: [
      field({ name: "name", label: "Name", type: "text", required: true }),
      field({ name: "company", label: "Company", type: "text" }),
      field({ name: "title", label: "Title", type: "text" }),
      field({ name: "photo", label: "Photo", type: "image" }),
      field({ name: "quote", label: "Quote", type: "textarea" }),
      field({ name: "logo", label: "Client logo (on the quote card)", type: "image" }),
      field({ name: "backgroundImage", label: "Quote card artwork", type: "image" }),
      field({ name: "flankLogo", label: "Logo for the side brand card (light artwork)", type: "image" }),
      field({ name: "flankFrom", label: "Side card gradient start (#RRGGBB)", type: "text" }),
      field({ name: "flankTo", label: "Side card gradient end (#RRGGBB)", type: "text" }),
    ],
  },
  {
    key: "footer-nav-categories",
    group: "site",
    label: "Footer Nav Categories",
    apiPath: "/v1/admin/content-items",
    contentType: "footer_nav_category",
    titleField: "label",
    fields: [
      field({ name: "label", label: "Label", type: "text", required: true }),
      field({ name: "links", label: "Links", type: "string-list" }),
    ],
  },
  {
    key: "hero-slides",
    label: "Hero Slides",
    apiPath: "/v1/admin/content-items",
    contentType: "hero_slide",
    titleField: "alt",
    fields: [
      field({
        name: "alt",
        label: "What the artwork says (alt text for search engines and screen readers)",
        type: "textarea",
        required: true,
      }),
      field({ name: "linkUrl", label: "Slide link, optional (e.g. #contact or https://…)", type: "text" }),
      field({
        name: "image",
        label: "Desktop image — 1920×699 (≥1280px screens)",
        type: "image",
        required: true,
        imageSpec: { width: 1920, height: 699 },
      }),
      field({
        name: "tabletImage",
        label: "Tablet image — 1536×864 (768–1279px screens)",
        type: "image",
        required: true,
        imageSpec: { width: 1536, height: 864 },
      }),
      field({
        name: "mobileImage",
        label: "Phone image — 750×1000 (screens under 768px)",
        type: "image",
        required: true,
        imageSpec: { width: 750, height: 1000 },
      }),
    ],
  },
  {
    key: "assistant-band",
    label: "Assistant Band",
    apiPath: "/v1/admin/content-items",
    contentType: "assistant_band",
    titleField: "title",
    fields: [
      field({ name: "title", label: "Heading", type: "textarea", required: true }),
      field({ name: "greeting", label: "Input placeholder", type: "text", required: true }),
      field({ name: "welcome", label: "First assistant message", type: "textarea" }),
      field({ name: "suggestions", label: "Suggested questions", type: "string-list" }),
      field({ name: "backdropImage", label: "Backdrop image", type: "image" }),
      field({ name: "orbImage", label: "Orb image", type: "image" }),
    ],
  },
  {
    key: "section-intros",
    group: "site",
    label: "Section Intros",
    apiPath: "/v1/admin/content-items",
    contentType: "section_intro",
    titleField: "title",
    fields: [
      field({
        name: "section",
        label: "Section (anchor id, e.g. whatwedo)",
        type: "text",
        required: true,
      }),
      field({ name: "eyebrow", label: "Eyebrow", type: "text" }),
      field({ name: "title", label: "Heading", type: "textarea", required: true }),
      field({ name: "body", label: "Paragraph", type: "textarea" }),
      field({ name: "kicker", label: "Small label above the list", type: "text" }),
      field({ name: "linkLabel", label: "\"See all\" link label", type: "text" }),
      field({ name: "linkUrl", label: "\"See all\" link URL", type: "text" }),
      field({ name: "image", label: "Decorative artwork", type: "image" }),
    ],
  },
  {
    key: "feature-band",
    label: "Feature Band",
    apiPath: "/v1/admin/content-items",
    contentType: "feature_band",
    titleField: "title",
    fields: [
      field({ name: "eyebrow", label: "Eyebrow (blue line above the heading)", type: "text" }),
      field({ name: "title", label: "Heading", type: "textarea", required: true }),
      field({ name: "body", label: "Paragraph", type: "textarea" }),
      field({ name: "ctaLabel", label: "Button label", type: "text" }),
      field({ name: "ctaUrl", label: "Button link", type: "text" }),
      field({
        name: "mediaType",
        label: "Backdrop",
        type: "select",
        options: [
          { label: "Photo", value: "image" },
          { label: "Video", value: "video" },
        ],
      }),
      field({ name: "image", label: "Background photo (wide, ~16:10) — also the video poster", type: "image" }),
      field({ name: "titleColor", label: "Heading colour (empty = design default)", type: "color", showInTable: false }),
      field({ name: "titleColorEnd", label: "Heading gradient end colour (set for a gradient)", type: "color", showInTable: false }),
      field({ name: "titleGradientAngle", label: "Heading gradient angle (0–360°, default 135)", type: "number", showInTable: false }),
      field({
        name: "video",
        label: "Background video (uploaded MP4/MOV/WebM, plays muted on loop)",
        type: "video",
        showWhen: { field: "mediaType", equals: "video" },
        showInTable: false,
      }),
    ],
  },
  {
    key: "site-footer",
    group: "site",
    label: "Site Footer",
    apiPath: "/v1/admin/content-items",
    contentType: "site_footer",
    titleField: "title",
    fields: [
      field({ name: "title", label: "Heading (e.g. Let's connect)", type: "text", required: true }),
      field({ name: "ctaLabel", label: "Link label (e.g. Get in touch)", type: "text" }),
      field({ name: "ctaUrl", label: "Link URL", type: "text" }),
      field({ name: "copyright", label: "Copyright line", type: "text" }),
      field({ name: "privacyLabel", label: "Privacy link label", type: "text" }),
      field({ name: "privacyUrl", label: "Privacy link URL", type: "text" }),
      field({ name: "termsLabel", label: "Terms link label", type: "text" }),
      field({ name: "termsUrl", label: "Terms link URL", type: "text" }),
      field({ name: "facebookUrl", label: "Facebook URL (empty hides the icon)", type: "text" }),
      field({ name: "twitterUrl", label: "Twitter / X URL", type: "text" }),
      field({ name: "instagramUrl", label: "Instagram URL", type: "text" }),
    ],
  },
  {
    key: "about-page",
    group: "about",
    label: "About page",
    apiPath: "/v1/admin/content-items",
    contentType: "about_page",
    titleField: "heroTitle",
    editorRoute: (id) => `/about-page/${id}`,
    editor: { slugPrefix: "about", singular: "About page", backPath: "/about-page", singleton: true, noSlug: true },
    seo: {
      fallbackTitle: () => "About Us | CMC Global",
      fallbackDescription: (v) => str(v.heroSubtitle),
      fallbackImage: (v) => str(v.heroImage),
    },
    groups: [
      { key: "hero", label: "Hero", defaultOpen: true },
      { key: "intro", label: "Meet CMC Global", toggleable: true },
      { key: "vision", label: "Our Vision", toggleable: true },
      { key: "mission", label: "Our Mission", toggleable: true },
      { key: "leaders", label: "Leadership team", toggleable: true },
      { key: "values", label: "Our Values (4C)", toggleable: true },
      { key: "seo", label: "SEO" },
    ],
    fields: [
      field({ name: "heroBreadcrumb", label: "Breadcrumb", type: "text", showInTable: false, group: "hero" }),
      field({ name: "heroTitle", label: "Headline", type: "textarea", required: true, group: "hero" }),
      field({ name: "heroSubtitle", label: "Intro line", type: "textarea", showInTable: false, group: "hero" }),
      field({ name: "ctaLabel", label: "Button label", type: "text", showInTable: false, group: "hero" }),
      field({ name: "ctaUrl", label: "Button link", type: "text", showInTable: false, group: "hero" }),
      field({ name: "heroImage", label: "Photo", type: "image", showInTable: false, group: "hero" }),
      field({ name: "heroStats", label: "Three figures (value + label)", type: "stat-list", group: "hero" }),
      field({ name: "introEyebrow", label: "Eyebrow", type: "text", showInTable: false, group: "intro" }),
      field({ name: "introTitle", label: "Heading", type: "text", showInTable: false, group: "intro" }),
      field({ name: "introBody", label: "Story (separate paragraphs with a blank line)", type: "textarea", showInTable: false, group: "intro" }),
      field({ name: "introImage", label: "Team photo", type: "image", showInTable: false, group: "intro" }),
      field({ name: "introDecorImage", label: "Decorative artwork behind the block", type: "image", showInTable: false, group: "intro" }),
      field({ name: "growthTitle", label: "Heading above the growth figures", type: "text", showInTable: false, group: "intro" }),
      field({ name: "growthSource", label: "Growth figures source", type: "text", showInTable: false, tableOnly: true }),
      field({
        name: "growthStats",
        label: "Growth figures (the first five are shown)",
        type: "stat-list",
        group: "intro",
        library: { contentType: "stat", sourceField: "growthSource", manageLabel: "Stats", managePath: "/content/stats" },
      }),
      field({ name: "visionEyebrow", label: "Eyebrow", type: "text", showInTable: false, group: "vision" }),
      field({ name: "visionTitle", label: "Statement (line break allowed)", type: "textarea", showInTable: false, group: "vision" }),
      field({ name: "visionImage", label: "Background photo", type: "image", showInTable: false, group: "vision" }),
      field({ name: "missionEyebrow", label: "Eyebrow", type: "text", showInTable: false, group: "mission" }),
      field({ name: "missionBody", label: "Text (separate paragraphs with a blank line)", type: "textarea", showInTable: false, group: "mission" }),
      field({ name: "missionImage", label: "Artwork", type: "image", showInTable: false, group: "mission" }),
      field({ name: "leadersEyebrow", label: "Eyebrow", type: "text", showInTable: false, group: "leaders" }),
      field({ name: "leadersTitle", label: "Heading", type: "text", showInTable: false, group: "leaders" }),
      field({ name: "leadersRingImage", label: "Ring graphic behind the portraits", type: "image", showInTable: false, group: "leaders" }),
      field({
        name: "leaders",
        label: "Leaders (in display order)",
        type: "object-list",
        addLabel: "Add leader",
        group: "leaders",
        itemFields: [
          field({ name: "name", label: "Name", type: "text", required: true }),
          field({ name: "title", label: "Role", type: "text" }),
          field({ name: "photo", label: "Portrait (cut-out, transparent background)", type: "image" }),
        ],
      }),
      field({ name: "valuesEyebrow", label: "Eyebrow", type: "text", showInTable: false, group: "values" }),
      field({ name: "valuesTitle", label: "Heading", type: "text", showInTable: false, group: "values" }),
      field({ name: "valuesBody", label: "Description", type: "textarea", showInTable: false, group: "values" }),
      field({
        name: "values",
        label: "Values (the first four are shown)",
        type: "object-list",
        addLabel: "Add value",
        group: "values",
        itemFields: [
          field({ name: "title", label: "Title (line break for two lines)", type: "textarea", required: true }),
          field({ name: "image", label: "Photo", type: "image" }),
        ],
      }),
      field({ name: "seoTitle", label: "SEO title", type: "text", showInTable: false, group: "seo" }),
      field({ name: "seoDescription", label: "SEO description", type: "textarea", showInTable: false, group: "seo" }),
      field({ name: "seoImage", label: "Share image (falls back to the main image)", type: "image", showInTable: false, group: "seo" }),
      field({ name: "seoNoindex", label: "Hide from search engines", type: "boolean", showInTable: false, group: "seo" }),
    ],
  },
  {
    key: "industry-pages",
    group: "industry",
    label: "Industry pages",
    apiPath: "/v1/admin/content-items",
    contentType: "industry_page",
    titleField: "navLabel",
    duplicable: true,
    editorRoute: (id) => `/industry-pages/${id}`,
    editor: { slugPrefix: "industries", singular: "industry page", backPath: "/content/industry-pages" },
    seo: {
      fallbackTitle: (v) => `${str(v.navLabel) || "Untitled industry"} | CMC Global`,
      fallbackDescription: (v) => str(v.heroSubtitle),
      fallbackImage: (v) => str(v.heroImage),
      url: (slug) => `/industries/${slug}`,
    },
    groups: [
      { key: "hero", label: "Hero", defaultOpen: true },
      { key: "solutions", label: "Solutions", toggleable: true },
      { key: "reasons", label: "Reasons to partner", toggleable: true },
      { key: "stories", label: "Success stories", toggleable: true },
      { key: "insights", label: "Insights strip", toggleable: true },
      { key: "seo", label: "SEO" },
    ],
    fields: [
      field({ name: "navLabel", label: "Industry name", type: "text", required: true }),
      field({ name: "slug", label: "URL slug", type: "text", required: true, tableOnly: true }),
      field({ name: "heroBreadcrumb", label: "Breadcrumb", type: "text", showInTable: false, group: "hero" }),
      field({ name: "heroTitle", label: "Headline", type: "textarea", required: true, showInTable: false, group: "hero" }),
      field({ name: "heroSubtitle", label: "Intro line", type: "textarea", showInTable: false, group: "hero" }),
      field({ name: "ctaLabel", label: "Button label", type: "text", showInTable: false, group: "hero" }),
      field({ name: "ctaUrl", label: "Button link", type: "text", showInTable: false, group: "hero" }),
      field({ name: "heroImage", label: "Artwork (transparent PNG/WebP)", type: "image", showInTable: false, group: "hero" }),
      field({ name: "heroStats", label: "Two figures (value + label)", type: "stat-list", group: "hero" }),
      field({ name: "solutionsEyebrow", label: "Eyebrow", type: "text", showInTable: false, group: "solutions" }),
      field({ name: "solutionsTitle", label: "Heading", type: "text", showInTable: false, group: "solutions" }),
      field({ name: "solutionsBody", label: "Description", type: "textarea", showInTable: false, group: "solutions" }),
      field({
        name: "solutionGroups",
        label: "Solution groups (the arrows move between groups)",
        type: "object-list",
        addLabel: "Add group",
        group: "solutions",
        itemFields: [
          field({ name: "name", label: "Group name (e.g. Core Banking)", type: "text", required: true }),
          field({
            name: "slides",
            label: "Slides (one tab each)",
            type: "object-list",
            addLabel: "Add slide",
            itemFields: [
              field({ name: "tabLabel", label: "Tab name", type: "text", required: true }),
              field({ name: "title", label: "Slide heading", type: "text" }),
              field({ name: "logo", label: "Partner logo", type: "image" }),
              field({ name: "image", label: "Photo", type: "image" }),
              field({ name: "columnA", label: "First column (one bullet per line)", type: "textarea" }),
              field({ name: "columnB", label: "Second column (one bullet per line)", type: "textarea" }),
            ],
          }),
        ],
      }),
      field({ name: "reasonsEyebrow", label: "Eyebrow", type: "text", showInTable: false, group: "reasons" }),
      field({ name: "reasonsTitle", label: "Heading", type: "text", showInTable: false, group: "reasons" }),
      field({ name: "reasonsBody", label: "Description", type: "textarea", showInTable: false, group: "reasons" }),
      field({
        name: "reasons",
        label: "Reasons (the first three are shown)",
        type: "object-list",
        addLabel: "Add reason",
        group: "reasons",
        itemFields: [
          field({ name: "title", label: "Title", type: "text", required: true }),
          field({ name: "image", label: "Image", type: "image" }),
        ],
      }),
      field({ name: "storiesEyebrow", label: "Eyebrow", type: "text", showInTable: false, group: "stories" }),
      field({ name: "storiesTitle", label: "Heading", type: "text", showInTable: false, group: "stories" }),
      field({ name: "storiesBody", label: "Description", type: "textarea", showInTable: false, group: "stories" }),
      field({ name: "storiesHeading", label: "Left-column heading", type: "text", showInTable: false, group: "stories" }),
      field({ name: "storiesMetrics", label: "Left-column figures (up to four)", type: "stat-list", group: "stories" }),
      field({
        name: "stories",
        label: "Stories",
        type: "object-list",
        addLabel: "Add story",
        group: "stories",
        itemFields: [
          field({ name: "tag", label: "Tag", type: "text" }),
          field({ name: "title", label: "Title", type: "text", required: true }),
          field({ name: "body", label: "Summary", type: "textarea" }),
          field({ name: "image", label: "Photo", type: "image" }),
          field({ name: "linkLabel", label: "Link label", type: "text" }),
          field({ name: "linkUrl", label: "Link", type: "text" }),
        ],
      }),
      field({ name: "insightsEyebrow", label: "Eyebrow", type: "text", showInTable: false, group: "insights" }),
      field({ name: "insightsTitle", label: "Heading", type: "text", showInTable: false, group: "insights" }),
      field({ name: "insightsBody", label: "Description", type: "textarea", showInTable: false, group: "insights" }),
      field({ name: "insightsLinkLabel", label: "Link label", type: "text", showInTable: false, group: "insights" }),
      field({ name: "insightsLinkUrl", label: "Link", type: "text", showInTable: false, group: "insights" }),
      field({ name: "seoTitle", label: "SEO title", type: "text", showInTable: false, group: "seo" }),
      field({ name: "seoDescription", label: "SEO description", type: "textarea", showInTable: false, group: "seo" }),
      field({ name: "seoImage", label: "Share image (falls back to the main image)", type: "image", showInTable: false, group: "seo" }),
      field({ name: "seoNoindex", label: "Hide from search engines", type: "boolean", showInTable: false, group: "seo" }),
    ],
  },
  {
    key: "service-pages",
    group: "services",
    label: "Service pages",
    apiPath: "/v1/admin/content-items",
    contentType: "service_page",
    titleField: "navLabel",
    duplicable: true,
    editorRoute: (id) => `/service-pages/${id}`,
    editor: { slugPrefix: "services/[category]", singular: "service page", backPath: "/content/service-pages" },
    seo: {
      fallbackTitle: (v) => `${str(v.navLabel) || "Untitled service"} | CMC Global`,
      fallbackDescription: (v) => str(v.heroSubtitle),
      fallbackImage: (v) => str(v.heroImage),
      url: (slug, v) => `/services/${str(v.category) || "…"}/${slug}`,
    },
    groups: [
      { key: "hero", label: "Hero", defaultOpen: true },
      { key: "capabilities", label: "Capabilities", toggleable: true },
      { key: "process", label: "Process", toggleable: true },
      { key: "stories", label: "Success stories", toggleable: true },
      { key: "reasons", label: "Reasons to partner", toggleable: true },
      {
        key: "industries",
        label: "Industries strip",
        toggleable: true,
        description: "Shows the site's industries strip. Edit its cards under Service pages → Industries strip.",
      },
      {
        key: "highlights",
        label: "Latest highlights",
        toggleable: true,
        description: "Shows the latest published insights. Nothing to edit here.",
      },
      { key: "seo", label: "SEO" },
    ],
    fields: [
      field({ name: "navLabel", label: "Menu name", type: "text", required: true }),
      field({ name: "shortLabel", label: "Breadcrumb name (defaults to the menu name)", type: "text", showInTable: false }),
      field({ name: "slug", label: "URL slug", type: "text", required: true, tableOnly: true }),
      field({
        name: "category",
        label: "Category",
        type: "content-ref",
        required: true,
        ref: { contentType: "service_category", valueField: "slug", labelField: "name" },
      }),
      field({ name: "heroTitle", label: "Headline (line break allowed)", type: "textarea", required: true, showInTable: false, group: "hero" }),
      field({ name: "heroSubtitle", label: "Intro line", type: "textarea", showInTable: false, group: "hero" }),
      field({ name: "ctaLabel", label: "Button label", type: "text", showInTable: false, group: "hero" }),
      field({ name: "ctaUrl", label: "Button link", type: "text", showInTable: false, group: "hero" }),
      field({ name: "heroImage", label: "Artwork", type: "image", showInTable: false, group: "hero" }),
      field({ name: "heroStats", label: "Figures (optional, up to two)", type: "stat-list", group: "hero" }),
      field({ name: "capabilitiesEyebrow", label: "Eyebrow", type: "text", showInTable: false, group: "capabilities" }),
      field({ name: "capabilitiesTitle", label: "Heading", type: "text", showInTable: false, group: "capabilities" }),
      field({ name: "capabilitiesSubtitle", label: "Description", type: "textarea", showInTable: false, group: "capabilities" }),
      field({
        name: "capabilities",
        label: "Capabilities (2 show as wide cards, 3+ as narrow ones)",
        type: "object-list",
        addLabel: "Add capability",
        group: "capabilities",
        itemFields: [
          field({ name: "title", label: "Title", type: "text", required: true }),
          field({ name: "body", label: "Description", type: "textarea" }),
          field({ name: "image", label: "Image", type: "image" }),
        ],
      }),
      field({ name: "processTitle", label: "Heading", type: "text", showInTable: false, group: "process" }),
      field({ name: "processSubtitle", label: "Description", type: "textarea", showInTable: false, group: "process" }),
      field({
        name: "processSteps",
        label: "Process steps (in order)",
        type: "object-list",
        addLabel: "Add step",
        group: "process",
        itemFields: [
          field({ name: "title", label: "Title", type: "text", required: true }),
          field({ name: "image", label: "Image", type: "image" }),
        ],
      }),
      field({ name: "storiesHeading", label: "Left-column heading", type: "text", showInTable: false, group: "stories" }),
      field({
        name: "caseStudies",
        label: "Case study slugs (first one supplies the figures)",
        type: "string-list",
        group: "stories",
      }),
      field({ name: "reasonsTitle", label: "Heading", type: "text", showInTable: false, group: "reasons" }),
      field({ name: "reasonsSubtitle", label: "Description", type: "textarea", showInTable: false, group: "reasons" }),
      field({
        name: "reasons",
        label: "Reasons to partner (shown as a slider)",
        type: "object-list",
        addLabel: "Add reason",
        group: "reasons",
        itemFields: [
          field({ name: "title", label: "Title", type: "text", required: true }),
          field({ name: "body", label: "Text", type: "textarea" }),
          field({ name: "image", label: "Large image", type: "image" }),
          field({ name: "sideImage", label: "Small image", type: "image" }),
        ],
      }),
      field({ name: "seoTitle", label: "SEO title", type: "text", showInTable: false, group: "seo" }),
      field({ name: "seoDescription", label: "SEO description", type: "textarea", showInTable: false, group: "seo" }),
      field({ name: "seoImage", label: "Share image (falls back to the main image)", type: "image", showInTable: false, group: "seo" }),
      field({ name: "seoNoindex", label: "Hide from search engines", type: "boolean", showInTable: false, group: "seo" }),
    ],
  },
  {
    key: "service-categories",
    group: "services",
    label: "Service categories",
    apiPath: "/v1/admin/content-items",
    contentType: "service_category",
    titleField: "name",
    fields: [
      field({ name: "name", label: "Name", type: "text", required: true }),
      field({ name: "slug", label: "URL slug", type: "text", required: true }),
    ],
  },
  {
    key: "industry-cards",
    group: "services",
    label: "Industries strip",
    apiPath: "/v1/admin/content-items",
    contentType: "industry_card",
    titleField: "label",
    fields: [
      field({ name: "label", label: "Name", type: "text", required: true }),
      field({ name: "image", label: "Icon (transparent; empty shows the name as text)", type: "image" }),
      field({ name: "url", label: "Link (empty = not a link)", type: "text" }),
    ],
  },
  {
    key: "banners",
    label: "Banners",
    apiPath: "/v1/admin/content-items",
    contentType: "banner",
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
