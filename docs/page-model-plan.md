# Plan: one row = one page (About, Industry, Service, Home)

Status: **proposal, nothing implemented yet.** Case Studies, Posts and Service pages already follow this (one `content_item` row is one page, edited in one form).

Ground rules for this plan:

- **All current data is test data.** The DB is dropped and recreated freely. No migration, no backward compatibility, no draft carry-over. Seed data is rewritten to the new shape.
- **A page is one row.** Content that belongs to the page is embedded in it. Reusable lists can instead come from a shared library, chosen per field by the editor (section 3).
- **Rows hold content only.** Layout, section order and styling live in code.

## 1. Where we are

| Page | Today | Rows per page |
|---|---|---|
| Post, Case study | one row, own editor, preview | 1 |
| Service page | one row (`service_page`) holding every section in `data` | 1 (old drawer editor, no preview; `caseStudies` is a list of slugs) |
| About | `about_hero`, `about_intro`, `about_statement` ×2, `leader` ×N, `about_value` ×N, plus Stats and Section Intros | ~15 |
| Industry (BFSI) | `industry_hero`, `industry_solution` ×N, `industry_reason` ×N, `industry_story` ×N | ~15, route hard-coded to `bfsi` |
| Home | `hero_slide` ×N, `stat`, `service_card`, `office`, `feature_band`, `assistant_band`, `section_intro`, `testimonial`, awards, partners… | dozens, mixed with site-wide rows |

The website reads About, Industry and Home out of one `HomeContentResponse.sections` map (type → rows). Service pages are the only ones served as a single page object (`ServiceCatalogService`).

## 2. Target

- One `content_item` row per page; `data` holds every section of that page.
- New types: `about_page`, `industry_page`, `home_page`. `service_page` stays.
- One generic editor (`ResourceEditorPage`) draws the form from the page type's field config, with collapsible section groups.
- One renderer component per page in `@cms/content-renderer`; the website and the admin preview render the same component.
- Public endpoint per page type returns the page with any library references resolved into plain items, so the website always sees one shape.
- Status, history and audit are per page.

## 3. Embedded or from library (editor's choice)

**Embedded only** (belongs to one page, nothing else reuses it): hero, intro, vision/mission, values, leaders, solutions, reasons, stories, capabilities, process, hero slides, feature/assistant bands.

**Embedded or from library** (reusable): stats, testimonials, offices, awards, certifications, partners, service cards, case study teasers. Each such field has a source switch in the editor:

- **Custom:** items are typed into the page row. Stored as `{ "mode": "embedded", "items": [...] }`.
- **From library:** the editor picks rows of that type by slug, or takes all rows of a type for global lists (partners, awards, offices). Stored as `{ "mode": "referenced", "refs": ["slug-a", "slug-b"] }`.
- A "Convert to custom" action copies the library items into the page so they can be edited locally.

Rules:

- The backend resolves references before responding. The website and renderer receive plain items and never know which mode was used.
- **The embedded item shape is exactly the library type's field config**, so conversion is lossless and the renderer has one code path.
- A missing or unpublished referenced row is skipped on render and flagged with a warning in the editor.
- Saving a library row evicts the cache of every page that references that type. Pages in embedded mode are unaffected.
- The preview resolves references from the unsaved editor state (one extra fetch).

**Not content of a page**:

- **Dynamic listings** (latest posts, the case study index): the website queries Posts / Case Studies at render time. Nothing is stored in the page row.
- **Site-wide chrome**: footer, footer nav, banners, service categories. Unchanged. A service page stores its category slug.

The library types (`testimonial`, `stat`, `office`, `award`, `partner`, `service_card`) stay. Only the per-page section types (`about_*`, `industry_*`, `leader`, `hero_slide`, `feature_band`, `assistant_band`, `section_intro`) and their screens are deleted.

## 4. Multiplicity

- `home_page`, `about_page`: **singleton per site**, enforced in the backend (unique per site + type), not just hidden in the UI. Sidebar opens the editor directly.
- `industry_page`: **many, by slug** (`/industries/[slug]`). Fixes the hard-coded `bfsi` route.
- `service_page`: many (unchanged).

