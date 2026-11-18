import { useMemo } from "react";
import type { IndustryPageData } from "@cms/content-renderer";
import { mediaSrc } from "../lib/media";
import { featuredStories, toPageBlocks, type GrowthFigure } from "../lib/pageBlocks";
import type { PreviewPage } from "../lib/previewProtocol";
import type { ResourcePreviewProps } from "./CaseStudyPreview";
import PreviewPanel from "./PreviewPanel";
import { useBlockPreviewData } from "./useBlockPreviewData";

const str = (v: unknown) => (typeof v === "string" && v.trim() ? v : undefined);
const list = <T,>(v: unknown, map: (item: Record<string, unknown>) => T): T[] | undefined =>
  Array.isArray(v) ? v.map((item) => map((item ?? {}) as Record<string, unknown>)) : undefined;
const stat = (s: Record<string, unknown>) => ({ value: str(s.value) ?? "", label: str(s.label) ?? "" });

/** The industry page form as the page component's data (media URLs resolved, blanks dropped). */
function toIndustryPageData(values: Record<string, unknown>, growthLibrary: GrowthFigure[] | undefined): IndustryPageData {
  return {
    slug: str(values.slug) ?? "",
    navLabel: str(values.navLabel) ?? "Untitled industry",
    heroBreadcrumb: str(values.heroBreadcrumb),
    heroTitle: str(values.heroTitle) ?? "",
    heroSubtitle: str(values.heroSubtitle),
    ctaLabel: str(values.ctaLabel),
    ctaUrl: str(values.ctaUrl),
    heroImage: mediaSrc(str(values.heroImage)),
    heroStats: list(values.heroStats, stat),
    blocks: toPageBlocks(values.blocks, growthLibrary),
  };
}

/** Preview of the industry page being edited: the form values, plus the case studies a case-studies block names. */
export default function IndustryPreview({ site, values, onClose }: ResourcePreviewProps) {
  const { caseStudies, storiesIntro, growthLibrary } = useBlockPreviewData(site, values.blocks);
  const page = useMemo<PreviewPage>(() => {
    const data = toIndustryPageData(values, growthLibrary);
    return { kind: "industry", page: data, stories: featuredStories(data.blocks, caseStudies), storiesIntro };
  }, [values, caseStudies, storiesIntro, growthLibrary]);
  return <PreviewPanel page={page} onClose={onClose} />;
}
