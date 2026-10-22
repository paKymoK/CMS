import { useEffect, useRef, useState, type ReactNode } from "react";
import { AutoComplete, Input, Tag } from "antd";
import { SearchOutlined, FileTextOutlined } from "@ant-design/icons";
import { useLocation, useNavigate } from "react-router-dom";
import { useSite } from "../lib/useSite";
import {
  adminSuggest,
  editorPathFor,
  searchPageHref,
  typeLabel,
  type Suggestion,
} from "../lib/adminSearch";

const MIN_CHARS = 2;
const DELAY_MS = 220;

/** `text` with the part the user typed in bold (where it occurs), so they see why it matches. */
function highlight(text: string, typed: string): ReactNode {
  const at = typed ? text.toLowerCase().indexOf(typed.toLowerCase()) : -1;
  if (at < 0) return text;
  return (
    <>
      {text.slice(0, at)}
      <b>{text.slice(at, at + typed.length)}</b>
      {text.slice(at + typed.length)}
    </>
  );
}

/**
 * Search box of the admin top bar: type to get suggestions — completed queries ("cyb" →
 * "cybersecurity") and matching items, drafts included, each opening in its editor — or press Enter
 * to see every match on the search page. It forgives spelling like the public site's search does.
 */
export default function AdminSearchBox() {
  const { site } = useSite();
  const navigate = useNavigate();
  const location = useLocation();
  // The search page's own query (empty elsewhere): the box starts with it and follows it.
  const onSearchPage = location.pathname === "/search";
  const pageQuery = onSearchPage ? (new URLSearchParams(location.search).get("q") ?? "") : "";
  const [text, setText] = useState(pageQuery);
  const [suggestions, setSuggestions] = useState<Suggestion[]>([]);
  // Enter both selects a highlighted option and submits the input; only the first should navigate.
  const picked = useRef(false);
  const [syncedQuery, setSyncedQuery] = useState(pageQuery);
  if (syncedQuery !== pageQuery) {
    // Adjusting state while rendering (React's documented way to follow a changing value).
    setSyncedQuery(pageQuery);
    setText(pageQuery);
  }

  const typed = text.trim();
  const searchable = !!site && typed.length >= MIN_CHARS;
  useEffect(() => {
    if (!site || !searchable) return;
    const controller = new AbortController();
    const timer = setTimeout(() => {
      adminSuggest(site, typed, controller.signal)
        // A term identical to what is already typed adds nothing.
        .then((found) => setSuggestions(found.filter((s) => s.text.toLowerCase() !== typed.toLowerCase())))
        .catch(() => {
          // Aborted by the next keystroke, or the request failed (the interceptor already told the user).
        });
    }, DELAY_MS);
    return () => {
      clearTimeout(timer);
      controller.abort();
    };
  }, [site, typed, searchable]);

  // Stale suggestions are hidden, not cleared, when the text gets too short to search.
  const shown = searchable ? suggestions : [];
  const options = shown.map((s, i) => ({
    value: `${i}`,
    label:
      s.kind === "term" ? (
        <span style={{ display: "flex", alignItems: "center", gap: 10 }}>
          <SearchOutlined style={{ color: "#6b7b8f" }} />
          <span>{highlight(s.text, typed)}</span>
        </span>
      ) : (
        <span style={{ display: "flex", alignItems: "center", gap: 10 }}>
          <FileTextOutlined style={{ color: "#6b7b8f" }} />
          <span style={{ flex: 1, minWidth: 0, overflow: "hidden", textOverflow: "ellipsis" }}>
            {highlight(s.text, typed)}
          </span>
          {s.type && <Tag style={{ marginInlineEnd: 0 }}>{typeLabel(s.type)}</Tag>}
        </span>
      ),
  }));

  const submit = (q: string) => {
    const query = q.trim();
    if (query) navigate(searchPageHref(query));
  };

  const onSelect = (value: string) => {
    const suggestion = shown[Number(value)];
    if (!suggestion) return;
    picked.current = true;
    setTimeout(() => {
      picked.current = false;
    }, 0);
    if (suggestion.kind === "term") {
      setText(suggestion.text);
      submit(suggestion.text);
      return;
    }
    const path = suggestion.type && suggestion.id != null ? editorPathFor(suggestion.type, suggestion.id) : null;
    // An item the admin has no screen for is shown on the search page instead.
    if (path) navigate(path);
    else submit(suggestion.text);
  };

  return (
    <AutoComplete
      className="cms-search"
      value={text}
      options={options}
      onChange={setText}
      onSelect={onSelect}
      popupMatchSelectWidth={360}
      notFoundContent={null}
      disabled={!site}
    >
      <Input
        allowClear
        prefix={<SearchOutlined style={{ color: "#6b7b8f" }} />}
        placeholder="Search content…"
        aria-label="Search content"
        onPressEnter={() => {
          if (!picked.current) submit(text);
        }}
      />
    </AutoComplete>
  );
}