## 5. JSONB depth and performance

### How deep

Levels counted as containers (object or array) from the root of `data`:

| Level | What | Example |
|---|---|---|
| 1 | page root object | `data` |
| 2 | section object or list | `hero`, `stats[]`, `slides[]` |
| 3 | list item object | one slide, one stat, one leader |
| 4 | a sub-list inside an item | a slide's `ctas[]`, an office's `lines[]` |

Service pages today reach level 3 (`capabilities[] → {title, body, image}`). **Home needs level 3 for almost everything and level 4 for a handful of items.** Rule: **no hard depth cap.** Depth is not a performance problem, so the model allows any nesting a page needs. Consequence: the editor and the validators must be **recursive** (a field of type object / object-list can contain any field type, including more object-lists), not hard-coded to a fixed number of levels. Home is expected to need 3 to 4 in practice; deeper is allowed when a design really calls for it.

Estimate, not measured: Home is ~15 sections and roughly 150–300 leaf fields. Counted from the component list and the service page seed, not from a real Home row.

### Performance

Depth itself costs almost nothing in Postgres: jsonb is parsed once into a binary tree and keys are looked up by binary search. What matters is **size** and **whole-row behaviour**:

| Concern | Effect | Handling |
|---|---|---|
| Row size | A service page is ~5 KB; Home with everything embedded is plausibly 30–80 KB. Over ~2 KB Postgres compresses and moves the value to TOAST. Images are URLs, not blobs, so this stays modest. | Fine. Reading one 50 KB row by `(site, type)` is one index lookup and cheaper than today's 30+ row reads. |
| Reads | Whole value is detoasted on every read, even if one field is needed. | Always read the whole page (we do). Cache the public response in Redis, evict on save/publish. |
| Writes | Any edit rewrites the whole row; the audit trigger also copies the whole row into `content_audit`. 50 KB × every save. | Fine for an editor-driven CMS. Keep audit retention bounded. |
| GIN on `data` (`idx_content_item_data`) | Indexes every key and value; on big rows it bloats and slows writes. Page rows are fetched by `(site, type, slug)`, never by searching inside `data`. | Drop it or switch to `jsonb_path_ops` and keep it only if a query needs it. Keep the composite btree indexes. |
| Search (`search_vec`) | Built from the row; large rows make a noisy vector. | Build it from title + summary fields only. |
| In-DB partial updates (`jsonb_set`) | Path gets long and fragile with depth. | Do not do partial updates; the editor saves the whole page. |
| Concurrent editors | Last-write-wins on a big row. | Send the existing optimistic `version`; show a conflict message. Phase 1. |

Bottom line: nesting depth and tens of KB are well within what jsonb handles easily. The real costs are **editor length** (solved with groups and a jump list), **recursive editor and validation code** (built once in phase 1), and keeping forms usable when nesting gets deep (nested items collapse by default).

## 6. What changes, by layer

**Admin**
- Editor: per-section on/off switch (writes `hiddenSections`), source switch (Custom / From library) with a picker, on reusable list fields (phase 7); collapsible field groups with a jump list, singleton mode, recursive nested field types (object list containing any field type, stat list, select, number, boolean), nested `imageSpec` check, version conflict handling.
- Sidebar: About / Home become single entries; Industry and Service are lists; old per-section screens are deleted.
- Preview: one `PreviewPage` kind per page type, same iframe protocol.

**Content-service**
- Fields classes + validation for the three new types (nested, any depth, validated recursively); delete the per-section Fields classes.
- Public endpoint per page type returning the page with library references resolved; delete `HomeContentResponse.sections`.
- Resolver for `{mode, refs}` fields (phase 7); cache eviction by type.
- Unique-per-site constraint for singleton types.
- `init-data.sql` / `init-schema.sql` rewritten to the new shape (drop GIN on `data`). **Edit init SQL, then recreate the local DB.**

**Website**
- Pages become thin wrappers: fetch one object, render the shared component with the site's `Link`/`Image` slots.
- Move current components (`components/about`, `industry`, `service`, `home`) into the renderer package.

