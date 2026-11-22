/* eslint-disable @typescript-eslint/no-explicit-any -- TSL nodes are passed around untyped */
import * as THREE from "three/webgpu";
import { Fn, texture, uv, vec4, mix, smoothstep, length, min } from "three/tsl";
import { cover, sine } from "./shared";
import { stir, CELLS, STEPS } from "./stir/simulate";
import type { EffectFactory } from "./types";

interface Stirring {
  record: Float32Array;
  cols: number;
  rows: number;
}

/**
 * "Stir": the image turns liquid and is stirred. A few invisible strokes drag through it like a
 * finger through paint, pulling the picture along in their wakes and curling it into eddies (a
 * real fluid simulation, worked out before the transition and recorded); as the liquid slows, the
 * next image settles out of the same flow into its own place, coming through first in the wakes.
 * Going back rewinds it.
 */
export const create: EffectFactory = ({ viewport, progress, mapA, mapB, sizeA, sizeB }) => {
  const flowData = new THREE.DataTexture(new Uint16Array(4), 1, 1, THREE.RGBAFormat, THREE.HalfFloatType);
  flowData.magFilter = flowData.minFilter = THREE.LinearFilter;
  const flow = texture(flowData);

  const material = new THREE.MeshBasicNodeMaterial({ depthTest: false, depthWrite: false });
  material.colorNode = Fn(() => {
    const st = uv();
    // Each image read from where the liquid has carried its content (offsets are 0 for the
    // current image at the start and for the next one at the end)
    const offsets = texture(flow, st);
    const current = texture(mapA, cover(st.add(offsets.xy), viewport, sizeA)).rgb;
    const next = texture(mapB, cover(st.add(offsets.zw), viewport, sizeB)).rgb;
    // The next one comes through while the liquid is moving most, a little sooner wherever it has
    // carried the current image furthest
    const stirred = min(length(offsets.xy).mul(4), 1);
    return vec4(mix(current, next, smoothstep(0.35, 0.65, progress.add(stirred.mul(0.12)))), 1);
  })();
  const plane = new THREE.Mesh(new THREE.PlaneGeometry(1, 1), material);
  const group = new THREE.Group();
  group.add(plane);

  /** The simulation grid for the current area: about CELLS square cells. */
  const grid = () => {
    const { x: width, y: height } = viewport.value;
    const cell = Math.sqrt((width * height) / CELLS);
    return { cols: Math.max(8, Math.round(width / cell)), rows: Math.max(8, Math.round(height / cell)) };
  };

  // Stirrings are simulated in a worker, so the page never waits for one; without it, between
  // transitions
  let worker: Worker | null = null;
  let stirring: Stirring | null = null; // the next one, once simulated
  let playing: Stirring | null = null; // the one on screen
  let timer = 0;
  try {
    worker = new Worker(new URL("./stir/stir.worker.ts", import.meta.url), { type: "module" });
    worker.onmessage = ({ data }: MessageEvent<Stirring>) => (stirring = data);
    worker.onerror = () => (worker = null);
  } catch {
    worker = null;
  }

  /** Starts simulating a new stirring for the next transition. */
  const prepareNext = () => {
    const { cols, rows } = grid();
    if (worker) worker.postMessage({ cols, rows });
    else timer = window.setTimeout(() => (stirring = { record: stir(cols, rows), cols, rows }), 50);
  };

  return {
    group,
    duration: 1.2,
    ease: sine,
    resize: (width, height) => plane.scale.set(width, height, 1),
    warm: prepareNext,
    prepare() {
      // The stirring simulated ahead of time (or, if the area has changed shape since, one now),
      // and a new one for next time
      const { cols, rows } = grid();
      const ready = stirring;
      playing = ready && ready.cols === cols && ready.rows === rows ? ready : { record: stir(cols, rows), cols, rows };
      stirring = null;
      prepareNext();
      (flowData as any).image = { data: new Uint16Array(cols * rows * 4), width: cols, height: rows };
    },
    // Before each frame, the liquid at that moment, as fractions of the area, between two recorded steps
    onRender() {
      if (!playing) return;
      const { record, cols, rows } = playing;
      const n = cols * rows;
      const at = progress.value * STEPS;
      const step = Math.min(Math.floor(at), STEPS - 1);
      const t = at - step;
      const a = step * n * 4;
      const b = a + n * 4;
      const half = THREE.DataUtils.toHalfFloat;
      const data = (flowData as any).image.data as Uint16Array;
      for (let i = 0; i < n * 4; i += 2) {
        data[i] = half((record[a + i] * (1 - t) + record[b + i] * t) / cols);
        data[i + 1] = half((record[a + i + 1] * (1 - t) + record[b + i + 1] * t) / rows);
      }
      flowData.needsUpdate = true;
    },
    dispose() {
      worker?.terminate();
      clearTimeout(timer);
      flowData.dispose();
    },
  };
};
