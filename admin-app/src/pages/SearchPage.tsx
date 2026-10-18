import { useQuery } from "@tanstack/react-query";
import { Empty, Spin, Tag } from "antd";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { siteLabel } from "../config/sites";
import { useSite } from "../lib/useSite";
import {
  adminSearch,
  editorPathFor,
  searchPageHref,
  typeLabel,
  type SearchHit,
} from "../lib/adminSearch";

/**
 * Results of a search from the top bar (/search?q=…): every matching item of the site — drafts
 * included, every content type — best match first, each opening in its editor. A misspelt query is
 * searched as corrected, with a link to search exactly what was typed instead (?exact=1); when
 * nothing matches at all, a likelier spelling is offered.
 */
export default function SearchPage() {
  const { site } = useSite();
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const q = params.get("q")?.trim() ?? "";
  const exact = params.get("exact") === "1";

  const query = useQuery({
    queryKey: ["admin-search", site, q, exact],
    queryFn: () => adminSearch(site!, q, exact),
    enabled: !!site && q.length > 0,
  });
  const result = query.data;
  const hits = result?.hits ?? [];

  const open = (hit: SearchHit) => {
    const path = editorPathFor(hit.type, hit.id);
    if (path) navigate(path);
  };

  return (
    <div>
      <div className="mb-6">
        <div className="cms-eyebrow mb-3">— Search · {site ? siteLabel(site) : ""}</div>
        <h1
          className="m-0"
          style={{ fontSize: "clamp(24px,3vw,32px)", fontWeight: 700, letterSpacing: "-.02em", color: "#002d68" }}
        >
          {q ? <>Results for “{q}”</> : "Search"}
        </h1>
        <p className="mt-2 mb-0" style={{ fontSize: 15, lineHeight: 1.6, color: "#55585f" }}>
          {!q && "Type in the search box above to find content on any screen."}
          {q && query.isLoading && "Searching…"}
          {q && result && !result.correctedQuery && (
            <>
              {hits.length} {hits.length === 1 ? "item" : "items"}
            </>
          )}
          {q && result?.correctedQuery && (
            <>
              {hits.length} {hits.length === 1 ? "item" : "items"} for{" "}
              <strong style={{ color: "#002d68" }}>{result.correctedQuery}</strong> ·{" "}
              <Link to={searchPageHref(result.query, true)}>Search instead for “{result.query}”</Link>
            </>
          )}
          {q && result?.didYouMean && (
            <>
              No results. Did you mean <Link to={searchPageHref(result.didYouMean)}>{result.didYouMean}</Link>?
            </>
          )}
        </p>
      </div>

      {query.isLoading && q && (
        <div style={{ padding: "48px 0", textAlign: "center" }}>
          <Spin />
        </div>
      )}

      {result && hits.length === 0 && !result.didYouMean && (
        <Empty description={`Nothing matches “${q}”`} style={{ padding: "48px 0" }} />
      )}

      {hits.length > 0 && (
        <ul className="m-0 p-0" style={{ listStyle: "none", background: "#fff", border: "1px solid #dfe3e8", borderRadius: 14 }}>
          {hits.map((hit, i) => {
            const editable = editorPathFor(hit.type, hit.id) !== null;
            const published = hit.status === "PUBLISHED";
            return (
              <li key={hit.id} style={{ borderTop: i ? "1px solid #eef1f4" : undefined }}>
                <button
                  type="button"
                  disabled={!editable}
                  onClick={() => open(hit)}
                  className="w-full text-left"
                  style={{
                    display: "flex",
                    alignItems: "center",
                    gap: 14,
                    padding: "14px 18px",
                    border: 0,
                    background: "transparent",
                    cursor: editable ? "pointer" : "default",
                    font: "inherit",
                  }}
                >
                  <span style={{ flex: 1, minWidth: 0 }}>
                    <span
                      style={{
                        display: "block",
                        fontSize: 16,
                        fontWeight: 600,
                        color: "#002d68",
                        overflow: "hidden",
                        textOverflow: "ellipsis",
                        whiteSpace: "nowrap",
                      }}
                    >
                      {hit.title}
                    </span>
                    {hit.slug && (
                      <span style={{ fontFamily: "var(--font-mono)", fontSize: 11, color: "#6a7c90" }}>/{hit.slug}</span>
                    )}
                  </span>
                  <Tag style={{ marginInlineEnd: 0 }}>{typeLabel(hit.type)}</Tag>
                  <span
                    style={{
                      padding: "3px 10px",
                      borderRadius: 999,
                      fontFamily: "var(--font-mono)",
                      fontSize: 10,
                      letterSpacing: ".08em",
                      background: published ? "rgba(0,169,255,.12)" : "#eef1f4",
                      color: published ? "#0159be" : "#5a5d64",
                    }}
                  >
                    {hit.status}
                  </span>
                </button>
              </li>
            );
          })}
        </ul>
      )}
    </div>
  );
}
