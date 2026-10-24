import { useQuery } from "@tanstack/react-query";
import { Empty, Spin, Tag } from "antd";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
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
        <h1 className="cms-page-title">{q ? <>Results for “{q}”</> : "Search"}</h1>
        <p className="cms-page-sub">
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
              <strong style={{ color: "#0b1b3a" }}>{result.correctedQuery}</strong> ·{" "}
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
        <ul className="m-0 p-0 cms-card" style={{ listStyle: "none", overflow: "hidden" }}>
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
                        color: "#0b1b3a",
                        overflow: "hidden",
                        textOverflow: "ellipsis",
                        whiteSpace: "nowrap",
                      }}
                    >
                      {hit.title}
                    </span>
                    {hit.slug && (
                      <span style={{ fontFamily: "var(--font-mono)", fontSize: 11, color: "#7b8aa5" }}>/{hit.slug}</span>
                    )}
                  </span>
                  <Tag style={{ marginInlineEnd: 0 }}>{typeLabel(hit.type)}</Tag>
                  <span
                    style={{
                      padding: "3px 10px",
                      borderRadius: 999,
                      fontSize: 12,
                      fontWeight: 600,
                      background: published ? "#e3f7ee" : hit.status === "SCHEDULED" ? "#e6f1ff" : hit.status === "ARCHIVED" ? "#eceff4" : "#fff4e0",
                      color: published ? "#0b7a55" : hit.status === "SCHEDULED" ? "#0058b3" : hit.status === "ARCHIVED" ? "#5b6b86" : "#9a5b00",
                    }}
                  >
                    {published ? "Published" : hit.status === "SCHEDULED" ? "Scheduled" : hit.status === "ARCHIVED" ? "Archived" : "Draft"}
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
