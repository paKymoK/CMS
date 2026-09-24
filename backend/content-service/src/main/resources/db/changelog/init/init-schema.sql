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

CREATE TABLE IF NOT EXISTS case_study
(
    id             bigserial PRIMARY KEY,
    site_id        bigint  NOT NULL REFERENCES site (id),
    date           character varying, -- display string (e.g. "Jun 2, 2026"), not a real date type
    image          character varying,
    title          character varying NOT NULL,
    category       character varying,
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

-- Generalized from the homepage's "insights" section per the decision log — "category"
-- lets this cover future post-like content (e.g. press releases) without a new table.
CREATE TABLE IF NOT EXISTS post
(
    id             bigserial PRIMARY KEY,
    site_id        bigint  NOT NULL REFERENCES site (id),
    category       character varying NOT NULL DEFAULT 'insight',
    image          character varying,
    title          character varying NOT NULL,
    excerpt        text,
    date           character varying,
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
