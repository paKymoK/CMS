"use client";

import {
  useCallback,
  useEffect,
  useLayoutEffect,
  useRef,
  useState,
  type ElementType,
  type ReactNode,
} from "react";

type Placement = "top" | "bottom";

/**
 * The site's one tooltip: a navy bubble that follows the app's tokens (see `.cmc-tip` in
 * globals.css). It wraps the element that should trigger it — the wrapper itself is rendered as
 * `as` (default `span`) with your `className`, so a whole card can be the trigger without an extra
 * box. Opens on hover (after a short delay, so sweeping the mouse over a row doesn't flash) and on
 * keyboard focus, closes on leave/blur/Escape, flips below when there's no room above, and is nudged
 * to stay inside the viewport.
 *
 * `truncatedSelector`: when given, the tooltip only opens if the matching descendant is actually
 * cut off (line-clamped or ellipsised) — use it to reveal clipped text without showing a redundant
 * bubble on text that already fits.
 *
 * The bubble is `aria-hidden`: it exists for pointer/keyboard users who can't see the clipped text;
 * give the trigger a full accessible name (e.g. aria-label) for screen readers.
 */
export function Tooltip({
  content,
  children,
  as: Tag = "span",
  className,
  placement = "top",
  truncatedSelector,
}: {
  content: ReactNode;
  children: ReactNode;
  as?: ElementType;
  className?: string;
  placement?: Placement;
  truncatedSelector?: string;
}) {
  const root = useRef<HTMLElement>(null);
  const bubble = useRef<HTMLSpanElement>(null);
  const timer = useRef<ReturnType<typeof setTimeout> | undefined>(undefined);
  const [open, setOpen] = useState(false);
  const [place, setPlace] = useState<Placement>(placement);
  const [shift, setShift] = useState(0);

  const isClipped = useCallback(() => {
    if (!truncatedSelector) return true;
    const el = root.current?.querySelector<HTMLElement>(truncatedSelector);
    return (
      !!el &&
      (el.scrollHeight > el.clientHeight + 1 ||
        el.scrollWidth > el.clientWidth + 1)
    );
  }, [truncatedSelector]);

  const show = useCallback(
    (delay: number) => {
      clearTimeout(timer.current);
      timer.current = setTimeout(() => {
        if (isClipped()) setOpen(true);
      }, delay);
    },
    [isClipped],
  );
  const hide = useCallback(() => {
    clearTimeout(timer.current);
    setOpen(false);
  }, []);

  useEffect(() => () => clearTimeout(timer.current), []);

  // Keep the bubble on screen: flip to the other side if it would leave the viewport vertically,
  // and slide it sideways (the arrow stays on the trigger's centre) if it would leave horizontally.
  useLayoutEffect(() => {
    if (!open || !bubble.current || !root.current) return;
    const margin = 8;
    const b = bubble.current.getBoundingClientRect();
    const t = root.current.getBoundingClientRect();
    const room =
      placement === "top"
        ? t.top - b.height - margin
        : innerHeight - t.bottom - b.height - margin;
    setPlace(room < 0 ? (placement === "top" ? "bottom" : "top") : placement);
    const left = b.left - shift;
    const right = b.right - shift;
    let next = 0;
    if (left < margin) next = margin - left;
    else if (right > innerWidth - margin) next = innerWidth - margin - right;
    setShift(next);
    // Re-measure only when it opens; the placement/shift it computes don't change its size.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open, placement]);

  return (
    <Tag
      ref={root}
      className={className ? `cmc-tip-host ${className}` : "cmc-tip-host"}
      onMouseEnter={() => show(150)}
      onMouseLeave={hide}
      onFocus={() => show(0)}
      onBlur={hide}
      onKeyDown={(e: React.KeyboardEvent) => e.key === "Escape" && hide()}
    >
      {children}
      <span
        ref={bubble}
        aria-hidden
        className="cmc-tip"
        data-open={open || undefined}
        data-place={place}
        style={{ "--tip-shift": `${shift}px` } as React.CSSProperties}
      >
        {content}
      </span>
    </Tag>
  );
}
