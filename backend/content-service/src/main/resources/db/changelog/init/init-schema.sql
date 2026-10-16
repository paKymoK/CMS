-- site is the tenant registry: every content table below is scoped to one row here.
-- Per cms-platform-plan.md's decision log, site == locale (one language per region),
-- content is independently authored per site (no shared "translations" jsonb column
-- anywhere in this schema), and routing resolves site_id from the request's subdomain.
CREATE TABLE IF NOT EXISTS site
(
    id          bigserial PRIMARY KEY,
    code        character varying(8)   NOT NULL UNIQUE, -- en, vi, ja, ko, de
    subdomain   character varying(128) NOT NULL UNIQUE,
    active      boolean                NOT NULL DEFAULT true,
    created_at  timestamp with time zone,
    created_by  jsonb,
    modified_at timestamp with time zone,
    modified_by jsonb
);

-- Registry of content types available through the generic content_item table (below) — per
-- cms-platform-plan.md's "generic content model" decision. A new section/component is a row here
-- (+ one small Java class implementing ContentFields for its strict fields, or none if it stays on
-- the fully-dynamic GenericContentFields fallback), not a new table/entity/repository/controller/
-- service. Service cards, offices, testimonials and case studies live there too — see content_item.
--
-- Global, not site-scoped: a content type (e.g. "testimonial") is the same shape in every
-- region — the site-specific part is the content_item rows themselves, not the type definition.
--
-- field_schema is descriptive metadata for admin-app to render a form dynamically (field name ->
-- input type) — it is NOT what enforces validity. Validation of content_item.data happens
-- server-side against the matching Java ContentFields class before a row is ever persisted (see
-- ContentDataWriter's registry lookup + explicit Validator.validate() call) — the same pattern
-- Workflow's TicketMapper uses for Ticket.detail. field_schema going stale relative to the Java
-- class is a UI-affordance bug, not a data-integrity one.
CREATE TABLE IF NOT EXISTS content_type
(
    id           bigserial PRIMARY KEY,
    key          character varying(64) NOT NULL UNIQUE, -- matches a ContentFields registry entry
    label        character varying     NOT NULL,
    field_schema jsonb                 NOT NULL DEFAULT '{}',
    active       boolean               NOT NULL DEFAULT true,
    created_at   timestamp with time zone,
    created_by   jsonb,
    modified_at  timestamp with time zone,
    modified_by  jsonb
);

-- Every typed content table below (stat, post, logo_badge, footer_nav_category, banner) carries the same envelope: `status` (DRAFT/PUBLISHED — the public
-- API only ever returns PUBLISHED+active rows; admin sees everything), `version` (Spring Data
-- R2DBC's @Version optimistic-locking column — a concurrent-edit guard, not a history table;
-- "simple versioning" per the plan, not full audit trails), and a composite index matching the
-- two query shapes every repository actually issues: findAllBySiteId...OrderByDisplayOrderAsc
-- (admin "get everything for this site") and findAllBySiteIdAndStatusAndActive...OrderByDisplay
-- OrderAsc (public/preview "get published"). Composite since every query filters by site_id
-- first — a lone site_id index wouldn't serve the second shape's status/active/order-by without
-- an extra sort/filter step.

CREATE TABLE IF NOT EXISTS stat
(
    id             bigserial PRIMARY KEY,
    site_id        bigint  NOT NULL REFERENCES site (id),
    value          character varying NOT NULL, -- e.g. "32+", not numeric — rendered verbatim
    label          character varying NOT NULL,
    note           character varying,
    display_order  integer NOT NULL DEFAULT 0,
    active         boolean NOT NULL DEFAULT true,
    status         character varying NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PUBLISHED')),
    version        integer NOT NULL DEFAULT 0,
    created_at     timestamp with time zone,
    created_by     jsonb,
    modified_at    timestamp with time zone,
    modified_by    jsonb
);
CREATE INDEX IF NOT EXISTS idx_stat_site_order ON stat (site_id, display_order);
CREATE INDEX IF NOT EXISTS idx_stat_site_status_active_order ON stat (site_id, status, active, display_order);

-- Generalized from the homepage's "insights" section per the decision log — "category"
-- lets this cover future post-like content (e.g. press releases) without a new table.
CREATE TABLE IF NOT EXISTS post
(
    id             bigserial PRIMARY KEY,
    site_id        bigint  NOT NULL REFERENCES site (id),
    slug           character varying, -- URL segment of the detail page; unique per site (index below)
    category       character varying NOT NULL DEFAULT 'insight',
    image          character varying,
    title          character varying NOT NULL,
    excerpt        text,
    date           character varying,
    body           text,              -- rich text (HTML), same convention as the case_study body
    author_name    character varying,
    author_role    character varying,
    author_bio     text,
    author_avatar  character varying,
    tags           jsonb   NOT NULL DEFAULT '[]', -- ["ai-governance","events"]
    -- Page layout chosen by the author (default | focused | wide | landing); the website switches
    -- its article wrapper on it. Validated in PostServiceImpl, not by a CHECK, so adding a layout
    -- never needs a migration.
    layout         character varying NOT NULL DEFAULT 'default',
    featured       boolean NOT NULL DEFAULT false,
    display_order  integer NOT NULL DEFAULT 0,
    active         boolean NOT NULL DEFAULT true,
    status         character varying NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PUBLISHED')),
    version        integer NOT NULL DEFAULT 0,
    created_at     timestamp with time zone,
    created_by     jsonb,
    modified_at    timestamp with time zone,
    modified_by    jsonb
);
CREATE INDEX IF NOT EXISTS idx_post_site_order ON post (site_id, display_order);
CREATE INDEX IF NOT EXISTS idx_post_site_status_active_order ON post (site_id, status, active, display_order);
-- Partial: rows without a slug never collide with each other, but once set a slug is unique within
-- its own site — never across sites, per the "content is not shared across sites" rule.
CREATE UNIQUE INDEX IF NOT EXISTS uq_post_site_slug ON post (site_id, slug) WHERE slug IS NOT NULL;

CREATE TABLE IF NOT EXISTS logo_badge
(
    id             bigserial PRIMARY KEY,
    site_id        bigint  NOT NULL REFERENCES site (id),
    type           character varying NOT NULL CHECK (type IN ('AWARD', 'CERTIFICATION', 'PARTNER')),
    name           character varying NOT NULL,
    logo           character varying NOT NULL,
    display_order  integer NOT NULL DEFAULT 0,
    active         boolean NOT NULL DEFAULT true,
    status         character varying NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PUBLISHED')),
    version        integer NOT NULL DEFAULT 0,
    created_at     timestamp with time zone,
    created_by     jsonb,
    modified_at    timestamp with time zone,
    modified_by    jsonb
);
-- logo_badge additionally filters by `type` (AWARD/CERTIFICATION/PARTNER) in every query shape.
CREATE INDEX IF NOT EXISTS idx_logo_badge_site_type_order ON logo_badge (site_id, type, display_order);
CREATE INDEX IF NOT EXISTS idx_logo_badge_site_type_status_active_order ON logo_badge (site_id, type, status, active, display_order);


CREATE TABLE IF NOT EXISTS footer_nav_category
(
    id             bigserial PRIMARY KEY,
    site_id        bigint  NOT NULL REFERENCES site (id),
    label          character varying NOT NULL,
    links          jsonb   NOT NULL DEFAULT '[]', -- string[]
    display_order  integer NOT NULL DEFAULT 0,
    active         boolean NOT NULL DEFAULT true,
    status         character varying NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PUBLISHED')),
    version        integer NOT NULL DEFAULT 0,
    created_at     timestamp with time zone,
    created_by     jsonb,
    modified_at    timestamp with time zone,
    modified_by    jsonb
);
CREATE INDEX IF NOT EXISTS idx_footer_nav_category_site_order ON footer_nav_category (site_id, display_order);
CREATE INDEX IF NOT EXISTS idx_footer_nav_category_site_status_active_order ON footer_nav_category (site_id, status, active, display_order);

-- Not used by the current homepage build, added ready for other pages per the decision log
-- ("if Landing page doesn't have [one], other part will probably have it").
CREATE TABLE IF NOT EXISTS banner
(
    id             bigserial PRIMARY KEY,
    site_id        bigint  NOT NULL REFERENCES site (id),
    title          character varying NOT NULL,
    body           text,
    image          character varying,
    cta_label      character varying,
    cta_url        character varying,
    active_from    timestamp with time zone,
    active_until   timestamp with time zone,
    display_order  integer NOT NULL DEFAULT 0,
    active         boolean NOT NULL DEFAULT true,
    status         character varying NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PUBLISHED')),
    version        integer NOT NULL DEFAULT 0,
    created_at     timestamp with time zone,
    created_by     jsonb,
    modified_at    timestamp with time zone,
    modified_by    jsonb
);
CREATE INDEX IF NOT EXISTS idx_banner_site_order ON banner (site_id, display_order);
CREATE INDEX IF NOT EXISTS idx_banner_site_status_active_order ON banner (site_id, status, active, display_order);

-- Generic content table per cms-platform-plan.md's "generic content model" decision log entry.
-- Content that differs only in a handful of type-specific fields shares one envelope — site_id,
-- display_order, active, status, version, timestamps — kept as real, typed, indexed columns
-- (exactly what every query here actually filters/sorts by); only the genuinely type-specific
-- fields go into `data`, so a new content type is a new content_type row + optional Java
-- ContentFields class, not a new table.
--
-- Types stored here today: the page sections (hero_slide, section_intro, about_*, industry_*, ...),
-- the service catalogue (service_category, service_page) and the homepage/detail content that used
-- to have a table each — service_card, office, testimonial and case_study. Posts, stats, logo
-- badges, footer navigation and banners still have their own tables.
--
-- This is deliberately NOT WordPress's wp_postmeta/EAV pattern: postmeta puts every field,
-- including the ones always filtered on, into an untyped key-value row per field. Here, `data`
-- holds one structured jsonb document per item, and the columns every query actually needs
-- (site_id, content_type, status, active, display_order) stay real indexed columns.
CREATE TABLE IF NOT EXISTS content_item
(
    id             bigserial PRIMARY KEY,
    site_id        bigint            NOT NULL REFERENCES site (id),
    content_type   character varying NOT NULL REFERENCES content_type (key),
    data           jsonb             NOT NULL DEFAULT '{}', -- validated server-side before write; see content_type above
    display_order  integer           NOT NULL DEFAULT 0,
    active         boolean           NOT NULL DEFAULT true,
    status         character varying NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PUBLISHED')),
    version        integer           NOT NULL DEFAULT 0,
    created_at     timestamp with time zone,
    created_by     jsonb,
    modified_at    timestamp with time zone,
    modified_by    jsonb
);
CREATE INDEX IF NOT EXISTS idx_content_item_site_type_order
    ON content_item (site_id, content_type, display_order);
CREATE INDEX IF NOT EXISTS idx_content_item_site_type_status_active_order
    ON content_item (site_id, content_type, status, active, display_order);
-- GIN index so a query into a specific field inside `data` (e.g. data->>'category') can still be
-- indexed if one is ever needed — the option WordPress's flat postmeta rows never had.
CREATE INDEX IF NOT EXISTS idx_content_item_data ON content_item USING GIN (data);
-- A case study's slug is the URL segment of its detail page: unique per site, never across sites.
-- Partial on the type, so the other content types keep their own (or no) slug rules.
CREATE UNIQUE INDEX IF NOT EXISTS uq_content_item_case_study_slug
    ON content_item (site_id, (data ->> 'slug'))
    WHERE content_type = 'case_study';

-- Token-based draft preview (see docs/cms-platform-plan.md). A row here is a narrow, single-purpose
-- credential: opaque (not a JWT — presenting it as an Authorization: Bearer anywhere under
-- /v1/admin/** just fails JWT parsing, it isn't accepted as that credential type at all),
-- site-scoped, short-lived. It authorizes nothing beyond the unauthenticated-at-the-Spring-Security-
-- layer /v1/preview/** endpoints, which validate it themselves (see PreviewTokenGuard) — never a
-- general bypass.
--
-- id is the real @Id (bigserial), not `token` — Spring Data R2DBC's save() only inserts when the
-- @Id is null before save; a manually-assigned String @Id would look "already existing" and attempt
-- an update instead. token is looked up via a derived query, not findById.
CREATE TABLE IF NOT EXISTS preview_token
(
    id         bigserial PRIMARY KEY,
    token      character varying        NOT NULL UNIQUE,
    site_id    bigint                   NOT NULL REFERENCES site (id),
    minted_at  timestamp with time zone NOT NULL,
    expires_at timestamp with time zone NOT NULL
);

-- Refresh rotates (delete old row, insert new) rather than extending expires_at in place, so a
-- captured-but-unused old token stops being usable the moment a real refresh happens. No separate
-- index needed for lookups by token — UNIQUE already creates one.
CREATE INDEX IF NOT EXISTS idx_preview_token_expires_at ON preview_token (expires_at);

-- Author-saved starting points for new posts. A template only pre-fills the editor (body, layout,
-- category, tags); nothing links a post back to the template it started from.
CREATE TABLE IF NOT EXISTS post_template
(
    id           bigserial PRIMARY KEY,
    site_id      bigint            NOT NULL REFERENCES site (id),
    name         character varying NOT NULL,
    description  text,
    layout       character varying NOT NULL DEFAULT 'default',
    category     character varying,
    tags         jsonb             NOT NULL DEFAULT '[]',
    body         text,
    created_at   timestamp with time zone,
    created_by   jsonb,
    modified_at  timestamp with time zone,
    modified_by  jsonb
);
CREATE INDEX IF NOT EXISTS idx_post_template_site ON post_template (site_id);
