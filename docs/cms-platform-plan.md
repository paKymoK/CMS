# CMS Platform — Plan & Decision Log

This file is referenced by the root `CLAUDE.md` as the platform's plan and decision
log. It currently covers the **draft content preview** feature, planned in full
before implementation started. Add further features/decisions below as they're
planned, newest at the bottom or in their own dated section — don't overwrite prior
entries.

---

## Draft content preview

### Context
`admin-app` (Vite/React SPA) and `website` (Next.js, 5 regional subdomains) are
separate deployments talking to `content-service` as their shared backend. The
public site is **one aggregated page per locale** (`app/[locale]/page.tsx`) — not
per-post — composed from ~8 admin-managed content types (banners, stats, service
cards, offices, case studies, posts, logo badges, testimonials, footer nav). Each
type has a `status` (`DRAFT`/`PUBLISHED`) and `active` flag; the public
`GET /v1/home` only ever returns `status = PUBLISHED AND active = true`, resolved
from the request's Host header, and is cached via Next.js ISR (`revalidate: 60`)
shared by every visitor.

There was no preview capability before this plan, and no `docs/api-contract.md`
either (also referenced by `CLAUDE.md`, also not yet created — worth doing
alongside this).

### Decision: keep admin-app and website split (rejected: merge like WordPress)
Considered combining `admin-app` into the `website` Next.js app (WordPress-style,
one app serving both authenticated editing and public pages) to get trivial
same-origin preview. Rejected:

- **SEO/perf**: not automatically worse if scoped correctly (Next.js route groups
  keep admin middleware/bundle out of public routes), but merging creates a
  standing risk of admin JS/deps leaking into the public bundle (Core Web
  Vitals/INP hit) and pressure to drop ISR for full dynamic SSR "so edits show
  immediately" — a real regression. The actual win (instant refresh) is available
  without merging: on-demand `revalidateTag`/webhook from `content-service` on
  publish, or (for preview specifically) `router.refresh()` — see "Live refresh"
  below.
- **Platform-specific**: this is 5 independently deployed regional sites, each
  owned by a different region's team, sharing one admin. WordPress's model
  assumes one site = one admin = one deploy. Merging would mean either one shared
  deployment serving all 5 regions' public traffic *and* the admin (an admin bug
  now risks all 5 live regions at once), or 5 separate deployments each
  duplicating the admin (worse than today). Neither improves on the current
  split.
- **Security boundary**: keeping admin auth off the public subdomains entirely
  (different origin, no admin session ever touches a visitor-facing domain)
  matches `CLAUDE.md`'s "public content API is unauthenticated; everything else
  requires auth" separation. Merging dilutes that boundary.

Preview is solved instead with Draft Mode + a token, below.

### Design
Preview has **two scopes**, sharing one token mechanism:

- **Page-level** — the composed homepage per locale, including drafts/inactive rows.
  Entry: the top-level "Preview site" button in `AppShell`.
- **Item-level** (added once Post / Case Study detail pages existed) — one post or
  case study on its real detail page, drafts included. Entry: the "Preview" button
  in `PostEditorPage` / `CaseStudyEditorPage`. See "Item-level preview" below."

Mechanism: **Next.js Draft Mode + a short-lived, site-scoped, opaque preview
token**, minted by `content-service`, redeemed by `website`.

Rejected alternative: passing the editor's real access JWT to the website. It's
long-lived, broad (grants full `/v1/admin/**` access), and would end up in a URL
(browser history/referrer/server logs). A narrow, single-purpose, short-TTL token
has much smaller blast radius if leaked and can't be replayed against admin APIs.

Token storage: a new Postgres table via Liquibase migration (opaque random
token + `site_id` + `expires_at`), not a new JWT signing key and not a new Redis
dependency for `content-service` — consistent with its existing reactive
R2DBC/Postgres stack.

#### 1. Backend — `content-service`
- **Migration**: `preview_token(token, site_id, expires_at)`, ~10 min TTL.
- **`POST /v1/admin/preview-tokens?site={code}`** — authenticated, reuses the
  existing `CmsAdminGuard.requireSiteAccess` (same authz as every other admin
  endpoint). Mints and returns a token scoped to that site.
