import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Popconfirm, message } from "antd";
import { contentApi } from "../lib/api";
import { useSite } from "../lib/useSite";

interface SlugRow {
  id: number;
  path: string;
  primary: boolean;
}

const API_PATH = "/v1/admin/slugs";

/**
 * The old addresses of a page: renaming a page keeps its previous slugs, and each one redirects to the
 * current address. Lists them (removing one gives that address up, so it 404s) and, while the slug is
 * being changed on a live page, says the old address will redirect. Nothing for a page not saved yet.
 */
export default function PreviousUrls({ itemId, slug, live, prefix }: { itemId?: string; slug: string; live: boolean; prefix: string }) {
  const { site } = useSite();
  const queryClient = useQueryClient();
  const queryKey = ["slugs", site, itemId];
  const query = useQuery({
    queryKey,
    queryFn: async () => {
      const { data } = await contentApi.get(API_PATH, { params: { site, itemId } });
      return data.data as SlugRow[];
    },
    enabled: !!site && !!itemId,
  });
  const removeMutation = useMutation({
    mutationFn: (slugId: number) => contentApi.delete(`${API_PATH}/${slugId}`, { params: { site } }),
    onSuccess: () => {
      message.success("Old URL removed");
      queryClient.invalidateQueries({ queryKey });
    },
  });

  if (!itemId) return null;
  const rows = query.data ?? [];
  const current = rows.find((r) => r.primary)?.path.split("/").pop();
  const aliases = rows.filter((r) => !r.primary);
  const renaming = live && !!slug && !!current && slug !== current;
  if (!renaming && aliases.length === 0) return null;

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: 6, fontSize: 12, color: "#5b6b86" }}>
      {renaming && <span>The old URL will redirect to the new one.</span>}
      {aliases.length > 0 && (
        <>
          <span style={{ fontWeight: 600 }}>Previous URLs (redirect here)</span>
          {aliases.map((row) => (
            <div key={row.id} style={{ display: "flex", alignItems: "center", gap: 10 }}>
              <span style={{ fontFamily: "var(--font-mono)", color: "#26354f", wordBreak: "break-all" }}>
                {prefix}
                {row.path}
              </span>
              <Popconfirm
                title="Remove this old URL?"
                description="Visitors and search engines using it will get a 404."
                okText="Remove"
                onConfirm={() => removeMutation.mutate(row.id)}
              >
                <button type="button" style={{ border: "none", background: "none", padding: 0, color: "#c0392b", cursor: "pointer", fontFamily: "inherit", fontSize: 12 }}>
                  Remove
                </button>
              </Popconfirm>
            </div>
          ))}
        </>
      )}
    </div>
  );
}
