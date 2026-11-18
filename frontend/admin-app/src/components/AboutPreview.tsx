import { useMemo } from "react";
import type { AboutPageData } from "@cms/content-renderer";
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

/** The About page form as the page component's data (media URLs resolved, blanks dropped). */
function toAboutPageData(values: Record<string, unknown>, growthLibrary: GrowthFigure[] | undefined): AboutPageData {
  return {
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

/** Preview of the About page being edited: the form values, plus the site content its blocks can read (case studies, the Stats library). */
export default function AboutPreview({ site, values, onClose }: ResourcePreviewProps) {
  const { caseStudies, storiesIntro, growthLibrary } = useBlockPreviewData(site, values.blocks);
  const page = useMemo<PreviewPage>(() => {
    const data = toAboutPageData(values, growthLibrary);
    return { kind: "about", page: data, stories: featuredStories(data.blocks, caseStudies), storiesIntro };
  }, [values, caseStudies, storiesIntro, growthLibrary]);
  return <PreviewPanel page={page} onClose={onClose} />;
}
