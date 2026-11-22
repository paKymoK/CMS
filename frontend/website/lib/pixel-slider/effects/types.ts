/* eslint-disable @typescript-eslint/no-explicit-any -- TSL nodes are passed around untyped */
import type { Object3D, Texture } from "three/webgpu";

/** What the engine hands every effect: shared shader inputs and the slide textures. */
export interface EffectContext {
  /** Container size in CSS pixels. */
  viewport: any;
  /** 0 = the first image, 1 = the second. */
  progress: any;
  /** The two images being moved between: texture slots, swapped by the engine before each run. */
  mapA: any;
  mapB: any;
  /** Their natural sizes, for framing them like `object-fit: cover`. */
  sizeA: any;
  sizeB: any;
  /** One texture per slide. */
  textures: Texture[];
}

/** One transition. `progress` 0 and 1 must be exactly the two images, and the output opaque. */
export interface Effect {
  /** Everything the effect draws; the engine shows it only while the effect runs. */
  group: Object3D;
  /** Seconds one transition takes. */
  duration: number;
  /** Maps linear time (0 to 1) to progress. */
  ease: (k: number) => number;
  /** Size the meshes (and anything that depends on the screen's shape). */
  resize: (width: number, height: number) => void;
  /** Before a run: which slides the first and second image are. */
  prepare: (first: number, second: number) => void;
  /** Before every frame. */
  onRender?: () => void;
  /** Once the engine can draw: start any work that can happen ahead of the first run. */
  warm?: () => void;
  dispose?: () => void;
}

export type EffectFactory = (context: EffectContext) => Effect;
