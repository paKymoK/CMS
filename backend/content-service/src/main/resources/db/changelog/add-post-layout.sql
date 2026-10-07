-- Per-post page layout chosen by the author (default | focused | wide | landing); the website
-- switches its article wrapper on it. Validated in PostServiceImpl, not by a CHECK, so adding a
-- layout never needs a migration.
ALTER TABLE post ADD COLUMN IF NOT EXISTS layout character varying NOT NULL DEFAULT 'default';
