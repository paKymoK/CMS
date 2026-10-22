# frontend

pnpm workspace for every web app and shared package. The Java services live in `../backend`.

| Path | What |
|---|---|
| `admin-app/` | CMS admin (Vite + React + antd) |
| `website/` | Public marketing site (Next.js) |
| `packages/content-renderer/` | The post and case-study pages, drawn by both the website and the admin preview |

## Setup

Node 22 and pnpm 10 (`corepack enable` picks the pinned version).

```bash
cd frontend
pnpm install
pnpm dev:admin      # admin-app
pnpm dev:website    # website
pnpm build          # build everything
```

Run a script in one app with `pnpm --filter cms-admin-app <script>` or `pnpm --filter landing-page <script>`.

The website image is built from this folder: `docker build -f website/Dockerfile -t cms-website .`

## pnpm only

Do not run `npm install`, `yarn` or `bun` anywhere under `frontend/`. Each package sets `engine-strict=true` (`.npmrc`) and an `engines` entry that npm and yarn reject, so they stop before touching `node_modules` or creating a lockfile. `scripts/enforce-pnpm.cjs` (the `preinstall` script) is a second check. Stray `package-lock.json` / `yarn.lock` files are git-ignored.

## Shared packages

`packages/content-renderer` (`@cms/content-renderer`) holds the post and case-study detail pages (`PostArticle`, `CaseStudyArticle`), their styles (`theme.css`, `post.css`) and the brand tokens. The website renders it for the live page; the admin renders the same component inside an iframe (`admin-app/preview-frame.html`) for the editors' Preview buttons, from the unsaved form state. Change a page's look in the package and both follow.

A new package needs its own `.npmrc` (`engine-strict=true`), the `engines` entry and the `preinstall` guard, as in `packages/content-renderer/package.json`. Packages ship TypeScript source; the website lists them in `transpilePackages` in `next.config.ts`.
