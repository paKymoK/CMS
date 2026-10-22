# Plan: one row = one page (About, Industry, Service, Home)

Status: **finished, with Home deferred by decision.** Service, Industry and About are now one row per page, edited in one form and previewed in the admin; Case Studies and Posts already were. Home keeps its current rows and screens. This document keeps the original design (sections 1 to 9), marked with what happened to each part, and ends with the outcome (sections 10 to 12).

Ground rules for this plan:

- **All current data is test data.** The DB is dropped and recreated freely. No migration, no backward compatibility, no draft carry-over. Seed data is rewritten to the new shape.
- **A page is one row.** Content that belongs to the page is embedded in it. Reusable lists can instead come from a shared library, chosen per field by the editor (section 3).
- **Rows hold content only.** Layout, section order and styling live in code.

## Outcome at a glance

| Page | Result |
|---|---|
| Case study, Post | Already one row with an editor and preview before this plan. Not changed beyond the shared editor. |
| Service page | Done. Generic editor with sections and on/off switches, shared `ServicePage` renderer, preview. |
| Industry | Done. `industry_page`, many by slug, `/industries/[slug]`, renderer, preview. The hard-coded BFSI route and its four per-section types are gone. |
| About | Done. `about_page`, one per site (enforced), `GET /v1/about`, renderer, preview. Five per-section types and screens are gone. |
| Home | **Deferred.** Unchanged: per-section rows and screens, no page row, no preview. See section 11. |

**Done as planned:** recursive nested fields in the editor; collapsible sections with a jump list and per-section on/off (`hiddenSections`); version-conflict check on save; one public endpoint per page type; singleton rule for About (service check plus a partial unique index); GIN index on `data` dropped; the per-section types, Fields classes, screens and sidebar entries for Industry and About deleted; Service, Industry and About moved into `@cms/content-renderer`, which the website and the admin preview both render.

