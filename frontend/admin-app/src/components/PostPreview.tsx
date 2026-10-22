import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import { contentApi } from "../lib/api";
import { SITES } from "../config/sites";
import { buildPreviewPost, relatedFor, type PostDraft, type PostListItem } from "../lib/contentPreview";
import type { PreviewPage } from "../lib/previewProtocol";
import PreviewPanel from "./PreviewPanel";

/** Preview of the post being edited: the form state, plus the related-articles strip from the site's published posts. */
export default function PostPreview({ site, slug, draft, onClose }: { site: string; slug: string; draft: PostDraft; onClose: () => void }) {
  const postsQuery = useQuery({
    queryKey: ["preview-related", "posts", site],
    queryFn: async () => {
      const { data } = await contentApi.get("/v1/admin/posts", { params: { site } });
      return data.data as PostListItem[];
    },
    staleTime: 60_000,
  });

  const page = useMemo<PreviewPage>(() => {
    const subdomain = SITES.find((s) => s.code === site)?.subdomain ?? "example.com";
    return {
      kind: "post",
      post: buildPreviewPost(draft, relatedFor(draft, postsQuery.data ?? [])),
      pageUrl: `https://${subdomain}/en/insights/${slug || "your-post"}`,
    };
  }, [draft, slug, site, postsQuery.data]);

  return <PreviewPanel page={page} onClose={onClose} />;
}
