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

-- Registry of content types available through the generic content_item table (bottom of this
-- file) — per cms-platform-plan.md's "generic content model" decision. A new section/component
-- going forward is a row here (+ one small Java class implementing ContentFields for its strict
-- fields, or none if it stays on the fully-dynamic GenericContentFields fallback), not a new
-- table/entity/repository/controller/service like the 9 typed tables below required.
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

-- Every content table below carries the same envelope: `status` (DRAFT/PUBLISHED — the public
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

-- Named service_card (not "service") to avoid colliding with the Service/@Service naming
-- every other entity in this codebase uses for its business-logic layer.
CREATE TABLE IF NOT EXISTS service_card
(
    id             bigserial PRIMARY KEY,
    site_id        bigint  NOT NULL REFERENCES site (id),
    name           character varying NOT NULL,
    image          character varying, -- nullable: falls back to a design placeholder when absent
    display_order  integer NOT NULL DEFAULT 0,
    active         boolean NOT NULL DEFAULT true,
    status         character varying NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PUBLISHED')),
    version        integer NOT NULL DEFAULT 0,
    created_at     timestamp with time zone,
    created_by     jsonb,
    modified_at    timestamp with time zone,
    modified_by    jsonb
);
CREATE INDEX IF NOT EXISTS idx_service_card_site_order ON service_card (site_id, display_order);
CREATE INDEX IF NOT EXISTS idx_service_card_site_status_active_order ON service_card (site_id, status, active, display_order);

CREATE TABLE IF NOT EXISTS office
(
    id             bigserial PRIMARY KEY,
    site_id        bigint            NOT NULL REFERENCES site (id),
    lat            double precision  NOT NULL,
    lon            double precision  NOT NULL,
    flag_color     character varying NOT NULL, -- placeholder swatch until real flag assets land
    big            boolean           NOT NULL DEFAULT false, -- renders larger w/ white border (HQ)
    city           character varying NOT NULL,
    address        text              NOT NULL,
    image          character varying, -- nullable: globe hover card falls back to a placeholder
    display_order  integer           NOT NULL DEFAULT 0,
    active         boolean           NOT NULL DEFAULT true,
    status         character varying NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PUBLISHED')),
    version        integer           NOT NULL DEFAULT 0,
    created_at     timestamp with time zone,
    created_by     jsonb,
    modified_at    timestamp with time zone,
    modified_by    jsonb
);
CREATE INDEX IF NOT EXISTS idx_office_site_order ON office (site_id, display_order);
CREATE INDEX IF NOT EXISTS idx_office_site_status_active_order ON office (site_id, status, active, display_order);

CREATE TABLE IF NOT EXISTS testimonial
(
    id             bigserial PRIMARY KEY,
    site_id        bigint  NOT NULL REFERENCES site (id),
    name           character varying NOT NULL,
    company        character varying,
    photo          character varying,
    -- [{name, color}, {name, color}] — the two flanking client-logo placeholder cards.
    -- Never translated, so plain jsonb rather than anything locale-aware.
    flank_logos    jsonb,
    title          character varying,
    quote          text,
    display_order  integer NOT NULL DEFAULT 0,
    active         boolean NOT NULL DEFAULT true,
    status         character varying NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PUBLISHED')),
    version        integer NOT NULL DEFAULT 0,
    created_at     timestamp with time zone,
    created_by     jsonb,
    modified_at    timestamp with time zone,
    modified_by    jsonb
);
CREATE INDEX IF NOT EXISTS idx_testimonial_site_order ON testimonial (site_id, display_order);
CREATE INDEX IF NOT EXISTS idx_testimonial_site_status_active_order ON testimonial (site_id, status, active, display_order);

CREATE TABLE IF NOT EXISTS case_study
(
    id             bigserial PRIMARY KEY,
    site_id        bigint  NOT NULL REFERENCES site (id),
    slug           character varying, -- URL segment of the detail page; unique per site (index below)
    date           character varying, -- display string (e.g. "Jun 2, 2026"), not a real date type
    image          character varying,
    title          character varying NOT NULL,
    category       character varying,
    summary        text,
    body           text,              -- rich text (HTML)
    -- [{"value":"40%","label":"Faster deployment cycle"}] — headline figures on the detail page.
    results        jsonb   NOT NULL DEFAULT '[]',
    -- Nullable: not every case study has a quote. References the existing testimonial table rather
    -- than duplicating name/quote/photo here — don't fork data that already has a home.
    testimonial_id bigint  REFERENCES testimonial (id),
    display_order  integer NOT NULL DEFAULT 0,
    active         boolean NOT NULL DEFAULT true,
    status         character varying NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'PUBLISHED')),
    version        integer NOT NULL DEFAULT 0,
    created_at     timestamp with time zone,
    created_by     jsonb,
    modified_at    timestamp with time zone,
    modified_by    jsonb
);
CREATE INDEX IF NOT EXISTS idx_case_study_site_order ON case_study (site_id, display_order);
CREATE INDEX IF NOT EXISTS idx_case_study_site_status_active_order ON case_study (site_id, status, active, display_order);
-- Partial: rows without a slug never collide with each other, but once set a slug is unique within
-- its own site — never across sites, per the "content is not shared across sites" rule.
CREATE UNIQUE INDEX IF NOT EXISTS uq_case_study_site_slug ON case_study (site_id, slug) WHERE slug IS NOT NULL;

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
    body           text,              -- rich text (HTML), same convention as case_study.body
    author_name    character varying,
    author_role    character varying,
    author_bio     text,
    author_avatar  character varying,
    tags           jsonb   NOT NULL DEFAULT '[]', -- ["ai-governance","events"]
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
-- Partial: see uq_case_study_site_slug.
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
-- Every table above shares an identical envelope — site_id, display_order, active, status,
-- version, timestamps — and differs only in a handful of type-specific columns. This table keeps
-- that envelope as real, typed, indexed columns (exactly what every query here actually
-- filters/sorts by) and moves only the genuinely type-specific fields into `data`, so a new
-- content type is a new content_type row + optional Java ContentFields class, not a new table.
--
-- This is deliberately NOT WordPress's wp_postmeta/EAV pattern: postmeta puts every field,
-- including the ones always filtered on, into an untyped key-value row per field. Here, `data`
-- holds one structured jsonb document per item, and the columns every query actually needs
-- (site_id, content_type, status, active, display_order) stay real indexed columns.
--
-- The 9 tables above are NOT migrated into this — that's explicitly deferred/optional per the
-- plan's Phase 3. This table only serves new content types going forward.
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
