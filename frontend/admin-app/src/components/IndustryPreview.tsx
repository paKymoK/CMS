import { useMemo } from "react";
import type { IndustryBlockData, IndustryPageData } from "@cms/content-renderer";
import { mediaSrc } from "../lib/media";
import type { PreviewPage } from "../lib/previewProtocol";
import type { ResourcePreviewProps } from "./CaseStudyPreview";
import PreviewPanel from "./PreviewPanel";

const str = (v: unknown) => (typeof v === "string" && v.trim() ? v : undefined);
const list = <T,>(v: unknown, map: (item: Record<string, unknown>) => T): T[] | undefined =>
  Array.isArray(v) ? v.map((item) => map((item ?? {}) as Record<string, unknown>)) : undefined;
const stat = (s: Record<string, unknown>) => ({ value: str(s.value) ?? "", label: str(s.label) ?? "" });

/** One stored block as the page component's data (media URLs resolved, blanks dropped); a block of an unknown type is skipped. */
function toBlock(raw: Record<string, unknown>): IndustryBlockData[] {
  const p = (raw.props ?? {}) as Record<string, unknown>;
  const base = { id: str(raw.id) ?? "", hidden: raw.hidden === true };
  switch (raw.type) {
    case "solutions":
      return [
        {
          ...base,
          type: "solutions",
          props: {
            eyebrow: str(p.eyebrow),
            title: str(p.title),
            body: str(p.body),
            groups: list(p.groups, (g) => ({
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
          },
        },
      ];
    case "reasons":
      return [
        {
          ...base,
          type: "reasons",
          props: {
            eyebrow: str(p.eyebrow),
            title: str(p.title),
            body: str(p.body),
            items: list(p.items, (r) => ({ title: str(r.title) ?? "", image: mediaSrc(str(r.image)) })),
          },
        },
      ];
    case "stories":
      return [
        {
          ...base,
          type: "stories",
          props: {
            eyebrow: str(p.eyebrow),
            title: str(p.title),
            body: str(p.body),
            heading: str(p.heading),
            metrics: list(p.metrics, stat),
            items: list(p.items, (s) => ({
              tag: str(s.tag),
              title: str(s.title) ?? "",
              body: str(s.body),
              image: mediaSrc(str(s.image)),
              linkLabel: str(s.linkLabel),
              linkUrl: str(s.linkUrl),
            })),
          },
        },
      ];
    case "insights":
      return [
        {
          ...base,
          type: "insights",
          props: { eyebrow: str(p.eyebrow), title: str(p.title), body: str(p.body), linkLabel: str(p.linkLabel), linkUrl: str(p.linkUrl) },
        },
      ];
    default:
      return [];
  }
}

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
    blocks: Array.isArray(values.blocks) ? (values.blocks as Record<string, unknown>[]).flatMap(toBlock) : undefined,
  };
}

/** Preview of the industry page being edited. Everything it shows is in the form, so nothing else is fetched. */
export default function IndustryPreview({ values, onClose }: ResourcePreviewProps) {
  const page = useMemo<PreviewPage>(() => ({ kind: "industry", page: toIndustryPageData(values) }), [values]);
  return <PreviewPanel page={page} onClose={onClose} />;
}
