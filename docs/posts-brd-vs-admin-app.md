# Posts: BRD vs current admin-app

Source BRD: `docs/MKT_Website_Renovation_BRD_Ver_02_211026.docx` (Posts, Categories, Tags sections)
Compared against: `content_item` table (`content_type = 'post'`), `content-service`, `admin-app`

A Post is a `content_item` row. Type-specific fields live in the `data` jsonb (`PostFields`).

Legend: ✅ covered · 🟡 partly covered · ❌ gap

## 1. Coverage

| # | BRD requirement | Current implementation | Status |
|---|---|---|---|
| 1 | Fields: title, slug, excerpt, featured image, author, date, category, tags | `PostFields` has all of them, plus `authorBio`, `authorAvatar`, `layout` | ✅ |
| 2 | Slug unique per site | Partial unique index `uq_content_item_slug`, auto-resolved by `Slugs.resolve` | ✅ |
| 3 | Statuses: Draft / Preview / Publish / Schedule / Archive | `DRAFT` / `SCHEDULED` / `PUBLISHED` / `ARCHIVED` for posts (DB CHECK; other content types stay Draft and Published, enforced by the server), plus an `active` flag. Archive and Schedule are done (G1). "Preview" is read as the preview action (row 11), not a stored status: the BRD's workflow section lists only Draft, Published, Scheduled and Unpublished/Archived | ✅ |
| 4 | Headings, bold/italic, lists, links, alignment | Tiptap toolbar: H2–H4, B/I/U/S, lists, link, align | ✅ |
| 5 | Image: alt, caption, size, alignment | `figure` node with float left/right/full and caption. Alt is hard-coded `""` on insert | 🟡 |
| 6 | Video via URL (YouTube/Vimeo), responsive | `embed` node, allow-listed hosts only | ✅ |
| 7 | Button / CTA | `LinkButton` (link rendered as button) | ✅ |
| 8 | Blockquote, table, divider | All present; table has row/col/merge/header controls | ✅ |
| 9 | Table of contents (H1→H3) | Outline panel built from H2 only; no TOC block | 🟡 |
| 10 | Undo / Redo | Toolbar | ✅ |
| 11 | Preview before publish (desktop/tablet/mobile) | Preview button opens the page in an iframe drawn by the same renderer as the website, from the **unsaved** editor state, with Desktop, Tablet (820 px) and Mobile (390 px) widths (`PreviewPanel`). Works for Posts, Case Studies, Service, Industry and About pages. The site header, footer and contact form are not part of the preview, and Home has none. The earlier token-based preview was removed | ✅ |
| 12 | Search-snippet preview (BRD: the post as it looks in a search result, mobile and desktop, with an example image) | "Search result preview" button in the SEO checklist opens desktop and mobile results built from the unsaved form (`SearchSnippetModal`); also on Case Studies and the page types | ✅ |
| 13 | One previous version + restore | Full history in `content_audit` (before/after snapshots), `HistoryDrawer`, RESTORE action | ✅ (exceeds BRD) |
| 14 | Audit log | DB trigger records CREATE/UPDATE/PUBLISH/UNPUBLISH/DELETE/RESTORE, including direct SQL | ✅ |
| 15 | Multi-site, per-site access | `site` table (one row per locale), `CmsAdminGuard.requireSiteAccess` | ✅ |
| 16 | Featured flag | `featured` | ✅ |
| 17 | Auto-fit layout | Four layouts: default, focused, wide, landing | ✅ |

Extras the app has that the BRD does not mention: device-width preview of unsaved edits for every page type, a version check that rejects a save made from an out-of-date form, per-section on/off switches on the page types, callout, two-column layout, accordion, inline code and code block, text colour, HTML source mode, save-as-template (`post_template`), word count and read time, activity feed, full-text and trigram search.

## 2. Gaps

### G1. Scheduling and Archive
- **Archive: done (posts).** Statuses are Draft, Published and Archived. Archived is off the site but kept, and can go back to Draft or be published again. The audit trail has an `ARCHIVE` action (archived back to draft is an update that lists the status change; publishing is still `PUBLISH`). **Archived posts stay editable (decision):** the status only controls whether the post is public, as with Draft and Private in WordPress, which has no built-in archived status and locks only Trash. Locking them would be a rule of our own: the server would reject content edits on an archived post and the editor would open read-only. Only posts may be archived; the server rejects ARCHIVED or any unknown status for the other content types. The Post editor has a three-way status switch, an **Archive** button (with a confirmation for a live post), **Save** and **Move to draft** for an archived post, and an "Archived" label in the list, Activity and search.
- **Public side:** an archived post answers "not found" like an unknown address and drops out of the public list and search with no read path changed (every public read matches `'PUBLISHED'` exactly). **Detail pages on the website are now fetched without Next's data cache** (`cache: "no-store"`): that cache only stores 200 responses, so a page that had been visited kept being served from its old copy after it was archived or unpublished. Lists keep the 60 s cache.
- **Schedule: done (posts), cron-based.** Statuses are now Draft, Scheduled, Published and Archived. The `active` flag is unchanged. See "Scheduled publishing" below.

