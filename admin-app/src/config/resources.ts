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
  | "stat-list"
  | "object-list"
  | "multi-select"
  | "content-ref"
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
  /** object-list: the fields of each entry (text / textarea / image only). */
  itemFields?: FieldConfig[];
  /** object-list: label of the "add entry" button. */
  addLabel?: string;
  /** content-ref: pick one item of another content type; the stored value is its `valueField`. */
  ref?: { contentType: string; valueField: string; labelField: string };
  /** Shown as a column in the list table. Defaults to true for scalar field types. */
  showInTable?: boolean;
}

/** Which sidebar group a screen lives in. */
export type ResourceGroup = "home" | "about" | "industry" | "services" | "site";

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
}

const scalarDefault = (type: FieldType) =>
  !["textarea", "json", "string-list", "stat-list", "object-list", "multi-select"].includes(type);

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
    label: "Case Studies",
    apiPath: "/v1/admin/case-studies",
    titleField: "title",
    editorRoute: (id) => `/case-studies/${id}`,
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
    editorRoute: (id) => `/posts/${id}`,
    fields: [
      field({ name: "title", label: "Title", type: "text", required: true }),
      field({ name: "category", label: "Category", type: "text" }),
      field({ name: "date", label: "Date", type: "text" }),
      field({ name: "image", label: "Image", type: "image" }),
      field({ name: "excerpt", label: "Excerpt", type: "textarea" }),
    ],
  },
  {
    key: "awards",
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
    titleField: "title",
    fields: [
      field({ name: "title", label: "Title (line breaks are kept)", type: "textarea", required: true }),
      field({ name: "subtitle", label: "Subtitle / body", type: "textarea" }),
      field({ name: "ctaLabel", label: "Button label", type: "text" }),
      field({ name: "ctaUrl", label: "Button link (e.g. #contact or https://…)", type: "text" }),
      field({
        name: "layout",
        label: "Text layout",
        type: "select",
        required: true,
        options: [
          { label: "Centered (white)", value: "center" },
          { label: "Left (white, light weight)", value: "left" },
          { label: "Right (navy, with body text)", value: "right" },
          { label: "Left, large (gradient)", value: "left-wide" },
          { label: "Bottom left (photo slide)", value: "bottom-left" },
        ],
      }),
      field({
        name: "ctaStyle",
        label: "Button style",
        type: "select",
        options: [
          { label: "White", value: "white" },
          { label: "Blue gradient", value: "blue" },
          { label: "Light gradient", value: "light" },
        ],
      }),
      field({ name: "backgroundImage", label: "Background image", type: "image" }),
      field({ name: "overlayImage", label: "Overlay artwork (transparent PNG/WebP)", type: "image" }),
      field({ name: "backgroundColor", label: "Background colour (#RRGGBB)", type: "text" }),
      field({ name: "backgroundColorEnd", label: "Gradient end colour (#RRGGBB)", type: "text" }),
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
      field({ name: "buttonLabel", label: "Button label", type: "text" }),
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
      field({ name: "image", label: "Background photo (wide, ~16:10)", type: "image" }),
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
    key: "about-hero",
    group: "about",
    label: "About · Hero",
    apiPath: "/v1/admin/content-items",
    contentType: "about_hero",
    titleField: "title",
    fields: [
      field({ name: "breadcrumb", label: "Breadcrumb", type: "text" }),
      field({ name: "title", label: "Headline", type: "textarea", required: true }),
      field({ name: "subtitle", label: "Intro line", type: "textarea" }),
      field({ name: "ctaLabel", label: "Button label", type: "text" }),
      field({ name: "ctaUrl", label: "Button link", type: "text" }),
      field({ name: "image", label: "Background photo", type: "image" }),
      field({ name: "stats", label: "Three figures (value + label)", type: "stat-list" }),
    ],
  },
  {
    key: "about-intro",
    group: "about",
    label: "About · Meet CMC Global",
    apiPath: "/v1/admin/content-items",
    contentType: "about_intro",
    titleField: "title",
    fields: [
      field({ name: "eyebrow", label: "Eyebrow", type: "text" }),
      field({ name: "title", label: "Heading", type: "text", required: true }),
      field({ name: "body", label: "Story (blank line between paragraphs)", type: "textarea" }),
      field({ name: "image", label: "Team collage (transparent PNG/WebP)", type: "image" }),
      field({ name: "decorImage", label: "Background artwork", type: "image" }),
      field({
        name: "statsTitle",
        label: "Heading above the growth figures (the figures come from the Stats screen)",
        type: "text",
      }),
    ],
  },
  {
    key: "about-statements",
    group: "about",
    label: "About · Vision & Mission",
    apiPath: "/v1/admin/content-items",
    contentType: "about_statement",
    titleField: "eyebrow",
    fields: [
      field({
        name: "variant",
        label: "Band",
        type: "select",
        required: true,
        options: [
          { label: "Vision (dark, big headline)", value: "vision" },
          { label: "Mission (light, paragraph)", value: "mission" },
        ],
      }),
      field({ name: "eyebrow", label: "Eyebrow", type: "text" }),
      field({ name: "title", label: "Headline (Vision)", type: "textarea" }),
      field({ name: "body", label: "Paragraph (Mission)", type: "textarea" }),
      field({ name: "image", label: "Image", type: "image" }),
    ],
  },
  {
    key: "leaders",
    group: "about",
    label: "About · Leadership",
    apiPath: "/v1/admin/content-items",
    contentType: "leader",
    titleField: "name",
    fields: [
      field({ name: "name", label: "Name", type: "text", required: true }),
      field({ name: "title", label: "Role", type: "text" }),
      field({ name: "photo", label: "Portrait (cut-out, transparent background)", type: "image" }),
    ],
  },
  {
    key: "industry-hero",
    group: "industry",
    label: "BFSI · Hero",
    apiPath: "/v1/admin/content-items",
    contentType: "industry_hero",
    titleField: "title",
    fields: [
      field({ name: "breadcrumb", label: "Breadcrumb", type: "text" }),
      field({ name: "title", label: "Headline", type: "textarea", required: true }),
      field({ name: "subtitle", label: "Intro line", type: "textarea" }),
      field({ name: "ctaLabel", label: "Button label", type: "text" }),
      field({ name: "ctaUrl", label: "Button link", type: "text" }),
      field({ name: "image", label: "Artwork (transparent PNG/WebP)", type: "image" }),
      field({ name: "stats", label: "Two figures (value + label)", type: "stat-list" }),
    ],
  },
  {
    key: "industry-solutions",
    group: "industry",
    label: "BFSI · Solutions",
    apiPath: "/v1/admin/content-items",
    contentType: "industry_solution",
    titleField: "tabLabel",
    fields: [
      field({
        name: "group",
        label: "Group (arrows move between groups, e.g. Core Banking)",
        type: "text",
        required: true,
      }),
      field({ name: "tabLabel", label: "Tab name", type: "text", required: true }),
      field({ name: "title", label: "Slide heading", type: "text" }),
      field({ name: "logo", label: "Partner logo", type: "image" }),
      field({ name: "image", label: "Photo", type: "image" }),
      field({ name: "columnA", label: "First column (one bullet per line)", type: "textarea" }),
      field({ name: "columnB", label: "Second column (one bullet per line)", type: "textarea" }),
    ],
  },
  {
    key: "industry-reasons",
    group: "industry",
    label: "BFSI · Reasons to partner",
    apiPath: "/v1/admin/content-items",
    contentType: "industry_reason",
    titleField: "title",
    fields: [
      field({ name: "title", label: "Title", type: "text", required: true }),
      field({ name: "image", label: "Image", type: "image" }),
    ],
  },
  {
    key: "industry-stories",
    group: "industry",
    label: "BFSI · Success stories",
    apiPath: "/v1/admin/content-items",
    contentType: "industry_story",
    titleField: "title",
    fields: [
      field({ name: "tag", label: "Tag", type: "text" }),
      field({ name: "title", label: "Title", type: "text", required: true }),
      field({ name: "body", label: "Summary", type: "textarea" }),
      field({ name: "image", label: "Photo", type: "image" }),
      field({ name: "linkLabel", label: "Link label", type: "text" }),
      field({ name: "linkUrl", label: "Link", type: "text" }),
      field({ name: "heading", label: "Left-column heading (first story)", type: "text" }),
      field({ name: "metrics", label: "Left-column figures (first story)", type: "stat-list" }),
    ],
  },
  {
    key: "about-values",
    group: "about",
    label: "About · Values (4C)",
    apiPath: "/v1/admin/content-items",
    contentType: "about_value",
    titleField: "title",
    fields: [
      field({ name: "title", label: "Title (line break for two lines)", type: "textarea", required: true }),
      field({ name: "image", label: "Photo", type: "image" }),
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
    fields: [
      field({ name: "navLabel", label: "Menu name", type: "text", required: true }),
      field({ name: "shortLabel", label: "Breadcrumb name (defaults to the menu name)", type: "text", showInTable: false }),
      field({ name: "slug", label: "URL slug", type: "text", required: true }),
      field({
        name: "category",
        label: "Category",
        type: "content-ref",
        required: true,
        ref: { contentType: "service_category", valueField: "slug", labelField: "name" },
      }),
      field({ name: "heroTitle", label: "Hero · Headline (line break allowed)", type: "textarea", required: true, showInTable: false }),
      field({ name: "heroSubtitle", label: "Hero · Intro line", type: "textarea", showInTable: false }),
      field({ name: "ctaLabel", label: "Hero · Button label", type: "text", showInTable: false }),
      field({ name: "ctaUrl", label: "Hero · Button link", type: "text", showInTable: false }),
      field({ name: "heroImage", label: "Hero · Artwork", type: "image", showInTable: false }),
      field({ name: "heroStats", label: "Hero · Figures (optional, up to two)", type: "stat-list" }),
      field({ name: "capabilitiesEyebrow", label: "Capabilities · Eyebrow", type: "text", showInTable: false }),
      field({ name: "capabilitiesTitle", label: "Capabilities · Heading", type: "text", showInTable: false }),
      field({ name: "capabilitiesSubtitle", label: "Capabilities · Description", type: "textarea", showInTable: false }),
      field({
        name: "capabilities",
        label: "Capabilities (2 show as wide cards, 3+ as narrow ones)",
        type: "object-list",
        addLabel: "Add capability",
        itemFields: [
          field({ name: "title", label: "Title", type: "text", required: true }),
          field({ name: "body", label: "Description", type: "textarea" }),
          field({ name: "image", label: "Image", type: "image" }),
        ],
      }),
      field({ name: "processTitle", label: "Process · Heading", type: "text", showInTable: false }),
      field({ name: "processSubtitle", label: "Process · Description", type: "textarea", showInTable: false }),
      field({
        name: "processSteps",
        label: "Process steps (in order)",
        type: "object-list",
        addLabel: "Add step",
        itemFields: [
          field({ name: "title", label: "Title", type: "text", required: true }),
          field({ name: "image", label: "Image", type: "image" }),
        ],
      }),
      field({ name: "storiesHeading", label: "Success stories · Left-column heading", type: "text", showInTable: false }),
      field({
        name: "caseStudies",
        label: "Success stories · Case study slugs (first one supplies the figures)",
        type: "string-list",
      }),
      field({ name: "reasonsTitle", label: "Reasons · Heading", type: "text", showInTable: false }),
      field({ name: "reasonsSubtitle", label: "Reasons · Description", type: "textarea", showInTable: false }),
      field({
        name: "reasons",
        label: "Reasons to partner (shown as a slider)",
        type: "object-list",
        addLabel: "Add reason",
        itemFields: [
          field({ name: "title", label: "Title", type: "text", required: true }),
          field({ name: "body", label: "Text", type: "textarea" }),
          field({ name: "image", label: "Large image", type: "image" }),
          field({ name: "sideImage", label: "Small image", type: "image" }),
        ],
      }),
      field({
        name: "hiddenSections",
        label: "Hide sections",
        type: "multi-select",
        options: [
          { label: "Capabilities", value: "capabilities" },
          { label: "Process", value: "process" },
          { label: "Success stories", value: "stories" },
          { label: "Reasons to partner", value: "reasons" },
          { label: "Industries strip", value: "industries" },
          { label: "Latest highlights", value: "highlights" },
        ],
      }),
      field({ name: "seoTitle", label: "SEO title", type: "text", showInTable: false }),
      field({ name: "seoDescription", label: "SEO description", type: "textarea", showInTable: false }),
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
