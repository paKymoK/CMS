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
