import { Navigate } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { Empty } from "antd";
import { contentApi } from "../lib/api";
import { useSite } from "../lib/useSite";
import { findResource } from "../config/resources";

/**
 * Entry point of a resource the site has exactly one of (the About page): opens that row's editor, or
 * the "new" editor while the site has none yet. There is no list screen to land on.
 */
export default function SingletonEditorRedirect({ resourceKey }: { resourceKey: string }) {
  const resource = findResource(resourceKey);
  const { site } = useSite();
  const query = useQuery({
    queryKey: ["singleton", resourceKey, site],
    queryFn: async () => {
      const { data } = await contentApi.get(resource!.apiPath, { params: { site, type: resource!.contentType } });
      return (data.data as { id: number }[])[0]?.id ?? null;
    },
    enabled: !!resource?.editorRoute && !!site,
    // The editor creates the row on first save: never reuse a stale "none yet" answer.
    gcTime: 0,
  });

  if (!resource?.editorRoute) return <Empty description="Unknown content type" />;
  if (!query.isSuccess) return <div style={{ padding: 48, textAlign: "center", color: "#7b8aa5" }}>Loading…</div>;
  return <Navigate to={resource.editorRoute(query.data ?? "new")} replace />;
}
