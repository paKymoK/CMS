import type { EffectFactory } from "./types";

/**
 * The effects, each loaded only when a slide uses it. Keys are what the CMS stores in a hero
 * slide's `transition` field (see `lib/pixel-slider/transitions.ts` for the list the admin shows).
 */
export const EFFECT_LOADERS = {
  rearrange: () => import("./rearrange"),
  unveil: () => import("./unveil"),
  darkroom: () => import("./darkroom"),
  duotone: () => import("./duotone"),
  sand: () => import("./sand"),
  stir: () => import("./stir"),
} satisfies Record<string, () => Promise<{ create: EffectFactory }>>;

export type EffectKey = keyof typeof EFFECT_LOADERS;

export const isEffectKey = (key: unknown): key is EffectKey =>
  typeof key === "string" && Object.hasOwn(EFFECT_LOADERS, key);
