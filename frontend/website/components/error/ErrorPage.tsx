import type { ReactNode } from "react";

type ErrorPageProps = {
  /** Status shown large behind the copy, e.g. "404" or "500". */
  code: string;
  eyebrow: string;
  title: string;
  body: string;
  actions: ReactNode;
  /** Small mono line under the actions, e.g. a reference id for support. */
  footnote?: string;
};

/**
 * Shared layout for the 404 and 5xx screens. The site header is absolutely positioned over the top
 * 58px of every page, so the section starts below it.
 */
export function ErrorPage({ code, eyebrow, title, body, actions, footnote }: ErrorPageProps) {
  return (
    <section className="relative isolate overflow-hidden bg-[var(--cmc-tint)] pt-[58px]">
      <span
        aria-hidden
        className="pointer-events-none absolute -right-[2vw] top-[58px] -z-10 select-none text-[clamp(180px,34vw,520px)] font-bold leading-[0.85] tracking-[-0.06em] text-cmc-blue/[0.07]"
      >
        {code}
      </span>
      <div className="mx-auto flex min-h-[min(640px,calc(100vh-58px))] w-full max-w-[1200px] flex-col justify-center gap-6 px-6 py-16 md:px-10">
        <p className="font-mono-wave text-[13px] font-medium uppercase tracking-[0.14em] text-cmc-blue">
          {code} · {eyebrow}
        </p>
        <h1 className="max-w-[16ch] text-balance text-[clamp(36px,5.4vw,72px)] font-semibold leading-[1.04] tracking-[-0.025em] text-cmc-navy">
          {title}
        </h1>
        <p className="max-w-[52ch] text-[17px] leading-[1.6] text-[#44546a]">{body}</p>
        <div className="mt-2 flex flex-wrap items-center gap-3">{actions}</div>
        {footnote ? <p className="font-mono-wave text-[12px] text-cmc-ink">{footnote}</p> : null}
      </div>
    </section>
  );
}

export const primaryActionClass =
  "inline-flex h-12 items-center rounded-full bg-cmc-sky px-7 text-[15px] font-medium text-cmc-navy-deep transition-colors hover:bg-[var(--cmc-sky-active)] focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-cmc-blue";
export const secondaryActionClass =
  "inline-flex h-12 items-center rounded-full border border-cmc-navy/25 px-7 text-[15px] font-medium text-cmc-navy transition-colors hover:border-cmc-navy hover:bg-white focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-cmc-blue";
