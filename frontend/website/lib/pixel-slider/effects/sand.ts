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
  float,
  floor,
  mix,
  smoothstep,
  select,
  hash,
  length,
  varying,
  instanceIndex,
  Discard,
  mx_noise_float,
  mx_noise_vec3,
} from "three/tsl";
import { cover, sine } from "./shared";
import type { EffectFactory } from "./types";

/** Grains leave between 0 and SPREAD, and fly for LIFE, both in progress units. */
const SPREAD = 1.38;
const LIFE = 0.5;

/**
 * "Sand": the image erodes into grains of sand (one sprite per 3+ px cell). The wind picks them up
 * from the bottom left, in patches and one grain at a time, and carries them up and across,
 * swirling and lifting, while the new image settles underneath. Going back blows the sand back in.
 */
export const create: EffectFactory = ({ viewport, progress, mapA, mapB, sizeA, sizeB, textures }) => {
  const seed = uniform(0); // a different wind every time
  const cellSize = uniform(4);
  const cols = uniform(1);
  const level = uniform(0); // mipmap level that averages one cell of the image

  // When a cell's grain leaves: 0 = still in place, 1 = gone. The plane holding the grains in
  // place and the grains in flight both use it, so they hand over exactly.
  const flight = Fn(([cell, index]: any[]) => {
    const st = cell.add(0.5).mul(cellSize).div(viewport);
    const p = st.sub(0.5).mul(vec2(viewport.x.div(viewport.y), 1));
    // The wind starts in the bottom left corner and works its way up and across,
    const along = st.x.mul(0.6).add(st.y.mul(0.4));
    // in patches, the way sand comes off a dune,
    const patches = mx_noise_float(vec3(p.mul(3), seed.mul(0.173))).mul(0.12);
    // and one grain at a time
    const loose = hash(index.add(seed.mul(7919))).mul(0.14);
    const delay = along.add(patches).add(loose).add(0.12).clamp(0, SPREAD);
    return progress.mul(SPREAD + LIFE).sub(delay).div(LIFE).clamp(0, 1);
  });

  // The image underneath settles as the sand clears: from a little closer, and darker
  const materialB = new THREE.MeshBasicNodeMaterial({ depthTest: false, depthWrite: false });
  materialB.colorNode = Fn(() => {
    const settle = progress.oneMinus().pow(2);
    const st = uv().sub(0.5).div(settle.mul(0.06).add(1)).add(0.5);
    return vec4(texture(mapB, cover(st, viewport, sizeB)).rgb.mul(settle.mul(0.3).oneMinus()), 1);
  })();

  // The image made of sand, with a hole wherever a grain has left
  const materialA = new THREE.MeshBasicNodeMaterial({ depthTest: false, depthWrite: false });
  materialA.colorNode = Fn(() => {
    const cell = floor(uv().mul(viewport).div(cellSize));
    Discard(flight(cell, cell.y.mul(cols).add(cell.x)).greaterThan(0));
    return vec4(texture(mapA, cover(uv(), viewport, sizeA)).rgb, 1);
  })();

  // The grains: one sprite per cell, drawn only once it has left
  const materialG = new THREE.SpriteNodeMaterial({ transparent: true, depthTest: false, depthWrite: false });
  const index = float(instanceIndex);
  const row = floor(index.add(0.5).div(cols));
  const cell = vec2(index.sub(row.mul(cols)), row);
  const t = flight(cell, index);
  const centre = cell.add(0.5).mul(cellSize).sub(viewport.mul(0.5));
  const random = vec3(hash(index.mul(3).add(1)), hash(index.mul(3).add(2)), hash(index.mul(3).add(3)));

  // Grains pick up speed as the wind takes them,
  const e = t.mul(t);
  // carried up and across, each at its own speed,
  const gust = vec3(0.82, 0.57, 0).mul(viewport.y).mul(random.x.mul(0.8).add(0.5));
  // swirling on the way
  const swirl = mx_noise_vec3(vec3(centre.mul(0.004), e.mul(1.6).add(seed.mul(0.173)))).mul(180);
  // and lifting towards the viewer
  const lift = vec3(0, 0, random.y.mul(400).add(80));
  materialG.positionNode = vec3(centre, 0).add(gust.add(swirl).add(lift).mul(e));

  // A grain leaves as a square of its cell, then shrinks to its own size and fades out
  const loose = smoothstep(0, 0.3, t);
  const size = mix(1, random.z.mul(0.5).add(0.45), loose).mul(smoothstep(0.7, 1, t).oneMinus());
  materialG.scaleNode = select(t.greaterThan(0), cellSize.mul(size), 0);

  // Its colour is its cell's average, catching the light a little differently as it tumbles
  const average = texture(mapA, cover(cell.add(0.5).mul(cellSize).div(viewport), viewport, sizeA)).level(level).rgb;
  materialG.colorNode = varying(average.mul(mix(1, random.x.mul(0.4).add(0.8), loose)));
  // and it rounds off as it goes
  const tv: any = varying(t);
  const radius = mix(0.8, 0.5, smoothstep(0, 0.3, tv));
  const round = smoothstep(radius.sub(0.08), radius, length(uv().sub(0.5))).oneMinus();
  materialG.opacityNode = round.mul(smoothstep(0.75, 1, tv).oneMinus());

  const planeB = new THREE.Mesh(new THREE.PlaneGeometry(1, 1), materialB);
  const planeA = new THREE.Mesh(new THREE.PlaneGeometry(1, 1), materialA);
  const grains = new THREE.Sprite(materialG);
  planeB.renderOrder = 0;
  planeA.renderOrder = 1;
  grains.renderOrder = 2;
  grains.frustumCulled = false;
  const group = new THREE.Group();
  group.add(planeB, planeA, grains);

  return {
    group,
    duration: 1.4,
    ease: sine,
    resize(width, height) {
      planeA.scale.set(width, height, 1);
      planeB.scale.set(width, height, 1);
      // The grains: about 110,000 at most
      const size = Math.max(3, Math.round(Math.sqrt((width * height) / 110000)));
      const across = Math.ceil(width / size);
      const down = Math.ceil(height / size);
      cellSize.value = size;
      cols.value = across;
      grains.count = across * down;
      // How many texels of the image a cell covers, as a mipmap level
      const { width: imageWidth, height: imageHeight } = textures[0].image as { width: number; height: number };
      const scale = Math.max(width / imageWidth, height / imageHeight);
      level.value = Math.log2(Math.max(1, size / scale));
    },
    prepare: () => (seed.value = Math.floor(Math.random() * 1000)),
  };
};
