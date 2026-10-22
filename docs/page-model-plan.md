# Plan: one row = one page (About, Industry, Service, Home)

Status: **proposal, nothing implemented yet.** Case Studies and Posts already follow this (one `content_item` row is one page, edited in one form, previewed in the iframe).

## 1. Where we are

| Page | Today | Rows per page |
|---|---|---|
| Post, Case study | one row, own editor, preview | 1 |
| Service page | one row (`service_page`) holding every section in `data` | 1 (editor is the old drawer, no preview) |
| About | `about_hero`, `about_intro`, `about_statement` ×2, `leader` ×N, `about_value` ×N, plus Stats and Section Intros | ~15, on 5 screens |
| Industry (BFSI) | `industry_hero`, `industry_solution` ×N, `industry_reason` ×N, `industry_story` ×N | ~15, on 4 screens, route hard-coded to `bfsi` |
| Home | `hero_slide` ×N, `stat`, `service_card`, `office`, `feature_band`, `assistant_band`, `section_intro`, `testimonial`, awards, partners… | dozens, mixed with site-wide rows |

The website reads About, Industry and Home out of one `HomeContentResponse.sections` map (type → rows) and each page picks what it needs. Service pages are the only ones served as a single page object (`ServiceCatalogService`).

**Service pages are the model to copy.** Their row already has hero, capabilities, process, stories, reasons, hidden sections and SEO, in fixed layout order.

## 2. Target

- One `content_item` row per page. `data` holds every section of that page.
- New content types: `about_page`, `industry_page`, `home_page`. `service_page` stays.
- One generic editor (`ResourceEditorPage`) draws the form from the page type's field config, with sections.
- One renderer component per page in `@cms/content-renderer` (`AboutPage`, `IndustryPage`, `ServicePage`, `HomePage`). The website and the admin preview render the same component, so the preview is the real design.
- Layout and section order stay fixed in code (designer-owned). Editors change content only. Optional per-section "hide" toggles, as service pages have today.
- Status, history and audit become per page (today they are per section row).

## 3. Embedded vs referenced

A rule for what goes inside the page row and what stays its own row:

- **Embedded** (only exists on that page): hero, intro, vision/mission, values, leaders, solutions, reasons, stories, capabilities, process, hero slides, feature/assistant bands, section intros used by that page.
- **Referenced** (reused, or has its own lifecycle): stats, testimonials, case studies, posts, offices, awards, certifications, partners, service cards, industry cards. Pages pick them by id/slug (`content-ref`), like case study → testimonial today.
- **Site-wide, not a page**: footer, footer nav, banners, service categories. They stay as they are.

## 4. Multiplicity

- `home_page`, `about_page`: **singleton per site.** Sidebar opens the editor directly; "create" only appears when the site has none. Enforced in the backend (unique per site + type), not just hidden in the UI.
- `industry_page`: **many, by slug** (`/industries/[slug]`). Fixes the hard-coded `bfsi` route and makes the next industry a new row, not code.
- `service_page`: many (unchanged).

## 5. What changes, by layer

**Admin**
- Editor: field groups (collapsible sections with a jump list), singleton mode, nested field types inside object lists (stat list, select, number, boolean), nested `imageSpec` check (hero slide sizes).
- Sidebar: About / Home become single entries; Industry and Service become lists; old per-section screens are deleted.
- Preview: one `PreviewPage` kind per page type, same iframe protocol.

**Content-service**
- Fields classes and validation for the three new types; delete `AboutHeroFields`, `IndustryHeroFields`, etc.
- Public endpoint per page type returning one object with referenced rows resolved (generalising `ServiceCatalogService.getPage`). `HomeContentResponse.sections` shrinks to what the home page really needs.
- Unique-per-site constraint for singleton types.
- `init-data.sql` rewritten to the new shape (DB is test data, nuke accepted). Editing init SQL breaks Liquibase checksums on existing DBs.

**Website**
- Pages become thin wrappers: fetch one object, render the shared component with the site's `Link`/`Image` slots (same pattern as posts and case studies).
- Move current components (`components/about`, `industry`, `service`, `home`) into the renderer package.

## 6. Risks (the "careful" part)

1. **Design drift while moving components.** Guard: before touching a page, capture baseline screenshots of the current website page at 3 widths from fixture data; after the move, diff them. A phase does not ship with a visible difference.
2. **Home is the hard one.** Globe, hero carousel, section dock, scroll effects, `next/image`, client-only code. These may not move into a shared package cleanly (dynamic imports, SSR). Home goes last; its preview may need to stay a reduced render, decided then.
3. **Backend cannot be compiled in this sandbox** (Maven Central 429). Backend changes are verified by you locally with `./gradlew :content-service:test`.
4. **Data loss in the rewrite of seed data.** Only seed rows; fine now, but any real content must be exported first.
5. **Big rows, last-write-wins.** A page row is large and several editors could collide. The row already has an optimistic `version`; the editor should send it and show a conflict message. Pending revisions (`draft_data`, so editing a live page does not go live on save) matter more once whole pages are rows; plan it with the status model (G1 in `posts-brd-vs-admin-app.md`).
6. **Nested forms get long.** Home would have ~15 sections. Groups with a jump list, collapsed by default.

## 7. Phases

0. **Decisions** (section 8).
1. **Editor foundations:** field groups, singleton, nested field types, nested `imageSpec`. Tested on service pages.
2. **Service pages:** move to the generic editor, `ServicePage` into the renderer, add the iframe preview. Lowest risk, proves the page-renderer pattern. No data change.
3. **Industry:** `industry_page` type, seed data, `/industries/[slug]`, renderer, preview.
4. **About:** `about_page`, same steps.
5. **Home:** `home_page`, last; includes the home-specific rendering decision.
6. **Cleanup:** delete old content types, Fields classes, screens, sidebar items and the unused `sections` plumbing.

Each phase is its own commit pushed to `main`, with baseline-vs-after screenshots for the website page it touches.

## 8. Decisions needed

1. **Embedded vs referenced** (section 3): accept this split? In particular leaders, values and hero slides embedded, stats and testimonials referenced.
2. **Industry**: many pages by slug, or keep BFSI as a single page for now?
3. **Order**: Service → Industry → About → Home, or a different order?
4. **Hide-section toggles** on every page type (as service pages have), or only where designers asked for them?
5. **Singleton rule**: Home and About exactly one per site, enforced in the backend. OK?
