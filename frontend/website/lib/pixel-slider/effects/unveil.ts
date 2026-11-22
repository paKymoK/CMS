/* eslint-disable @typescript-eslint/no-explicit-any -- TSL nodes are passed around untyped */
import * as THREE from "three/webgpu";
import {
  Fn,
  uniform,
  texture,
  uv,
  vec2,
  vec3,
  vec4,
  mix,
  min,

  select,
  sin,
  cos,
  exp,
  pow,
  dot,
  cross,
  normalize,
  luminance,
  varying,
  frontFacing,
  positionLocal,
  PI,
} from "three/tsl";
import { cover, linear } from "./shared";
import type { EffectFactory } from "./types";

/**
 * "Unveil": the image is a sheet laid over the next one. It lifts from the top right corner and
 * rolls back over itself along a soft, wavy fold, catching the light and casting a shadow, and is
 * drawn off past the far corner while the next image settles underneath. Going back unrolls it.
 */
export const create: EffectFactory = ({ viewport, progress, mapA, mapB, sizeA, sizeB }) => {
  const seed = uniform(0); // a new wave in the fold for every transition

  // The fold, measured from the top right corner along `across` (towards the bottom left), and
  // `along` the fold line. Every point of the screen is 0 to `reach` across.
  const corner = viewport.mul(0.5);
  const across = normalize(vec2(-1, -0.8));
  const along = vec2(across.y.negate(), across.x);
  const reach = dot(viewport.negate(), across);
  const radius = min(viewport.x, viewport.y).mul(0.11);
  // The fold sweeps from just outside the corner to where the whole sheet has rolled past the far
  // corner, so the first and last frames are the flat sheet and no sheet at all.
  const sweep = mix(reach.mul(-0.02), reach.add(radius.mul(PI)).add(reach.mul(0.02)), progress);
  // Fabric doesn't fold in a straight line: let it wave a little while it moves
  const active = sin(progress.mul(PI));
  const foldAt = Fn(([side]: any[]) =>
    sweep.add(
      sin(side.mul(9.5).div(reach).add(seed))
        .mul(0.6)
        .add(sin(side.mul(4.1).div(reach).add(seed.mul(1.9))))
        .mul(radius.mul(0.9))
        .mul(active)
    )
  );

  // Where a point of the flat sheet ends up: flat until the fold reaches it, then around a
  // cylinder of `radius` along the fold, then flat again, face down, on top of the rest
  const sheet = Fn(([p]: any[]) => {
    const x = dot(p.sub(corner), across);
    const y = dot(p.sub(corner), along);
    const past = foldAt(y).sub(x);
    const angle = past.div(radius).clamp(0, PI);
    const rolled = select(
      past.lessThan(radius.mul(PI)),
      foldAt(y).sub(radius.mul(sin(angle))),
      foldAt(y).add(past.sub(radius.mul(PI)))
    );
    const height = select(past.lessThan(radius.mul(PI)), radius.mul(cos(angle).oneMinus()), radius.mul(2));
    const lifted = past.greaterThan(0);
    const position = corner.add(across.mul(select(lifted, rolled, x))).add(along.mul(y));
    return vec3(position, select(lifted, height, 0));
  });

  // The image underneath settles as the sheet leaves: from a little closer, and darker, and in
  // the shadow of the fold
  const materialB = new THREE.MeshBasicNodeMaterial({ depthTest: false, depthWrite: false });
  materialB.colorNode = Fn(() => {
    const settle = progress.oneMinus().pow(2);
    const st = uv().sub(0.5).div(settle.mul(0.04).add(1)).add(0.5);
    const p = uv().mul(viewport).sub(corner);
    const open = foldAt(dot(p, along)).sub(dot(p, across)).max(0);
    const shadow = exp(open.div(radius.mul(1.3)).negate()).mul(0.5).mul(active);
    const color = texture(mapB, cover(st, viewport, sizeB)).rgb;
    return vec4(color.mul(settle.mul(0.25).oneMinus()).mul(shadow.oneMinus()), 1);
  })();

  // The sheet: a finely divided plane, bent by `sheet` in the vertex shader
  const materialA = new THREE.MeshBasicNodeMaterial({ side: THREE.DoubleSide });
  const flat = positionLocal.xy.mul(viewport);
  const position = sheet(flat);
  // The surface's normal from two neighbouring points, for the light
  const normal = varying(
    normalize(cross(sheet(flat.add(vec2(1, 0))).sub(position), sheet(flat.add(vec2(0, 1))).sub(position)))
  );
  // Where the point lies on the flat sheet, for the fragment shader (linear, so it interpolates exactly)
  const onSheet = varying(flat.sub(corner));
  materialA.positionNode = position;

  materialA.colorNode = Fn(() => {
    const image = texture(mapA, cover(uv(), viewport, sizeA)).rgb;
    // The back of the sheet is pale, with the print showing faintly through
    const back = mix(vec3(0.86, 0.84, 0.8), vec3(luminance(image)), 0.22);
    const facing = select(frontFacing, normalize(normal), normalize(normal).negate());
    const base = select(frontFacing, image, back);
    // Light from the top left: shade the curl and give it a soft sheen. Both are measured against
    // a flat sheet, so they vanish when it lies flat.
    const light = normalize(vec3(-0.45, 0.55, 0.7));
    const halfway = normalize(light.add(vec3(0, 0, 1)));
    const shade = dot(facing, light).sub(light.z).mul(1.1);
    const sheen = pow(dot(facing, halfway).max(0), 24).sub(pow(halfway.z, 24)).max(0).mul(0.3);
    // The flap's tip shades the sheet just ahead of it
    const x = dot(onSheet, across);
    const tip = foldAt(dot(onSheet, along)).mul(2).sub(radius.mul(PI));
    const tipShadow = select(x.greaterThan(tip), exp(x.sub(tip).div(radius.mul(0.7)).negate()), 0)
      .mul(0.35)
      .mul(active);
    return vec4(base.mul(shade.add(1)).add(sheen).mul(tipShadow.oneMinus()), 1);
  })();

  const planeB = new THREE.Mesh(new THREE.PlaneGeometry(1, 1), materialB);
  const sheetMesh = new THREE.Mesh(new THREE.PlaneGeometry(1, 1, 192, 120), materialA);
  planeB.renderOrder = 0;
  sheetMesh.renderOrder = 1;
  sheetMesh.frustumCulled = false;
  const group = new THREE.Group();
  group.add(planeB, sheetMesh);

  return {
    group,
    duration: 1.7,
    ease: linear,
    // The image underneath is the size of the area (the sheet scales itself in the shader)
    resize: (width, height) => planeB.scale.set(width, height, 1),
    prepare: () => (seed.value = Math.random() * 10),
  };
};
