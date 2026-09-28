import type { PostDetail, PostSummary } from "./types";

const CMS_BASE = process.env.NEXT_PUBLIC_CMS_API_BASE_URL!;

/**
 * content-service resolves the site from this request's Host header (never a query param) —
 * same convention as homeContent.ts's getHomeContent. Unlike the homepage there's no bundled
 * static fallback for posts, so a failure here degrades to an empty list / notFound() rather
 * than stale content — this is a listing/detail page, not the always-must-render homepage.
 */
export async function getPosts(params?: { tag?: string; q?: string }): Promise<PostSummary[]> {
  const search = new URLSearchParams();
  if (params?.tag) search.set("tag", params.tag);
  if (params?.q) search.set("q", params.q);
  const qs = search.toString();

  try {
    const res = await fetch(`${CMS_BASE}/content-service/v1/posts${qs ? `?${qs}` : ""}`, {
      next: { revalidate: 60 },
    });
    if (!res.ok) {
      console.error(`Failed to load posts: ${res.status} ${res.statusText}`);
      return [];
    }
    const body = (await res.json()) as { data: PostSummary[] };
    return body.data ?? [];
  } catch (err) {
    console.error("Failed to load posts", err);
    return [];
  }
}

export async function getPost(slug: string): Promise<PostDetail | null> {
  try {
    const res = await fetch(`${CMS_BASE}/content-service/v1/posts/${encodeURIComponent(slug)}`, {
      next: { revalidate: 60 },
    });
    if (!res.ok) {
      if (res.status !== 404) {
        console.error(`Failed to load post "${slug}": ${res.status} ${res.statusText}`);
      }
      return null;
    }
    const body = (await res.json()) as { data: PostDetail };
    return body.data ?? null;
  } catch (err) {
    console.error(`Failed to load post "${slug}"`, err);
    return null;
  }
}
