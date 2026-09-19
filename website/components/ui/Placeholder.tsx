import type { CSSProperties, ReactNode } from "react";

const STRIPES: Record<string, string> = {
  navy: "repeating-linear-gradient(135deg, #0c2447 0 6px, #143c6e 6px 12px)",
  warm: "repeating-linear-gradient(135deg, #3a1c0c 0 6px, #5c2f12 6px 12px)",
  cool: "repeating-linear-gradient(135deg, #123a5c 0 8px, #17496f 8px 16px)",
  dark: "repeating-linear-gradient(135deg, #061225 0 6px, #0b1a2f 6px 12px)",
  light: "repeating-linear-gradient(135deg, #dfe6ee 0 6px, #eef2f6 6px 12px)",
};

/**
 * Every image/video in the redesigned homepage is a placeholder — real
 * assets are pending from the client. This renders the same repeating-stripe
 * + mono label treatment the design handoff specified, reused across
 * Services, CaseStudies, Insights, WorkforceBand, Certifications and the
 * testimonial video pane instead of duplicating the gradient CSS in each.
 */
export function Placeholder({
  label,
  tone = "navy",
  className = "",
  style,
  children,
}: {
  label: string;
  tone?: keyof typeof STRIPES;
  className?: string;
  style?: CSSProperties;
  children?: ReactNode;
}) {
  return (
    <div
      className={`relative overflow-hidden ${className}`}
      style={{ background: STRIPES[tone], ...style }}
    >
      <span
        aria-hidden
        className="font-mono-wave absolute bottom-2.5 left-2.5 text-[9px] tracking-[0.06em] text-white/70 uppercase"
      >
        {label}
      </span>
      {children}
    </div>
  );
}
