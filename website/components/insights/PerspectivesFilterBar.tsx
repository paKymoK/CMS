"use client";

import { useEffect, useRef, useState, useTransition } from "react";
import { useRouter } from "@/i18n/navigation";
import {
  perspectivesHref,
  type PerspectivesQuery,
} from "@/lib/cms/perspectivesNav";
import { useReportPending } from "@/components/list/ResultsPending";

const SEARCH_DELAY_MS = 400;

const Caret = () => (
  <svg viewBox="0 0 12 8" className="csl-caret" aria-hidden>
    <path
      d="m1 1.5 5 5 5-5"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
    />
  </svg>
);

const sentence = (s: string) => s.charAt(0).toUpperCase() + s.slice(1);

/**
 * The Perspectives filter bar (wireframe "Filter by"): Content Type (a post's category), Industry
 * and Topic (its tags), then a pill-shaped search with its magnifier on the left. Unlike the Case
 * Studies bar there is no Search button — typing searches after a short pause (Enter searches at
 * once) and a clear button resets it. Posts carry no industry yet, so Industry is drawn but inert,
 * as on Case Studies. Every change keeps the other filters and returns to page one.
 */
export function PerspectivesFilterBar({
  categories,
  tags,
  current,
}: {
  categories: string[];
  tags: string[];
  current: Pick<PerspectivesQuery, "category" | "tag" | "q" | "sort">;
}) {
  const router = useRouter();
  const [isPending, startTransition] = useTransition();
  useReportPending(isPending);
  const [text, setText] = useState(current.q ?? "");
  const timer = useRef<ReturnType<typeof setTimeout> | undefined>(undefined);
  useEffect(() => () => clearTimeout(timer.current), []);

  const go = (next: Partial<PerspectivesQuery>, replace = false) => {
    const href = perspectivesHref({ ...current, ...next, page: 1 });
    startTransition(() => (replace ? router.replace(href) : router.push(href)));
  };

  const search = (value: string, delay: number) => {
    setText(value);
    clearTimeout(timer.current);
    timer.current = setTimeout(
      () => go({ q: value.trim() || undefined }, true),
      delay,
    );
  };

  return (
    <form
      className="psp-filter"
      role="search"
      aria-label="Filter perspectives"
      onSubmit={(e) => {
        e.preventDefault();
        search(text, 0);
      }}
    >
      <span className="csl-filter-label">Filter by</span>

      <label className="csl-box psp-box">
        <span className="sr-only">Content type</span>
        <select
          value={current.category ?? ""}
          onChange={(e) => go({ category: e.target.value || undefined })}
        >
          <option value="">Content Type</option>
          {categories.map((c) => (
            <option key={c} value={c}>
              {sentence(c)}
            </option>
          ))}
        </select>
        <Caret />
      </label>

      <button
        type="button"
        className="csl-box psp-box"
        disabled
        aria-label="Industry"
      >
        <span>Industry</span>
        <Caret />
      </button>

      <label className="csl-box psp-box">
        <span className="sr-only">Topic</span>
        <select
          value={current.tag ?? ""}
          onChange={(e) => go({ tag: e.target.value || undefined })}
        >
          <option value="">Topic</option>
          {tags.map((t) => (
            <option key={t} value={t}>
              {t}
            </option>
          ))}
        </select>
        <Caret />
      </label>

      <span className="psp-divider" aria-hidden />

      <label className="psp-search">
        <span className="sr-only">Search perspectives</span>
        <svg viewBox="0 0 21 21" className="psp-search-icon" aria-hidden>
          <circle
            cx="9"
            cy="9"
            r="7"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
          />
          <path
            d="m14.5 14.5 5 5"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
          />
        </svg>
        <input
          type="search"
          value={text}
          onChange={(e) => search(e.target.value, SEARCH_DELAY_MS)}
          placeholder="Search perspectives…"
          autoComplete="off"
        />
        {text && (
          <button
            type="button"
            className="psp-search-clear"
            aria-label="Clear search"
            onClick={() => search("", 0)}
          >
            <svg viewBox="0 0 12 12" aria-hidden>
              <path
                d="m2 2 8 8M10 2l-8 8"
                fill="none"
                stroke="currentColor"
                strokeWidth="1.8"
                strokeLinecap="round"
              />
            </svg>
          </button>
        )}
      </label>
    </form>
  );
}
