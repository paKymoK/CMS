# API contract

Consumed by `admin-app` and the separately-built Next.js sites. All paths below are
`content-service` paths; through the gateway they are prefixed `/content-service`.

> Only the draft-preview endpoints are documented so far. The rest of the API
> (public home/posts/case-studies reads, `/v1/admin/**` CRUD) still needs writing up here.

## Draft preview

Token-gated, **not** JWT-authenticated. A preview token is opaque, scoped to one site, and
valid for 15 minutes. The site is always taken from the token — there is no `site` param on
`/v1/preview/**`.

| Method | Path | Auth | Notes |
|---|---|---|---|
| POST | `/v1/admin/preview-tokens?site={code}` | JWT + site access | Mints a token. Returns `{ token, expiresAt }`. |
| POST | `/v1/preview/tokens/refresh?token=` | token | Rotates: deletes the old token, returns a new one. |
| GET | `/v1/preview/home?token=` | token | Homepage content including drafts / inactive rows. |
| GET | `/v1/preview/posts/{slug}?token=` | token | One post, any `status`/`active`. `PostDetailResponse`. |
| GET | `/v1/preview/case-studies/{slug}?token=` | token | One case study, any `status`/`active`. `CaseStudyDetailResponse`. |

Invalid/expired token, or a slug that doesn't exist **on the token's site**, returns an error
response (never another site's data).

### Website entry point

`GET {site-origin}/api/preview?token={token}[&path={path}]`

- `path` optional; defaults to `/en`. Must match `/{locale}` or
  `/{locale}/(insights|case-studies)/{slug}` (slug = lowercase letters, digits, hyphens);
  otherwise `400 Invalid preview path`.
- Bad/expired token → `401`. Success → enables Next.js Draft Mode, sets the httpOnly
  `cms_preview_token` cookie, redirects to `path`.

## Rich-text bodies (posts, case studies)

`POST`/`PUT /v1/admin/posts` and `/v1/admin/case-studies` pass `body` through
`RichTextSanitizer` (content-service) on every save:

- **Rejected with HTTP 400** (`status.message` lists what was found, shown to the author) if the
  body contains `<script>`-like elements (script, style, object, embed, form, svg, …), `on*` event
  handler attributes, `javascript:`/`vbscript:`/`data:` URLs, or an `<iframe>` whose source isn't an
  allowed embed provider (YouTube-nocookie, Vimeo, Google Forms, Tally, Typeform, Jotform,
  Microsoft Forms — https only). The admin editor can't produce these, so a hit means a client
  bypassed it.
- **Silently cleaned** otherwise: tags/attributes outside the allowlist (e.g. table inline styles)
  are dropped, and every link gets `rel="noopener noreferrer"`.

The public site renders `body` as-is, trusting this write-time step. Bodies saved before this was
added were not re-sanitized.