**Scheduled publishing (built).**

- **Mechanism: pg_cron, once a minute.** The Postgres image already had pg_cron (`Dockerfile`, `postgresql.conf`: `shared_preload_libraries` and `cron.database_name = 'content'`). `init-schema.sql` now creates the extension and the job `publish-scheduled-content` (`* * * * *`, running `SELECT publish_scheduled_content()`). Once a minute was chosen over once a second to keep load low under pressure. The job is created only where pg_cron exists (the database named by `cron.database_name`); elsewhere (test databases, a managed Postgres without the extension) the schema still loads with a warning and scheduled items wait until the function is run some other way.
- **The function** `publish_scheduled_content()` moves due `SCHEDULED` items to `PUBLISHED` and due `PUBLISHED` items (with an `unpublish_at`) to `ARCHIVED`, clearing `unpublish_at`. Each direction is one `UPDATE` over a `FOR UPDATE SKIP LOCKED` selection: atomic, and a second runner skips rows the first holds. It takes everything overdue, so downtime or a missed run only delays publishing: seen live, after the machine slept for 14 minutes the job published and archived on its first run back. It stamps the write as the `System` user and bumps `version`, so Activity shows "System" and an editor with the item open gets the out-of-date-form warning.
- **Data:** `content_item.publish_at` / `unpublish_at` (timestamptz), checks (a scheduled item needs `publish_at`; `unpublish_at` must be later), and two small partial indexes so the minute check touches only waiting rows. The audit trigger has a `SCHEDULE` action and records both times (UTC) in the snapshots.
- **Rules (`ScheduleRules`):** posts only. Draft and Archived carry no times; Scheduled needs a future publish time (optional later unpublish time); a live post may have a future unpublish time. Times are kept to the minute. A time unchanged from what is stored is not re-checked against the clock, so saving in the minute before the job runs is not refused. A scheduled post without a display date is dated the day it goes live.
- **Editor:** a four-way status switch; for Scheduled a "Publish at" picker, for Scheduled and Published an optional "Take offline at" picker, with the editor's time zone shown (times are picked in it and stored as UTC). The main button reads Publish, Update, Schedule or Update schedule; publishing or scheduling a draft with SEO items missing asks once, as before. Labels for Scheduled are in the list, Activity and search.
- **Timing:** up to a minute for the job, plus the website's 60 s list cache; detail pages are not cached, so they appear as soon as the job runs.
- **Verified:** 129 content-service tests (new: `ScheduleRulesTest`, scheduling tests in `ContentAuditTriggerTest` that run the function against a real database, `StatusRulesTest`); the real pg_cron job published and archived a test row at the exact minutes; the editor was driven in a browser (schedule, SEO prompt, saved times, audit entry).
- **Limits:** the website's public pages and tablet and mobile widths of the new picker panel were not re-checked in a browser. If production Postgres is managed without pg_cron, the job must be set up there (or `publish_scheduled_content()` called by another scheduler); the schema loads either way but would only warn.
- **Still open:** the `active` flag stays as it is; Trash stays skipped (Archived plus the delete rule covers it); other content types are not schedulable.

### G2. Categories and Tags management
- `category` is a free-text string; `tags` is a string array inside `data`.
- No Category or Tag tables, no list pages, no parent/child, no active/inactive, no per-category post counts, no bulk actions.
- Editor category field is a plain text input defaulting to `"insight"`.

### G3. Post list view
- Posts use the generic `ContentPage`: ID, title, a few columns, status, active, order.
- No bulk actions, no search, no filter (status/category/site), no sort by updated date or updater, no language column.

### G4. SEO
- **Done** for Posts, Case Studies, Service, Industry and About pages: meta title (`seoTitle`, the whole page title), meta description, share image (falls back to the cover or main image) and noindex. Each falls back to the title, excerpt or summary and image used before, so nothing changes until a field is filled in. The Posts editor has an SEO card; the other pages have an SEO section. The website's page metadata (title, description, Open Graph and Twitter image, robots) follows the fields.
- **SEO checklist warning (BRD): done.** An inline panel lists the slug, meta title, meta description and share image as OK, over the usual length, or missing (with the text that will be used instead), plus a noindex notice. Publishing a draft with items missing asks once ("Publish anyway" / "Go back"); updating a live page never asks. Nothing blocks saving or publishing.
- **Still missing:** redirects when a URL changes (with the warning), the head/body/footer code-injection field (needs sanitising rules), and a social image field beyond the one share image. Roles and per-site rules are unchanged.

### G5. Per-media metadata in a post
- Alt text on inserted images is hard-coded `alt=""`.
- No title, description or size picker. Only the cover image uses the media picker.

### G6. Preview
- **Done:** the Desktop / Tablet / Mobile preview of unsaved edits (row 11).
- **Done:** the search-result preview (row 12), desktop and mobile, from the unsaved form.
- **Limits of what exists:** the preview draws the article or page only, without the site header, footer or contact form; the site-wide sections of Service and Industry pages show a labelled stand-in; Home (not on the page model) has no preview.

