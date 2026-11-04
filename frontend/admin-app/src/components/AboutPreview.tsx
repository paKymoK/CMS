import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import type { AboutBlockData, AboutPageData } from "@cms/content-renderer";
import { contentApi } from "../lib/api";
import { mediaSrc } from "../lib/media";
import type { PreviewPage } from "../lib/previewProtocol";
import type { ResourcePreviewProps } from "./CaseStudyPreview";
import PreviewPanel from "./PreviewPanel";

const str = (v: unknown) => (typeof v === "string" && v.trim() ? v : undefined);
const list = <T,>(v: unknown, map: (item: Record<string, unknown>) => T): T[] | undefined =>
  Array.isArray(v) ? v.map((item) => map((item ?? {}) as Record<string, unknown>)) : undefined;
const stat = (s: Record<string, unknown>) => ({ value: str(s.value) ?? "", label: str(s.label) ?? "" });

/** One stored block as the page component's data (media URLs resolved, blanks dropped); a block of an unknown type is skipped. */
function toBlock(raw: Record<string, unknown>, library: { value: string; label: string }[] | undefined): AboutBlockData[] {
  const p = (raw.props ?? {}) as Record<string, unknown>;
  const base = { id: str(raw.id) ?? "", hidden: raw.hidden === true };
  switch (raw.type) {
    case "intro":
      return [
        {
          ...base,
          type: "intro",
          props: {
            eyebrow: str(p.eyebrow),
            title: str(p.title),
            body: str(p.body),
            image: mediaSrc(str(p.image)),
            decorImage: mediaSrc(str(p.decorImage)),
            growthTitle: str(p.growthTitle),
            growthStats: p.growthSource === "library" ? (library ?? []) : list(p.growthStats, stat),
          },
        },
      ];
    case "vision":
      return [{ ...base, type: "vision", props: { eyebrow: str(p.eyebrow), title: str(p.title), image: mediaSrc(str(p.image)) } }];
    case "mission":
      return [{ ...base, type: "mission", props: { eyebrow: str(p.eyebrow), body: str(p.body), image: mediaSrc(str(p.image)) } }];
    case "leaders":
      return [
        {
          ...base,
          type: "leaders",
          props: {
            eyebrow: str(p.eyebrow),
            title: str(p.title),
            ringImage: mediaSrc(str(p.ringImage)),
            items: list(p.items, (l) => ({ name: str(l.name) ?? "", title: str(l.title), photo: mediaSrc(str(l.photo)) })),
          },
        },
      ];
    case "values":
      return [
        {
          ...base,
          type: "values",
          props: {
            eyebrow: str(p.eyebrow),
            title: str(p.title),
            body: str(p.body),
            items: list(p.items, (v) => ({ title: str(v.title) ?? "", image: mediaSrc(str(v.image)) })),
          },
        },
      ];
    default:
      return [];
  }
}

/** The About page form as the page component's data (media URLs resolved, blanks dropped). */
function toAboutPageData(values: Record<string, unknown>, library: { value: string; label: string }[] | undefined): AboutPageData {
  return {
    heroBreadcrumb: str(values.heroBreadcrumb),
    heroTitle: str(values.heroTitle) ?? "",
    heroSubtitle: str(values.heroSubtitle),
    ctaLabel: str(values.ctaLabel),
    ctaUrl: str(values.ctaUrl),
    heroImage: mediaSrc(str(values.heroImage)),
    heroStats: list(values.heroStats, stat),
    blocks: Array.isArray(values.blocks) ? (values.blocks as Record<string, unknown>[]).flatMap((b) => toBlock(b, library)) : undefined,
  };
}

/** Preview of the About page being edited. Everything it shows is in the form, except the growth figures when they come from the Stats library. */
export default function AboutPreview({ site, values, onClose }: ResourcePreviewProps) {
  // The intro block's growth figures can come from the Stats library.
  const library = Array.isArray(values.blocks) && (values.blocks as { type?: string; props?: { growthSource?: string } }[]).some((b) => b.type === "intro" && b.props?.growthSource === "library");
  // Growth figures from the Stats library: the published rows, as the public endpoint serves them.
  const statsQuery = useQuery({
    queryKey: ["preview-stats", site],
    queryFn: async () => {
      const { data } = await contentApi.get("/v1/admin/content-items", { params: { site, type: "stat" } });
      return (data.data as { status: string; data: { value: string; label: string } }[])
        .filter((row) => row.status === "PUBLISHED")
        .map((row) => ({ value: row.data.value, label: row.data.label }));
    },
    staleTime: 60_000,
    enabled: library,
  });
  const page = useMemo<PreviewPage>(() => ({ kind: "about", page: toAboutPageData(values, statsQuery.data) }), [values, statsQuery.data]);
  return <PreviewPanel page={page} onClose={onClose} />;
}
