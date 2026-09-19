"use client";

import { useEffect, useRef, useState, useCallback } from "react";
import Image from "next/image";
import type { Topology } from "topojson-specification";
import type { Office } from "@/content/home/types";
import { Placeholder } from "@/components/ui/Placeholder";
import { usePrefersReducedMotion } from "@/lib/usePrefersReducedMotion";
import {
  buildGlobeDots,
  createOrthographicView,
  projectDots,
  type GlobeDot,
} from "@/lib/globe/projection";

const AUTOROTATE_DEG_PER_S = 2.1;
const DRAG_DEG_PER_PX = 0.45;
const FRICTION = 0.94;
const TILT_DEG = 14;
// The sphere/canvas sit inset 26px from the outer (marker-layer) container
// on every side — projection must use the sphere's own diameter, not the
// outer container's, or the dot field renders shifted and mis-scaled
// relative to the visible sphere circle.
const SPHERE_INSET = 26;

export function Globe({ offices }: { offices: Office[] }) {
  const containerRef = useRef<HTMLDivElement>(null);
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const markerRefs = useRef<(HTMLDivElement | null)[]>([]);
  const cardRef = useRef<HTMLDivElement>(null);

  const [size, setSize] = useState(320);
  const [hoveredIndex, setHoveredIndex] = useState<number | null>(null);
  const reducedMotion = usePrefersReducedMotion();

  const dotsRef = useRef<GlobeDot[] | null>(null);
  const rotationRef = useRef(0);
  const velocityRef = useRef(0);
  const dragRef = useRef<{ lastX: number } | null>(null);
  const hoveredIndexRef = useRef<number | null>(null);
  useEffect(() => {
    hoveredIndexRef.current = hoveredIndex;
  }, [hoveredIndex]);

  // Resize: derive canvas/projection sizing from the container's actual
  // rendered box (fixed pixel constants broke below 340px in the original
  // prototype, per the handoff's own "known issues" note).
  useEffect(() => {
    const el = containerRef.current;
    if (!el) return;
    const ro = new ResizeObserver((entries) => {
      const box = entries[0]?.contentRect;
      if (box) setSize(Math.round(box.width));
    });
    ro.observe(el);
    return () => ro.disconnect();
  }, []);

  // Load the vendored world map + build the dot field once.
  useEffect(() => {
    let cancelled = false;
    fetch("/data/countries-110m.json")
      .then((r) => r.json())
      .then((topology: Topology) => {
        if (cancelled) return;
        dotsRef.current = buildGlobeDots(topology);
      })
      .catch(() => {
        dotsRef.current = [];
      });
    return () => {
      cancelled = true;
    };
  }, []);

  const draw = useCallback(() => {
    const canvas = canvasRef.current;
    const dots = dotsRef.current;
    // The canvas/sphere are inset SPHERE_INSET from the outer (marker-layer)
    // container on every side — the projection must use the sphere's own
    // diameter, not the outer container's, or the dot field renders shifted
    // and mis-scaled relative to the visible sphere circle.
    const sphereSize = size - SPHERE_INSET * 2;
    if (!canvas || !dots || sphereSize <= 0) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    const dpr = Math.min(window.devicePixelRatio || 1, 2);
    const cw = Math.round(sphereSize * dpr);
    if (canvas.width !== cw) {
      canvas.width = cw;
      canvas.height = cw;
    }
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0);
    ctx.clearRect(0, 0, sphereSize, sphereSize);

    const view = createOrthographicView({
      size: sphereSize,
      rotationLon: rotationRef.current,
      tiltDeg: TILT_DEG,
    });
    const projected = projectDots(dots, view);
    for (const p of projected) {
      const alpha = Math.max(0.08, 1 - p.depth) * 0.85;
      const r = Math.max(0.5, 1.1 - p.depth * 0.5);
      ctx.beginPath();
      ctx.fillStyle = `rgba(47,134,207,${alpha.toFixed(3)})`;
      ctx.arc(p.x, p.y, r, 0, Math.PI * 2);
      ctx.fill();
    }

    // Markers: absolutely-positioned DOM nodes updated imperatively via
    // translate3d every frame — left/top positioning visibly zigzags while
    // rotating (a documented bug in the original prototype); translate3d
    // keeps the transform on the GPU/subpixel path instead. They live in
    // the OUTER container's coordinate space, so the sphere-relative
    // projection is offset back out by SPHERE_INSET.
    offices.forEach((office, i) => {
      const el = markerRefs.current[i];
      if (!el) return;
      const p = view.project(office.lon, office.lat);
      if (!p || p.depth > 0.94) {
        el.style.opacity = "0";
        el.style.pointerEvents = "none";
        return;
      }
      const limbFade = p.depth > 0.72 ? Math.max(0, 1 - (p.depth - 0.72) / 0.22) : 1;
      el.style.opacity = String(limbFade);
      el.style.pointerEvents = "auto";
      el.style.transform = `translate3d(${p.x + SPHERE_INSET}px, ${p.y + SPHERE_INSET}px, 0) translate(-50%, -50%)`;
    });
  }, [offices, size]);

  // Rotation loop: autorotate, drag with inertia, freeze on marker hover.
  useEffect(() => {
    if (reducedMotion) {
      draw();
      return;
    }
    let raf: number;
    let last = performance.now();

    const frame = (now: number) => {
      const dt = Math.min((now - last) / 1000, 0.05);
      last = now;

      if (!dragRef.current) {
        if (Math.abs(velocityRef.current) > 0.01) {
          rotationRef.current += velocityRef.current;
          velocityRef.current *= FRICTION;
        } else if (hoveredIndexRef.current === null) {
          rotationRef.current += AUTOROTATE_DEG_PER_S * dt;
        }
      }
      draw();
      raf = requestAnimationFrame(frame);
    };
    raf = requestAnimationFrame(frame);
    return () => cancelAnimationFrame(raf);
  }, [draw, reducedMotion]);

  const onPointerDown = (e: React.PointerEvent) => {
    dragRef.current = { lastX: e.clientX };
    velocityRef.current = 0;
    (e.currentTarget as Element).setPointerCapture(e.pointerId);
  };
  const onPointerMove = (e: React.PointerEvent) => {
    if (!dragRef.current) return;
    const dx = e.clientX - dragRef.current.lastX;
    dragRef.current.lastX = e.clientX;
    rotationRef.current += dx * DRAG_DEG_PER_PX;
    velocityRef.current = dx * DRAG_DEG_PER_PX;
  };
  const endDrag = () => {
    dragRef.current = null;
  };

  const openOffice = (i: number) => setHoveredIndex(i);
  const closeOffice = () => setHoveredIndex(null);

  const active = hoveredIndex !== null ? offices[hoveredIndex] : null;

  return (
    <div
      ref={containerRef}
      className="relative flex-none cursor-grab touch-pan-y active:cursor-grabbing"
      style={{ width: "clamp(280px, 32vw, 360px)", aspectRatio: "1 / 1" }}
      onPointerDown={onPointerDown}
      onPointerMove={onPointerMove}
      onPointerUp={endDrag}
      onPointerCancel={endDrag}
      onPointerLeave={endDrag}
    >
      <div
        className="pointer-events-none absolute rounded-full border border-dashed border-[#b9daf4]"
        style={{ inset: 2 }}
        aria-hidden
      />
      <div
        className="pointer-events-none absolute rounded-full"
        style={{
          inset: 26,
          background:
            "radial-gradient(circle at 33% 27%, #fff 0%, #f6f9fc 44%, #e6eef6 74%, #d4e1ee 100%)",
          boxShadow: "0 26px 58px rgba(16,58,102,.16), inset -16px -20px 54px rgba(16,58,102,.09)",
        }}
        aria-hidden
      />
      <canvas
        ref={canvasRef}
        className="pointer-events-none absolute"
        style={{ inset: 26, width: "calc(100% - 52px)", height: "calc(100% - 52px)" }}
        aria-hidden
      />

      {offices.map((office, i) => (
        <div
          key={office.city}
          ref={(el) => {
            markerRefs.current[i] = el;
          }}
          className="absolute top-0 left-0 flex items-center justify-center"
          style={{ width: 44, height: 44, marginLeft: -22, marginTop: -22, opacity: 0 }}
          onPointerEnter={() => openOffice(i)}
          onPointerLeave={closeOffice}
          onClick={() => (hoveredIndex === i ? closeOffice() : openOffice(i))}
        >
          <span
            className="block rounded-full shadow-[0_2px_8px_rgba(16,58,102,.35)]"
            style={{
              width: office.big ? 12 : 8,
              height: office.big ? 12 : 8,
              background: "#2f86cf",
              border: office.big ? "2px solid #fff" : undefined,
            }}
          />
        </div>
      ))}

      {active && (
        <div
          ref={cardRef}
          className="absolute top-1/2 z-[6] w-[min(200px,calc(100%-16px))] -translate-y-1/2 rounded-[3px] bg-white shadow-[0_14px_34px_rgba(16,58,102,.22)]"
          style={{ left: "50%" }}
        >
          {active.image ? (
            <div className="relative h-[76px] w-full">
              <Image src={active.image} alt={active.city} fill className="object-cover" />
            </div>
          ) : (
            <Placeholder tone="light" label="OFFICE PHOTO" className="h-[76px]" />
          )}
          <div className="flex items-center gap-2 px-3 pt-2.5">
            <span className="h-2.5 w-[15px] flex-none rounded-[1px]" style={{ background: active.flag }} />
            <span className="text-[11.5px] font-bold text-[#10314f]">{active.city}</span>
          </div>
          <p className="px-3 pt-1 pb-3 text-[9px] leading-[1.5] whitespace-pre-line text-[#5a6b80]">
            {active.address}
          </p>
        </div>
      )}
    </div>
  );
}
