# API Contract

No gateway in front of these services yet (see `docs/cms-platform-plan.md`'s Phase 0 status) — every
client, including `admin-app`, calls each service's own port directly. Full request/response schemas
are in each service's Swagger UI once it's running:

- `auth-service`: `http://localhost:9000` (OAuth2 endpoints below; no `/v1/doc` Swagger UI)
- `content-service`: `http://localhost:8084/v1/doc/swagger-ui.html`
- `media-service`: `http://localhost:8082/v1/doc/swagger-ui.html`
- `chat-service`: `http://localhost:8083/v1/doc/swagger-ui.html`

This file tracks the shape and auth requirement of every endpoint — keep it current as endpoints
change, since `admin-app` and the separately-built Next.js sites both depend on it.

## auth-service (`:9000`) — OAuth2, PKCE, public `cms-admin` client

- `GET /oauth2/authorize`, `POST /oauth2/token`, `GET /oauth2/jwks`, `GET /connect/logout` — standard
  Authorization Code + PKCE flow, `client_id=cms-admin`, no client secret. JWT carries `roles` (global)
  and `project_roles` (`{siteCode: [role, ...]}`) claims — see core-v1's `AuthenticationUtil`.

## content-service (`:8084`) — the CMS data + public read API

- **Public, unauthenticated**: `GET /v1/home` — site resolved from the request's `Host` header.
  Returns `ResultMessage<HomeContentResponse>` (12 lists: `primaryNav`, `navSections`, `stats`,
  `services`, `offices`, `caseStudies`, `insights`, `awards`, `certifications`, `partners`,
  `testimonials`, `footerNav`). `Banner` is not part of this response yet.
- **Admin, authenticated + site-scoped** (`CmsAdminGuard`, global `ADMIN` or `project_roles[site]`
  containing `ADMIN`): one identical CRUD shape per resource, all under `?site=<code>`:
  - `GET /v1/admin/{resource}?site=` — list
  - `GET /v1/admin/{resource}/{id}?site=` — by id
  - `POST /v1/admin/{resource}?site=` — create
  - `PUT /v1/admin/{resource}?site=` — update (id in the request body)
  - `DELETE /v1/admin/{resource}/{id}?site=` — delete

  Resources: `primary-nav-items`, `nav-sections`, `stats`, `services`, `offices`, `case-studies`,
  `posts`, `logo-badges` (+ `?type=AWARD|CERTIFICATION|PARTNER` filter on GET), `testimonials`,
  `footer-nav-categories`, `banners`.

## media-service (`:8082`) — site-scoped uploads

- `POST /v1/upload/single?site=`, `POST /v1/upload/multiple?site=` — multipart image upload.
  Authenticated + site-scoped (`CmsAdminGuard`). Returns `UploadFile` (or `List<UploadFile>`)
  directly, not `ResultMessage`-wrapped.
- `GET /v1/upload?site=` — media library listing (images + finished chunked uploads) for one site.
- `POST /v1/videos/upload?site=` — video upload, queues FFmpeg transcode. Returns `JobResponse`.
- `GET /v1/videos?site=` — video library listing for one site.
- `DELETE /v1/videos/{videoId}?site=` — delete a video (ownership-checked against `site`).
- `GET /v1/jobs/{jobId}?site=` — poll a transcode job's status.
- `GET /v1/upload/chunked/**` — large-file chunked upload API (`start`/`{sessionId}/chunks/{index}`/
  `finish`/`files`), all `?site=`-scoped the same way; a generic large-file mechanism, not itself an
  image/video type.
- **Public, unauthenticated**: `GET /images/{id}{ext}`, `GET /files/{id}{ext}`,
  `GET /v1/videos/{videoId}/master.m3u8` (HLS playback) — content-addressed by an unguessable UUID,
  same as any published page's embedded media URL.

## chat-service (`:8083`) — public marketing assistant

- **Public, unauthenticated, IP-rate-limited**: `POST /v1/assistant/ask` — body
  `{ site, question, history: [{role, content}, ...] }` (client keeps its own conversation history;
  no server-side session). Returns `PublicAnswerResponse { answer, sources }`. One Qdrant collection
  per site (`cms_<site>`) — see `docs/cms-platform-plan.md`'s Phase 5 status.
- **Admin, authenticated + site-scoped**: `POST /v1/assistant/ingest?site=` — re-ingests that site's
  published content from content-service's `GET /v1/home` into its Qdrant collection (full
  wipe-and-reload). Returns `ResultMessage<Integer>` (documents ingested).
- `/assist/**` (sessions, ask, ingest, applications) — the pre-existing, untouched employee-facing
  CRM assistant forked from Workflow. Not part of the CMS; still authenticated, still Vietnamese-only,
  still backed by the original single `crm_vi` collection. Left running, not wired to anything on the
  CMS side.
