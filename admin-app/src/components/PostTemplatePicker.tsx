import { useQuery, useQueryClient } from "@tanstack/react-query";
import { Popconfirm, message } from "antd";
import { contentApi } from "../lib/api";
import { useSite } from "../lib/useSite";
import { BUILT_IN_TEMPLATES, type PostTemplate, type SavedPostTemplate } from "../lib/postTemplates";

export const TEMPLATES_PATH = "/v1/admin/post-templates";

const labelStyle = {
  fontFamily: "var(--font-mono)",
  fontSize: 10,
  letterSpacing: ".12em",
  textTransform: "uppercase",
  color: "#5a5d64",
} as const;

function Card({ t, onPick, onDelete }: { t: PostTemplate; onPick: (t: PostTemplate) => void; onDelete?: () => void }) {
  return (
    <div style={{ position: "relative", background: "#ffffff", border: "1px solid #e0e4e9" }}>
      <button
        type="button"
        onClick={() => onPick(t)}
        style={{ display: "flex", flexDirection: "column", gap: 6, width: "100%", minHeight: 108, padding: "16px 18px", border: "none", background: "transparent", textAlign: "left", cursor: "pointer", fontFamily: "inherit" }}
      >
        <span style={{ fontSize: 15, fontWeight: 600, color: "#10314f" }}>{t.name}</span>
        <span style={{ fontSize: 12.5, lineHeight: 1.5, color: "#6a7c90" }}>{t.description || "Saved template"}</span>
        <span style={{ ...labelStyle, marginTop: "auto", color: "#9aa3ad" }}>{t.layout} layout</span>
      </button>
      {onDelete && (
        <Popconfirm title="Delete this template?" description="Posts already made from it are not affected." okText="Delete" okButtonProps={{ danger: true }} onConfirm={onDelete}>
          <button type="button" aria-label={`Delete template ${t.name}`} style={{ position: "absolute", top: 8, right: 8, width: 24, height: 24, border: "none", borderRadius: "50%", background: "#eef1f4", color: "#5a5d64", cursor: "pointer" }}>
            ×
          </button>
        </Popconfirm>
      )}
    </div>
  );
}

/** Shown for /posts/new before the editor: choose a starting point, or a blank page. */
export default function PostTemplatePicker({ onPick }: { onPick: (t: PostTemplate) => void }) {
  const { site } = useSite();
  const queryClient = useQueryClient();

  const saved = useQuery({
    queryKey: ["post-templates", site],
    queryFn: async () => {
      const { data } = await contentApi.get(TEMPLATES_PATH, { params: { site } });
      return data.data as SavedPostTemplate[];
    },
    enabled: !!site,
  });

  const remove = async (id: number) => {
    try {
      await contentApi.delete(`${TEMPLATES_PATH}/${id}`, { params: { site } });
      queryClient.invalidateQueries({ queryKey: ["post-templates", site] });
    } catch {
      message.error("Could not delete the template");
    }
  };

  return (
    <div style={{ minHeight: "100vh", background: "#f6f9fc", padding: "28px clamp(16px,3vw,32px) 80px" }}>
      <div style={{ maxWidth: 1000, margin: "0 auto", display: "flex", flexDirection: "column", gap: 28 }}>
        <div>
          <div style={labelStyle}>Posts / New</div>
          <h1 style={{ margin: "6px 0 4px", fontSize: 24, fontWeight: 700, color: "#10314f" }}>Start a new post</h1>
          <p style={{ margin: 0, fontSize: 14, color: "#6a7c90" }}>Pick a starting point. It only pre-fills the page; you can change everything.</p>
        </div>

        <section style={{ display: "flex", flexDirection: "column", gap: 12 }}>
          <div style={labelStyle}>— Built-in</div>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill,minmax(220px,1fr))", gap: 14 }}>
            {BUILT_IN_TEMPLATES.map((t) => (
              <Card key={t.id} t={t} onPick={onPick} />
            ))}
          </div>
        </section>

        <section style={{ display: "flex", flexDirection: "column", gap: 12 }}>
          <div style={labelStyle}>— Saved by your team</div>
          {saved.data && saved.data.length > 0 ? (
            <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fill,minmax(220px,1fr))", gap: 14 }}>
              {saved.data.map((t) => (
                <Card key={t.id} t={t} onPick={onPick} onDelete={() => remove(t.id)} />
              ))}
            </div>
          ) : (
            <span style={{ fontSize: 13, color: "#9aa3ad" }}>
              {saved.isLoading ? "Loading…" : "Nothing yet. Use “Save as template” in any post's editor."}
            </span>
          )}
        </section>
      </div>
    </div>
  );
}
