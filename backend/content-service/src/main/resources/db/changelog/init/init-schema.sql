-- site is the tenant registry: every content table below is scoped to one row here.
-- Per cms-platform-plan.md's decision log, site == locale (one language per region),
-- content is independently authored per site (no shared "translations" jsonb column
-- anywhere in this schema), and routing resolves site_id from the request's subdomain.
CREATE TABLE IF NOT EXISTS site
(
    id          bigserial PRIMARY KEY,
    code        character varying(8)  NOT NULL UNIQUE, -- en, vi, ja, ko, de
    subdomain   character varying(128) NOT NULL UNIQUE,
    active      boolean               NOT NULL DEFAULT true,
    created_at  timestamp with time zone,
    created_by  jsonb,
    modified_at timestamp with time zone,
    modified_by jsonb
);

CREATE TABLE IF NOT EXISTS primary_nav_item
(
    id             bigserial PRIMARY KEY,
    site_id        bigint  NOT NULL REFERENCES site (id),
    has_dropdown   boolean NOT NULL DEFAULT false,
    label          character varying NOT NULL,
    display_order  integer NOT NULL DEFAULT 0,
    active         boolean NOT NULL DEFAULT true,
    created_at     timestamp with time zone,
    created_by     jsonb,
    modified_at    timestamp with time zone,
    modified_by    jsonb
);

CREATE TABLE IF NOT EXISTS nav_section
(
    id             bigserial PRIMARY KEY,
    site_id        bigint  NOT NULL REFERENCES site (id),
    anchor         character varying NOT NULL, -- DOM section id this nav item scrolls to
    label          character varying NOT NULL,
    display_order  integer NOT NULL DEFAULT 0,
    active         boolean NOT NULL DEFAULT true,
    created_at     timestamp with time zone,
    created_by     jsonb,
    modified_at    timestamp with time zone,
    modified_by    jsonb
);

CREATE TABLE IF NOT EXISTS stat
(
    id             bigserial PRIMARY KEY,
    site_id        bigint  NOT NULL REFERENCES site (id),
    value          character varying NOT NULL, -- e.g. "32+", not numeric — rendered verbatim
    label          character varying NOT NULL,
    note           character varying,
    display_order  integer NOT NULL DEFAULT 0,
    active         boolean NOT NULL DEFAULT true,
    created_at     timestamp with time zone,
    created_by     jsonb,
    modified_at    timestamp with time zone,
    modified_by    jsonb
);

CREATE TABLE IF NOT EXISTS service
(
    id             bigserial PRIMARY KEY,
    site_id        bigint  NOT NULL REFERENCES site (id),
    name           character varying NOT NULL,
    display_order  integer NOT NULL DEFAULT 0,
    active         boolean NOT NULL DEFAULT true,
    created_at     timestamp with time zone,
    created_by     jsonb,
    modified_at    timestamp with time zone,
    modified_by    jsonb
);

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
    display_order  integer           NOT NULL DEFAULT 0,
    active         boolean           NOT NULL DEFAULT true,
    created_at     timestamp with time zone,
    created_by     jsonb,
    modified_at    timestamp with time zone,
    modified_by    jsonb
);

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
    created_at     timestamp with time zone,
    created_by     jsonb,
    modified_at    timestamp with time zone,
    modified_by    jsonb
);

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
    created_at     timestamp with time zone,
    created_by     jsonb,
    modified_at    timestamp with time zone,
    modified_by    jsonb
);

CREATE TABLE IF NOT EXISTS logo_badge
(
    id             bigserial PRIMARY KEY,
    site_id        bigint  NOT NULL REFERENCES site (id),
    type           character varying NOT NULL CHECK (type IN ('AWARD', 'CERTIFICATION', 'PARTNER')),
    name           character varying NOT NULL,
    logo           character varying NOT NULL,
    display_order  integer NOT NULL DEFAULT 0,
    active         boolean NOT NULL DEFAULT true,
    created_at     timestamp with time zone,
    created_by     jsonb,
    modified_at    timestamp with time zone,
    modified_by    jsonb
);

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
    created_at     timestamp with time zone,
    created_by     jsonb,
    modified_at    timestamp with time zone,
    modified_by    jsonb
);

CREATE TABLE IF NOT EXISTS footer_nav_category
(
    id             bigserial PRIMARY KEY,
    site_id        bigint  NOT NULL REFERENCES site (id),
    label          character varying NOT NULL,
    links          jsonb   NOT NULL DEFAULT '[]', -- string[]
    display_order  integer NOT NULL DEFAULT 0,
    active         boolean NOT NULL DEFAULT true,
    created_at     timestamp with time zone,
    created_by     jsonb,
    modified_at    timestamp with time zone,
    modified_by    jsonb
);

-- New — not used by the current homepage build, added ready for other pages per the
-- decision log ("if Landing page doesn't have [one], other part will probably have it").
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
    created_at     timestamp with time zone,
    created_by     jsonb,
    modified_at    timestamp with time zone,
    modified_by    jsonb
);
