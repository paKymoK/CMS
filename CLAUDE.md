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

Full plan and decision log: see `docs/cms-platform-plan.md` in this repo.

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

## Open item from scaffolding — no gateway/discovery/Kafka yet
Only `core-v1`, `infrastructure`, `auth-service`, `media-service`,
`chat-service`, and `content-service` were forked/scaffolded — NOT
`discovery-service` or `gateway-service`. The three forked services still
carry their Eureka-client and Kafka config/dependencies from Workflow;
without a running Eureka or Kafka broker they'll just fail to
register/connect in the background (non-fatal for local dev — Spring's
Eureka client and Lettuce/Kafka clients are lazy, they don't block
startup). `infra/docker-compose.yml` only brings up Postgres, Redis,
MinIO, and Qdrant — no Kafka, no Eureka, no gateway. There is currently no
gateway, so inter-service and admin-app calls hit each service's own port
directly. Decide before Phase 6 whether this platform needs its own
gateway/discovery/Kafka or can stay without them (fewer moving parts, but
then CORS and per-service TLS/ports become the admin-app's problem
directly, and auth-service's account/department/unit Kafka events and
chat-service's employee-directory sync silently go nowhere).

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
