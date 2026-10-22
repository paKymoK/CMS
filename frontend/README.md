# frontend

pnpm workspace for every web app and shared package. The Java services live in `../backend`.

| Path | What |
|---|---|
| `admin-app/` | CMS admin (Vite + React + antd) |
| `website/` | Public marketing site (Next.js) |
| `packages/` | Shared packages, imported as `workspace:*` |

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
