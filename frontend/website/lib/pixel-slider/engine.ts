/* eslint-disable @typescript-eslint/no-explicit-any -- TSL nodes and the renderer are held in loosely typed fields */
import * as THREE from "three/webgpu";
import { uniform, texture } from "three/tsl";
import { EFFECT_LOADERS, type EffectKey } from "./effects";
import type { Effect } from "./effects/types";

/**
 * Draws the WebGL slide transitions over a container. It owns the canvas, the slide textures and
 * the shader inputs every effect shares; each effect (see `effects/`) builds its own meshes and
 * the engine shows one at a time while it plays. WebGPU where the browser has it, WebGL 2
 * otherwise. The canvas only draws while a transition runs.
 */
export class PixelEngine {
  isReady = false;
  isLost = false;
  canvas: HTMLCanvasElement;

  private container: HTMLElement;
  private urls: string[];
  private keys: EffectKey[];
  private renderer: any;
  private scene: any;
  private camera: any;
  private textures: any[] = [];
  private effects = new Map<EffectKey, Effect>();
  private active: Effect | null = null;
  private raf = 0;
  private resizeObserver?: ResizeObserver;
  private disposed = false;
  private onLost: () => void;

  private viewport = uniform(new THREE.Vector2(1, 1));
  private progress = uniform(0);
  private mapA: any;
  private mapB: any;
  private sizeA = uniform(new THREE.Vector2(1, 1));
  private sizeB = uniform(new THREE.Vector2(1, 1));

  /**
   * @param container - The element the canvas goes into (positioned, and the size of the slides).
   * @param urls - One image URL per slide, in slide order.
   * @param keys - The effects to build (the ones the slides use).
   * @param onLost - Called if the GPU drops the canvas.
   */
  constructor(container: HTMLElement, urls: string[], keys: EffectKey[], onLost: () => void) {
    this.container = container;
    this.urls = urls;
    this.keys = keys;
    this.onLost = onLost;
    this.renderer = new THREE.WebGPURenderer({ alpha: true, antialias: true });
    this.renderer.setClearColor(0x000000, 0);
    this.renderer.onDeviceLost = (info: { api: string; message: string }) => {
      this.isLost = true;
      this.isReady = false;
      this.hide();
      console.warn(`The ${info.api} device was lost, slides will crossfade.`, info.message);
      this.onLost();
    };
    this.canvas = this.renderer.domElement;
    Object.assign(this.canvas.style, {
      position: "absolute",
      inset: "0",
      width: "100%",
      height: "100%",
      pointerEvents: "none",
      visibility: "hidden",
    });
    container.append(this.canvas);
    this.scene = new THREE.Scene();
    this.camera = new THREE.PerspectiveCamera(30, 1, 1, 10000);
  }

  /** Starts the renderer, loads the images and builds the effects. Rejects if it can't draw. */
  async init() {
    await this.renderer.init();
    if (this.isSoftware()) throw new Error("Software rendering only");

    const loader = new THREE.TextureLoader();
    // Needed to read the images' pixels; the image host must send CORS headers
    loader.setCrossOrigin("anonymous");
    this.textures = await Promise.all(
      this.urls.map((src) =>
        loader.loadAsync(src).then((t: any) => {
          t.colorSpace = THREE.SRGBColorSpace;
          t.anisotropy = 4;
          return t;
        })
      )
    );
    const modules = await Promise.all(this.keys.map((key) => EFFECT_LOADERS[key]()));
    if (this.disposed) return;

    this.resize();
    // Two different textures to start with: nodes made from the same one would share a binding
    this.mapA = texture(this.textures[0]);
    this.mapB = texture(this.textures[Math.min(1, this.textures.length - 1)]);
    const context = {
      viewport: this.viewport,
      progress: this.progress,
      mapA: this.mapA,
      mapB: this.mapB,
      sizeA: this.sizeA,
      sizeB: this.sizeB,
      textures: this.textures,
    };
    this.keys.forEach((key, i) => {
      const effect = modules[i].create(context);
      effect.group.visible = false;
      effect.resize(this.viewport.value.x, this.viewport.value.y);
      this.effects.set(key, effect);
      this.scene.add(effect.group);
    });

    // Compile every effect's shaders ahead of its first transition, so none stutters
    for (const effect of this.effects.values()) {
      effect.group.visible = true;
      await this.renderer.compileAsync(this.scene, this.camera);
      effect.group.visible = false;
      if (this.disposed) return;
    }
    this.effects.forEach((effect) => effect.warm?.());

    this.resizeObserver = new ResizeObserver(() => this.resize());
    this.resizeObserver.observe(this.container);
    this.isReady = true;
  }

