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
| 3 | Statuses: Draft / Preview / Publish / Schedule / Archive | Only `DRAFT` / `PUBLISHED` (DB CHECK), plus an `active` flag | 🟡 |
| 4 | Headings, bold/italic, lists, links, alignment | Tiptap toolbar: H2–H4, B/I/U/S, lists, link, align | ✅ |
| 5 | Image: alt, caption, size, alignment | `figure` node with float left/right/full and caption. Alt is hard-coded `""` on insert | 🟡 |
| 6 | Video via URL (YouTube/Vimeo), responsive | `embed` node, allow-listed hosts only | ✅ |
| 7 | Button / CTA | `LinkButton` (link rendered as button) | ✅ |
| 8 | Blockquote, table, divider | All present; table has row/col/merge/header controls | ✅ |
| 9 | Table of contents (H1→H3) | Outline panel built from H2 only; no TOC block | 🟡 |
| 10 | Undo / Redo | Toolbar | ✅ |
| 11 | Preview before publish (desktop/tablet/mobile) | Token-based preview on the real site; no device toggle | 🟡 |
| 12 | Search-snippet preview | none | ❌ |
| 13 | One previous version + restore | Full history in `content_audit` (before/after snapshots), `HistoryDrawer`, RESTORE action | ✅ (exceeds BRD) |
| 14 | Audit log | DB trigger records CREATE/UPDATE/PUBLISH/UNPUBLISH/DELETE/RESTORE, including direct SQL | ✅ |
| 15 | Multi-site, per-site access | `site` table (one row per locale), `CmsAdminGuard.requireSiteAccess` | ✅ |
| 16 | Featured flag | `featured` | ✅ |
| 17 | Auto-fit layout | Four layouts: default, focused, wide, landing | ✅ |

Extras the app has that the BRD does not mention: callout, two-column layout, accordion, inline code and code block, text colour, HTML source mode, save-as-template (`post_template`), word count and read time, activity feed, full-text and trigram search.

## 2. Gaps

### G1. Scheduling and Archive
- DB `CHECK (status IN ('DRAFT','PUBLISHED'))`, so no `SCHEDULED` or `ARCHIVED`.
- No publish or unpublish date columns and no scheduler.

### G2. Categories and Tags management
- `category` is a free-text string; `tags` is a string array inside `data`.
- No Category or Tag tables, no list pages, no parent/child, no active/inactive, no per-category post counts, no bulk actions.
- Editor category field is a plain text input defaulting to `"insight"`.

### G3. Post list view
- Posts use the generic `ContentPage`: ID, title, a few columns, status, active, order.
- No bulk actions, no search, no filter (status/category/site), no sort by updated date or updater, no language column.

### G4. SEO
- `PostFields` has no meta title, meta description, noindex, social image or redirect.
- No head/body/footer code-injection field (schema, FAQ).
- No SEO checklist warning.

### G5. Per-media metadata in a post
- Alt text on inserted images is hard-coded `alt=""`.
- No title, description or size picker. Only the cover image uses the media picker.

### G6. Preview
- No desktop/tablet/mobile toggle.
- No Google search-snippet preview.

### G7. Editor blocks missing
- Share buttons block, table of contents block, form block (only a raw iframe embed), spacer block.
- No drag-and-drop to reorder blocks, no "duplicate block".
- It is a Tiptap text editor, not a block builder.

### G8. Delete rule and Trash
- `PostServiceImpl.delete` calls `contentItemWriter.delete` directly, with no status check.
- BRD: live content must be unpublished or archived before delete; wants Trash with restore.
- Today a deleted item is recoverable only via the audit trail ("Create new from this version").

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

1. **Status model:** `DRAFT / PUBLISHED / SCHEDULED / ARCHIVED`, `publish_at` / `unpublish_at`, scheduler. Also enables the delete rule and Trash (G1, G8).
2. **Category and Tag tables** per site, with posts referencing them (G2).
3. **SEO fields** in `PostFields`, DTOs and editor sidebar (G4).
4. **Dedicated post list page** with search, filters, bulk actions (G3).
5. Translated-draft action (G10), after G1 and G2 so the copy carries the right status and taxonomy.
6. Editor blocks and preview improvements (G5–G7).
7. Roles check (G9).

## 5. Open questions for the critical review

- Is `active` still needed once `status` covers Scheduled/Archived?
- Do Categories and Tags need a tenant-wide or per-site scope, and are they translated per site?
- Translation engine for G10: which service, who pays, and is the call synchronous or a background job?
- Does the translated draft keep an optional "translated from" reference, or stay fully independent?
- Does G10 need permission rules (who may create drafts in other sites)?
- Is the Tiptap editor acceptable, or does the BRD "block" language require a block builder?
- Where should the scheduler live (content-service job vs external)?

## Key files

- Schema: `backend/content-service/src/main/resources/db/changelog/init/init-schema.sql`
- Post fields: `backend/content-service/src/main/java/com/takypok/contentservice/model/content/types/PostFields.java`
- Post service: `backend/content-service/src/main/java/com/takypok/contentservice/service/impl/PostServiceImpl.java`
- Sanitizer: `backend/content-service/src/main/java/com/takypok/contentservice/util/RichTextSanitizer.java`
- Editor page: `admin-app/src/pages/PostEditorPage.tsx`
- Rich text editor: `admin-app/src/components/RichTextEditor.tsx`, `admin-app/src/components/rte/extensions.ts`
- List page and resource config: `admin-app/src/pages/ContentPage.tsx`, `admin-app/src/config/resources.ts`
