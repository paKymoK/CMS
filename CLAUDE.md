# CLAUDE.md

Instructions for Claude Code running on a developer machine in this repo (Claude Code loads this file automatically from the repo root). It tells Claude how to build, run and test, so it can check its own work instead of guessing.

Repo layout: `backend/` (Gradle, Spring WebFlux, Java 21), `frontend/` (pnpm workspace: `admin-app`, `website`, `packages/content-renderer`), `docs/`.

## Ground rules

- **Always build and test what you changed before saying it is done.** Run the commands below and report real output. If something cannot be run (no Docker, no DB), say so; do not claim it passes.
- **Frontend uses pnpm only** (Node >= 22, pnpm >= 10, run from `frontend/`). Never run `npm install` or `yarn` there; a guard blocks it, and a stray `package-lock.json` is gitignored.
- **Backend DB changes edit the init SQL, not new changesets** (`backend/<service>/src/main/resources/db/changelog/init/init-schema.sql` / `init-data.sql`). Liquibase checksums applied files, so after any edit the local DB must be recreated (`DROP DATABASE` / `CREATE DATABASE`, then restart the service). Say so whenever you edit one.
- **Do not add a Claude/model name to code, comments or commit messages** beyond the commit trailer the tooling adds.
- Keep changes minimal and match the surrounding code (naming, comment density, idiom). No drive-by refactors.
- Git: commit and push only when asked.

## Prerequisites (one-time)

| Tool | Version | Check |
|---|---|---|
| JDK | 21 (`JAVA_HOME` set) | `java -version` |
| Node | >= 22 | `node -v` |
| pnpm | >= 10 (repo pins `pnpm@10.28.0`) | `pnpm -v` |
| Docker | any recent | `docker version` |

Windows: use PowerShell. `corepack enable` then `corepack prepare pnpm@10.28.0 --activate` if pnpm is old. Use `gradlew.bat` (or `./gradlew` in Git Bash). IntelliJ may cache stale module links after a rename: File → Invalidate Caches. The package link `frontend/website/node_modules/@cms/content-renderer` is a junction; if installs misbehave, delete every `node_modules` and run `pnpm install` again.

## Backend

All commands from `backend/`.

```powershell
.\gradlew.bat build                       # compile + tests, all services
.\gradlew.bat :content-service:test       # one service's tests
.\gradlew.bat :content-service:test --tests "*PostServiceImplTest"   # one class
.\gradlew.bat :content-service:compileJava                           # fast compile check
```

Gradle needs internet on first run (Maven Central). If it fails with HTTP 429, retry later; that is a network problem, not a code problem.

Test reports: `backend/<service>/build/reports/tests/test/index.html`. When a test fails, read the report or the stack trace, fix the cause, re-run that test, then the whole service.

### Running the stack locally

Ports: discovery 8761, gateway 8080, auth 9000, media 8082, chat 8083, content 8086, Postgres 5432, Redis 6379. The admin app and website talk only to the gateway (`http://localhost:8080`).

1. Infrastructure (from `backend/infrastructure/`): `postgres` (databases `authentication`, `content`, ... are created by `init-databases.sql`; user/password `postgres`/`postgres`) and `redis`, each with `docker compose up -d` in its folder.
2. Start, in this order, each with `.\gradlew.bat :<service>:bootRun` in its own terminal: `discovery-service`, `auth-service`, `media-service`, `content-service`, `gateway-service` (add `chat-service` only if the assistant is needed).
3. Dev login: `admin` / `admin` (seeded by auth-service `init-data.sql`, local only).
4. Seed images come from media-service itself; content-service seed rows point at them. A fresh environment needs nothing else.

Quick checks: `http://localhost:8761` (Eureka shows registered services), `http://localhost:8086/actuator/health`.

## Frontend

All commands from `frontend/`.

```powershell
pnpm install                              # once, and after pulling dependency changes
pnpm dev:admin                            # admin app, http://localhost:5173
pnpm dev:website                          # website, http://localhost:3000
pnpm build                                # every package (tsc + vite build, next build)
pnpm lint                                 # eslint everywhere
pnpm --filter cms-admin-app exec tsc -b   # admin typecheck only
pnpm --filter landing-page exec tsc --noEmit   # website typecheck only
```

`--filter` takes the **package name** (`cms-admin-app`, `landing-page`, `@cms/content-renderer`), not the folder name.

Env: copy `frontend/admin-app/.env.example` to `.env` (set `VITE_BASE_URL=http://localhost:8080`). Without it the admin crashes at load (`VITE_BASE_URL` undefined). The website needs `NEXT_PUBLIC_CMS_API_BASE_URL` (the gateway URL): copy `frontend/website/.env.example` to `.env.local`.

After changing the website, delete `frontend/website/.next` if Turbopack serves stale output.

### Shared renderer (`frontend/packages/content-renderer`)

Pure React components (`PostArticle`, `CaseStudyArticle`) used by both the website pages and the admin preview iframe. Rules:

- No `next/*` or `next-intl` imports in the package; links and images come in through the `slots` prop.
- CSS is imported by **relative path** from the website (`app/globals.css`) and the admin preview (`src/preview/frame.css`), not through the package name (Tailwind failed to resolve it through Windows junctions).
- A change here affects both apps: typecheck and build **both** `admin-app` and `website`.

### Admin preview

The preview runs in an iframe (`/preview-frame.html`, a second Vite entry) fed by `postMessage` from the editor's unsaved state. To test it end to end: open an item in the admin, press Preview, switch Desktop / Tablet / Mobile.

## Verifying UI changes

Type checks do not prove a UI works. For any change to a page, editor or preview: start the app, open the page in a browser (Playwright is fine: `npx playwright-core` with the installed Chromium), exercise the change, and look at a screenshot. Check Desktop and Mobile widths for anything visual. If the backend is not running, intercept the gateway calls (`page.route("http://localhost:8080/**", ...)`) and serve fixture JSON; a valid-looking JWT (`{sub, exp, roles:["ADMIN"]}`) in `sessionStorage.access_token` gets past the admin login.

## Where things live

- Content model: single table `content_item` (jsonb `data`, `status`, `display_order`, optimistic `version`, audit trigger → `content_audit`). `site == locale`. Schema: `backend/content-service/src/main/resources/db/changelog/init/init-schema.sql`.
- Admin resource config (generic forms/lists): `frontend/admin-app/src/config/resources.ts`; generic full-page editor: `src/pages/ResourceEditorPage.tsx`; field components: `src/components/FieldInput.tsx`.
- Plans and status: `docs/posts-brd-vs-admin-app.md` (BRD gaps), `docs/page-model-plan.md` (one row = one page, proposal).

## Definition of done

1. It compiles/typechecks (`compileJava` / `tsc`), and lint is clean for the touched code.
2. Relevant tests pass; new behaviour has a test where the area already has tests.
3. UI changes were seen in a browser.
4. You state what you ran, what passed, and anything you could not run.
