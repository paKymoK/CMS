# CMS Platform — 5-region CMC Global site, forked from the Workflow platform

> Status: **draft plan, not yet approved for implementation.** Discussed and decided over several rounds with the user; not yet reviewed with their team.
>
> Supersedes `cms-service-plan.md` in this folder (single-site version). This plan reuses that file's content research (exact fields, what's hardcoded vs. data-driven in the current homepage, the media-service upload mechanics) but re-homes the schema around per-region ownership instead of one shared table with translations.

## What this is

One Next.js codebase (today's `D:\Code\landing-page`) serving **5 country/region variants** of the CMC Global homepage, each on its own subdomain (`vn.cmcglobal.com`, `jp.cmcglobal.com`, etc.), each maintained independently by that region's own marketing team. Backend is a new platform, built by **forking three services out of the existing Workflow monorepo** (`D:\Code\Workflow`) — `auth-service`, `media-service`, `chat-service` — plus one net-new `content-service`. `workflow-service` and `employee-service` stay untouched; different business domain, out of scope.

## Decision log

Each of these was an open question, resolved in conversation before writing this plan. Recorded with the reasoning so a future session doesn't re-litigate them.

| Decision | Chosen | Why |
|---|---|---|
| Site vs. locale | **Same axis.** One language per region/site. | The regions map 1:1 onto languages the business already operates in (en, vi, ja, ko, de) — there's no case of one region needing two languages. |
| Content ownership | **Independent per site**, not shared+translated | Each region has its own marketing team authoring its own posts/banners/content — not one team translating one canonical set. |
| Content-model shape | `site_id`-scoped plain tables, **no `translations` jsonb** | Direct consequence of the two rows above: since site = locale and content isn't shared across sites, there's nothing to translate-and-fall-back — each row is already in the one language its site uses. This simplifies the original single-site plan's `JsonNode translations` + locale-fallback resolver design out of existence for this platform. |
| Chatbot knowledge isolation | **Separate Qdrant collection per site**, not one collection + `site_id` filter | Structurally impossible to leak cross-region, and there's no shared-language benefit to pooling anyway — a Vietnamese answer is useless to a German-site visitor regardless of filtering correctness. |
| Chatbot abuse protection | **IP-based rate limiting, anonymous requests only** | Public/unauthenticated traffic is the actual risk (scripted abuse, Claude API cost); authenticated/internal users (testing, internal tools) should never be throttled. |
| Site routing | **Subdomain per region** (`vn.`, `jp.`, `kr.`, `de.`, `en.` or apex) | Matches "5 independent sites" more than a path prefix would; user will maintain the CORS allowlist manually (small, fixed list of 5 known origins, not dynamic). |
| Reused-service strategy | **Fork**, accept drift, no formal upstream-sync process | `auth-service`/`media-service`/`chat-service`'s CMS versions serve a genuinely different purpose (public multi-tenant marketing platform) than their Workflow-platform originals (internal ticketing/HR tooling) — trying to keep them convergent would fight that difference rather than serve it. |
| Phase 1 authz retrofit detail | **Deferred** to when Phase 1 actually starts | User is separately running their own security review pass on the pre-fork code; the detailed endpoint-by-endpoint checklist can wait until that's done and Phase 1 is imminent. |
| Rollout order | **en + vi first**, ja/ko/de later | Same schema/pipeline handles all 5 from day one (`site_id` isn't hardcoded to 2 values anywhere) — this is a rollout sequencing choice, not a schema constraint. |
| `Post`/`Banner` primitives | `Post` = generalized `insights` (adds a `category` column so future post-like content doesn't need new tables). `Banner` = **new**, not used by the current homepage, built as a ready capability since other pages will need it. | Keeps the schema aligned with what's actually on the page today while leaving room for content types the current homepage doesn't have yet. |

## Repo layout

```
cms-platform/
├── CLAUDE.md
├── backend/
│   ├── auth-service/          # forked from Workflow platform, extended for multi-site access
│   ├── media-service/         # forked from Workflow platform, extended for site-scoped storage
│   ├── chat-service/          # forked from Workflow platform, generation swapped to Claude, per-site Qdrant collections
│   └── content-service/       # net-new — the actual "CMS" data + API
├── admin-app/                 # Vite + React SPA (editor UI), separate deploy from the Next.js sites
├── docs/
│   └── api-contract.md
└── infra/                     # docker-compose for local dev
```

Repo location: `D:\Code\cms-platform`, sibling to `D:\Code\Workflow` and `D:\Code\landing-page`. Scaffolded 2026-09-17, local only — no git remote yet, add one when ready to push. `core-v1` and `infrastructure` were added to `backend/` beyond what this diagram shows: `auth-service`/`media-service`/`chat-service` all declare `project(':core-v1')` and `project(':infrastructure')` as compile dependencies, so they don't build standalone without them. This is a discovered technical necessity, not a scope change.

## Content model (`content-service`)

Every table below is scoped by `site_id` (a short code — `en`/`vi`/`ja`/`ko`/`de` — or an FK to a small `site` lookup table; recommend the lookup table since Phase 1's user↔site access model needs a `sites` registry to join against anyway, so it isn't a table built only for this). All extend the same base pattern as the single-site plan: bigserial `id` + audit columns, `display_order integer DEFAULT 0`, `active boolean DEFAULT true`. **No `translations` jsonb column anywhere** — see decision log. Plain text columns hold whatever language that site's editors write in.

| Table | Columns (beyond `site_id`/`display_order`/`active`) | Notes |
|---|---|---|
| `primary_nav_item` | `has_dropdown`, `label` | |
| `nav_section` | `anchor` (slug, matches the DOM section id), `label` | |
| `stat` | `value`, `label`, `note` | |
| `service` | `name` | was `solution` in the old model |
| `office` | `lat`, `lon`, `flag_color`, `big` (bool), `city`, `address` | drives the D3 globe |
| `case_study` | `date`, `image`, `title`, `category` | |
| `post` | `category`, `image`, `title`, `excerpt?`, `date?` | generalized from `insights` per decision log — `category` lets "insight" be one value among others later, no new table needed |
| `logo_badge` | `type` (AWARD/CERTIFICATION/PARTNER), `name`, `logo` | one table, filtered by `?type=` |
| `testimonial` | `name`, `company`, `photo`, `flank_logos` (jsonb `[{name,color}]`, not translatable, just a placeholder pair), `title`, `quote` | |
| `footer_nav_category` | `label`, `links` (jsonb `string[]`) | |
| `banner` | `title`, `body?`, `image?`, `cta_label?`, `cta_url?`, `active_from?`, `active_until?` | **new**, unused by the current homepage — built ready for future pages |

The `JsonNode`-via-Jackson-R2DBC-converter pattern from `workflow-service` (`JsonNodeReader`/`JsonNodeWriter`) is still worth copying in — `flank_logos` and `links` still need it — but it's now just "store a jsonb array," not the locale-fallback multiplexing job it did in the single-site plan. No `TranslationResolver` needed.

## Public + admin REST contract

- **Public**: `GET /v1/home` — the incoming request's subdomain resolves `site_id` (middleware/interceptor, not a query param — the whole point of subdomain routing is the site is already known from the `Host` header). Returns `ResultMessage<HomeContentResponse>` scoped to that one site. One aggregated call per site, same `Mono.zip` array-combinator approach as the single-site plan (11 sources now).
- **Admin**: `/v1/admin/{resource}` per entity, **every** request additionally scoped/validated against the authenticated user's site access (Phase 1's user↔site model) — an editor for the Vietnam site must not be able to list, read, or write another site's rows even with a guessed id. This is the multi-tenant discipline the CLAUDE.md below makes non-negotiable.
- **Auth**: same `CmsAdminGuard`-style manual check pattern as the single-site plan, extended to check site access, not just role.

## Phases

Adapted from the reuse-based plan the user got from another session, with the 10 decisions above folded in.

### Phase 0 — Foundations
- Decide the new repo's filesystem location; scaffold `cms-platform/` per the layout above.
- Fork `auth-service`, `media-service`, `chat-service` from `D:\Code\Workflow` into `backend/`.
- Scaffold `content-service` fresh (same stack: Java 21, Spring Boot 3.5.x, WebFlux/R2DBC, Liquibase — matching the rest of the platform, not the "17+"/"Flyway-or-Liquibase, check what's there" hedge from the source plan, since we already know Workflow's actual stack).
- `docker-compose.yml` covering all four services + Postgres, MinIO, Qdrant.
- Confirm all three forked services still build and their existing tests still pass, unmodified, before any new work starts.
- `.env.example` per service, no secrets committed.

**Acceptance**: all four services run via `docker-compose up`; each forked service's pre-existing tests still pass unmodified.

### Phase 1 — Auth: multi-site on the forked `auth-service`
- Full security audit of the forked code before changing it (bcrypt + OAuth2 Authorization Server already there and reviewed-good; rate limiting and injection patterns were only lightly checked before, not fully).
- Add a `sites` table and a user↔site access model (doesn't exist today — Workflow platform never needed it).
- Extend authorization so every protected endpoint validates site access, not just "is logged in." **Detailed endpoint-by-endpoint checklist deferred** — user is running their own security pass on the pre-fork code first; write the checklist when this phase actually starts.
- Login rate limiting keyed to server-observed IP.
- Since this is a fork, not the shared running Workflow `auth-service`: no regression risk to Workflow's production ticketing/HR system from this phase — that risk is what forking was chosen to eliminate. Still run the forked service's own pre-existing tests to confirm the fork itself didn't break anything in transit.

**Acceptance**: forked service's pre-existing tests still pass; a new site-access check correctly blocks a user scoped to site A from acting on site B.

### Phase 2 — `content-service` (net-new)
- All 11 entities from the content model above, every one carrying `site_id` from its first migration.
- CRUD endpoints, all queries scoped by `site_id` via a shared base repository pattern — no one-off filters.
- Draft/publish workflow + simple versioning.
- Public content API: `GET /v1/home`, site resolved from the `Host` header, unauthenticated, read-only.
- All queries parameterized.

**Acceptance**: publish content for the `vn` site, confirm it's retrievable via the public API on `vn.cmcglobal.com` and absent on `jp.cmcglobal.com`.

### Phase 3 — Media: site-scoping on the forked `media-service`
- Security audit of the forked code (not yet fully reviewed).
- Site-aware storage organization — uploads/listings scoped by site.
- Confirm the existing FFmpeg video pipeline still works post-change.
- Media library listing endpoint, scoped by site, for the admin picker.

**Acceptance**: upload an image and a video for `vn`, confirm both retrievable, confirm neither appears browsing `jp`'s media library.

### Phase 4 — Admin UI (Vite + React SPA)
- App shell: login, protected routes, site switcher (from Phase 1's site-access list).
- Content list + editor views for the 11 content types — richer forms for `Post`/`Banner` (rich text, SEO-adjacent fields, image), simpler for the structural ones (`stat`, `office`, `logo_badge`).
- Media library UI with picker modal (images + video).
- Basic user management screen if in scope.

**Acceptance**: an editor logs in, sees only their site(s), publishes content with media independently per site, no cross-site data visible in the UI.

### Phase 5 — Chatbot: forked `chat-service`, Claude generation, per-site collections
- `spring-ai-starter-model-anthropic` dependency, `spring.ai.anthropic.api-key` config.
- Swap the `ChatClient` bean to the Anthropic-backed model; keep the existing embedding model (Anthropic has no embeddings API, no reason to touch that half).
- Drop the Ollama-specific `disableThinking()` call.
- One Qdrant **collection per site** (per decision log) — ingestion and retrieval both scoped to the requesting site's collection, never cross-queried.
- Public marketing persona per site's own content/language — not the CRM-support prompt.
- **Remove the "must be authenticated" requirement** on the assistant endpoints — today's CRM assumption is wrong for anonymous public visitors.
- **Add IP-based rate limiting for anonymous requests** on the assistant endpoint specifically (per decision log) — authenticated/internal callers are exempt.
- Re-ingest from `content-service` per site instead of the old CRM docs folder.

**Acceptance**: an anonymous visitor on one site's subdomain gets a grounded answer sourced only from that site's collection; scripted rapid-fire anonymous requests get rate-limited; an authenticated internal test account does not.

### Phase 6 — Hardening & integration
- Full security pass across all four backend services, including the Phase 1/3 audits done properly (not deferred further).
- Load test the public content API and chatbot endpoint (both take public, unauthenticated traffic).
- CORS: apply the user-maintained allowlist of the 5 site subdomains (+ the admin-app's own origin) on the gateway.
- Keep `docs/api-contract.md` current via OpenAPI/Swagger.
- End-to-end test: publish in admin UI → confirm via public content API on that site's subdomain → confirm the Next.js frontend picks it up correctly.
- Since services are forked (not shared): no Workflow-platform regression pass needed here — that was only a concern under the shared-services option, which wasn't chosen.

**Acceptance**: full content lifecycle works end-to-end for at least 2 sites (en, vi — the initial rollout pair), chatbot answers are correctly site-scoped and rate-limited, CORS allowlist enforced.

## Phase 0 status: scaffolded 2026-09-17

- Repo created at `D:\Code\cms-platform`, local only (no git remote yet).
- `core-v1`, `infrastructure`, `auth-service`, `media-service`, `chat-service` forked verbatim (build artifacts stripped before copying). `content-service` scaffolded fresh: `build.gradle`, `ContentServiceApplication`, `PostgresConfig` (registers `UserReader`/`UserWriter` from `core-v1` plus new `JsonNodeReader`/`JsonNodeWriter`), full config set (`r2dbc.yml`, `oauth2.yml`, `liquibase.yml`, `springdoc.yml`), and a complete Liquibase `init-schema.sql` for all 11 content tables plus a `site` lookup table (decided: FK, not a raw code column — see below), seeded with all 5 sites.
- `site_id` decision made: **FK to a `site` lookup table** (id, code, subdomain, active), not a raw code column — as recommended, since Phase 1's user↔site access model needs this registry regardless.
- Verified: `:auth-service:compileJava`, `:media-service:compileJava`, `:chat-service:compileJava`, `:content-service:compileJava` all succeed under JDK 21. `:auth-service:test` and `:media-service:test` pass. `:chat-service:test` fails identically to the original Workflow copy (needs a live Redis for its full-context test) — confirmed pre-existing, not something the fork broke.
- `infra/docker-compose.yml` covers Postgres, Redis, MinIO, Qdrant, and all four services — **no Kafka, no Eureka discovery, no gateway** (none of those were forked). Services still carry their Eureka-client/Kafka config from Workflow; without a running Eureka/Kafka they just fail to connect in the background (non-fatal for local dev). This means there's currently no gateway — inter-service and admin-app calls hit each service's own port directly. Decide before Phase 6 whether this platform needs its own gateway/discovery/Kafka.
- `CLAUDE.md` written to the new repo's root (same content as below, plus the gateway/Kafka open item).
- Git initialized locally (no remote yet — add one when ready to push), first commit made (`caed887`). Plan doc copied into the new repo's own `docs/`.
- Caught and fixed before committing: `cp -r` doesn't respect `.gitignore`, so the fork initially dragged along Workflow's real `media-service/uploads/` (actual uploaded files from Workflow's own usage), a live Firebase service-account JSON key, and a redundant per-service Gradle wrapper — all deleted, and matching `.gitignore` patterns added so they can't slip in again.
- Not yet done: actually running `docker-compose up` end-to-end (Docker wasn't exercised, only direct Gradle compile/test).
- ja/ko/de rollout timing still open — schema supports all 5 from day one, this is purely a sequencing/staffing question.

## How to run this with Claude Code

Same guidance as the source plan: one phase per session/branch, plan mode before code (especially Phase 0/1/2 since the site-scoping pattern set there repeats everywhere after), point Claude Code at the existing forked code first and ask it to summarize before changing anything, tests alongside implementation matching the acceptance criteria above.

---

# CLAUDE.md (for the new `cms-platform` repo)

```markdown
# CMS Platform — Project Instructions

## Context
One Next.js codebase serves 5 regional variants of the CMC Global homepage,
each on its own subdomain, each maintained by that region's own marketing
team. Site = locale (one language per region): en, vi, ja, ko, de. Rolling
out en + vi first; schema supports all 5 from day one.

This platform reuses `auth-service`, `media-service`, and `chat-service`
from the Workflow platform as FORKED starting points — not shared, not
kept in sync. They serve a different purpose here (public multi-tenant
marketing platform) than in Workflow (internal ticketing/HR tooling).
`content-service` is net-new.

## Stack
- Backend: Java 21, Spring Boot 3.5.x, Spring WebFlux + R2DBC (fully
  reactive, no blocking JPA/JDBC), PostgreSQL, Liquibase
- Admin UI: Vite + React (SPA, no SSR) — separate app, separate deploy
- Storage: MinIO/S3 for media, Qdrant for chatbot vector search
  (one collection PER SITE, never shared/filtered-across), Claude API
  (via spring-ai-starter-model-anthropic) for chatbot generation
- Public content API and chatbot endpoint are unauthenticated; everything
  else requires auth + site-access checks
- Site resolution: from the request's subdomain (Host header), not a
  query param or path segment

## Multi-tenant rules (non-negotiable)
- Every content table has `site_id` from its FIRST migration. Never add it later.
- Every query against a content/media/chat-collection table MUST filter by
  site_id. Use the shared base repository pattern — no one-off queries
  that skip it.
- Content is NOT shared or translated across sites — each site's rows are
  independently authored by that region's own team. There is no
  "translations" jsonb pattern in this codebase; don't introduce one.
- The chatbot uses ONE QDRANT COLLECTION PER SITE. Never query across
  collections, never put multiple sites' embeddings in one collection.
- Every new endpoint touching content, media, or chat must be tested for
  cross-site leakage before it's considered done.

## Chatbot-specific rules
- The assistant endpoint is PUBLIC and UNAUTHENTICATED by design — do not
  add a login requirement back.
- Anonymous requests to the assistant endpoint MUST be IP-rate-limited.
  Authenticated/internal requests are exempt from this limit — don't
  throttle internal testing.

## Reused-service rules
- Before modifying `auth-service`, `media-service`, or `chat-service`, run
  a full security review of the code being touched — these were only
  lightly reviewed before the fork, not fully audited.
- Existing test suites in these three services must keep passing after any
  change. A failing pre-existing test is a regression, not something to delete.
- Don't assume behavior from memory — read the actual existing code in each
  service before extending it.
- These are now independent from the Workflow platform's copies. Don't try
  to keep them in sync; that's a deliberate decision, not an oversight.

## Security rules (non-negotiable)
- NEVER build SQL by string concatenation. Always parameterized queries /
  R2DBC criteria / derived queries.
- NEVER disable password encoding. Passwords are always bcrypt-hashed.
- JWT secret from environment/secrets manager ONLY. Never hardcoded, never
  committed, minimum 256 bits.
- NEVER commit real credentials, API keys, or connection strings anywhere
  in this repo. Env vars + `.env.example` placeholders only.
- Rate limiting on auth endpoints keys off server-observed IP or
  authenticated user ID — never a client-supplied header.
- Validate uploaded file types by actual content, not filename extension.
- CORS allowlist is the 5 known site subdomains + the admin-app's origin,
  maintained manually. Never wildcard it.

## Conventions
- Run tests and lint before reporting a task complete.
- Keep `docs/api-contract.md` updated as endpoints change — the admin app
  and the separately-built Next.js sites both depend on it.
- Small, reviewable commits per logical change.
```
