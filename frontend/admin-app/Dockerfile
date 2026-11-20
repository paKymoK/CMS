# Build from the frontend workspace root (run inside frontend/):
#   docker build -f admin-app/Dockerfile -t cms-admin \
#     --build-arg VITE_BASE_URL=https://api.example.com \
#     --build-arg VITE_AUTH_SERVER=https://auth.example.com \
#     --build-arg VITE_REDIRECT_URI=https://admin.example.com/callback \
#     --build-context backend=../backend .
# The extra `backend` context supplies rich-text-embed-hosts.json, which src/lib/embeds.ts imports from content-service.
# All VITE_* values are inlined into the JS bundle at build time, so each environment needs its own build.

# --- deps ---
FROM node:22-alpine AS deps
RUN corepack enable
WORKDIR /app
COPY package.json pnpm-workspace.yaml pnpm-lock.yaml ./
COPY scripts scripts
COPY admin-app/package.json admin-app/
COPY website/package.json website/
COPY packages/content-renderer/package.json packages/content-renderer/
RUN --mount=type=cache,target=/root/.local/share/pnpm/store \
    pnpm install --frozen-lockfile --filter cms-admin-app...

# --- builder ---
FROM node:22-alpine AS builder
RUN corepack enable
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY --from=deps /app/admin-app/node_modules ./admin-app/node_modules
COPY --from=deps /app/packages/content-renderer/node_modules ./packages/content-renderer/node_modules
COPY package.json pnpm-workspace.yaml pnpm-lock.yaml ./
COPY admin-app admin-app
COPY packages packages
# embeds.ts imports ../../../../backend/... relative to /app/admin-app/src/lib
COPY --from=backend content-service/src/main/resources/rich-text-embed-hosts.json /backend/content-service/src/main/resources/rich-text-embed-hosts.json

ARG VITE_BASE_URL
ARG VITE_AUTH_SERVER
ARG VITE_CLIENT_ID=cms-admin
ARG VITE_REDIRECT_URI
ARG VITE_SCOPES="openid profile offline_access"
ARG VITE_WEBSITE_ORIGIN
ENV VITE_BASE_URL=$VITE_BASE_URL \
    VITE_AUTH_SERVER=$VITE_AUTH_SERVER \
    VITE_CLIENT_ID=$VITE_CLIENT_ID \
    VITE_REDIRECT_URI=$VITE_REDIRECT_URI \
    VITE_SCOPES=$VITE_SCOPES \
    VITE_WEBSITE_ORIGIN=$VITE_WEBSITE_ORIGIN

RUN pnpm --filter cms-admin-app build

# --- runner ---
FROM nginxinc/nginx-unprivileged:1.27-alpine AS runner
COPY admin-app/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/admin-app/dist /usr/share/nginx/html
EXPOSE 8080
HEALTHCHECK --interval=15s --timeout=3s --start-period=5s --retries=5 \
    CMD wget -qO /dev/null http://127.0.0.1:8080/ || exit 1