**Done differently:**
- *Checking for design drift* used a computed-style and layout digest of every element at 1440, 820 and 390 px plus a saved-HTML text diff, not screenshots. A control taken in the same session was needed when baselines drifted, because **the digest depends on the OS colour scheme** (the site has dark-mode styles; baselines taken in light mode do not match in dark mode). Compare within one theme, or compare geometry and text.
- *Library mode* was cut down to one case (About's growth figures from the Stats library): a stat list can take a `library` option, stored as `growthSource: "custom" | "library"` beside the embedded `growthStats`. It is not the generic `{mode, refs}` shape of section 3.
- *Industry solutions* are stored as nested groups (`solutionGroups[].slides[]`) instead of flat slides tagged with a group name, which used the recursive editor for real.
- *Stats and section intros on About* were embedded in the page row (a copy of the figures at first, then the library option above).
- *Case studies featured on a service page* stayed a list of slugs resolved by the server, as before; the "library switch with a picker" of decision 2 was not built.

**Not done, and why:**
- **Home** (phase 5): the globe, hero carousel, assistant band and about 1,800 lines of CSS made a full conversion cost more than it gains (section 11).
- **Redis cache for page responses**: measured, pages answer in about 8 to 14 ms and the website already caches for 60 s; not worth a new dependency and invalidation paths.
- **Generic library machinery**: the `{mode, refs}` shape, an item picker, dangling-reference handling and cache eviction. Nothing needs them without Home. The case study's own testimonial reference was not moved onto it either.
- **Pending revisions** (`draft_data`): editing a published page still goes live on save. Tied to the status model (G1 in `posts-brd-vs-admin-app.md`), which did not change.
- **Collapsed-by-default nested items**: list entries are not collapsible, so deep forms (About, Industry) are long. The sections and jump list are the only help.
- **Version-conflict check for Posts**: covers generic items and case studies; Posts have their own editor and endpoint and were left alone.
- **Search vector from title and summary only**, and **bounded audit retention** (section 5): not looked at.
- **Header menu and sitemap**: the header's Industries menu is still hard-coded in `content/nav.ts` (its Healthcare and Automotive links 404), and the sitemap lists only the home and About pages. A new industry row therefore serves a page but does not appear in the menu or the sitemap by itself. (The Services menu is already data-driven.)
- **Verification gaps**: the nested image-size check (`imageSpec` inside list entries, found by `findFieldByPath`) was only type-checked, never exercised in the browser; `next build` was never run (it would overwrite the dev server's `.next`); preview widths were checked at Desktop and Mobile, not Tablet; Case Studies were re-checked in the browser in phase 1 only, and afterwards only through type checks, builds and tests, although section 10 promised a check per phase.
- **Tidy-ups**: two empty folders remain (`components/about`, `industries/bfsi`) and `spotlessCheck` still fails on old `chat-service` files.

**Added beyond the plan:** generic sidebar highlighting for editor routes (service and industry editors did not highlight their entry); `tableOnly` and `noSlug` editor options; a singleton redirect page; "Duplicate" opening the editor prefilled; the editor saving only declared fields (loaded row metadata used to leak into `data`); an optional `alt` on the renderer's image slot; extra `Placeholder` tones; public-route carve-outs in both the gateway and core security.

## 1. Where we started

| Page | Then | Rows per page |
|---|---|---|
| Post, Case study | one row, own editor, preview | 1 |
| Service page | one row (`service_page`) holding every section in `data` | 1 (old drawer editor, no preview; `caseStudies` is a list of slugs) |
| About | `about_hero`, `about_intro`, `about_statement` ×2, `leader` ×N, `about_value` ×N, plus Stats and Section Intros | ~15 |
| Industry (BFSI) | `industry_hero`, `industry_solution` ×N, `industry_reason` ×N, `industry_story` ×N | ~15, route hard-coded to `bfsi` |
| Home | `hero_slide` ×N, `stat`, `service_card`, `office`, `feature_band`, `assistant_band`, `section_intro`, `testimonial`, awards, partners… | dozens, mixed with site-wide rows |

The website read About, Industry and Home out of one `HomeContentResponse.sections` map (type → rows). Service pages were the only ones served as a single page object (`ServiceCatalogService`). Today Service, Industry and About each have their own public endpoint; Home and the site chrome still use `/v1/home` and `sections`.

## 2. Target

- ✅ One `content_item` row per page; `data` holds every section of that page. *(Service, Industry, About. Home deferred.)*
- ✅ New types `about_page`, `industry_page`; `service_page` stays. ❌ `home_page` not created.
- ✅ One generic editor (`ResourceEditorPage`) draws the form from the page type's field config, with collapsible section groups.
- ✅ One renderer component per page in `@cms/content-renderer` (`ServicePage`, `IndustryPage`, `AboutPage`); the website and the admin preview render the same component. ❌ `HomePage`.
- ✅ Public endpoint per page type. The "library references resolved" part exists only for About's growth figures.
- ✅ Status, history and audit are per page.

## 3. Embedded or from library (editor's choice)

*What was built: only the stat-list case described under "Done differently". The text below is the design that was agreed and is kept for when Home or another page needs it.*

**Embedded only** (belongs to one page, nothing else reuses it): hero, intro, vision/mission, values, leaders, solutions, reasons, stories, capabilities, process, hero slides, feature/assistant bands.

**Embedded or from library** (reusable): stats, testimonials, offices, awards, certifications, partners, service cards, case study teasers. Each such field has a source switch in the editor:

- **Custom:** items are typed into the page row. *(Designed as `{ "mode": "embedded", "items": [...] }`; built as the plain list plus `growthSource: "custom"`.)*
- **From library:** the editor picks rows of that type by slug, or takes all rows of a type for global lists. *(Designed as `{ "mode": "referenced", "refs": [...] }`; built only as "all published rows of a type", with no picker.)*
- A "Convert to custom" action copies the library items into the page so they can be edited locally. ✅ Built; it reads the library at click time.

Rules:

- The backend resolves references before responding; the website and renderer never know which mode was used. ✅ (About growth figures)
- The embedded item shape is the library type's field config, so conversion is lossless. ✅ for stats.
- A missing or unpublished referenced row is skipped on render and flagged in the editor. ❌ Not built (nothing references single rows).
- Saving a library row evicts the cache of pages that reference that type. ❌ There is no cache.
- The preview resolves references from the unsaved editor state. ✅ (About growth figures)

**Not content of a page**:

- **Dynamic listings** (latest posts, the case study index): the website queries Posts / Case Studies at render time. Nothing is stored in the page row.
- **Site-wide chrome**: footer, footer nav, banners, service categories. Unchanged.

The library types (`testimonial`, `stat`, `office`, `award`, `partner`, `service_card`) stay. The per-section types for Industry and About were deleted with their screens; Home's (`hero_slide`, `feature_band`, `assistant_band`, `section_intro`) remain because Home was deferred.

## 4. Multiplicity

- `about_page`: **singleton per site**, enforced in the backend (service check plus the partial unique index `uq_content_item_singleton`), and the sidebar opens the editor directly. ✅ `home_page` not created.
- `industry_page`: **many, by slug** (`/industries/[slug]`). ✅
- `service_page`: many (unchanged). ✅

## 5. JSONB depth and performance

### How deep

Levels counted as containers (object or array) from the root of `data`:

| Level | What | Example |
|---|---|---|
| 1 | page root object | `data` |
| 2 | section object or list | `hero`, `stats[]`, `slides[]` |
| 3 | list item object | one slide, one stat, one leader |
| 4 | a sub-list inside an item | a group's `slides[]` |

**No hard depth cap**, and the editor and validators are recursive. In practice the deepest page is Industry at four levels (`solutionGroups[].slides[]`), validated with nested `@Valid` and tested.

### Performance

Depth itself costs almost nothing in Postgres. What matters is **size** and **whole-row behaviour**. Measured: the three pages are 4 to 6 KB each.

| Concern | Outcome |
|---|---|
| Row size | Small (4 to 6 KB). Home's rows would total about 12 KB if it were ever merged. |
| Reads | A public page answers in about 8 to 14 ms (p95 13 to 18 ms) straight from content-service. **No Redis cache built** (see Outcome). |
| Writes | Any edit rewrites the whole row; the audit trigger copies it too. Fine at this size. Audit retention is not bounded (not done). |
| GIN on `data` | ✅ Dropped: 0 scans, the largest index on the table, nothing queries by containment. |
| Search vector | Not changed (not done). |
| Partial updates (`jsonb_set`) | Not used; the editor saves the whole page. |
| Concurrent editors | ✅ The form sends the version it loaded and a stale save is a 409 with a plain message. Not applied to Posts. |

## 6. What changes, by layer

**Admin**
- ✅ Editor: per-section on/off (`hiddenSections`), collapsible groups with a jump list, singleton mode, recursive nested fields, nested `imageSpec` check (type-checked only, not run in the browser), version conflict handling, source switch for stat lists. ❌ Item picker for library rows; ❌ collapsed-by-default nested items.
- ✅ Sidebar: About is a single entry; Industry and Service are lists; the old Industry and About per-section screens are deleted. ❌ Home unchanged.
- ✅ Preview: one `PreviewPage` kind per page type, same iframe protocol. Site-wide sections (industries strip, latest highlights or insights, contact form) show a labelled stand-in.

**Content-service**
- ✅ Fields classes and nested validation for `industry_page` and `about_page`; the per-section classes deleted.
- ✅ Public endpoints `GET /v1/industries/{slug}` and `GET /v1/about` (allow-listed in core security and the gateway). ❌ `HomeContentResponse.sections` not deleted: Home and the site chrome still use it.
- ✅ Unique-per-site constraint for singleton types.
- ✅ `init-data.sql` and `init-schema.sql` rewritten to the new shape; GIN dropped. (The local DB was recreated each time.)

**Website**
- ✅ Service, Industry and About pages are thin wrappers: fetch one object, render the shared component with the site's `Link` and `Image` slots.
- ✅ Their components moved into the renderer package (with `service.css`, `industry.css`, `about.css` and a small `shared.css`). ❌ `components/home` stays.

## 7. Risks (and what happened)

1. **Design drift while moving components.** Guarded by the digest and text diff described above; Service, Industry and About ended with identical text and geometry at three widths. A client-component crash ("functions cannot be passed to Client Components") was caught on the first Service attempt and fixed by handing client views ready-made image elements.
2. **Home is the hard one.** Confirmed; deferred (section 11).
3. **Embedded copies of reusable content can drift.** Real for About's growth figures, which is why the stat library option exists. Not addressed for other copies.
4. **Editor length.** Sections and the jump list help; nested items are not collapsible (not done).
5. **Pending revisions** (`draft_data`): not done; still tied to the status model (G1 in `posts-brd-vs-admin-app.md`).

## 8. Phases

1. **Editor foundations** (done): sections, nested field types, nested `imageSpec`, version conflict, nested validation. Singleton mode was built with About.
2. **Service pages** (done): generic editor, `ServicePage` in the renderer, iframe preview.
3. **Industry** (done): `industry_page` with nested `solutionGroups[].slides[]`, public endpoint, `/industries/[slug]`, renderer, preview. BFSI migrated into the new row.
4. **About** (done): `about_page`, singleton, public endpoint, renderer, preview.
5. **Home** (deferred, not planned): see section 11.
6. **Cleanup** (done): the per-section types and screens were deleted with phases 3 and 4; the GIN index was dropped; the Redis cache was measured and not built.
7. **Library mode** (done, cut down): see "Done differently".

## 9. Decisions

1. **Embedding**: hybrid. *Outcome:* embedded everywhere; library only for About's growth figures.
2. **Featured case studies** use the library switch (by slug). *Outcome:* not built; they remain slugs on the service page.
3. **Industry**: many pages by slug. ✅
4. **Order**: Service, Industry, About, then cleanup and library mode; Home dropped from the order. Case Studies as the watched baseline: see section 10.
5. **Section on/off toggles** on every section-based page, stored as `hiddenSections`; not on Posts and Case Studies. ✅
6. **Depth**: no hard cap; recursive editor and validation. ✅
7. **Home deferred** (after investigation): cost too high for the gain. Revisit if a cheaper approach appears.
8. **Redis cache skipped** after measuring.
9. **Library mode cut down** to the one place that needed it.

Open: none for the finished scope.

## 10. Case Studies watch

Case Studies was the first page on this model (generic editor, `CaseStudyArticle` in the renderer, iframe preview), so it was meant to be the regression baseline, re-checked every phase.

What actually happened: it was exercised in the browser in phase 1 (the nested `results` list through the new recursive editor, and the version-conflict check with two tabs). In later phases it was covered only by the content-service tests (all of them, every phase), the type checks and the admin build; its preview and its website page were **not** re-opened. The version check and the shared renderer changes did touch it, and nothing in the tests failed, but the per-phase browser check was not done. The case study's testimonial reference was not moved onto the library mechanism.

## 11. Why Home is deferred

Investigated after phase 4. Home is ten sections (hero carousel, assistant band, what we do, testimonials, global delivery, extended workforce, case studies, insights, recognition, contact form) fed by about 50 rows (~12 KB), most of them Home-only (service cards, offices, awards, certifications, partners, stats). Only `testimonial` (case studies reference it) and the live case study / post lists are genuinely shared.

What makes a full conversion costly: the canvas globe (needs `d3-geo`, `topojson-client` and a data file fetched from the website's public folder), the hero carousel (autoplay and art-directed `next/image` sources), the assistant band (a live chat widget with `next-intl`, scroll-spy and the section dock), and about 1,800 lines of CSS to move. A preview would need stand-ins for most of it.

Considered split, if it is ever picked up: 5a one `home_page` row and editor with the website components unchanged (no design-drift risk); 5b move the sections that move cleanly plus a reduced preview; 5c the globe, carousel and assistant, one at a time. Decision: cost is too high for the gain, so Home stays as it is. `/v1/home` keeps serving Home, the header, the footer and the list pages' intros.

## 12. If this is picked up again

In rough order of value for effort:

1. Make the header's Industries menu data-driven and add service, industry and case study pages to the sitemap, so a new industry row needs no code.
2. Version-conflict check for Posts.
3. Collapse nested list items by default.
4. Re-check Case Studies in the browser (editor, preview, website page) once, since the shared pieces changed after phase 1.
5. Home 5a, if one editor for Home content is wanted.
6. The generic library machinery, only if a second place needs a library.
