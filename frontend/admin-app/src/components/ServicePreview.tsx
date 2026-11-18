import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import type { ServicePageData } from "@cms/content-renderer";
import { contentApi } from "../lib/api";
import { mediaSrc } from "../lib/media";
import { featuredStories, toPageBlocks, type GrowthFigure } from "../lib/pageBlocks";
import type { PreviewPage } from "../lib/previewProtocol";
import type { ResourcePreviewProps } from "./CaseStudyPreview";
import PreviewPanel from "./PreviewPanel";
import { useBlockPreviewData } from "./useBlockPreviewData";

const str = (v: unknown) => (typeof v === "string" && v.trim() ? v : undefined);
const list = <T,>(v: unknown, map: (item: Record<string, unknown>) => T): T[] | undefined =>
  Array.isArray(v) ? v.map((item) => map((item ?? {}) as Record<string, unknown>)) : undefined;

/** The service page form as the page component's data (media URLs resolved, blanks dropped). */
function toServicePageData(values: Record<string, unknown>, growthLibrary: GrowthFigure[] | undefined): ServicePageData {
  return {
    slug: str(values.slug) ?? "",
    category: str(values.category) ?? "",
    navLabel: str(values.navLabel) ?? "Untitled service",
    shortLabel: str(values.shortLabel),
    heroTitle: str(values.heroTitle) ?? "",
    heroSubtitle: str(values.heroSubtitle),
    ctaLabel: str(values.ctaLabel),
    ctaUrl: str(values.ctaUrl),
    heroImage: mediaSrc(str(values.heroImage)),
    heroStats: list(values.heroStats, (s) => ({ value: str(s.value) ?? "", label: str(s.label) ?? "" })),
    blocks: toPageBlocks(values.blocks, growthLibrary),
  };
}

/** Preview of the service page being edited: the form values, plus the site content the page reads (category name, featured case studies, section intro). */
export default function ServicePreview({ site, values, onClose }: ResourcePreviewProps) {
  const categoriesQuery = useQuery({
    queryKey: ["preview-service-categories", site],
    queryFn: async () => {
      const { data } = await contentApi.get("/v1/admin/content-items", { params: { site, type: "service_category" } });
      return (data.data as { data: { slug: string; name: string } }[]).map((row) => row.data);
    },
    staleTime: 60_000,
  });
  const { caseStudies, storiesIntro, growthLibrary } = useBlockPreviewData(site, values.blocks);

  const page = useMemo<PreviewPage>(() => {
    const data = toServicePageData(values, growthLibrary);
    return {
      kind: "service",
      page: data,
      categoryName: categoriesQuery.data?.find((c) => c.slug === data.category)?.name ?? "",
      stories: featuredStories(data.blocks, caseStudies),
      storiesIntro,
    };
  }, [values, categoriesQuery.data, caseStudies, storiesIntro, growthLibrary]);

  return <PreviewPanel page={page} onClose={onClose} />;
}
