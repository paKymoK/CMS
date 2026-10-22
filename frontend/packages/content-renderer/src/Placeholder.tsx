const STRIPES = {
  navy: "repeating-linear-gradient(135deg, #0c2447 0 6px, #143c6e 6px 12px)",
  warm: "repeating-linear-gradient(135deg, #3a1c0c 0 6px, #5c2f12 6px 12px)",
  cool: "repeating-linear-gradient(135deg, #123a5c 0 8px, #17496f 8px 16px)",
  dark: "repeating-linear-gradient(135deg, #061225 0 6px, #0b1a2f 6px 12px)",
  light: "repeating-linear-gradient(135deg, #dfe6ee 0 6px, #eef2f6 6px 12px)",
} as const;

/** Striped stand-in for a missing photo, same look as the website's Placeholder. */
export function Placeholder({
  label,
  tone = "navy",
  className = "",
  fill = false,
}: {
  label: string;
  tone?: keyof typeof STRIPES;
  className?: string;
  /** Cover the (relative) parent instead of sizing itself. */
  fill?: boolean;
}) {
  return (
    <div
      className={`${fill ? "absolute inset-0" : "relative"} overflow-hidden ${className}`}
      style={{ background: STRIPES[tone] }}
    >
      <span
        aria-hidden
        className="font-mono-wave absolute bottom-2.5 left-2.5 text-[9px] tracking-[0.06em] text-white/70 uppercase"
      >
        {label}
      </span>
    </div>
  );
}
