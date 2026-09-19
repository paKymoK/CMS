-- "What We Do" service cards render a gray placeholder on the site today since service_card
-- never had an image column — nullable, same fallback-to-placeholder pattern as
-- add-office-image.sql.
ALTER TABLE service_card ADD COLUMN IF NOT EXISTS image character varying;
