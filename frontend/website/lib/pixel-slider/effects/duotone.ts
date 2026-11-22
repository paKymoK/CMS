import * as THREE from "three/webgpu";
import { Fn, texture, uv, vec3, vec4, mix, smoothstep, select, sin, luminance, float } from "three/tsl";
import { cover, linear } from "./shared";
import type { EffectFactory } from "./types";

/** The two inks. THREE.Color turns the sRGB hex values into the linear values the shader works in. */
const INK = new THREE.Color(0x1c1a1a);
const PAPER = new THREE.Color(0xd4503e);

/**
 * "Duotone": the current image sinks into a two-tone print, near-black ink on vermilion, its
 * shapes cut from its tones; the shapes flow into the next image's, cut from a blend of the two
 * images' tones, and the next image's colour comes back. Going back rewinds it.
 */
export const create: EffectFactory = ({ viewport, progress, mapA, mapB, sizeA, sizeB }) => {
  const material = new THREE.MeshBasicNodeMaterial({ depthTest: false, depthWrite: false });
  material.colorNode = Fn(() => {
    const st = uv();
    const current = texture(mapA, cover(st, viewport, sizeA)).rgb;
    const next = texture(mapB, cover(st, viewport, sizeB)).rgb;

    // The tones the print is cut from: each image's, a little softened so the shapes are clean,
    // blending from the current image's to the next one's
    const toneA = luminance(texture(mapA, cover(st, viewport, sizeA)).level(float(1.5)).rgb);
    const toneB = luminance(texture(mapB, cover(st, viewport, sizeB)).level(float(1.5)).rgb);
    const blend = smoothstep(0.3, 0.7, progress);
    const tone = mix(toneA, toneB, blend);
    // Where the ink stops: at each image's own average tone (its smallest mipmap), so every print
    // splits into ink and paper, breathing a little as the shapes change
    const averageA = luminance(texture(mapA, cover(st, viewport, sizeA)).level(float(12)).rgb);
    const averageB = luminance(texture(mapB, cover(st, viewport, sizeB)).level(float(12)).rgb);
    const cut = mix(averageA, averageB, blend).add(sin(progress.mul(Math.PI * 2)).mul(0.03));
    const ink = vec3(INK.r, INK.g, INK.b);
    const paper = vec3(PAPER.r, PAPER.g, PAPER.b);
    const print = mix(ink, paper, smoothstep(cut.sub(0.02), cut.add(0.02), tone));

    // In and out of the print: the photographs at either end, the print in between
    const printed = smoothstep(0, 0.22, progress).mul(smoothstep(0.78, 1, progress).oneMinus());
    const photo = select(progress.lessThan(0.5), current, next);
    return vec4(mix(photo, print, printed), 1);
  })();

  const plane = new THREE.Mesh(new THREE.PlaneGeometry(1, 1), material);
  const group = new THREE.Group();
  group.add(plane);

  return {
    group,
    duration: 2,
    ease: linear,
    resize: (width, height) => plane.scale.set(width, height, 1),
    prepare: () => {},
  };
};
