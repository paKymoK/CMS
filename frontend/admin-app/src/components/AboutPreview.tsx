import { useMemo } from "react";
import type { AboutPageData } from "@cms/content-renderer";
import { mediaSrc } from "../lib/media";
import type { PreviewPage } from "../lib/previewProtocol";
import type { ResourcePreviewProps } from "./CaseStudyPreview";
import PreviewPanel from "./PreviewPanel";

const str = (v: unknown) => (typeof v === "string" && v.trim() ? v : undefined);
const list = <T,>(v: unknown, map: (item: Record<string, unknown>) => T): T[] | undefined =>
  Array.isArray(v) ? v.map((item) => map((item ?? {}) as Record<string, unknown>)) : undefined;
const stat = (s: Record<string, unknown>) => ({ value: str(s.value) ?? "", label: str(s.label) ?? "" });

/** The About page form as the page component's data (media URLs resolved, blanks dropped). */
function toAboutPageData(values: Record<string, unknown>): AboutPageData {
  return {
    heroBreadcrumb: str(values.heroBreadcrumb),
    heroTitle: str(values.heroTitle) ?? "",
    heroSubtitle: str(values.heroSubtitle),
    ctaLabel: str(values.ctaLabel),
    ctaUrl: str(values.ctaUrl),
    heroImage: mediaSrc(str(values.heroImage)),
    heroStats: list(values.heroStats, stat),
    introEyebrow: str(values.introEyebrow),
    introTitle: str(values.introTitle),
    introBody: str(values.introBody),
    introImage: mediaSrc(str(values.introImage)),
    introDecorImage: mediaSrc(str(values.introDecorImage)),
    growthTitle: str(values.growthTitle),
    growthStats: list(values.growthStats, stat),
    visionEyebrow: str(values.visionEyebrow),
    visionTitle: str(values.visionTitle),
    visionImage: mediaSrc(str(values.visionImage)),
    missionEyebrow: str(values.missionEyebrow),
    missionBody: str(values.missionBody),
    missionImage: mediaSrc(str(values.missionImage)),
    leadersEyebrow: str(values.leadersEyebrow),
    leadersTitle: str(values.leadersTitle),
    leadersRingImage: mediaSrc(str(values.leadersRingImage)),
    leaders: list(values.leaders, (l) => ({
      name: str(l.name) ?? "",
      title: str(l.title),
      photo: mediaSrc(str(l.photo)),
    })),
    valuesEyebrow: str(values.valuesEyebrow),
    valuesTitle: str(values.valuesTitle),
    valuesBody: str(values.valuesBody),
    values: list(values.values, (v) => ({ title: str(v.title) ?? "", image: mediaSrc(str(v.image)) })),
    hiddenSections: Array.isArray(values.hiddenSections) ? (values.hiddenSections as string[]) : undefined,
  };
}

/** Preview of the About page being edited. Everything it shows is in the form, so nothing else is fetched. */
export default function AboutPreview({ values, onClose }: ResourcePreviewProps) {
  const page = useMemo<PreviewPage>(() => ({ kind: "about", page: toAboutPageData(values) }), [values]);
  return <PreviewPanel page={page} onClose={onClose} />;
}
