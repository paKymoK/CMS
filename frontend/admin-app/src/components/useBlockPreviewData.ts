import { useQuery } from "@tanstack/react-query";
import type { SectionIntroData } from "@cms/content-renderer";
import { contentApi } from "../lib/api";
import type { FeaturedRow, GrowthFigure } from "../lib/pageBlocks";

/** The site content a page's blocks read: the case studies a case-studies block can name, the "Success Stories" section intro, and the Stats library's published rows when an intro block asks for them. */
export function useBlockPreviewData(site: string, blocks: unknown) {
  // An intro block can take its growth figures from the Stats library.
  const wantsStats = Array.isArray(blocks) && (blocks as { type?: string; props?: { growthSource?: string } }[]).some((b) => b.type === "intro" && b.props?.growthSource === "library");
  const caseStudies = useQuery({
    queryKey: ["preview-featured-case-studies", site],
    queryFn: async () => {
      const { data } = await contentApi.get("/v1/admin/case-studies", { params: { site } });
      return data.data as FeaturedRow[];
    },
    staleTime: 60_000,
  });
  const intros = useQuery({
    queryKey: ["preview-section-intros", site],
    queryFn: async () => {
      const { data } = await contentApi.get("/v1/admin/content-items", { params: { site, type: "section_intro" } });
      return (data.data as { data: SectionIntroData & { section: string } }[]).map((row) => row.data);
    },
    staleTime: 60_000,
  });
  const stats = useQuery({
    queryKey: ["preview-stats", site],
    enabled: wantsStats,
    queryFn: async () => {
      const { data } = await contentApi.get("/v1/admin/content-items", { params: { site, type: "stat" } });
      return (data.data as { status: string; data: GrowthFigure }[])
        .filter((row) => row.status === "PUBLISHED")
        .map((row) => ({ value: row.data.value, label: row.data.label }));
    },
    staleTime: 60_000,
  });
  const storiesIntro = intros.data?.find((s) => s.section === "service-stories");
  return {
    caseStudies: caseStudies.data,
    growthLibrary: stats.data,
    storiesIntro: storiesIntro && { eyebrow: storiesIntro.eyebrow, title: storiesIntro.title, body: storiesIntro.body },
  };
}
