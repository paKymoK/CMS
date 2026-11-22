import { isEffectKey, type EffectKey } from "./effects";

/**
 * Resolves a hero slide's `transition` value to an effect key, or `null` for a plain crossfade.
 * A slide with no value (one saved before the option existed) crossfades, and so does a value this
 * version doesn't know.
 */
export function resolveTransition(value: string | undefined): EffectKey | null {
  return isEffectKey(value) ? value : null;
}
