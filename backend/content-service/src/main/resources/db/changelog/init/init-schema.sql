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
-- service. Every other kind of content lives there too — see content_item.
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

-- The one content table, per cms-platform-plan.md's "generic content model" decision log entry.
-- Every piece of site content (a post, a case study, a stat, an office, a hero slide, a service
-- page, ...) is a row here, identified by `content_type`. They all share one envelope — site_id,
-- display_order, active, status, version, timestamps — kept as real, typed, indexed columns
-- (exactly what every query filters/sorts by); only the genuinely type-specific fields go into
-- `data`, so a new content type is a new content_type row + optional Java ContentFields class, not
-- a new table.
--
-- Envelope semantics: `status` (DRAFT/PUBLISHED — the public API only ever returns PUBLISHED+active
-- rows; admin sees everything) and `version` (Spring Data R2DBC's @Version optimistic-locking
-- column — a concurrent-edit guard, not a history table; "simple versioning" per the plan, not
-- full audit trails). The two composite indexes below match the two query shapes every repository
-- issues: "everything of this type for this site" (admin) and "published of this type" (public /
-- preview). Composite since every query filters by site_id first.
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
-- A post's or case study's slug is the URL segment of its detail page: unique per site and type,
-- never across sites. Partial on the types with a detail page, so the others keep their own (or
-- no) slug rules.
CREATE UNIQUE INDEX IF NOT EXISTS uq_content_item_slug
    ON content_item (site_id, content_type, (data ->> 'slug'))
    WHERE content_type IN ('post', 'case_study');

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
