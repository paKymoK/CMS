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
