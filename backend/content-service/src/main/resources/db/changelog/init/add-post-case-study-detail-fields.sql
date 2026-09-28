-- Post/CaseStudy started as homepage-teaser-card shapes only (title/image/date/category). These
-- columns add what a real detail page needs: a stable URL slug, a rich-text body, and the fields
-- Post Detail / Case Study Detail / Insights (design/project/*.dc.html) require. Additive-only —
-- existing rows get NULL slug/body (unique index below excludes them, not blocking existing data)
-- and empty tags/results so old admin rows keep working until an editor fills them in.

ALTER TABLE post
    ADD COLUMN IF NOT EXISTS slug          character varying,
    ADD COLUMN IF NOT EXISTS body          text, -- rich text (HTML), same convention as caseStudy.body
    ADD COLUMN IF NOT EXISTS author_name   character varying,
    ADD COLUMN IF NOT EXISTS author_role   character varying,
    ADD COLUMN IF NOT EXISTS author_bio    text,
    ADD COLUMN IF NOT EXISTS author_avatar character varying,
    ADD COLUMN IF NOT EXISTS tags          jsonb   NOT NULL DEFAULT '[]'::jsonb, -- ["ai-governance","events"]
    ADD COLUMN IF NOT EXISTS featured      boolean NOT NULL DEFAULT false;

-- Partial (WHERE slug IS NOT NULL) so existing NULL-slug rows never collide with each other, but
-- once a post is given a real slug it must be unique within its own site — never across sites,
-- per the "content is not shared across sites" rule.
CREATE UNIQUE INDEX IF NOT EXISTS uq_post_site_slug ON post (site_id, slug) WHERE slug IS NOT NULL;

ALTER TABLE case_study
    ADD COLUMN IF NOT EXISTS slug            character varying,
    ADD COLUMN IF NOT EXISTS body            text,
    ADD COLUMN IF NOT EXISTS summary         text,
    ADD COLUMN IF NOT EXISTS results         jsonb NOT NULL DEFAULT '[]'::jsonb, -- [{"value":"40%","label":"Faster deployment cycle"}]
    -- Nullable: not every case study has a quote. References the existing testimonial table
    -- rather than duplicating name/quote/photo here — same reason FooterNavCategory reuses jsonb
    -- instead of a new table for its links: don't fork data that already has a home.
    ADD COLUMN IF NOT EXISTS testimonial_id  bigint REFERENCES testimonial (id);

CREATE UNIQUE INDEX IF NOT EXISTS uq_case_study_site_slug ON case_study (site_id, slug) WHERE slug IS NOT NULL;
