import type { CSSProperties } from "react";

/**
 * Inline style for CMS-controlled text colour. Both colours -> a gradient painted through the
 * glyphs; one colour -> a solid fill (clearing any gradient a stylesheet class applies); neither ->
 * undefined, so the section's own default look stays. Colours are validated #rrggbb by the backend.
 */
export function textPaint(
  color?: string,
  colorEnd?: string,
  angle?: number,
): CSSProperties | undefined {
  if (colorEnd) {
    return {
      backgroundImage: `linear-gradient(${angle ?? 135}deg, ${color ?? "#ffffff"}, ${colorEnd})`,
      WebkitBackgroundClip: "text",
      backgroundClip: "text",
      color: "transparent",
    };
  }
  if (color) return { backgroundImage: "none", color };
  return undefined;
}
