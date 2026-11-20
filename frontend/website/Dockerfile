# Build from the frontend workspace root:
#   docker build -f website/Dockerfile -t cms-website \
#     --build-arg NEXT_PUBLIC_CMS_API_BASE_URL=https://api.example.com \
#     --build-arg SITE_URL=https://www.example.com .      (run inside frontend/)
# NEXT_PUBLIC_* values are inlined at build time, so each environment needs its own build.
# The gateway URL is also fetched server-side from inside the container, so it must be reachable from there.

# --- deps ---
FROM node:22-alpine AS deps
RUN corepack enable
WORKDIR /app
COPY package.json pnpm-workspace.yaml pnpm-lock.yaml ./
COPY scripts scripts
COPY website/package.json website/
COPY admin-app/package.json admin-app/
COPY packages/content-renderer/package.json packages/content-renderer/
RUN pnpm install --frozen-lockfile --filter landing-page...

# --- builder ---
FROM node:22-alpine AS builder
RUN corepack enable
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY --from=deps /app/website/node_modules ./website/node_modules
COPY --from=deps /app/packages/content-renderer/node_modules ./packages/content-renderer/node_modules
COPY . .

ARG NEXT_PUBLIC_CMS_API_BASE_URL
ARG SITE_URL
ENV NEXT_PUBLIC_CMS_API_BASE_URL=$NEXT_PUBLIC_CMS_API_BASE_URL \
    SITE_URL=$SITE_URL \
    NEXT_OUTPUT=standalone
RUN pnpm --filter landing-page build

# --- runner ---
FROM node:22-alpine AS runner
WORKDIR /app
ENV NODE_ENV=production
ENV PORT=3000
ENV HOSTNAME=0.0.0.0
# The standalone output mirrors the workspace layout, so the server lives under website/.
COPY --from=builder /app/website/.next/standalone ./
COPY --from=builder /app/website/.next/static ./website/.next/static
COPY --from=builder /app/website/public ./website/public
EXPOSE 3000
CMD ["node", "website/server.js"]
