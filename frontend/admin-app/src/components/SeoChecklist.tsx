import { type SeoCheck, type SeoLevel } from "../lib/seo";

const ICON: Record<SeoLevel, { glyph: string; color: string }> = {
  ok: { glyph: "✓", color: "#0b7a55" },
  warn: { glyph: "!", color: "#b26a00" },
  missing: { glyph: "✗", color: "#c4341c" },
};

/** The checklist panel, shown at the bottom of an SEO section. Informational: nothing here blocks saving. */
export function SeoChecklist({ checks, onPreview }: { checks: SeoCheck[]; onPreview?: () => void }) {
  return (
    <div style={{ marginTop: 14, border: "1px solid #e3e9f4", borderRadius: 10, padding: "10px 12px", background: "#fafbfc" }}>
      <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", gap: 8, marginBottom: 8 }}>
        <span style={{ fontSize: 12, fontWeight: 600, color: "#5b6b86" }}>SEO checklist</span>
        {onPreview && (
          <button
            type="button"
            onClick={onPreview}
            style={{ padding: "5px 10px", border: "1px solid #bcd9fb", borderRadius: 8, background: "#ffffff", fontFamily: "inherit", fontSize: 12, fontWeight: 600, color: "#0083ff", cursor: "pointer" }}
          >
            Search result preview
          </button>
        )}
      </div>
      <ul style={{ margin: 0, padding: 0, listStyle: "none", display: "flex", flexDirection: "column", gap: 8 }}>
        {checks.map((c) => (
          <li key={c.key} style={{ display: "flex", gap: 8, alignItems: "flex-start", fontSize: 13, lineHeight: 1.35 }}>
            <span aria-hidden style={{ width: 14, flex: "none", fontWeight: 700, color: ICON[c.level].color }}>
              {ICON[c.level].glyph}
            </span>
            <span style={{ minWidth: 0 }}>
              <span style={{ fontWeight: 600, color: "#0b1b3a" }}>{c.label}</span>
              <span style={{ display: "block", color: "#7b8aa5", overflowWrap: "anywhere" }}>{c.detail}</span>
            </span>
          </li>
        ))}
      </ul>
    </div>
  );
}
