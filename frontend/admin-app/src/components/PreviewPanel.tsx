import { useEffect, useRef, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import type { IndustryCardData, SectionLinkIntroData } from "@cms/content-renderer";
import { contentApi } from "../lib/api";
import { mediaSrc } from "../lib/media";
import { useSite } from "../lib/useSite";
import {
  MSG_HEIGHT,
  MSG_READY,
  MSG_RENDER,
  PREVIEW_FRAME_URL,
  type PreviewChrome,
  type PreviewMessage,
  type PreviewPage,
} from "../lib/previewProtocol";

const DEVICES = [
  { key: "desktop", label: "Desktop", width: "100%" },
  { key: "tablet", label: "Tablet", width: "820px" },
  { key: "mobile", label: "Mobile", width: "390px" },
] as const;

type DeviceKey = (typeof DEVICES)[number]["key"];

/**
 * Full-screen preview of the item being edited. The page is drawn by @cms/content-renderer inside an
 * iframe (preview-frame.html) from the editor's current, unsaved state — nothing is saved and no
 * token or website is involved. The device buttons only change the iframe's width, so the page's
 * own breakpoints do the rest.
 */
export default function PreviewPanel({
  page,
  onClose,
}: {
  page: PreviewPage;
  onClose: () => void;
}) {
  const frameRef = useRef<HTMLIFrameElement>(null);
  const [frameReady, setFrameReady] = useState(false);
  const [frameHeight, setFrameHeight] = useState<number | null>(null);
  const [device, setDevice] = useState<DeviceKey>("desktop");
  const { site } = useSite();

  // The site-wide footer the page sits above on the website. A failed fetch only leaves the footer out.
  const chromeQuery = useQuery({
    queryKey: ["preview-chrome", site],
    enabled: !!site,
    staleTime: 60_000,
    queryFn: async (): Promise<PreviewChrome> => {
      type Row<T> = { data: T; status?: string };
      const list = async <T,>(type: string) => {
        const { data } = await contentApi.get("/v1/admin/content-items", { params: { site, type } });
        return (data.data as Row<T>[]).filter((r) => !r.status || r.status === "PUBLISHED").map((r) => r.data);
      };
      const [nav, footers, posts, cards, intros] = await Promise.all([
        list<{ label: string; links?: string[] | null }>("footer_nav_category"),
        list<NonNullable<PreviewChrome["footer"]>>("site_footer"),
        list<{ slug?: string; title: string; date?: string; image?: string; category?: string }>("post"),
        list<IndustryCardData>("industry_card"),
        list<SectionLinkIntroData & { section: string }>("section_intro"),
      ]);
      return {
        footerNav: nav.map((c) => ({ label: c.label, links: c.links ?? [] })),
        footer: footers[0],
        // Same rule as the website's home content: only "insight" posts, in list order.
        insights: posts
          .filter((p) => p.category?.toLowerCase() === "insight")
          .map((p) => ({ slug: p.slug, title: p.title, date: p.date, image: p.image && mediaSrc(p.image) })),
        industryCards: cards.map((c) => ({ ...c, image: c.image && mediaSrc(c.image) })),
        intros: Object.fromEntries(intros.map((i) => [i.section, i])),
      };
    },
  });
  const chrome = chromeQuery.data;

  useEffect(() => {
    const onReady = (e: MessageEvent) => {
      if (
        e.origin !== window.location.origin ||
        e.source !== frameRef.current?.contentWindow
      )
        return;
      if (e.data?.type === MSG_READY) setFrameReady(true);
      else if (e.data?.type === MSG_HEIGHT && typeof e.data.height === "number")
        setFrameHeight(e.data.height);
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
    if (!frameReady) return;
    const message: PreviewMessage = { type: MSG_RENDER, page, chrome };
    frameRef.current?.contentWindow?.postMessage(
      message,
      window.location.origin,
    );
  }, [frameReady, page, chrome]);

  const width = DEVICES.find((d) => d.key === device)!.width;

  return (
    <div
      role="dialog"
      aria-label="Preview"
      style={{
        position: "fixed",
        inset: 0,
        zIndex: 1000,
        display: "flex",
        flexDirection: "column",
        background: "#e8eef8",
      }}
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
        <div
          style={{
            display: "flex",
            alignItems: "baseline",
            gap: 10,
            minWidth: 0,
          }}
        >
          <strong style={{ fontSize: 14, color: "#0b1b3a" }}>Preview</strong>
          <span style={{ fontSize: 12.5, color: "#7b8aa5" }}>
            Shows your current edits, saved or not.
          </span>
        </div>
        <div
          style={{
            display: "flex",
            gap: 4,
            padding: 4,
            border: "1px solid #dde4f0",
            borderRadius: 12,
            background: "#e8eef8",
          }}
        >
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
                boxShadow:
                  device === d.key ? "0 1px 3px rgba(10,30,70,.12)" : "none",
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
      <div style={{ flex: 1, minHeight: 0, overflowY: "auto" }}>
        <div
          style={{
            display: "flex",
            justifyContent: "center",
            padding: device === "desktop" ? 0 : 20,
          }}
        >
          <iframe
            ref={frameRef}
            src={PREVIEW_FRAME_URL}
            title="Preview"
            style={{
              width,
              maxWidth: "100%",
              height: frameHeight ?? "100%",
              display: "block",
              border: "none",
              background: "#ffffff",
              boxShadow:
                device === "desktop" ? "none" : "0 8px 30px rgba(10,30,70,.18)",
              transition: "width .2s",
            }}
          />
        </div>
      </div>
    </div>
  );
}
