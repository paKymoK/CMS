-- auth-service initial data.
--
-- DEV BOOTSTRAP ONLY: the 'admin' account below uses the well-known password 'admin' so a fresh
-- local environment can sign in to admin-app. The password is stored bcrypt-encoded, never
-- plaintext, but it is still a published credential — change or remove this account before any
-- environment other than local development, and never run this seed against production.
--
-- Not seeded here, on purpose: the 'cms-admin' OAuth client and admin's ADMIN role on sites
-- 'en' and 'vi' are bootstrapped at startup by AuthorizationServerConfig (idempotent upsert, with
-- redirect URIs taken from configuration), so they track config rather than a frozen SQL row.

INSERT INTO users (username, password, enabled)
VALUES ('admin', '{bcrypt}$2a$10$XfRuODiLRrrJ23jzAlDnueLFN8YfvEkVmv0uW9wTYR3Tt.Siv.P3a', true);

INSERT INTO authorities (username, authority)
VALUES ('admin', 'ROLE_ADMIN'),
       ('admin', 'ROLE_USER');

INSERT INTO userinfo (sub, name, email)
VALUES ('admin', 'Admin', 'admin@gmail.com');
