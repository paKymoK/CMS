-- pg_trgm: trigram similarity, behind the typo-tolerant search and "did you mean" (see SearchService).
-- Trusted since PostgreSQL 13, so the database owner can create it.
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- site is the tenant registry: every content table below is scoped to one row here.
-- site == locale (one language per region),
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

-- The one content table.
-- Every piece of site content (a post, a case study, a stat, an office, a hero slide, a service
-- page, ...) is a row here, identified by `content_type`. They all share one envelope — site_id,
-- display_order, active, status, version, timestamps — kept as real, typed, indexed columns
-- (exactly what every query filters/sorts by); only the genuinely type-specific fields go into
-- `data`, so a new content type is a new Java ContentFields class (@ContentTypeKey), not a new table.
-- There is deliberately no content_type registry table: the Java classes are the registry.
--
-- Envelope semantics: `status` (DRAFT/SCHEDULED/PUBLISHED/ARCHIVED — the public API only ever returns PUBLISHED+active
-- rows; admin sees everything) and `version` (Spring Data R2DBC's @Version optimistic-locking
-- column — a concurrent-edit guard, not a history table, not full audit trails). The two composite indexes below match the two query shapes every repository
-- issues: "everything of this type for this site" (admin) and "published of this type" (public site).
-- Composite since every query filters by site_id first.
--
-- This is deliberately NOT WordPress's wp_postmeta/EAV pattern: postmeta puts every field,
-- including the ones always filtered on, into an untyped key-value row per field. Here, `data`
-- holds one structured jsonb document per item, and the columns every query actually needs
-- (site_id, content_type, status, active, display_order) stay real indexed columns.
CREATE TABLE IF NOT EXISTS content_item
(
    id             bigserial PRIMARY KEY,
    site_id        bigint            NOT NULL REFERENCES site (id),
    content_type   character varying NOT NULL, -- must match a @ContentTypeKey class; checked in ContentItemMapper
    data           jsonb             NOT NULL DEFAULT '{}', -- validated server-side before write; see ContentItemMapper
    display_order  integer           NOT NULL DEFAULT 0,
    active         boolean           NOT NULL DEFAULT true,
    status         character varying NOT NULL DEFAULT 'DRAFT'
        CHECK (status IN ('DRAFT', 'SCHEDULED', 'PUBLISHED', 'ARCHIVED')),
    version        integer           NOT NULL DEFAULT 0,
    -- When a SCHEDULED item goes live, and when a live one is taken off the site (archived). Used by
    -- posts only; publish_scheduled_content() below, run every minute by pg_cron, does the flipping.
    publish_at     timestamp with time zone,
    unpublish_at   timestamp with time zone,
    -- Search text, written by the application whenever `data` is (ContentItemSearchCallback; rows
    -- inserted by SQL are filled in at startup by SearchIndexBackfill). Plain text with accents
    -- folded, split by weight: the title/name, then category/tags/summary, then everything else.
    -- search_vec is derived from them by Postgres itself, so it can never drift.
    search_title   text,
    search_meta    text,
    search_body    text,
    search_vec     tsvector GENERATED ALWAYS AS (
                       setweight(to_tsvector('english', coalesce(search_title, '')), 'A') ||
                       setweight(to_tsvector('english', coalesce(search_meta, '')), 'B') ||
                       setweight(to_tsvector('english', coalesce(search_body, '')), 'C')
                   ) STORED,
    created_at     timestamp with time zone,
    created_by     jsonb,
    modified_at    timestamp with time zone,
    modified_by    jsonb,
    CONSTRAINT ck_content_item_scheduled_has_time CHECK (status <> 'SCHEDULED' OR publish_at IS NOT NULL),
    CONSTRAINT ck_content_item_schedule_order CHECK (publish_at IS NULL OR unpublish_at IS NULL OR unpublish_at > publish_at)
);
CREATE INDEX IF NOT EXISTS idx_content_item_site_type_order
    ON content_item (site_id, content_type, display_order);
CREATE INDEX IF NOT EXISTS idx_content_item_site_type_status_active_order
    ON content_item (site_id, content_type, status, active, display_order);
