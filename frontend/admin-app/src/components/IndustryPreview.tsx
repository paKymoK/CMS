import { useMemo } from "react";
import type { IndustryPageData } from "@cms/content-renderer";
import { mediaSrc } from "../lib/media";
import type { PreviewPage } from "../lib/previewProtocol";
import type { ResourcePreviewProps } from "./CaseStudyPreview";
import PreviewPanel from "./PreviewPanel";

const str = (v: unknown) => (typeof v === "string" && v.trim() ? v : undefined);
const list = <T,>(v: unknown, map: (item: Record<string, unknown>) => T): T[] | undefined =>
  Array.isArray(v) ? v.map((item) => map((item ?? {}) as Record<string, unknown>)) : undefined;
const stat = (s: Record<string, unknown>) => ({ value: str(s.value) ?? "", label: str(s.label) ?? "" });

/** The industry page form as the page component's data (media URLs resolved, blanks dropped). */
function toIndustryPageData(values: Record<string, unknown>): IndustryPageData {
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
    solutionsEyebrow: str(values.solutionsEyebrow),
    solutionsTitle: str(values.solutionsTitle),
    solutionsBody: str(values.solutionsBody),
    solutionGroups: list(values.solutionGroups, (g) => ({
      name: str(g.name) ?? "",
      slides: list(g.slides, (s) => ({
        tabLabel: str(s.tabLabel) ?? "",
        title: str(s.title),
        logo: mediaSrc(str(s.logo)),
        image: mediaSrc(str(s.image)),
        columnA: str(s.columnA),
        columnB: str(s.columnB),
      })),
    })),
    reasonsEyebrow: str(values.reasonsEyebrow),
    reasonsTitle: str(values.reasonsTitle),
    reasonsBody: str(values.reasonsBody),
    reasons: list(values.reasons, (r) => ({ title: str(r.title) ?? "", image: mediaSrc(str(r.image)) })),
    storiesEyebrow: str(values.storiesEyebrow),
    storiesTitle: str(values.storiesTitle),
    storiesBody: str(values.storiesBody),
    storiesHeading: str(values.storiesHeading),
    storiesMetrics: list(values.storiesMetrics, stat),
    stories: list(values.stories, (s) => ({
      tag: str(s.tag),
      title: str(s.title) ?? "",
      body: str(s.body),
      image: mediaSrc(str(s.image)),
      linkLabel: str(s.linkLabel),
      linkUrl: str(s.linkUrl),
    })),
    insightsEyebrow: str(values.insightsEyebrow),
    insightsTitle: str(values.insightsTitle),
    insightsBody: str(values.insightsBody),
    insightsLinkLabel: str(values.insightsLinkLabel),
    insightsLinkUrl: str(values.insightsLinkUrl),
    hiddenSections: Array.isArray(values.hiddenSections) ? (values.hiddenSections as string[]) : undefined,
  };
}

/** Preview of the industry page being edited. Everything it shows is in the form, so nothing else is fetched. */
export default function IndustryPreview({ values, onClose }: ResourcePreviewProps) {
  const page = useMemo<PreviewPage>(() => ({ kind: "industry", page: toIndustryPageData(values) }), [values]);
  return <PreviewPanel page={page} onClose={onClose} />;
}
