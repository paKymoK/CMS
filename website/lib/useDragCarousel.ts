"use client";

import { useLayoutEffect, useRef, useState, type PointerEvent } from "react";

/**
 * Index-based drag/arrow carousel. Reads actual rendered item offsets off
 * the DOM (`trackRef.current.children[i].offsetLeft`) rather than assuming
 * a uniform card width, so it works for both fixed-width tracks (Services)
 * and variable-width tracks (Insights' featured+peek layout). The track
 * element the ref is attached to must be `position: relative` (or itself
 * establish a positioning context) so children's `offsetLeft` is relative
 * to the track, not some further-up ancestor.
 */
export function useDragCarousel(itemCount: number) {
  const trackRef = useRef<HTMLDivElement>(null);
  const [index, setIndexState] = useState(0);
  const [dragPx, setDragPx] = useState(0);
  const [isDragging, setIsDragging] = useState(false);
  const [translateX, setTranslateX] = useState(0);
  const dragStartX = useRef<number | null>(null);

  const clamp = (i: number) => Math.max(0, Math.min(itemCount - 1, i));
  const goTo = (i: number) => setIndexState(clamp(i));

  // Layout reads happen here (after DOM mutations, before paint), never
  // directly in the render body — the latter is a lint-flagged anti-pattern
  // and also just wrong: refs aren't render inputs.
  useLayoutEffect(() => {
    const el = trackRef.current?.children[index] as HTMLElement | undefined;
    const offset = el ? el.offsetLeft : 0;
    setTranslateX(-offset + dragPx);
  }, [index, dragPx, itemCount]);

  const onPointerDown = (e: PointerEvent<HTMLDivElement>) => {
    dragStartX.current = e.clientX;
    setIsDragging(true);
    e.currentTarget.setPointerCapture(e.pointerId);
  };
  const onPointerMove = (e: PointerEvent<HTMLDivElement>) => {
    if (dragStartX.current === null) return;
    setDragPx(e.clientX - dragStartX.current);
  };
  const endDrag = () => {
    if (dragStartX.current === null) return;
    const dx = dragPx;
    dragStartX.current = null;
    setIsDragging(false);
    setDragPx(0);
    const DRAG_THRESHOLD_PX = 40;
    if (dx < -DRAG_THRESHOLD_PX) goTo(index + 1);
    else if (dx > DRAG_THRESHOLD_PX) goTo(index - 1);
  };

  return {
    trackRef,
    index,
    goTo,
    translateX,
    isDragging,
    atStart: index === 0,
    atEnd: index === itemCount - 1,
    dragHandlers: {
      onPointerDown,
      onPointerMove,
      onPointerUp: endDrag,
      onPointerCancel: endDrag,
    },
  };
}
