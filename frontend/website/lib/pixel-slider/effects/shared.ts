/* eslint-disable @typescript-eslint/no-explicit-any -- TSL nodes are passed around untyped */
import { Fn, vec2, min } from "three/tsl";

/**
 * Texture coordinates that frame an image like `object-fit: cover`, centred.
 * @param uv - Coordinates across the viewport, 0 to 1.
 * @param viewport - Viewport size.
 * @param image - Image size.
 */
export const cover: any = Fn(([uv, viewport, image]: any[]) => {
  const ratio = vec2(
    min(viewport.x.div(viewport.y).div(image.x.div(image.y)), 1),
    min(viewport.y.div(viewport.x).div(image.y.div(image.x)), 1)
  );
  return uv.sub(0.5).mul(ratio).add(0.5);
});

export const linear = (k: number) => k;
export const sine = (k: number) => -(Math.cos(Math.PI * k) - 1) / 2;
