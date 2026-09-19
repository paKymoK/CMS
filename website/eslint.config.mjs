import { defineConfig, globalIgnores } from "eslint/config";
import nextVitals from "eslint-config-next/core-web-vitals";
import nextTs from "eslint-config-next/typescript";

const eslintConfig = defineConfig([
  ...nextVitals,
  ...nextTs,
  // Override default ignores of eslint-config-next.
  globalIgnores([
    // Default ignores of eslint-config-next:
    ".next/**",
    "out/**",
    "build/**",
    "next-env.d.ts",
    // Design handoff reference material (Claude Design canvas prototype +
    // its preview runtime) — not application source, never shipped/built.
    "design_handoff_cmc_homepage/**",
    "support.js",
    "wave-engine.js",
    "Homepage Animated.dc.html",
    "Homepage Motion Spec.dc.html",
  ]),
]);

export default eslintConfig;
