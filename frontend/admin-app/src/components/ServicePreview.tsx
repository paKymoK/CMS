import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import type { IndustryStoryData, SectionIntroData, ServiceBlockData, ServicePageData, ServiceStatData } from "@cms/content-renderer";
import { contentApi } from "../lib/api";
import { mediaSrc } from "../lib/media";
import type { PreviewPage } from "../lib/previewProtocol";
import type { ResourcePreviewProps } from "./CaseStudyPreview";
import PreviewPanel from "./PreviewPanel";

const str = (v: unknown) => (typeof v === "string" && v.trim() ? v : undefined);
const list = <T,>(v: unknown, map: (item: Record<string, unknown>) => T): T[] | undefined =>
  Array.isArray(v) ? v.map((item) => map((item ?? {}) as Record<string, unknown>)) : undefined;

/** One stored block as the page component's data (media URLs resolved, blanks dropped); a block of an unknown type is skipped. */
function toBlock(raw: Record<string, unknown>): ServiceBlockData[] {
  const props = (raw.props ?? {}) as Record<string, unknown>;
  const base = { id: str(raw.id) ?? "", hidden: raw.hidden === true };
  switch (raw.type) {
    case "capabilities":
      return [
        {
          ...base,
          type: "capabilities",
          props: {
            eyebrow: str(props.eyebrow),
            title: str(props.title),
            subtitle: str(props.subtitle),
            items: list(props.items, (c) => ({ title: str(c.title) ?? "", body: str(c.body), image: mediaSrc(str(c.image)) })),
          },
        },
      ];
    case "process":
      return [
        {
          ...base,
          type: "process",
          props: {
            title: str(props.title),
            subtitle: str(props.subtitle),
            steps: list(props.steps, (s) => ({ title: str(s.title) ?? "", image: mediaSrc(str(s.image)) })),
          },
        },
      ];
    case "stories":
      return [
        {
          ...base,
          type: "stories",
          props: { heading: str(props.heading), caseStudies: Array.isArray(props.caseStudies) ? (props.caseStudies as string[]) : undefined },
        },
      ];
    case "reasons":
      return [
        {
          ...base,
          type: "reasons",
          props: {
            title: str(props.title),
            subtitle: str(props.subtitle),
            items: list(props.items, (r) => ({
              title: str(r.title) ?? "",
              body: str(r.body),
              image: mediaSrc(str(r.image)),
              sideImage: mediaSrc(str(r.sideImage)),
            })),
          },
        },
      ];
    case "industries":
      return [{ ...base, type: "industries", props: {} }];
    case "highlights":
      return [{ ...base, type: "highlights", props: {} }];
    default:
      return [];
  }
}

/** The service page form as the page component's data (media URLs resolved, blanks dropped). */
function toServicePageData(values: Record<string, unknown>): ServicePageData {
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
    blocks: Array.isArray(values.blocks) ? (values.blocks as Record<string, unknown>[]).flatMap(toBlock) : undefined,
  };
}

/** A case study as the admin list returns it, as far as a service page's story card is concerned. */
interface FeaturedRow {
  slug: string | null;
  title: string;
  summary?: string | null;
  image: string | null;
  category: string | null;
  results?: ServiceStatData[] | null;
  status: string;
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
  const caseStudiesQuery = useQuery({
    queryKey: ["preview-featured-case-studies", site],
    queryFn: async () => {
      const { data } = await contentApi.get("/v1/admin/case-studies", { params: { site } });
      return data.data as FeaturedRow[];
    },
    staleTime: 60_000,
  });
  const introQuery = useQuery({
    queryKey: ["preview-section-intros", site],
    queryFn: async () => {
      const { data } = await contentApi.get("/v1/admin/content-items", { params: { site, type: "section_intro" } });
      return (data.data as { data: SectionIntroData & { section: string } }[]).map((row) => row.data);
    },
    staleTime: 60_000,
  });

  const page = useMemo<PreviewPage>(() => {
    const data = toServicePageData(values);
    const storiesBlock = data.blocks?.find((b) => b.type === "stories");
    const featured = (storiesBlock?.type === "stories" ? (storiesBlock.props.caseStudies ?? []) : [])
      .map((slug) => caseStudiesQuery.data?.find((c) => c.slug === slug && c.status === "PUBLISHED"))
      .filter((c): c is FeaturedRow => !!c);
    // The first story supplies the left-column heading and figures, as on the website.
    const stories: IndustryStoryData[] = featured.map((c, i) => ({
      tag: c.category ?? undefined,
      title: c.title,
      body: c.summary ?? undefined,
      image: mediaSrc(c.image),
      linkLabel: "Read the case study →",
      linkUrl: `/case-studies/${c.slug}`,
      ...(i === 0 ? { metrics: featured[0].results ?? undefined } : {}),
    }));
    const intro = introQuery.data?.find((s) => s.section === "service-stories");
    return {
      kind: "service",
      page: data,
      categoryName: categoriesQuery.data?.find((c) => c.slug === data.category)?.name ?? "",
      stories,
      storiesIntro: intro && { eyebrow: intro.eyebrow, title: intro.title, body: intro.body },
    };
  }, [values, categoriesQuery.data, caseStudiesQuery.data, introQuery.data]);

  return <PreviewPanel page={page} onClose={onClose} />;
}