-- What the once-a-minute job looks for: only the few rows that are waiting for a time, so the check
-- costs the same however big the table is.
CREATE INDEX IF NOT EXISTS idx_content_item_due_publish
    ON content_item (publish_at) WHERE status = 'SCHEDULED';
CREATE INDEX IF NOT EXISTS idx_content_item_due_unpublish
    ON content_item (unpublish_at) WHERE status = 'PUBLISHED' AND unpublish_at IS NOT NULL;
-- A site has exactly one item of each singleton type (the About page). ContentItemServiceImpl rejects a
-- second one with a plain message; this index is the backstop against two concurrent creates.
CREATE UNIQUE INDEX IF NOT EXISTS uq_content_item_singleton
    ON content_item (site_id, content_type) WHERE content_type IN ('about_page');
-- Deliberately no GIN index on `data`: a page is one large row, so such an index grows with every key
-- and value in it and slows every write, while nothing queries by containment (lookups use
-- `data ->> 'slug'`, which uq_content_item_slug serves, and a GIN index would not). If a containment
-- query ever appears, add a `jsonb_path_ops` index for it then.
-- Full-text search over search_vec, and trigram matching on the title for typos and half-typed words.
CREATE INDEX IF NOT EXISTS idx_content_item_search ON content_item USING GIN (search_vec);
CREATE INDEX IF NOT EXISTS idx_content_item_search_title_trgm
    ON content_item USING GIN (search_title gin_trgm_ops);
-- The words of every item's title/category/tags/summary, with how many items use each: the
-- vocabulary the search corrects a misspelt word against and suggests completions from. A plain
-- view, not a table — always current, nothing to maintain on write or delete — and cheap at this
-- size; if the content ever grows into the hundreds of thousands of words, make it materialized
-- (and index `word` with gin_trgm_ops). `visible` marks what the public site may see.
CREATE OR REPLACE VIEW search_term AS
SELECT ci.site_id,
       w.word,
       (ci.status = 'PUBLISHED' AND ci.active) AS visible,
       count(DISTINCT ci.id)                   AS doc_count
FROM content_item ci
         CROSS JOIN LATERAL regexp_split_to_table(
        lower(coalesce(ci.search_title, '') || ' ' || coalesce(ci.search_meta, '')), '[^a-z0-9]+') AS w(word)
WHERE length(w.word) >= 3
  AND w.word !~ '^[0-9]+$'
GROUP BY ci.site_id, w.word, (ci.status = 'PUBLISHED' AND ci.active);
-- A post's or case study's slug is the URL segment of its detail page: unique per site and type,
-- never across sites. Partial on the types with a detail page, so the others keep their own (or
-- no) slug rules.
CREATE UNIQUE INDEX IF NOT EXISTS uq_content_item_slug
    ON content_item (site_id, content_type, (data ->> 'slug'))
    WHERE content_type IN ('post', 'case_study');

