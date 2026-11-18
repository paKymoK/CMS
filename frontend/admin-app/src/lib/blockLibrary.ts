import { useQuery, useQueryClient } from "@tanstack/react-query";
import { contentApi } from "./api";

/**
 * Saved starting points for Service and Industry pages (content-service /v1/admin/block-library):
 * a whole page layout (PAGE, an ordered list of blocks) or one block (BLOCK). Blocks are copied out of
 * an entry; the page keeps no link back to it. Blocks are stored without ids.
 */
export type LibraryKind = "PAGE" | "BLOCK";

export interface LibraryBlock {
  type: string;
  hidden?: boolean;
  props?: Record<string, unknown>;
}

export interface LibraryItem {
  id: number;
  kind: LibraryKind;
  name: string;
  description: string | null;
  blocks: LibraryBlock[];
}

const PATH = "/v1/admin/block-library";

export function useLibrary(site: string | null | undefined, kind: LibraryKind, enabled = true) {
  return useQuery({
    queryKey: ["block-library", site, kind],
    enabled: !!site && enabled,
    queryFn: async () => {
      const { data } = await contentApi.get(PATH, { params: { site, kind } });
      return data.data as LibraryItem[];
    },
  });
}

export function useLibraryActions(site: string | null | undefined) {
  const queryClient = useQueryClient();
  const refresh = (kind: LibraryKind) => queryClient.invalidateQueries({ queryKey: ["block-library", site, kind] });
  return {
    save: async (kind: LibraryKind, name: string, description: string, blocks: unknown[]) => {
      await contentApi.post(PATH, { kind, name, description: description || null, blocks }, { params: { site } });
      await refresh(kind);
    },
    remove: async (kind: LibraryKind, id: number) => {
      await contentApi.delete(`${PATH}/${id}`, { params: { site } });
      await refresh(kind);
    },
  };
}

const newId = (type: string) => `${type}-${Math.random().toString(36).slice(2, 8)}`;

/** A library entry's blocks as page blocks: each copy gets an id of its own. */
export function toPageBlocks(item: LibraryItem) {
  return item.blocks.map((b) => ({ id: newId(b.type), type: b.type, hidden: b.hidden === true, props: JSON.parse(JSON.stringify(b.props ?? {})) }));
}
