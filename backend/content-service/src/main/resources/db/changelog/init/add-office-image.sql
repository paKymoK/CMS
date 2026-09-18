-- Office photo shown in the public homepage's globe hover card — added after the initial
-- schema since it wasn't part of the original handoff scope (offices only had geo/address
-- fields). Nullable: falls back to the frontend's design placeholder when absent.
ALTER TABLE office ADD COLUMN IF NOT EXISTS image character varying;