-- Audit trail of content changes: one row per create, update, publish, unpublish, delete or
-- restore of a content_item, written by the content_item_audit trigger below — so it records every
-- write, from any service or from plain SQL, in the same statement as the change itself. Append-only
-- by convention (nothing updates or deletes a row) and kept indefinitely.
--
-- item_id has no foreign key on purpose: the history of a deleted item must outlive it (and a
-- DELETE row's `before_data` is what restores it). `title` is copied from the item's data so the
-- activity feed stays readable after the item is gone. before_data/after_data are whole snapshots of
-- the item ({contentType, data, status, active, displayOrder}), null on the side that doesn't exist
-- (no before for a create, no after for a delete); `changed` lists the top-level fields that differ,
-- so the feed can say what changed without loading the snapshots.
CREATE TABLE IF NOT EXISTS content_audit
(
    id           bigserial PRIMARY KEY,
    site_id      bigint                   NOT NULL REFERENCES site (id),
    item_id      bigint                   NOT NULL,
    content_type character varying        NOT NULL,
    action       character varying        NOT NULL
        CHECK (action IN ('CREATE', 'UPDATE', 'SCHEDULE', 'PUBLISH', 'UNPUBLISH', 'ARCHIVE', 'DELETE', 'RESTORE')),
    title        text,
    actor        jsonb,                   -- the signed-in user, as stored in created_by/modified_by
    actor_name   character varying,       -- the user's display name, for filtering
    occurred_at  timestamp with time zone NOT NULL DEFAULT now(),
    changed      jsonb                    NOT NULL DEFAULT '[]',
    before_data  jsonb,
    after_data   jsonb
);
-- The activity feed (newest first, per site) and one item's history.
CREATE INDEX IF NOT EXISTS idx_content_audit_site_at ON content_audit (site_id, occurred_at DESC, id DESC);
CREATE INDEX IF NOT EXISTS idx_content_audit_item ON content_audit (item_id, occurred_at DESC, id DESC);

-- ---- Recording the trail --------------------------------------------------------------------------
-- The trigger works out WHO from the row itself: Spring Data auditing stamps modified_by/modified_at
-- on every insert and update the application makes, so NEW.modified_by is the signed-in user. Two
-- cases it cannot read that way, and what it does instead:
--   * A write that did not come through the application (plain SQL: modified_by/modified_at did not
--     change, or an insert with no modified_by) is recorded as "Direct database change" by the
--     database login that made it.
--   * A delete: OLD.modified_by is only the last editor. The application therefore re-saves the row
--     as the current user just before deleting it, in the same transaction; the trigger knows that
--     happened when the row version it is looking at was written by the current transaction (xmin).
--     A delete without that step is recorded as a direct database change.
-- The application can also name the action: setting cms.audit_action to RESTORE in the transaction
-- (set_config(..., true)) records the write as a RESTORE instead of a CREATE/UPDATE.
-- Saves that change nothing an editor can see (re-saving, the version counter, the search text) are
-- not recorded. The seed script switches the trigger off while it loads (see init-data.sql).

-- A value with nulls and empty objects removed, all the way down, so "field absent", "field null" and
-- "empty object" compare equal: saving an old item through validation adds null fields, which is not
-- an edit anyone made. Null when nothing is left.
CREATE OR REPLACE FUNCTION audit_norm(v jsonb) RETURNS jsonb AS
$$
DECLARE
    pruned jsonb;
BEGIN
    IF v IS NULL OR v = 'null'::jsonb THEN
        RETURN NULL;
    END IF;
    IF jsonb_typeof(v) = 'object' THEN
        SELECT jsonb_object_agg(e.key, e.val)
        INTO pruned
        FROM (SELECT key, audit_norm(value) AS val FROM jsonb_each(v)) e
        WHERE e.val IS NOT NULL;
        RETURN pruned; -- null when the object had nothing left
    END IF;
    RETURN v;
END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- What the trail records of an item: everything an editor can change, nothing derived or bookkeeping.
CREATE OR REPLACE FUNCTION audit_snapshot(ci content_item) RETURNS jsonb AS
$$
SELECT jsonb_build_object('contentType', ci.content_type, 'data', ci.data, 'status', ci.status,
                          'active', ci.active, 'displayOrder', ci.display_order)
           -- the schedule times (UTC) only when set, so items that never had one keep the old shape
           || CASE WHEN ci.publish_at IS NULL THEN '{}'::jsonb
                   ELSE jsonb_build_object('publishAt', to_char(ci.publish_at AT TIME ZONE 'UTC', 'YYYY-MM-DD"T"HH24:MI:SS"Z"')) END
           || CASE WHEN ci.unpublish_at IS NULL THEN '{}'::jsonb
                   ELSE jsonb_build_object('unpublishAt', to_char(ci.unpublish_at AT TIME ZONE 'UTC', 'YYYY-MM-DD"T"HH24:MI:SS"Z"')) END
$$ LANGUAGE sql IMMUTABLE;

-- The readable title of an item's data (rich text reduced to text), '' when it has none.
CREATE OR REPLACE FUNCTION audit_title(item_data jsonb) RETURNS text AS
$$
SELECT coalesce((SELECT btrim(regexp_replace(regexp_replace(t.v, '<[^>]*>', ' ', 'g'), '\s+', ' ', 'g'))
                 FROM unnest(ARRAY [item_data ->> 'title', item_data ->> 'name', item_data ->> 'navLabel',
                     item_data ->> 'heroTitle', item_data ->> 'heading', item_data ->> 'label',
                     item_data ->> 'question']) WITH ORDINALITY AS t(v, n)
                 WHERE btrim(t.v) <> ''
                 ORDER BY t.n
                 LIMIT 1), '')
$$ LANGUAGE sql IMMUTABLE;

-- The top-level fields that differ between two snapshots: the item's own data fields, then any of
-- contentType/status/active/displayOrder/publishAt/unpublishAt. Every field for a create or a delete (one side is null).
CREATE OR REPLACE FUNCTION audit_changed(before_snap jsonb, after_snap jsonb) RETURNS jsonb AS
$$
SELECT coalesce(jsonb_agg(c.name ORDER BY c.grp, c.name), '[]'::jsonb)
FROM (SELECT k.key AS name, 0 AS grp
      FROM (SELECT jsonb_object_keys(coalesce(before_snap -> 'data', '{}'::jsonb)) AS key
            UNION
            SELECT jsonb_object_keys(coalesce(after_snap -> 'data', '{}'::jsonb))) k
      WHERE before_snap IS NULL
         OR after_snap IS NULL
         OR audit_norm(before_snap #> ARRAY ['data', k.key]) IS DISTINCT FROM
            audit_norm(after_snap #> ARRAY ['data', k.key])
      UNION ALL
      SELECT e.name, 1
      FROM unnest(ARRAY ['contentType', 'status', 'active', 'displayOrder', 'publishAt', 'unpublishAt']) AS e(name)
      -- the schedule times are in a snapshot only when set, so they are listed only when one side has them
      WHERE (e.name NOT IN ('publishAt', 'unpublishAt')
          OR coalesce(jsonb_exists(before_snap, e.name), false)
          OR coalesce(jsonb_exists(after_snap, e.name), false))
        AND (before_snap IS NULL
          OR after_snap IS NULL
          OR before_snap -> e.name IS DISTINCT FROM after_snap -> e.name)) c
$$ LANGUAGE sql IMMUTABLE;

CREATE OR REPLACE FUNCTION content_audit_record() RETURNS trigger AS
$$
DECLARE
    item   content_item;
    b      jsonb;
    a      jsonb;
    act    text;
    who    jsonb;
    direct boolean;
BEGIN
    IF TG_OP = 'INSERT' THEN
        item := NEW;
        a := audit_snapshot(NEW);
        act := 'CREATE';
        direct := NEW.modified_by IS NULL;
    ELSIF TG_OP = 'UPDATE' THEN
        item := NEW;
        b := audit_snapshot(OLD);
        a := audit_snapshot(NEW);
        IF audit_norm(b -> 'data') IS NOT DISTINCT FROM audit_norm(a -> 'data')
            AND (b - 'data') = (a - 'data') THEN
            RETURN NULL; -- nothing an editor can see changed
        END IF;
        act := CASE
                   WHEN NEW.status = OLD.status THEN 'UPDATE'
                   WHEN NEW.status = 'PUBLISHED' THEN 'PUBLISH'
                   WHEN NEW.status = 'SCHEDULED' THEN 'SCHEDULE'
                   WHEN NEW.status = 'ARCHIVED' THEN 'ARCHIVE'
                   WHEN OLD.status = 'PUBLISHED' THEN 'UNPUBLISH'
                   ELSE 'UPDATE' END; -- e.g. ARCHIVED or SCHEDULED back to DRAFT: the status change is listed as a changed field
        direct := NEW.modified_at IS NOT DISTINCT FROM OLD.modified_at
            AND NEW.modified_by IS NOT DISTINCT FROM OLD.modified_by;
    ELSE
        item := OLD;
        b := audit_snapshot(OLD);
        act := 'DELETE';
        -- OLD was written by this very transaction only if the application re-saved it just before.
        direct := OLD.xmin::text <> (txid_current() % 4294967296)::text;
    END IF;

    IF TG_OP <> 'DELETE' AND nullif(current_setting('cms.audit_action', true), '') = 'RESTORE' THEN
        act := 'RESTORE';
    END IF;

    who := CASE
               WHEN direct THEN jsonb_build_object('sub', 'db:' || session_user,
                                                   'name', 'Direct database change')
               WHEN TG_OP = 'DELETE' THEN OLD.modified_by
               ELSE NEW.modified_by END;

    INSERT INTO content_audit (site_id, item_id, content_type, action, title, actor, actor_name,
                               occurred_at, changed, before_data, after_data)
    VALUES (item.site_id, item.id, item.content_type, act, audit_title(coalesce(a, b) -> 'data'), who,
            coalesce(who ->> 'name', who ->> 'sub'), clock_timestamp(), audit_changed(b, a), b, a);
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS content_item_audit ON content_item;
CREATE TRIGGER content_item_audit
    AFTER INSERT OR UPDATE OR DELETE
    ON content_item
    FOR EACH ROW
EXECUTE FUNCTION content_audit_record();

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

-- ---- Scheduled publishing -------------------------------------------------------------------------
-- Moves what is due: SCHEDULED items whose publish_at has passed become PUBLISHED, and PUBLISHED items
-- whose unpublish_at has passed become ARCHIVED (their unpublish_at is cleared, so publishing the item
-- again by hand is not undone at once). Returns how many items it moved.
--
-- One statement per direction, so it is atomic, and SKIP LOCKED makes a second runner (a second
-- database, a manual call) skip rows the first one holds instead of waiting or doing them twice. It
-- is also catch-up by construction: it takes everything overdue, so a missed run, or downtime, only
-- delays publishing. The write is stamped as the "System" user and the version is bumped, so the audit
-- trail credits System and an editor with the item open gets the usual out-of-date-form warning.
CREATE OR REPLACE FUNCTION publish_scheduled_content() RETURNS integer AS
$$
DECLARE
    moved integer := 0;
    n     integer;
    sys   CONSTANT jsonb := jsonb_build_object('sub', 'system', 'name', 'System');
BEGIN
    UPDATE content_item ci
    SET status      = 'PUBLISHED',
        version     = ci.version + 1,
        modified_at = now(),
        modified_by = sys
    FROM (SELECT id
          FROM content_item
          WHERE status = 'SCHEDULED'
            AND publish_at <= now()
          ORDER BY publish_at
          FOR UPDATE SKIP LOCKED) due
    WHERE ci.id = due.id;
    GET DIAGNOSTICS n = ROW_COUNT;
    moved := moved + n;

    UPDATE content_item ci
    SET status       = 'ARCHIVED',
        unpublish_at = NULL,
        version      = ci.version + 1,
        modified_at  = now(),
        modified_by  = sys
    FROM (SELECT id
          FROM content_item
          WHERE status = 'PUBLISHED'
            AND unpublish_at <= now()
          ORDER BY unpublish_at
          FOR UPDATE SKIP LOCKED) due
    WHERE ci.id = due.id;
    GET DIAGNOSTICS n = ROW_COUNT;
    RETURN moved + n;
END;
$$ LANGUAGE plpgsql;

-- The job: every minute, in the database named by cron.database_name (see the postgres image's
-- postgresql.conf). It is created only where pg_cron is there, so a database without it (a test
-- database, a managed Postgres without the extension) still loads; scheduled items then wait until
-- publish_scheduled_content() is run some other way.
DO
$$
BEGIN
    IF current_database() = current_setting('cron.database_name', true)
        AND EXISTS (SELECT 1 FROM pg_available_extensions WHERE name = 'pg_cron') THEN
        CREATE EXTENSION IF NOT EXISTS pg_cron;
        PERFORM cron.schedule('publish-scheduled-content', '* * * * *', 'SELECT publish_scheduled_content()');
    ELSE
        RAISE WARNING 'pg_cron is not available in database %: scheduled content will not be published automatically', current_database();
    END IF;
END
$$;
