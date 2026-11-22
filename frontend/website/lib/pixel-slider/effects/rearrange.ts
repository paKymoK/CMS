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
  sin,
  hash,
  length,
  varying,
  instanceIndex,
  Discard,
  PI,
} from "three/tsl";
import { cover, linear } from "./shared";
import type { EffectFactory } from "./types";

/** Pixels start moving between 0 and SPREAD, and fly for FLIGHT, both in progress units. */
const SPREAD = 0.6;
const FLIGHT = 0.4;
/** Pixels pair up within this many bands of brightness, and leave band by band. */
const BANDS = 12;

/**
 * Draws an image at a small size, framed like `object-fit: cover`, and returns each cell's tone,
 * bottom row first. Falls back to noise when the canvas is tainted (an image served without CORS
 * headers), which still works but pairs pixels at random.
 */
function tonesOf(image: any, cols: number, rows: number) {
  const tones = new Float32Array(cols * rows);
  try {
    const canvas = document.createElement("canvas");
    canvas.width = cols;
    canvas.height = rows;
    const context = canvas.getContext("2d", { willReadFrequently: true })!;
    const scale = Math.max(cols / image.width, rows / image.height);
    const w = image.width * scale;
    const h = image.height * scale;
    context.drawImage(image, (cols - w) / 2, (rows - h) / 2, w, h);
    const { data } = context.getImageData(0, 0, cols, rows);
    for (let row = 0; row < rows; row++) {
      for (let col = 0; col < cols; col++) {
        const i = ((rows - 1 - row) * cols + col) * 4;
        // A touch of noise, so equal tones pair up at random rather than in reading order
        tones[row * cols + col] =
          (0.2126 * data[i] + 0.7152 * data[i + 1] + 0.0722 * data[i + 2]) / 255 + Math.random() * 0.002;
      }
    }
  } catch {
    for (let i = 0; i < tones.length; i++) tones[i] = Math.random();
  }
  return tones;
}

/**
 * A cell's place along a Hilbert curve filling an n x n grid (n a power of two): cells that are
 * close along the curve are close on the screen, without the seams of rows or tiles.
 */
function hilbert(n: number, x: number, y: number) {
  let d = 0;
  for (let s = n >> 1; s > 0; s >>= 1) {
    const rx = (x & s) > 0 ? 1 : 0;
    const ry = (y & s) > 0 ? 1 : 0;
    d += s * s * ((3 * rx) ^ ry);
    if (ry === 0) {
      if (rx === 1) {
        x = n - 1 - x;
        y = n - 1 - y;
      }
      [x, y] = [y, x];
    }
  }
  return d;
}

/**
 * "Rearrange": the next image is rebuilt from the current one's pixels. The area is a grid of tiny
 * cells, each a pixel that can fly. Every pixel of the current image is paired with a place in the
 * next image of the same brightness, and they lift off band by band, highlights first, flying in
 * soft curves and shifting colour until the next image stands. Going back rewinds it.
 */
