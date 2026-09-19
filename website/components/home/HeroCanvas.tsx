"use client";

import { useEffect, useRef } from "react";
import * as THREE from "three";
import { usePrefersReducedMotion } from "@/lib/usePrefersReducedMotion";

// vanta ships no type definitions.
// @ts-expect-error -- untyped module
import NET from "vanta/dist/vanta.net.min.js";

type VantaEffect = { destroy: () => void };

/**
 * Hero background — Vanta NET (WebGL lattice), per the design handoff's
 * "Hero background: two options -> Option A". Must be dynamically imported
 * by its parent with `ssr:false` (this file touches `window`/WebGL at
 * effect time, which is fine, but importing `vanta`/`three` at module scope
 * during a server render would crash it).
 *
 * Vanta does not honour prefers-reduced-motion itself, so that branch is
 * handled manually here: skip init entirely and paint a static gradient.
 */
export function HeroCanvas() {
  const hostRef = useRef<HTMLDivElement>(null);
  const effectRef = useRef<VantaEffect | null>(null);
  const reducedMotion = usePrefersReducedMotion();

  useEffect(() => {
    if (reducedMotion || !hostRef.current) return;

    effectRef.current = NET({
      el: hostRef.current,
      THREE,
      mouseControls: true,
      touchControls: true,
      gyroControls: false,
      minHeight: 200,
      minWidth: 200,
      scale: 1,
      scaleMobile: 0.7,
      color: 0x2f86cf,
      backgroundColor: 0x03091a,
      points: 11,
      maxDistance: 23,
      spacing: 16,
      showDots: true,
    }) as VantaEffect;

    return () => {
      effectRef.current?.destroy();
      effectRef.current = null;
    };
  }, [reducedMotion]);

  return (
    <div
      ref={hostRef}
      aria-hidden
      className="absolute inset-0"
      style={
        reducedMotion
          ? { background: "linear-gradient(180deg, #03091a, #04102a, #061634)" }
          : undefined
      }
    />
  );
}
