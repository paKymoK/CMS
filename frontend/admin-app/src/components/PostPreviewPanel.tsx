import { useEffect, useMemo, useRef, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { contentApi } from "../lib/api";
import { SITES } from "../config/sites";
import { buildPreviewPost, relatedFor, type PostDraft, type PostListItem } from "../lib/postPreview";
import { MSG_POST, MSG_READY, PREVIEW_FRAME_URL, type PreviewMessage } from "../lib/previewProtocol";

const DEVICES = [
  { key: "desktop", label: "Desktop", width: "100%" },
  { key: "tablet", label: "Tablet", width: "820px" },
  { key: "mobile", label: "Mobile", width: "390px" },
] as const;

type DeviceKey = (typeof DEVICES)[number]["key"];

/**
 * Full-screen preview of the post being edited. The page is the shared PostArticle drawn inside an
 * iframe (preview-frame.html) from the editor's current, unsaved state — nothing is saved and no
 * token or website is involved. The device buttons only change the iframe's width, so the page's
 * own breakpoints do the rest.
 */
export default function PostPreviewPanel({
  site,
  slug,
  draft,
  onClose,
}: {
  site: string;
  slug: string;
  draft: PostDraft;
  onClose: () => void;
}) {
  const frameRef = useRef<HTMLIFrameElement>(null);
  const [frameReady, setFrameReady] = useState(false);
  const [device, setDevice] = useState<DeviceKey>("desktop");

  // The related-articles strip shows published posts, as on the live page.
  const postsQuery = useQuery({
    queryKey: ["preview-related", site],
    queryFn: async () => {
      const { data } = await contentApi.get("/v1/admin/posts", { params: { site } });
      return data.data as PostListItem[];
    },
    staleTime: 60_000,
  });

  const message = useMemo<PreviewMessage>(() => {
    const subdomain = SITES.find((s) => s.code === site)?.subdomain ?? "example.com";
    return {
      type: MSG_POST,
      post: buildPreviewPost(draft, relatedFor(draft, postsQuery.data ?? [])),
      pageUrl: `https://${subdomain}/en/insights/${slug || "your-post"}`,
    };
  }, [draft, slug, site, postsQuery.data]);

  useEffect(() => {
    const onReady = (e: MessageEvent) => {
      if (e.origin === window.location.origin && e.source === frameRef.current?.contentWindow && e.data?.type === MSG_READY) {
        setFrameReady(true);
      }
    };
    const onKey = (e: KeyboardEvent) => e.key === "Escape" && onClose();
    window.addEventListener("message", onReady);
    window.addEventListener("keydown", onKey);
    return () => {
      window.removeEventListener("message", onReady);
      window.removeEventListener("keydown", onKey);
    };
  }, [onClose]);

  useEffect(() => {
    if (frameReady) frameRef.current?.contentWindow?.postMessage(message, window.location.origin);
  }, [frameReady, message]);

  const width = DEVICES.find((d) => d.key === device)!.width;

  return (
    <div
      role="dialog"
      aria-label="Post preview"
      style={{ position: "fixed", inset: 0, zIndex: 1000, display: "flex", flexDirection: "column", background: "#e8eef8" }}
    >
      <div
        style={{
          flex: "none",
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          gap: 12,
          padding: "10px 18px",
          background: "#ffffff",
          borderBottom: "1px solid #e3e9f4",
        }}
      >
        <div style={{ display: "flex", alignItems: "baseline", gap: 10, minWidth: 0 }}>
          <strong style={{ fontSize: 14, color: "#0b1b3a" }}>Preview</strong>
          <span style={{ fontSize: 12.5, color: "#7b8aa5" }}>Shows your current edits, saved or not.</span>
        </div>
        <div style={{ display: "flex", gap: 4, padding: 4, border: "1px solid #dde4f0", borderRadius: 12, background: "#e8eef8" }}>
          {DEVICES.map((d) => (
            <button
              key={d.key}
              type="button"
              onClick={() => setDevice(d.key)}
              style={{
                height: 30,
                padding: "0 14px",
                border: "none",
                borderRadius: 9,
                cursor: "pointer",
                fontFamily: "inherit",
                fontSize: 12.5,
                fontWeight: 600,
                background: device === d.key ? "#ffffff" : "transparent",
                boxShadow: device === d.key ? "0 1px 3px rgba(10,30,70,.12)" : "none",
                color: device === d.key ? "#0b1b3a" : "#5b6b86",
              }}
            >
              {d.label}
            </button>
          ))}
        </div>
        <button
          type="button"
          onClick={onClose}
          style={{
            minHeight: 36,
            padding: "0 16px",
            border: "1px solid #dde4f0",
            borderRadius: 10,
            background: "#ffffff",
            fontFamily: "inherit",
            fontSize: 13,
            fontWeight: 600,
            color: "#26354f",
            cursor: "pointer",
          }}
        >
          Close
        </button>
      </div>
      <div style={{ flex: 1, minHeight: 0, display: "flex", justifyContent: "center", padding: device === "desktop" ? 0 : 20 }}>
        <iframe
          ref={frameRef}
          src={PREVIEW_FRAME_URL}
          title="Post preview"
          style={{
            width,
            maxWidth: "100%",
            height: "100%",
            border: "none",
            background: "#ffffff",
            boxShadow: device === "desktop" ? "none" : "0 8px 30px rgba(10,30,70,.18)",
            transition: "width .2s",
          }}
        />
      </div>
    </div>
  );
}
