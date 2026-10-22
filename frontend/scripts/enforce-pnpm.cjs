// Runs as `preinstall` in every frontend package: stops npm/yarn/bun from installing here.
// This workspace is pnpm-only (see pnpm-workspace.yaml); a stray `npm install` in a subfolder
// would create its own lockfile and node_modules and break the shared install.
const agent = process.env.npm_config_user_agent || "";
if (!agent.startsWith("pnpm/")) {
  console.error(
    "\n✖ This workspace uses pnpm. Do not run npm, yarn or bun here.\n" +
      "  From the frontend/ folder run:  corepack enable && pnpm install\n" +
      "  (see frontend/README.md)\n",
  );
  process.exit(1);
}
