-- Rollout order per the decision log: en + vi first, ja/ko/de land later. All 5 are
-- registered from day one since site_id isn't hardcoded to any particular value —
-- rollout is a sequencing/staffing choice, not a schema one.
INSERT INTO site (code, subdomain)
VALUES ('en', 'en.cmcglobal.com'),
       ('vi', 'vn.cmcglobal.com'),
       ('ja', 'jp.cmcglobal.com'),
       ('ko', 'kr.cmcglobal.com'),
       ('de', 'de.cmcglobal.com')
ON CONFLICT (code) DO NOTHING;
