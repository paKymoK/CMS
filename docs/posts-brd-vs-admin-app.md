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
| 3 | Statuses: Draft / Preview / Publish / Schedule / Archive | `DRAFT` / `PUBLISHED` / `ARCHIVED` for posts (DB CHECK; other content types stay Draft and Published, enforced by the server), plus an `active` flag. Archive is done; Schedule is parked (G1). "Preview" is read as the preview action (row 11), not a stored status: the BRD's workflow section lists only Draft, Published, Scheduled and Unpublished/Archived. The remaining gap is Scheduled (G1) | 🟡 |
| 4 | Headings, bold/italic, lists, links, alignment | Tiptap toolbar: H2–H4, B/I/U/S, lists, link, align | ✅ |
| 5 | Image: alt, caption, size, alignment | `figure` node with float left/right/full and caption. Alt is hard-coded `""` on insert | 🟡 |
| 6 | Video via URL (YouTube/Vimeo), responsive | `embed` node, allow-listed hosts only | ✅ |
| 7 | Button / CTA | `LinkButton` (link rendered as button) | ✅ |
| 8 | Blockquote, table, divider | All present; table has row/col/merge/header controls | ✅ |
| 9 | Table of contents (H1→H3) | Outline panel built from H2 only; no TOC block | 🟡 |
| 10 | Undo / Redo | Toolbar | ✅ |
| 11 | Preview before publish (desktop/tablet/mobile) | Preview button opens the page in an iframe drawn by the same renderer as the website, from the **unsaved** editor state, with Desktop, Tablet (820 px) and Mobile (390 px) widths (`PreviewPanel`). Works for Posts, Case Studies, Service, Industry and About pages. The site header, footer and contact form are not part of the preview, and Home has none. The earlier token-based preview was removed | ✅ |
| 12 | Search-snippet preview (BRD: the post as it looks in a search result, mobile and desktop, with an example image) | none. The fields it needs now exist (G4), so it can be built next | ❌ |
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
- **Schedule: not done, parked** (see below). No `SCHEDULED` status and no `publish_at` / `unpublish_at` yet.

**Parked by decision: scheduling and the `active` flag are left for later while the owner thinks them over.** The analysis so far, so nothing has to be rediscovered:

- **Safest scheduling design: a background check that flips the status.** Every place that decides what the public sees (about 15, all exact matches on `'PUBLISHED'`) already ignores any other status, so Scheduled and Archived are invisible with no read path changed. A check every ~30 s flips `SCHEDULED` to `PUBLISHED` when `publish_at` is due (and `PUBLISHED` to `ARCHIVED` at `unpublish_at`); after downtime it catches up on overdue items. The media service already has a `@Scheduled` sweeper to copy, the auditing code already credits "System" when nobody is logged in, and the version check makes two servers flipping the same row harmless. The alternative, computing visibility from the clock on every read, needs a time condition in all ~15 places, and missing one would show an unpublished post early.
- **Timing:** about 30 s plus the website's 60 s page cache, so "09:00" means roughly 09:00 to 09:02.
- **Statuses (Posts first):** Draft, Scheduled, Published, Archived. The BRD lists "Unpublished/Archived" as one state. "Preview" stays the preview action. Other content types keep Draft and Published, enforced by the server so they cannot get stuck as Scheduled.
- **Database:** widen the status check, add `publish_at` / `unpublish_at` with sanity checks and small indexes. **The audit trigger only knows PUBLISH and UNPUBLISH** (anything else it logs as UNPUBLISH), so Schedule and Archive actions must be added, and the two times recorded in the snapshots. Restoring an old version that was Scheduled for a time now past should come back as Draft.
- **Delete rule (G8 lite):** only Draft or Archived items can be deleted. A real Trash is a separate decision.
- **Editor:** the Draft/Published switch becomes Save draft, Publish now, Schedule… (time, optional unpublish time), Archive / Move to Draft; the status labels in the list and Activity need the new states.
- **Open decisions:** (1) the background check (recommended) or visibility decided on read; (2) the time zone of the picker: the editor's browser zone, shown clearly and stored as UTC (recommended), or a zone per site (needs a new setting); (3) keep `active` as it is for now (other content types rely on it) or fold it into the status; (4) skip Trash and let Archived plus the delete rule cover it (recommended), or add a Trash status and a purge job; (5) Posts only first (recommended).
- **Suggested split when picked up:** part 1 statuses, rules, delete rule and editor labels (no time handling); part 2 the schedule columns, the check and the picker.

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
- **Missing:** the search-result preview (row 12): title, URL and description as a search engine would show them, mobile and desktop, with the image. It depends on G4 for Posts (meta title and description), and it is not built for any page type.
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

1. **Status model:** `DRAFT / PUBLISHED / SCHEDULED / ARCHIVED`, `publish_at` / `unpublish_at`, scheduler. Also enables the delete rule and Trash (G1, G8). *(Archive and the delete rule are done; scheduling is parked: see G1.)*
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