export const create: EffectFactory = ({ viewport, progress, mapA, mapB, sizeA, sizeB, textures }) => {
  const cellSize = uniform(4); // one pixel per cell, in CSS pixels
  const grid = uniform(new THREE.Vector2(1, 1)); // cells across and down
  const level = uniform(0); // mipmap level that averages one cell of an image
  const cellData = new THREE.DataTexture(new Uint16Array(4), 1, 1, THREE.RGBAFormat, THREE.HalfFloatType);
  cellData.magFilter = cellData.minFilter = THREE.NearestFilter;
  // Per cell: when its pixel leaves, when the one landing there arrives, where it goes
  const cells = texture(cellData);

  // How far along a pixel is, from when it leaves: 0 = in place, 1 = landed
  const flight = (delay: any) => progress.mul(SPREAD + FLIGHT).sub(delay).div(FLIGHT).clamp(0, 1);
  // A cell's data, read from the middle of its texel
  const cellAt = (cell: any) => texture(cells, cell.add(0.5).div(grid)).level(float(0));

  // Behind everything, a dimmed blur of the two images, turning from one to the other
  const materialBackdrop = new THREE.MeshBasicNodeMaterial({ depthTest: false, depthWrite: false });
  materialBackdrop.colorNode = Fn(() => {
    const softA = texture(mapA, cover(uv(), viewport, sizeA)).level(float(5)).rgb;
    const softB = texture(mapB, cover(uv(), viewport, sizeB)).level(float(5)).rgb;
    return vec4(mix(softA, softB, smoothstep(0.3, 0.7, progress)).mul(0.8), 1);
  })();
  const backdrop = new THREE.Mesh(new THREE.PlaneGeometry(1, 1), materialBackdrop);
  backdrop.renderOrder = 0;

  // The first image, wherever its pixel hasn't left yet
  const materialA = new THREE.MeshBasicNodeMaterial({ depthTest: false, depthWrite: false });
  materialA.colorNode = Fn(() => {
    const cell = floor(uv().mul(viewport).div(cellSize));
    Discard(flight(cellAt(cell).r).greaterThan(0));
    return vec4(texture(mapA, cover(uv(), viewport, sizeA)).rgb, 1);
  })();
  const planeA = new THREE.Mesh(new THREE.PlaneGeometry(1, 1), materialA);
  planeA.renderOrder = 1;

  // The second image, wherever its pixel has landed
  const materialB = new THREE.MeshBasicNodeMaterial({ depthTest: false, depthWrite: false });
  materialB.colorNode = Fn(() => {
    const cell = floor(uv().mul(viewport).div(cellSize));
    Discard(flight(cellAt(cell).g).lessThan(1));
    return vec4(texture(mapB, cover(uv(), viewport, sizeB)).rgb, 1);
  })();
  const planeB = new THREE.Mesh(new THREE.PlaneGeometry(1, 1), materialB);
  planeB.renderOrder = 2;

  // The pixels in flight: one sprite per cell of the first image
  const materialPixels = new THREE.SpriteNodeMaterial({ transparent: true, depthTest: false, depthWrite: false });
  const index = float(instanceIndex);
  const row = floor(index.add(0.5).div(grid.x));
  const start = vec2(index.sub(row.mul(grid.x)), row);
  const data = cellAt(start);
  const t = flight(data.r);
  const end = floor(data.ba.mul(grid));
  const toPixels = (cell: any) => cell.add(0.5).mul(cellSize).sub(viewport.mul(0.5));
  const from = toPixels(start);
  const to = toPixels(end);
  // Easing in and out, on a soft curve to one side, its own way for each pixel
  const eased = t.mul(t).mul(t.mul(-2).add(3));
  const route = to.sub(from);
  const side = vec2(route.y.negate(), route.x);
  const bend = hash(index.add(11)).sub(0.5).mul(0.8);
  const swirl = sin(t.mul(PI)).mul(bend);
  materialPixels.positionNode = vec3(mix(from, to, eased).add(side.mul(swirl)), 0);
  // Square in place at both ends (so it hands over to the images), a soft dot in flight
  const loose = sin(t.mul(PI));
  materialPixels.scaleNode = select(t.greaterThan(0).and(t.lessThan(1)), cellSize.mul(mix(1, 0.8, loose)), 0);
  const colourFrom = texture(mapA, cover(start.add(0.5).div(grid), viewport, sizeA)).level(level).rgb;
  const colourTo = texture(mapB, cover(end.add(0.5).div(grid), viewport, sizeB)).level(level).rgb;
  materialPixels.colorNode = varying(mix(colourFrom, colourTo, smoothstep(0.25, 0.75, t)));
  const vLoose = varying(loose);
  const radius = mix(0.75, 0.5, vLoose);
  materialPixels.opacityNode = smoothstep(radius.sub(0.12), radius, length(uv().sub(0.5))).oneMinus();
  const pixels = new THREE.Sprite(materialPixels);
  pixels.renderOrder = 3;
  pixels.frustumCulled = false;

  const group = new THREE.Group();
  group.add(backdrop, planeA, planeB, pixels);

  /**
   * Pairs every cell of one image with a cell of the other in the same band of brightness (ranked,
   * so both images have as many cells in each band), as near as it can: within a band, the cells
   * are paired in their order along a Hilbert curve. Writes when each pixel leaves, when the one
   * landing there arrives and where it goes into the cell data texture.
   */
  const pair = (first: number, second: number) => {
    const { x: cols, y: rows } = grid.value;
    const count = cols * rows;
    const tonesA = tonesOf(textures[first].image, cols, rows);
    const tonesB = tonesOf(textures[second].image, cols, rows);
    const n = 2 ** Math.ceil(Math.log2(Math.max(cols, rows)));
    const curve = new Uint32Array(count);
    for (let i = 0; i < count; i++) curve[i] = hilbert(n, i % cols, Math.floor(i / cols));
    // Every cell, brightest first, in both images
    const rank = (tones: Float32Array) =>
      Uint32Array.from({ length: count }, (_, i) => i).sort((a, b) => tones[b] - tones[a]);
    const orderA = rank(tonesA);
    const orderB = rank(tonesB);
    const along = (a: number, b: number) => curve[a] - curve[b];
    const out = new Uint16Array(count * 4);
    const half = THREE.DataUtils.toHalfFloat;
    for (let band = 0; band < BANDS; band++) {
      const from = Math.floor((band * count) / BANDS);
      const to = Math.floor(((band + 1) * count) / BANDS);
      const bandA = orderA.subarray(from, to).sort(along);
      const bandB = orderB.subarray(from, to).sort(along);
      for (let i = 0; i < bandA.length; i++) {
        const a = bandA[i];
        const b = bandB[i];
        const delay = ((band + 2 * Math.random()) / (BANDS + 1)) * SPREAD;
        out[a * 4] = half(delay); // when the pixel in this cell leaves
        out[b * 4 + 1] = half(delay); // when the one landing in this cell arrives
        out[a * 4 + 2] = half(((b % cols) + 0.5) / cols); // where it lands
        out[a * 4 + 3] = half((Math.floor(b / cols) + 0.5) / rows);
      }
    }
    (cellData as any).image = { data: out, width: cols, height: rows };
    cellData.needsUpdate = true;
  };

  return {
    group,
    duration: 2.6,
    ease: linear,
    resize(width, height) {
      backdrop.scale.set(width, height, 1);
      planeA.scale.set(width, height, 1);
      planeB.scale.set(width, height, 1);
      // A grid of 3+ px cells, about 90,000 at most
      const size = Math.max(3, Math.round(Math.sqrt((width * height) / 90000)));
      cellSize.value = size;
      grid.value.set(Math.ceil(width / size), Math.ceil(height / size));
      pixels.count = grid.value.x * grid.value.y;
      // How many texels of an image one cell covers, as a mipmap level
      const { width: w, height: h } = textures[0].image as { width: number; height: number };
      level.value = Math.log2(Math.max(1, size / Math.max(width / w, height / h)));
      // The data texture follows the grid's size; the next run pairs for real (see `prepare`)
      (cellData as any).image = {
        data: new Uint16Array(grid.value.x * grid.value.y * 4),
        width: grid.value.x,
        height: grid.value.y,
      };
      cellData.needsUpdate = true;
    },
    prepare: pair,
    dispose() {
      cellData.dispose();
    },
  };
};
