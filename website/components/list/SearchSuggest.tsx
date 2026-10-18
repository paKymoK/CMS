"use client";

import {
  useEffect,
  useId,
  useState,
  type KeyboardEvent,
  type ReactNode,
} from "react";
import { useRouter } from "@/i18n/navigation";
import type { Suggestion } from "@/lib/cms/search";

const MIN_CHARS = 2;
const DELAY_MS = 150;

/** `text` with the part the visitor typed in bold (where it occurs), so they see why it matches. */
function highlight(text: string, typed: string): ReactNode {
  const at = text.toLowerCase().indexOf(typed.toLowerCase());
  if (!typed || at < 0) return text;
  return (
    <>
      {text.slice(0, at)}
      <b>{text.slice(at, at + typed.length)}</b>
      {text.slice(at + typed.length)}
    </>
  );
}

const SearchIcon = () => (
  <svg viewBox="0 0 21 21" className="sgs-icon" aria-hidden>
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
);

const DocIcon = () => (
  <svg viewBox="0 0 20 20" className="sgs-icon" aria-hidden>
    <path
      d="M5 2.5h7l3.5 3.5v11.5H5z M12 2.5V6h3.5 M7.5 10h5 M7.5 13h5"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.6"
      strokeLinejoin="round"
      strokeLinecap="round"
    />
  </svg>
);

/**
 * As-you-type suggestions for a search box: completed queries ("cyb" → "cybersecurity") and
 * matching items, fetched from /api/search/suggest after a short pause. Returns the props to spread
 * on the <input> (combobox semantics, arrow keys + Enter + Escape) and the dropdown to render next
 * to it inside a `position: relative` parent.
 *
 * Picking a term hands it to `onPickTerm` (the caller searches for it); picking an item goes to its
 * page via `titleHref`. The input keeps focus throughout — pressing an option doesn't blur it.
 */
export function useSearchSuggest({
  type,
  text,
  itemLabel,
  onPickTerm,
  titleHref,
}: {
  /** Content type to suggest from: "post" or "case_study". */
  type: string;
  /** The input's current text. */
  text: string;
  /** What an item suggestion is called, e.g. "Perspective" — shown beside its title. */
  itemLabel: string;
  onPickTerm: (term: string) => void;
  titleHref: (suggestion: Suggestion) => string;
}) {
  const router = useRouter();
  const listId = useId();
  const [items, setItems] = useState<Suggestion[]>([]);
  const [open, setOpen] = useState(false);
  const [active, setActive] = useState(-1);
  const typed = text.trim();

  useEffect(() => {
    if (!open || typed.length < MIN_CHARS) return;
    const controller = new AbortController();
    const timer = setTimeout(async () => {
      try {
        const res = await fetch(
          `/api/search/suggest?${new URLSearchParams({ q: typed, type })}`,
          {
            signal: controller.signal,
          },
        );
        if (!res.ok) return;
        const body = (await res.json()) as { suggestions?: Suggestion[] };
        // A term identical to what is already typed adds nothing.
        setItems(
          (body.suggestions ?? []).filter(
            (s) => s.text.toLowerCase() !== typed.toLowerCase(),
          ),
        );
        setActive(-1);
      } catch {
        // Aborted by the next keystroke, or offline: the dropdown just stays as it was.
      }
    }, DELAY_MS);
    return () => {
      clearTimeout(timer);
      controller.abort();
    };
  }, [typed, type, open]);

  const visible = open && typed.length >= MIN_CHARS && items.length > 0;

  const pick = (suggestion: Suggestion) => {
    setOpen(false);
    if (suggestion.kind === "title") router.push(titleHref(suggestion));
    else onPickTerm(suggestion.text);
  };

  const onKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Escape") {
      // While the dropdown is open Escape only closes it: a search input would otherwise also
      // clear its own text. With it closed, Escape keeps its native "clear the field" meaning.
      if (visible) e.preventDefault();
      setOpen(false);
    } else if (!visible) {
      if (e.key === "ArrowDown") setOpen(true);
    } else if (e.key === "ArrowDown") {
      e.preventDefault();
      setActive((i) => (i + 1) % items.length);
    } else if (e.key === "ArrowUp") {
      e.preventDefault();
      setActive((i) => (i <= 0 ? items.length - 1 : i - 1));
    } else if (e.key === "Enter") {
      if (active >= 0) {
        e.preventDefault();
        pick(items[active]);
      } else {
        // Nothing highlighted: Enter submits the search as typed, and the dropdown has done its job.
        setOpen(false);
      }
    }
  };

  const inputProps = {
    role: "combobox" as const,
    "aria-expanded": visible,
    "aria-controls": listId,
    "aria-autocomplete": "list" as const,
    "aria-activedescendant":
      visible && active >= 0 ? `${listId}-${active}` : undefined,
    onFocus: () => setOpen(true),
    onInput: () => setOpen(true),
    onBlur: () => setOpen(false),
    onKeyDown,
  };

  const popup = visible ? (
    // Pressing an option must not blur the input, or the dropdown would close before the click lands.
    <ul
      id={listId}
      role="listbox"
      className="sgs"
      onMouseDown={(e) => e.preventDefault()}
    >
      {items.map((s, i) => (
        <li
          key={`${s.kind}-${s.id ?? s.text}`}
          id={`${listId}-${i}`}
          role="option"
          aria-selected={i === active}
          className="sgs-item"
          data-active={i === active || undefined}
          onClick={() => pick(s)}
          onMouseMove={() => setActive(i)}
        >
          {s.kind === "term" ? <SearchIcon /> : <DocIcon />}
          <span className="sgs-text">{highlight(s.text, typed)}</span>
          {s.kind === "title" && <span className="sgs-tag">{itemLabel}</span>}
        </li>
      ))}
    </ul>
  ) : null;

  return { inputProps, popup };
}
