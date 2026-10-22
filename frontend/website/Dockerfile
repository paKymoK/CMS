# Build from the frontend workspace root:
#   docker build -f website/Dockerfile -t cms-website .      (run inside frontend/)
# Needs `output: "standalone"` enabled in next.config.ts (it is commented out while deploying to Vercel).

# --- deps ---
FROM node:22-alpine AS deps
RUN corepack enable
WORKDIR /app
COPY package.json pnpm-workspace.yaml pnpm-lock.yaml ./
COPY website/package.json website/
COPY admin-app/package.json admin-app/
RUN pnpm install --frozen-lockfile --filter landing-page...

# --- builder ---
FROM node:22-alpine AS builder
RUN corepack enable
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY --from=deps /app/website/node_modules ./website/node_modules
COPY . .
RUN pnpm --filter landing-page build

# --- runner ---
FROM node:22-alpine AS runner
WORKDIR /app
ENV NODE_ENV=production
ENV PORT=3000
# The standalone output mirrors the workspace layout, so the server lives under website/.
COPY --from=builder /app/website/.next/standalone ./
COPY --from=builder /app/website/.next/static ./website/.next/static
COPY --from=builder /app/website/public ./website/public
EXPOSE 3000
CMD ["node", "website/server.js"]