### G7. Editor blocks missing
- Share buttons block, table of contents block, form block (only a raw iframe embed), spacer block.
- No drag-and-drop to reorder blocks, no "duplicate block".
- It is a Tiptap text editor, not a block builder.

### G8. Delete rule and Trash
- **Delete rule: done (posts).** A published post cannot be deleted, from the API (400: "archive it or move it to draft first") or the list (the Delete button is disabled with a tooltip). Draft and archived posts can be deleted.
- **Trash with restore: not built.** A deleted post is still recoverable only through the audit trail ("Create new from this version"). Archived is the safe place for content that should leave the site but not be lost.

### G10. Create a translated draft in another site (feature)
- BRD intent: when an editor drafts a post in EN, they can create a new **DRAFT** of it in another site (e.g. VI) with the content auto-translated, then review and edit it independently.
- Model stays `site == locale`. The action copies the post into the target site as a new `content_item` row (`status = DRAFT`), so the existing per-site slug rule, audit trail and access guard apply unchanged.
- Fields to translate: title, excerpt, body (HTML, tags and embeds must survive), image alt/captions, SEO fields once G4 exists. Not translated: slug is re-resolved for the target site, tags/category follow G2, media URLs and dates are copied.
- Target sites per BRD: Global → VI (auto-translate or manual upload); APAC → Thai/Chinese/Malay; Korea and Deutschland → auto from EN; Japan → manual.
- Existing building block: the "Create a new item from this version" prefill flow (`prefillAsPost`) already opens the editor on a new draft with copied content.
- A source reference on the new row (optional) would only be for display ("translated from EN post #123"), not required for the feature.

### G9. Roles
- Guard checks site access only. No Editor vs Admin separation on post actions found.
- To verify: `CmsAdminGuard`, auth-service client roles.

## 3. Design differences to decide

| Topic | Current | BRD | Note |
|---|---|---|---|
| Language model | `site == locale`; content authored independently per site | Manage EN/VI versions of the same content | **Keep as is.** The BRD need is a feature (see G10), not a model change. |
| `active` vs `status` | Both exist and overlap (`active=false` acts like hidden) | Status only | Consider folding `active` into the status model. |
| Version history | Full history | 1 previous version | Keep the app's approach. |
| Tag parent field | n/a | Tags section lists "parent category" | Ignore: BRD says Tags are non-hierarchical. |

## 4. Suggested order

1. **Status model:** `DRAFT / PUBLISHED / SCHEDULED / ARCHIVED`, `publish_at` / `unpublish_at`, scheduler. Also enables the delete rule and Trash (G1, G8). *(Done: archive, the delete rule and scheduling with a pg_cron job; see G1.)*
2. **Category and Tag tables** per site, with posts referencing them (G2).
3. ~~**SEO fields** in `PostFields`, DTOs and editor sidebar (G4)~~ done, together with the checklist; redirects and code injection remain.
4. **Dedicated post list page** with search, filters, bulk actions (G3).
5. Translated-draft action (G10), after G1 and G2 so the copy carries the right status and taxonomy.
6. Editor blocks (G5, G7) and the search-snippet preview (G6; the meta fields it needs now exist).
7. Roles check (G9).

## 5. Open questions for the critical review

- Is `active` still needed once `status` covers Scheduled/Archived?
- Do Categories and Tags need a tenant-wide or per-site scope, and are they translated per site?
- Translation engine for G10: which service, who pays, and is the call synchronous or a background job?
- Does the translated draft keep an optional "translated from" reference, or stay fully independent?
- Does G10 need permission rules (who may create drafts in other sites)?
- Is the Tiptap editor acceptable, or does the BRD "block" language require a block builder?
- Where should the scheduler live (content-service job vs external)?
- Is "Preview" in the Post status list a stored status, or the preview action? The workflow section lists Draft, Published, Scheduled, Unpublished/Archived and leaves internal review to Marketing's own process, which suggests the action (done), not a status.
- Search-snippet preview: Google only, or other engines too? Is a computed snippet enough (title, URL, description, image), or must it match the live search result?

## Key files

- Schema: `backend/content-service/src/main/resources/db/changelog/init/init-schema.sql`
- Post fields: `backend/content-service/src/main/java/com/takypok/contentservice/model/content/types/PostFields.java`
- Post service: `backend/content-service/src/main/java/com/takypok/contentservice/service/impl/PostServiceImpl.java`
- Sanitizer: `backend/content-service/src/main/java/com/takypok/contentservice/util/RichTextSanitizer.java`
- Editor page: `admin-app/src/pages/PostEditorPage.tsx`
- Rich text editor: `admin-app/src/components/RichTextEditor.tsx`, `admin-app/src/components/rte/extensions.ts`
- Preview: `admin-app/src/components/PreviewPanel.tsx` (device widths), `admin-app/src/components/resourcePreviews.ts` and `*Preview.tsx` (one per page type), `admin-app/src/preview/Frame.tsx` (the iframe), `frontend/packages/content-renderer` (the shared renderer)
- List page and resource config: `admin-app/src/pages/ContentPage.tsx`, `admin-app/src/config/resources.ts`
