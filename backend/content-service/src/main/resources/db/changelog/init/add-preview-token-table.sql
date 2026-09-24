-- Token-based draft preview per cms-platform-plan.md's preview feature decision log. A row here
-- is a narrow, single-purpose credential: opaque (not a JWT — presenting it as an
-- Authorization: Bearer anywhere under /v1/admin/** just fails JWT parsing, it isn't accepted as
-- that credential type at all), site-scoped, short-lived. It authorizes nothing beyond the
-- unauthenticated-at-the-Spring-Security-layer /v1/preview/** endpoints, which validate it
-- themselves (see PreviewTokenGuard) — never treated as a general bypass.
--
-- id is the real @Id (bigserial), not `token` — Spring Data R2DBC's save() only inserts when the
-- @Id is null before save; a manually-assigned String @Id would look "already existing" and
-- attempt an update instead. token is looked up via a derived query, not findById.
CREATE TABLE IF NOT EXISTS preview_token
(
    id         bigserial PRIMARY KEY,
    token      character varying        NOT NULL UNIQUE,
    site_id    bigint                    NOT NULL REFERENCES site (id),
    minted_at  timestamp with time zone  NOT NULL,
    expires_at timestamp with time zone  NOT NULL
);

-- Refresh rotates (delete old row, insert new) rather than extending expires_at in place, so a
-- captured-but-unused old token stops being usable the moment a real refresh happens. No
-- separate index needed for lookups by token — UNIQUE already creates one.
CREATE INDEX IF NOT EXISTS idx_preview_token_expires_at ON preview_token (expires_at);
