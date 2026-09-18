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

## Gateway + discovery — added post-Phase-6, forked more fully from Workflow
`discovery-service` (Eureka server, `:8761`) and `gateway-service`
(Spring Cloud Gateway, `:8080`, actuator on `:8090`) are now forked in,
at the user's explicit request for centralized traffic monitoring
(unified per-request log line via `LoggingFilter`, `GET /api/health`
fan-out, aggregated Swagger, its own Redis-backed `RequestRateLimiter`
default-filter on every route — keyed by server-observed IP only, never
a client-supplied header). Routes use real `lb://` service discovery now
(not static URIs), which is why `content-service` also gained a Eureka
client it didn't have before (it was scaffolded fresh, unlike the three
forked services which already carried Eureka-client config from
Workflow). The gateway duplicates each backend's own JWT auth
enforcement (same as it did in Workflow) rather than being the only
place auth is checked — backends must keep defending themselves
regardless of what the gateway does.

Still NOT forked: Kafka. `auth-service`'s account/department/unit Kafka
events and `chat-service`'s employee-directory sync still go nowhere —
those Kafka client dependencies are inert background config, same as
before this addition (non-fatal, Spring's Kafka clients are lazy).

`admin-app` still calls `content-service`/`media-service` directly by
port, NOT through the gateway — this was a deliberate scope call when
the gateway was added, not an oversight. Routing it through the gateway
is a reasonable follow-up (it's what would make admin-app's own traffic
show up in the gateway's monitoring), but wasn't done here.

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
