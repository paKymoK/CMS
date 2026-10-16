"use client";

import type { FormEvent } from "react";
import { useRouter } from "@/i18n/navigation";

const Caret = () => (
  <svg viewBox="0 0 12 8" className="csl-caret" aria-hidden>
    <path d="m1 1.5 5 5 5-5" fill="none" stroke="currentColor" strokeWidth="2" />
  </svg>
);

/**
 * The "Filter by:" bar (Figma "filter bar"): Capabilities, Industry, Geo and a text search. Case
 * studies only carry a category today, so Capabilities (= category) and Search work, while Industry
 * and Geo are drawn as in the design but inert until those fields exist.
 */
export function CaseStudyFilterBar({
  categories,
  category,
  q,
}: {
  categories: string[];
  category?: string;
  q?: string;
}) {
  const router = useRouter();

  const go = (form: HTMLFormElement) => {
    const data = new FormData(form);
    const query: Record<string, string> = {};
    const c = String(data.get("category") ?? "");
    const text = String(data.get("q") ?? "").trim();
    if (c) query.category = c;
    if (text) query.q = text;
    router.push({ pathname: "/case-studies", query });
  };

  const onSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    go(e.currentTarget);
  };

  return (
    <form className="csl-filter" role="search" aria-label="Filter case studies" onSubmit={onSubmit}>
      <span className="csl-filter-label">Filter by:</span>

      <label className="csl-box csl-box--cap">
        <span className="sr-only">Capabilities</span>
        <select
          name="category"
          defaultValue={category ?? ""}
          onChange={(e) => go(e.currentTarget.form!)}
        >
          <option value="">Capabilities</option>
          {categories.map((c) => (
            <option key={c} value={c}>
              {c}
            </option>
          ))}
        </select>
        <Caret />
      </label>

      <button type="button" className="csl-box csl-box--ind" disabled aria-label="Industry">
        <span>Industry</span>
        <Caret />
      </button>
      <button type="button" className="csl-box csl-box--geo" disabled aria-label="Geo">
        <span>Geo</span>
        <Caret />
      </button>

      <label className="csl-box csl-box--search">
        <span className="sr-only">Search case studies</span>
        <input type="search" name="q" defaultValue={q ?? ""} placeholder="Search" autoComplete="off" />
        <svg viewBox="0 0 21 21" className="csl-mag" aria-hidden>
          <circle cx="9" cy="9" r="7" fill="none" stroke="currentColor" strokeWidth="2" />
          <path d="m14.5 14.5 5 5" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
        </svg>
      </label>

      <button type="submit" className="csl-go">
        Search
      </button>
    </form>
  );
}