## 7. Risks

1. **Design drift while moving components.** Before touching a page, capture baseline screenshots of the current page at 3 widths from fixture data; diff after. A phase does not ship with a visible difference.
2. **Home is the hard one.** Globe, hero carousel, section dock, scroll effects, `next/image`, client-only code may not move into a shared package cleanly (dynamic imports, SSR). Home goes last; its preview may stay a reduced render, decided then.
3. **Embedded copies of reusable content can drift.** That is why the library mode exists; editors choose per field.
4. **Editor length.** Home is ~15 sections: groups collapsed by default, jump list.
5. **Pending revisions** (`draft_data`, so editing a live page does not go live on save) are not required for this plan; plan them with the status model (G1 in `posts-brd-vs-admin-app.md`).

## 8. Phases

1. **Editor foundations:** groups, singleton, nested field types, nested `imageSpec`, version conflict, Fields validation for nested data. Tested on service pages.
2. **Service pages:** generic editor, `ServicePage` in the renderer, iframe preview, embed the case study teasers. Lowest risk; proves the pattern.
3. **Industry:** `industry_page`, seed data, `/industries/[slug]`, renderer, preview.
4. **About:** `about_page`, same steps.
5. **Home:** `home_page`, last; includes the home-specific rendering decision.
6. **Cleanup:** delete the per-page section types, Fields classes, screens, sidebar items; drop the GIN index; add the Redis cache for page responses.
7. **Library mode:** `{mode, refs}` fields, resolver, picker UI, "Convert to custom", dangling-reference handling, preview resolve, cache eviction. Embedded mode works without it, so earlier phases do not wait.

Each phase is its own commit, with baseline-vs-after screenshots for the website page it touches. Build and test per `CLAUDE.md`.

## 9. Decisions

Resolved:

1. **Embedding**: hybrid. Editor chooses Custom or From library per reusable field; embedded first, library mode in phase 7.
2. **Featured case studies** use the library switch (picked by slug, resolved at read time), not a stored snapshot. "Latest posts" style lists stay live queries.
3. **Industry**: many pages by slug (`/industries/[slug]`), not a single hard-coded BFSI page.
4. **Order**: Service → Industry → About → Home, then cleanup; library mode (phase 7) last. Case Studies is the reference page and is watched in every phase (section 10).
5. **Section on/off toggles** on every section-based page (Service, Industry, About, Home): these pages have no rich-text body, so an editor cannot remove a section by deleting text. Stored the way service pages already do it, `hiddenSections: ["sectionKey", ...]` in `data`; the renderer skips hidden sections. Not offered on Posts and Case Studies (they have a TipTap body).

6. **Depth**: no hard cap. Editor and validation are recursive, so any nesting works. Deep nesting is a form-usability concern (collapsed by default), not a storage or performance one.

Open: none. Next step is Phase 1.

## 10. Case Studies watch

Case Studies is the first page already on this model (generic editor, `CaseStudyArticle` in the renderer, iframe preview), so it is the regression baseline for everything above.

- **Every phase re-checks it** before it ships: open a case study in the admin, edit, preview at Desktop / Tablet / Mobile, save, and compare the website page `case-studies/[slug]` with its baseline screenshot.
- **Phase 1 changes the shared editor** (groups, nested types, version conflict). Case Studies must keep working unchanged; it is the first thing tested after each editor change.
- **Phase 2+ renderer moves** touch `content-renderer`: build and typecheck both `admin-app` and `website`, since `CaseStudyArticle` shares the package and CSS.
- **Backend:** run the case study tests (`CaseStudyService` and its fields class) with every content-service change.
- **Phase 7:** Case Studies is both a library type (featured case studies on other pages resolve to it) and a consumer of the library (its testimonial reference). Its own testimonial reference should be moved onto the same `{mode, refs}` field so the pattern is proven on the existing page first.
- Anything learned while building Case Studies (editor gaps, awkward field types, preview quirks) is fed back into the plan before the next page starts.
