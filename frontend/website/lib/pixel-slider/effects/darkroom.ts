import * as THREE from "three/webgpu";
import {
  Fn,
  uniform,
  texture,
  uv,
  vec2,
  vec3,
  vec4,
  floor,
  mix,
  smoothstep,
  sin,
  pow,
  dot,
  hash,
  length,
  luminance,
  PI,
} from "three/tsl";
import { cover, linear } from "./shared";
import type { EffectFactory } from "./types";

/** How soft the tone thresholds are: higher develops more evenly, lower more in steps. */
const SOFTNESS = 0.35;
/** How far the developer wash makes some areas run ahead of others. */
const WASH = 0.14;
/** The photographic paper, and the darkroom's safelight. */
const PAPER = [0.94, 0.92, 0.88] as const;
const SAFELIGHT = [0.95, 0.5, 0.4] as const;

/**
 * "Darkroom": the current photograph bleaches out to blank paper, brightest tones first, then the
 * next develops on it like a print in the tray, darkest tones first. An uneven developer wash
 * makes some areas run ahead as the tray rocks, under a red safelight, paper grain and a soft
 * vignette that clear by the end. Going back rewinds it.
 */
export const create: EffectFactory = ({ viewport, progress, mapA, mapB, sizeA, sizeB }) => {
  const seed = uniform(0); // a different wash every time

  const material = new THREE.MeshBasicNodeMaterial({ depthTest: false, depthWrite: false });
  material.colorNode = Fn(() => {
    const st = uv();
    const p = st.sub(0.5).mul(vec2(viewport.x.div(viewport.y), 1));
    const phase = seed.mul(2 * Math.PI);
    const paper = vec3(...PAPER);

    // The developer doesn't reach everywhere at once: large soft areas run a little ahead or
    // behind, and they shift as the tray rocks
    const rock = progress.mul(2.5);
    const wash = sin(p.x.mul(2.1).add(p.y.mul(1.3)).add(phase).add(rock))
      .mul(0.6)
      .add(sin(p.x.mul(-1.2).add(p.y.mul(2.7)).add(phase.mul(1.7)).sub(rock.mul(0.7))).mul(0.4))
      .mul(WASH);

    // Bleaching, over the first 55%: a tone goes once the level passes it, brightest first. The
    // level runs from below every tone to above all of them, so it starts and ends clean.
    const span = 1 + SOFTNESS + WASH * 2;
    const bleachLevel = progress.div(0.55).clamp(0, 1).mul(span).sub(WASH).add(wash);
    const current = texture(mapA, cover(st, viewport, sizeA)).rgb;
    const bleached = smoothstep(0, SOFTNESS, bleachLevel.sub(luminance(current).oneMinus()));
    const fading = mix(current, paper, bleached);

    // Developing, over the last 55%: a tone comes up once the level passes it, darkest first
    const developLevel = progress.sub(0.45).div(0.55).clamp(0, 1).mul(span).sub(WASH).add(wash.negate());
    const next = texture(mapB, cover(st, viewport, sizeB)).rgb;
    const developed = smoothstep(0, SOFTNESS, developLevel.sub(luminance(next)));
    const print = mix(fading, next, developed);

    // The darkroom, strongest in the middle and gone at both ends
    const dark = pow(sin(progress.mul(PI)).max(0), 1.5);
    // the paper's grain,
    const grain = hash(dot(floor(st.mul(viewport)), vec2(1, 4093))).sub(0.5).mul(0.05);
    // the corners falling off into the dark,
    const vignette = smoothstep(0.35, 1.1, length(p)).mul(0.45);
    // and the red safelight over everything
    const lit = print.add(grain).mul(vignette.oneMinus()).mul(vec3(...SAFELIGHT));

    return vec4(mix(print, lit, dark), 1);
  })();

  const plane = new THREE.Mesh(new THREE.PlaneGeometry(1, 1), material);
  const group = new THREE.Group();
  group.add(plane);

  return {
    group,
    duration: 2.6,
    ease: linear,
    resize: (width, height) => plane.scale.set(width, height, 1),
    prepare: () => (seed.value = Math.random()),
  };
};