  has(key: EffectKey) {
    return this.effects.has(key);
  }

  /** Whether the browser is rendering on the CPU, which these shaders can't keep up with. */
  private isSoftware() {
    const { backend } = this.renderer;
    if (backend.isWebGPUBackend) return backend.device.adapterInfo?.isFallbackAdapter === true;
    const { gl } = backend;
    const info = gl.getExtension("WEBGL_debug_renderer_info");
    const name = gl.getParameter(info ? info.UNMASKED_RENDERER_WEBGL : gl.RENDERER);
    return /swiftshader|llvmpipe|software/i.test(name);
  }

  /** Matches the canvas and camera to the container, one world unit per CSS pixel at z = 0. */
  private resize() {
    const width = this.container.clientWidth;
    const height = this.container.clientHeight;
    if (!width || !height || this.disposed) return;

    this.renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
    this.renderer.setSize(width, height, false);

    this.camera.aspect = width / height;
    this.camera.position.z = height / 2 / Math.tan(THREE.MathUtils.degToRad(this.camera.fov / 2));
    this.camera.near = this.camera.position.z / 10;
    this.camera.far = this.camera.position.z * 10;
    this.camera.updateProjectionMatrix();

    this.viewport.value.set(width, height);
    this.effects.forEach((effect) => effect.resize(width, height));
  }

  private setImage(map: any, size: any, index: number) {
    const image = this.textures[index];
    map.value = image;
    size.value.set(image.image.width, image.image.height);
  }

  private render() {
    if (this.isLost || this.disposed) return;
    this.active?.onRender?.();
    this.renderer.render(this.scene, this.camera);
  }

  show() {
    this.canvas.style.visibility = "visible";
  }

  hide() {
    this.canvas.style.visibility = "hidden";
  }

  /**
   * Plays an effect between two slides and resolves on its last frame, which is identical to the
   * target slide. The canvas stays visible: call `hide()` once the target slide is on screen
   * underneath. Going next the effect plays forward (progress 0 to 1) from the current slide;
   * going back it plays in reverse with the images swapped (1 to 0), like a rewind.
   */
  run(key: EffectKey, from: number, to: number, direction: 1 | -1): Promise<void> {
    const effect = this.effects.get(key)!;
    this.effects.forEach((other) => (other.group.visible = other === effect));
    this.active = effect;

    const [first, second] = direction === 1 ? [from, to] : [to, from];
    this.setImage(this.mapA, this.sizeA, first);
    this.setImage(this.mapB, this.sizeB, second);
    effect.prepare(first, second);

    const start = direction === 1 ? 0 : 1;
    const end = 1 - start;
    this.progress.value = start;
    this.render(); // the first frame, identical to the current slide, before showing the canvas
    this.show();

    return new Promise((resolve) => {
      const t0 = performance.now();
      const tick = (now: number) => {
        if (this.disposed) return resolve();
        const k = Math.min((now - t0) / (effect.duration * 1000), 1);
        this.progress.value = start + (end - start) * effect.ease(k);
        this.render();
        if (k < 1) this.raf = requestAnimationFrame(tick);
        else resolve();
      };
      this.raf = requestAnimationFrame(tick);
    });
  }

  dispose() {
    this.disposed = true;
    cancelAnimationFrame(this.raf);
    this.resizeObserver?.disconnect();
    this.effects.forEach((effect) => effect.dispose?.());
    this.textures.forEach((t) => t.dispose());
    this.renderer.dispose();
    this.canvas.remove();
  }
}