- **`GET /v1/preview/verify?token=...`** — public but single-purpose: validates
  the token, checks expiry, returns the site code, consumes/expires it on use.
- **`GET /v1/preview/home?token=...`** — mirrors `HomeContentServiceImpl
  .getHomeContent`, but every `getPublished(siteId)` call swapped for the
  already-existing `getAll(siteId)` per content type (same method the admin CRUD
  screens already use), so preview shows everything — draft and inactive —
  matching what the admin list screens show.
  - **Known gap to fix as part of this**: `Banner` is CRUD-able today but not
    yet wired into `HomeContentResponse`/`HomeContentServiceImpl` at all. Preview
    must include it, or banner drafts will silently be missing.
- **Mandatory before done**: cross-site isolation test — a token minted for `en`
  must never resolve `vi` (or any other site's) draft content.

#### 2. `website` (Next.js)
- **`app/api/preview/route.ts`** — takes `?token=`, calls `/v1/preview/verify`,
  on success calls `draftMode().enable()` and redirects to `/`; on failure,
  redirects to an error page (never silently falls through to draft content).
- **`app/api/preview/exit/route.ts`** — `draftMode().disable()`, redirect back to
  the plain public page.
- **`page.tsx`** — checks `(await draftMode()).isEnabled`; if true, fetches
  `/v1/preview/home?token=...` with `cache: 'no-store'` instead of the normal
  ISR-cached `/v1/home` fetch. The public page's 60s ISR cache entry is never
  touched by this path.
- **Draft banner** (client component, shown only when draft mode is on): "You're
  previewing draft content" + **Exit preview** + **Refresh preview**.
- Preview pages get `noindex`; never linked from anywhere public.

#### 3. Live refresh (no full page reload on new changes)
Next.js App Router navigation/refresh is already SPA-like — `router.refresh()`
re-runs the server components on the current page and streams back just the new
RSC payload; the browser doesn't reload, and scroll position/client state
elsewhere on the page survive.

- **Phase A — manual refresh button** (ship first): the draft banner's "Refresh
  preview" button calls `router.refresh()`, which re-fetches
  `/v1/preview/home` and patches the DOM in place. One line of client code, no
  new infra.
- **Phase B — automatic push refresh** (follow-up, not blocking): a lightweight
  WebSocket/SSE channel from `content-service` — separate from `chat-service`'s
  existing socket, not shared with it — fires on save; the open preview tab
  listens and calls `router.refresh()` itself. Changes appear ~1s after Save
  with no manual action.
- **Explicitly out of scope**: per-keystroke live-typing preview
  (WordPress/Gutenberg-style). That needs client-rendered components fed
  directly from form state via `postMessage`, a materially bigger lift than
  this feature — revisit only if it becomes an explicit requirement.

#### 4. `admin-app`
- **"Preview" button**, top-level (next to the site switcher, since preview is
  whole-page, not per-row) — calls the mint-token endpoint, opens
  `https://{site.subdomain}/api/preview?token=...` in a new tab. Subdomain is
  already known client-side via `config/sites.ts`.
- No gateway or CORS changes needed — `/content-service/**` is already a
  catch-all gateway route.

### Security checklist (mapped to `CLAUDE.md` non-negotiables)
- Token: opaque, random, short TTL, single-site-scoped, DB-stored — not the
  editor's real JWT, not a new signing key.
- Cross-site leakage test required before ship (token for site A must not
  unlock site B).
- Public `/v1/home` and its ISR cache are structurally untouched by any of this.
- No new CORS entries; no wildcarding.

### Testing checklist
- Cross-site token leakage (as above).
- Token expiry + reuse-after-expiry rejected.
- Public ISR-cached page unaffected by a concurrent preview session.
- Banner drafts actually show up in preview once wired into the aggregator.
- `router.refresh()` picks up a save without a full reload.

### Rollout (small, reviewable commits)
1. `preview_token` migration + mint/verify endpoints + cross-site test.
2. `getAll`-based preview aggregator in `content-service` (+ fix Banner gap).
3. Website route handlers + draft banner + `page.tsx` draft-mode branch.
4. admin-app "Preview" button.
5. Live refresh Phase A (manual button).
6. Live refresh Phase B (push-based), as a follow-up once A is validated.

### Related, not in scope here
An on-demand `revalidateTag`/webhook call from `content-service` on **publish**
(not draft save) would let the *live* public page refresh instantly instead of
waiting out the 60s ISR window. Separate, smaller plan if/when wanted.

### Known documentation gap
`CLAUDE.md` states admin-app calls `content-service`/`media-service` directly by
port, not through the gateway. That's stale — `admin-app/src/lib/api.ts` already
routes every call through `gateway-service` (`VITE_BASE_URL` points at the
gateway's own port, using its route ids). Worth a small correction to
`CLAUDE.md` separately; it doesn't affect this plan since the gateway's
`/content-service/**` route is a catch-all.

---

## Generic content model — implemented (database + Java layer)

Built per the "Database upgrade" and "Java layer" conversations: `content_type`
(registry) + `content_item` (generic rows, `data jsonb`) tables, alongside the 9
existing typed tables (untouched, per Phase 3 above being deferred/optional).

**Java pattern** — adapted from `Workflow/workflow-service`'s `Ticket<T extends
TicketDetail>` / `TicketDetailReader`/`Writer` / `TicketMapper` (`Ticket.detail`),
with two deliberate changes:
- The registry is keyed by a stable string (`@ContentTypeKey("faq")`, matching
  `content_type.key`), not the Java class's fully-qualified name — Workflow's
  `CLAZZ_NAME`-in-jsonb approach couples stored rows to a specific package path;
  renaming/moving a class there breaks deserializing old rows.
- `ContentItem.data` is stored as a plain `JsonNode` (reusing the existing
  `JsonNodeReader`/`Writer` R2DBC converters), not resolved to a concrete
  `ContentFields` class at the persistence layer. Workflow embeds a discriminator
  *inside* the jsonb specifically because a column-level `Converter<Json, T>`
  can't see sibling columns — but that breaks for the flexible tier here, since
  `GenericContentFields` alone can't say which content type it was. Resolving
  `contentType` + `data` together in `ContentItemMapper` (where both are always
  in hand at once) needed no embedded discriminator at all.

Validation: `ContentItemMapper.resolveAndValidate()` converts the incoming JSON to
the registered `ContentFields` class (falling back to `GenericContentFields` for
an unregistered type) and runs `jakarta.validation.Validator` by hand before
anything reaches the repository — same explicit pattern as `TicketMapper`, since
`@Valid` can't cascade into a raw `JsonNode`.

First real type: `FaqFields` (`question`/`answer`, both `@NotBlank`), proving the
whole path rather than leaving the mechanism unexercised.

### Decision: content types are authored at the code level, not self-service
A new content type is a new `ContentFields` Java class (+ a `content_type` seed
row) — not something a non-developer admin user can create through admin-app.

**Why**: a true self-service "define your own content type" builder (WordPress's
Custom Post Types + ACF, or Strapi's content-type builder) is a materially
different, much riskier feature — it means either exposing raw JSON-schema
editing to end users (easy to produce a type nobody can render safely) or
building a real plugin/extension system to keep it safe, which this platform
doesn't have and isn't taking on. Explicitly deferred, not an oversight.

**How to apply**: don't build a `content_type` CRUD API/admin-app UI as a
default next step just because the registry table exists. If self-service type
creation is ever wanted, it needs its own design pass (validation-of-validation,
who's allowed to define a type, how a type change affects already-published
rows) — treat it as a new, separately-scoped feature, not a natural extension of
this one.

### Fixed: friendly error on an unknown/inactive content_type
`ContentItemMapper.resolveAndValidate` now checks `content_type` exists (and is
active) via `ContentTypeRepository` before validating `data`, raising a normal
`ApplicationException` ("Unknown content type: x" / "Content type is not active:
x"). Previously an invalid `content_type` only surfaced as a raw Postgres FK-
violation from `content_item`'s `REFERENCES content_type(key)` constraint — the
DB-level integrity was always enforced either way; this just gives it a message
consistent with the rest of the codebase's error handling.

---

## Draft content preview — token mechanism implemented; revision-history deferred

Revisited after `content_item` landed. Found a real gap the original design above
didn't cover: `status`/`active` only distinguish "never-published draft" from
"published" — editing an **already-published** row (any table, old or new)
mutates that row in place, so there was no way to preview an edit before it goes
live, only to preview a brand-new not-yet-published item. WordPress's answer is
autosave/revision rows (`wp_posts`, `post_type='revision'`, `post_parent` pointing
at the live post) rendered in place of the published row on a nonce-verified
preview request, without touching the published row itself.

**Decision: build the full-revision-history version of this (a `content_item`-
scoped `content_item_revision`/`content_item_draft` table), not just a single
pending-draft row** — explicitly deferred, still being thought through. **Not**
implemented in this pass. `content_item`/the 9 typed tables still have no
draft-of-a-published-edit mechanism; today's preview only covers "preview a
not-yet-published item," same as the original design.

**Decision: no OAuth2 login on `website`.** Considered giving `website` its own
session-cookie-based OAuth2 flow (mirroring `admin-app`'s PKCE flow against
`auth-service`, but server-side/cookie-based rather than `admin-app`'s
`sessionStorage`-based SPA flow, since `website` is Server-Component-rendered).
Technically feasible — `auth-service`'s `RegisteredClientRepository` already
supports registering additional redirect URIs — but rejected: `website` is **5
separate origins** (one deployment per regional subdomain), so a session cookie
set on `en.cmcglobal.com` doesn't carry to `vn.cmcglobal.com` — an editor
reviewing content across regions would need up to 5 separate logins, versus one
admin-app login covering every site via a minted token. Also would give the
public marketing domain real session/cookie/CSRF surface it has zero of today.
Kept the token-based design instead, with the token made longer-lived and
refreshable to cover a longer editing/review session without paying either cost.

### What's implemented
**content-service**:
- `preview_token` table (`init-schema.sql`): `id` (real `bigserial`
  `@Id` — not `token` itself, since Spring Data R2DBC's `save()` only inserts
  when the `@Id` is null beforehand; a manually-assigned String `@Id` would look
  "already existing" and attempt an update instead), `token` (opaque, `SecureRandom`
  + base64url, 32 bytes), `site_id`, `minted_at`, `expires_at`.
- `PreviewTokenGuard` (`config/`, mirrors `CmsAdminGuard`'s role) — the one place
  every `/v1/preview/**` endpoint validates a token and resolves its `siteId`.
  `siteId` always comes from the token, never a caller-supplied parameter — there
  is no `?site=` on any preview endpoint, so a valid token can't be pointed at a
  mismatched site.
- `POST /v1/admin/preview-tokens?site=` — mint, authenticated exactly like every
  other admin endpoint (`CmsAdminGuard.requireSiteAccess`).
- `GET /v1/preview/home?token=` — draft-inclusive content, via
  `HomeContentService.getPreviewContent` (mirrors `getHomeContent` field-for-
  field with every `getPublished` swapped for `getAll`, exactly as originally
  planned). Doubles as token verification for `website`'s entry route — no
  separate `/v1/preview/verify` endpoint was needed.
- `POST /v1/preview/tokens/refresh?token=` — **rotates**: deletes the current
  token, mints a fresh one for the same site with a new 15-minute expiry. A
  captured-but-unused old token stops working the moment a real refresh happens.
  Sliding window, no hard cap on total session length.
- Both `/v1/preview/**` endpoints are `permitAll()` in `core-v1`'s shared
  `SecurityConfig` (same carve-out mechanism already used for `/v1/home/**` and
  chat-service's `/v1/assistant/ask`) — not because they're unauthenticated in
  spirit, but because the credential type (opaque token) isn't a JWT the OAuth2
  resource server would ever recognize as a `Bearer` token in the first place.
  Presenting a preview token against any `/v1/admin/**` endpoint just fails JWT
  parsing — it's structurally inert everywhere except these two routes, not
  scoped-by-policy.

**website**:
- `lib/cms/previewCookie.ts` — the `cms_preview_token` cookie name/options
  shared by every route handler (httpOnly, `sameSite: lax`, 15-minute `maxAge`).
  Separate from Next's own `draftMode()` cookie, which only signals on/off and
  can't carry the token value itself.
- `lib/cms/getPreviewHomeContent` — `cache: "no-store"`, and deliberately does
  **not** fall back to the bundled static content on failure the way
  `getHomeContent` does; that fallback exists so a real visitor never sees a
  broken page, but silently showing placeholder content here would hide an
  invalid/expired token from the one person who needs to see that error.
- `app/api/preview` — validates the token (by actually fetching preview content
  with it) before calling `draftMode().enable()`; a bad link fails clearly here
  rather than silently enabling draft mode and failing later on the page.
- `app/api/preview/exit` — disables draft mode, clears the cookie.
- `app/api/preview/renew` — rotates the token via content-service, updates the
  cookie. Called by `DraftBanner`'s background interval (every 10 minutes, under
  the 15-minute TTL) so an open preview tab never lapses on its own.
- `[locale]/page.tsx` branches on `draftMode().isEnabled`; missing/invalid token
  degrades to normal published content with a `console.error` rather than
  throwing, since a page render (unlike the explicit `/api/preview` entry) should
  never 500 for a visitor.
- `DraftBanner` (client component) — "previewing draft content" notice, **Refresh
  preview** (`router.refresh()` — the live-refresh mechanism from the original
  plan, no full page reload), **Exit preview**.

**admin-app**: a **Preview** button in `AppShell`, next to the site switcher
(matches preview being whole-page, not per-row) — mints a token, opens
`https://{subdomain}/api/preview?token=...` in a new tab using the subdomain
already in `config/sites.ts`. Known local-dev caveat: those subdomains aren't
real/resolvable in local dev the same way the rest of `SITES` already isn't
(no local-dev override for this today).

Verified: `content-service` compiles and its test suite passes; `website`
builds and type-checks clean (`next build`), lints clean; `admin-app` type-checks
(`tsc -b`) and lints clean.

### Local testing: two real bugs found and fixed by actually running the stack
Compiling/testing in isolation missed both of these — only found by starting
`content-service`, `gateway-service`, and `website` together and hitting the real
endpoints.

1. **`PreviewToken` extended `IdEntity`**, inheriting `BaseEntity`'s
   `createdBy`/`modifiedAt`/`modifiedBy` audit columns — but `preview_token` was
   never given those columns (`mintedAt`/`expiresAt` already cover its lifecycle,
   and it's immutable — refresh rotates, never updates a row in place). Every
   query 500'd (`column preview_token.created_at does not exist`). Fixed by
   making `PreviewToken` a plain class with its own `@Id` instead of extending
   `IdEntity`.
2. **`gateway-service` has its own separate, hand-maintained security config** —
   `authentication/AuthenticationConfig.java` — completely independent from
   `core-v1`'s `SecurityConfig`. `GatewayServiceApplication` scopes its component
   scan to only `com.takypok.gatewayservice`, so it never loads `core-v1`'s
   `SecurityConfig` at all, despite depending on the `core-v1` module for other
   things. Adding `/v1/preview/**` there had zero effect on the gateway; the
   gateway kept 401ing every preview request with a generic
   `WWW-Authenticate: Bearer` challenge until the *same* permitAll rule was added
   to `AuthenticationConfig.java` too, using the full `/content-service/v1/...`
   prefix (gateway's own patterns include the route prefix; `core-v1`'s don't,
   since content-service is hit directly without one).
   **Any future public content-service endpoint needs its permitAll carve-out
   added in *both* places** — `core-v1`'s `SecurityConfig` (for hitting
   content-service directly) and `gateway-service`'s `AuthenticationConfig` (for
   hitting it through the gateway, which is the only path `website`/`admin-app`
   actually use). Nothing enforces these staying in sync; this is a standing trap
   for the next person who adds one.

Also cleaned up incidental local data drift from testing: `seed-home-content.sql`
had run twice against the local DB (once via a manual raw-SQL apply while
validating the consolidated `init-schema.sql`, once for real via Liquibase on a
later app boot that didn't know about the manual run) — doubled every seeded
content row. Not a bug in this feature; a one-time side effect of that earlier
verification method. Deduplicated in the local DB; `databasechangelog` is now
correctly populated so this specific duplication can't recur.

## HTTPS for `auth-service` — planned, not yet implemented

### Context
Production needs `auth-service` reachable over HTTPS; it currently only serves
plain HTTP on `:9000`. It's not like the other backend services: admin-app's
browser-based PKCE flow (`admin-app/src/auth/pkce.ts`, `VITE_AUTH_SERVER`)
talks to `auth-service` **directly from the browser** (`/login`,
`/oauth2/authorize`, `/oauth2/token`, `/connect/logout`), bypassing
`gateway-service` entirely — a deliberate existing split (`VITE_BASE_URL` for
content/media/chat via the gateway, `VITE_AUTH_SERVER` separate). So
`auth-service` uniquely needs its own public HTTPS origin; the other services
validate JWTs against it server-to-server over the private network and can
stay plain HTTP (per their `.env.example` comments: "production: auth-service
VPS's private IP").

Deploy topology confirmed with the user: **HAProxy** in front of internal
service-to-service traffic (private network, unaffected by any of this), and
**Cloudflare** in front of whatever's public.

### Decision: Caddy reverse proxy in front of `auth-service`, production-only
Rejected two alternatives:
- **Spring Boot's own `server.ssl.*`/keystore** — means hand-managing a
  PKCS12 keystore and Let's Encrypt renewal inside the app; no benefit to
  local dev (nobody wants a self-signed-cert prompt for `localhost:9000`).
  Caddy's automatic HTTPS needs a ~5-line Caddyfile and handles issuance,
  renewal, and HTTP→HTTPS redirect on its own.
- **Route `auth-service` through `gateway-service`** instead of giving it its
  own edge — rejected. Doesn't avoid needing a TLS terminator anyway (gateway
  has none either, so Caddy would just move in front of gateway instead).
  Makes gateway a single point of failure for login platform-wide (today
  auth is reachable independently of gateway). Collides gateway's blunt
  per-IP `RequestRateLimiter` (shared across every route) with
  `auth-service`'s purpose-built IP+username lockout. Adds OIDC
  issuer/path-prefix fragility (Spring Authorization Server builds absolute
  URLs — discovery doc, `token_endpoint`, `redirect_uri` validation — from
  the request it sees) and a new cookie/CORS namespace shared with every
  other service. No offsetting benefit.

Local dev is untouched either way — `./gradlew :auth-service:bootRun` or the
plain `docker-compose.yml`, `http://localhost:9000`, no cert, nothing new to
install.

### Design

1. **`backend/auth-service/Caddyfile`** (new):
   ```
   {$AUTH_PUBLIC_DOMAIN} {
       reverse_proxy auth-service:9000
   }
   ```
2. **`backend/auth-service/docker-compose.prod.yml`** (new override, used as
   `docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d`) —
   adds a `caddy` service (80/443 + cert volume), removes `auth-service`'s own
   public `9000:9000` mapping so Caddy is the sole path in. Base
   `docker-compose.yml` stays as-is for local dev.
3. **`application.yaml`** — add `server.forward-headers-strategy: framework`
   under `server:`, so Spring trusts `X-Forwarded-Proto`/`Host`/`Port` from
   the (now sole, trusted) reverse proxy — correct `Secure` cookie flag and
   correct scheme/host in generated OIDC URLs. No-op locally (nothing forwards
   headers there).
4. **`.env.example`** — document `AUTH_PUBLIC_DOMAIN`
   (e.g. `auth.cmcglobal.com`) and note `AUTH_ISSUER_URI` must be the matching
   `https://` value in production.
5. No changes needed in `content-service`/`media-service`/`chat-service`.

**Cloudflare-specific additions** (surfaced once the user described the real
deploy topology):
- Cloudflare SSL/TLS mode must be **Full (strict)**, not Flexible — Flexible
  leaves the Cloudflare-edge-to-origin hop (including login POST bodies and
  session cookies) unencrypted. Full (strict) needs a real cert on the
  origin, which Caddy provides (or a Cloudflare Origin CA cert, served
  statically by Caddy instead of via ACME).
- Use Caddy's `caddy-dns/cloudflare` plugin for a **DNS-01** challenge
  instead of HTTP-01 — avoids HTTP-01 conflicting with Cloudflare's
  proxying/redirect behavior on port 80, and lets port 80 stay closed on the
  origin entirely.
- Configure Caddy's `trusted_proxies` with Cloudflare's published IP ranges,
  so `X-Forwarded-For` is rewritten to the real client IP (not Cloudflare's
  PoP IP) before reaching `auth-service` — required for the IP-keyed lockout
  to key off the real client rather than Cloudflare.
- Firewall the VPS so `auth-service`'s public listener only accepts
  connections from Cloudflare's IP ranges — otherwise someone can bypass
  Cloudflare, hit the origin directly, and spoof the forwarded-IP header
  themselves.

### Production checklist this unlocks (env vars only)
- `auth-service`: `AUTH_PUBLIC_DOMAIN`, `AUTH_ISSUER_URI=https://...`,
  `CMS_ADMIN_REDIRECT_URIS`/`CMS_ADMIN_POST_LOGOUT_REDIRECT_URIS` set to the
  real admin-app HTTPS origin, `CORS_ALLOWED_ORIGINS` including that origin.
- `admin-app` production build: `VITE_AUTH_SERVER=https://...`,
  `VITE_REDIRECT_URI=https://<admin-app-domain>/callback`.

### Verification (once implemented)
- Local: `curl http://localhost:9000/login` unaffected.
- Production dry run: Caddy issues a cert (check logs);
  `curl https://auth.../.well-known/openid-configuration` and confirm
  `"issuer"` matches `AUTH_ISSUER_URI` exactly; full admin-app login round
  trip over HTTPS with the session cookie's `Secure` flag confirmed in
  devtools.

### Status
**Blocked, on purpose** — the user flagged the backend Dockerfiles aren't
production-ready yet and wants that fixed first (see next section). Nothing
above has been implemented; `server.forward-headers-strategy` in particular
was confirmed as the right fix when the user tried a Cloudflare Tunnel
locally and saw `auth-service` generate `http://` URLs against an `https://`
tunnel — but even that one-line, harmless-when-unused change was deferred
until the Dockerfile work lands, at the user's request.

### Item-level preview (posts, case studies)
Same site-scoped `preview_token`; the *item* is chosen by the URL, not the token.

- **Backend**: `GET /v1/preview/posts/{slug}?token=` and
  `GET /v1/preview/case-studies/{slug}?token=` — guarded by `PreviewTokenGuard`, site
  taken from the token only, `findBySiteIdAndSlug` ignores `status`/`active` (including
  the linked testimonial's `active`). "Related" stays published-only so it matches what
  visitors will see. Cross-site tests: `PostServiceImplTest`, `CaseStudyServiceImplTest`,
  `PreviewControllerTest`.
- **Website**: `/api/preview?token=&path=` deep-links to one page. `path` is checked
  against a strict allowlist (`lib/cms/previewPath.ts`: a locale home, or
  `/{locale}/insights|case-studies/{slug}`) — anything else is a 400, never a redirect
  (open-redirect guard). Detail pages use `getPreviewToken()`; with a token they fetch
  from the preview endpoints (`no-store`), show `DraftBanner`, and set `noindex`.
  Without one they take the unchanged published/ISR path.
- **admin-app**: the editor's Preview button (`usePreviewItem`) previews the **saved**
  row. New item or draft with unsaved edits → saved as DRAFT first. Published item with
  unsaved edits → *not* saved (that would push edits live); previews the last saved
  version and warns. The tab is opened synchronously before the token mint to avoid
  popup blockers (`lib/preview.ts`).
- **Out of scope**: live typing preview of unsaved form state.

