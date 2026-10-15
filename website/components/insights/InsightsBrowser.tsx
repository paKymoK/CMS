"use client";

import { useMemo, useState } from "react";
import { InsightCard } from "@/components/insights/InsightCard";
import type { PostSummary } from "@/lib/cms/types";

const PAGE_SIZE = 6;

/**
 * The Insights listing (Figma "Insight" → "Capabilities"): a topic filter — "All" plus every tag
 * in use — over a three-column grid of article cards, six at a time with a "Show More" button.
 */
export function InsightsBrowser({ posts }: { posts: PostSummary[] }) {
  const [tag, setTag] = useState<string | null>(null);
  const [shown, setShown] = useState(PAGE_SIZE);

  const tags = useMemo(() => Array.from(new Set(posts.flatMap((p) => p.tags))).sort(), [posts]);
  const filtered = tag ? posts.filter((p) => p.tags.includes(tag)) : posts;
  const visible = filtered.slice(0, shown);

  const choose = (next: string | null) => {
    setTag(next);
    setShown(PAGE_SIZE);
  };

  return (
    <section id="insights-list" aria-label="Insights" className="design-canvas">
      <div className="ins-list">
        <div className="ins-filter">
          <div className="ins-filter-tab">Topic</div>
          <div className="ins-chips" role="group" aria-label="Filter by topic">
            {[null, ...tags].map((t) => (
              <button
                key={t ?? "all"}
                type="button"
                className="ins-chip"
                aria-pressed={tag === t}
                onClick={() => choose(t)}
              >
                {t ?? "All"}
              </button>
            ))}
          </div>
        </div>

        {visible.length === 0 ? (
          <p className="ins-empty">No insights yet — check back soon.</p>
        ) : (
          <div className="ins-grid">
            {visible.map((post) => (
              <InsightCard key={post.id} post={post} />
            ))}
          </div>
        )}

        {filtered.length > shown && (
          <button type="button" className="ins-showmore" onClick={() => setShown((n) => n + PAGE_SIZE)}>
            Show More
          </button>
        )}
      </div>
    </section>
  );
}
