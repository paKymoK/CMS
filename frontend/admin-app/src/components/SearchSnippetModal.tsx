import type { CSSProperties } from "react";
import { Modal } from "antd";
import { mediaSrc } from "../lib/media";
import { seoSnippet, type SnippetDevice, type SnippetInput } from "../lib/seo";

const FONT = "Arial, Helvetica, sans-serif";

/** The round site icon and the address line shared by both layouts. */
function SiteLine({ host, crumbs }: { host: string; crumbs: string[] }) {
  return (
    <div style={{ display: "flex", alignItems: "center", gap: 10, minWidth: 0 }}>
      <span
        aria-hidden
        style={{ width: 26, height: 26, flex: "none", borderRadius: "50%", background: "#f1f3f4", border: "1px solid #dadce0", display: "flex", alignItems: "center", justifyContent: "center", fontSize: 12, fontWeight: 700, color: "#5f6368" }}
      >
        {host.charAt(0).toUpperCase()}
      </span>
      <span style={{ minWidth: 0, display: "flex", flexDirection: "column", lineHeight: 1.3 }}>
        <span style={{ fontSize: 14, color: "#202124" }}>{host}</span>
        <span style={{ fontSize: 12, color: "#4d5156", overflow: "hidden", textOverflow: "ellipsis", whiteSpace: "nowrap" }}>
          {["https://" + host, ...crumbs].join(" › ")}
        </span>
      </span>
    </div>
  );
}

/** One search result, laid out the way a desktop or a phone results page shows it. */
function Result({ input, device }: { input: SnippetInput; device: SnippetDevice }) {
  const s = seoSnippet(input, device);
  const mobile = device === "mobile";
  const clamp = (lines: number): CSSProperties => ({
    display: "-webkit-box",
    WebkitLineClamp: lines,
    WebkitBoxOrient: "vertical",
    overflow: "hidden",
  });
  return (
    <div
      style={{
        fontFamily: FONT,
        background: "#ffffff",
        border: "1px solid #e3e9f4",
        borderRadius: mobile ? 14 : 8,
        padding: mobile ? 14 : "16px 18px",
        width: mobile ? 360 : "100%",
        maxWidth: "100%",
        boxSizing: "border-box",
        opacity: s.hidden ? 0.5 : 1,
      }}
    >
      <SiteLine host={s.host} crumbs={s.crumbs} />
      <div style={{ display: "flex", gap: 12, marginTop: 8 }}>
        <div style={{ minWidth: 0, flex: 1 }}>
          <div style={{ fontSize: mobile ? 18 : 20, lineHeight: 1.3, color: "#1a0dab", ...(mobile ? clamp(2) : { whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }) }}>
            {s.title || "Untitled"}
          </div>
          <div style={{ marginTop: 4, fontSize: 14, lineHeight: 1.58, color: "#4d5156", ...clamp(mobile ? 3 : 2) }}>
            {s.description || "No description: search engines will pick some text from the page."}
          </div>
        </div>
        {mobile && s.image ? (
          <img src={mediaSrc(s.image)} alt="" style={{ width: 92, height: 92, flex: "none", objectFit: "cover", borderRadius: 8 }} />
        ) : null}
      </div>
    </div>
  );
}

/**
 * How the page would look in search results on desktop and on a phone, from the form as it is being
 * edited. Approximate: search engines cut titles and descriptions by pixel width and may rewrite them.
 */
export default function SearchSnippetModal({ open, onClose, input }: { open: boolean; onClose: () => void; input: SnippetInput }) {
  const desktop = seoSnippet(input, "desktop");
  const mobile = seoSnippet(input, "mobile");
  const cut = [
    desktop.titleCut || mobile.titleCut ? "title" : null,
    desktop.descriptionCut || mobile.descriptionCut ? "description" : null,
  ].filter(Boolean);
  return (
    <Modal open={open} onCancel={onClose} footer={null} width={720} title="Search result preview">
      <div style={{ display: "flex", flexDirection: "column", gap: 16 }}>
        {desktop.hidden && (
          <div style={{ padding: "10px 12px", borderRadius: 8, background: "#fff4e0", color: "#8a4b00", fontSize: 13 }}>
            This page is set to be hidden from search engines, so it will not appear in results. This is how it would look if it did.
          </div>
        )}
        <div>
          <div style={{ fontSize: 12, fontWeight: 600, color: "#5b6b86", marginBottom: 6 }}>Desktop</div>
          <Result input={input} device="desktop" />
        </div>
        <div>
          <div style={{ fontSize: 12, fontWeight: 600, color: "#5b6b86", marginBottom: 6 }}>Mobile</div>
          <Result input={input} device="mobile" />
        </div>
        <div style={{ fontSize: 12, color: "#7b8aa5" }}>
          {cut.length > 0 ? `The ${cut.join(" and ")} would be cut off with “…”. ` : ""}
          Approximate: search engines cut by width, not characters, and sometimes show different text.
        </div>
      </div>
    </Modal>
  );
}
