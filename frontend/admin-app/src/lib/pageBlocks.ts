import type { IndustryStoryData, PageBlockData, SectionIntroData, ServiceStatData } from "@cms/content-renderer";
import { mediaSrc } from "./media";

const str = (v: unknown) => (typeof v === "string" && v.trim() ? v : undefined);
const list = <T,>(v: unknown, map: (item: Record<string, unknown>) => T): T[] | undefined =>
  Array.isArray(v) ? v.map((item) => map((item ?? {}) as Record<string, unknown>)) : undefined;
const stat = (s: Record<string, unknown>) => ({ value: str(s.value) ?? "", label: str(s.label) ?? "" });

/** The published Stats rows, as the public endpoints serve them to an intro block set to use the library. */
export type GrowthFigure = { value: string; label: string };

/** One stored block as the page component's data (media URLs resolved, blanks dropped); a block of an unknown type is skipped. */
function toBlock(raw: Record<string, unknown>, growthLibrary: GrowthFigure[] | undefined): PageBlockData[] {
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
    case "case-studies":
      return [
        {
          ...base,
          type: "case-studies",
          props: { heading: str(props.heading), caseStudies: Array.isArray(props.caseStudies) ? (props.caseStudies as string[]) : undefined },
        },
      ];
    case "reasons-slider":
      return [
        {
          ...base,
          type: "reasons-slider",
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
    case "solutions":
      return [
        {
          ...base,
          type: "solutions",
          props: {
            eyebrow: str(props.eyebrow),
            title: str(props.title),
            body: str(props.body),
            groups: list(props.groups, (g) => ({
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
    case "reasons-cards":
      return [
        {
          ...base,
          type: "reasons-cards",
          props: {
            eyebrow: str(props.eyebrow),
            title: str(props.title),
            body: str(props.body),
            items: list(props.items, (r) => ({ title: str(r.title) ?? "", image: mediaSrc(str(r.image)) })),
          },
        },
      ];
    case "stories":
      return [
        {
          ...base,
          type: "stories",
          props: {
            eyebrow: str(props.eyebrow),
            title: str(props.title),
            body: str(props.body),
            heading: str(props.heading),
            metrics: list(props.metrics, stat),
            items: list(props.items, (s) => ({
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
          props: { eyebrow: str(props.eyebrow), title: str(props.title), body: str(props.body), linkLabel: str(props.linkLabel), linkUrl: str(props.linkUrl) },
        },
      ];
    case "intro":
      return [
        {
          ...base,
          type: "intro",
          props: {
            eyebrow: str(props.eyebrow),
            title: str(props.title),
            body: str(props.body),
            image: mediaSrc(str(props.image)),
            decorImage: mediaSrc(str(props.decorImage)),
            growthTitle: str(props.growthTitle),
            growthStats: props.growthSource === "library" ? (growthLibrary ?? []) : list(props.growthStats, stat),
          },
        },
      ];
    case "vision":
      return [{ ...base, type: "vision", props: { eyebrow: str(props.eyebrow), title: str(props.title), image: mediaSrc(str(props.image)) } }];
    case "mission":
      return [{ ...base, type: "mission", props: { eyebrow: str(props.eyebrow), body: str(props.body), image: mediaSrc(str(props.image)) } }];
    case "leaders":
      return [
        {
          ...base,
          type: "leaders",
          props: {
            eyebrow: str(props.eyebrow),
            title: str(props.title),
            ringImage: mediaSrc(str(props.ringImage)),
            items: list(props.items, (l) => ({ name: str(l.name) ?? "", title: str(l.title), photo: mediaSrc(str(l.photo)) })),
          },
        },
      ];
    case "values":
      return [
        {
          ...base,
          type: "values",
          props: {
            eyebrow: str(props.eyebrow),
            title: str(props.title),
            body: str(props.body),
            items: list(props.items, (v) => ({ title: str(v.title) ?? "", image: mediaSrc(str(v.image)) })),
          },
        },
      ];
    default:
      return [];
  }
}

/** The stored `blocks` list of a Service or Industry page form as the page components' data. */
export function toPageBlocks(blocks: unknown, growthLibrary?: GrowthFigure[]): PageBlockData[] | undefined {
  return Array.isArray(blocks) ? (blocks as Record<string, unknown>[]).flatMap((b) => toBlock(b, growthLibrary)) : undefined;
}

/** A case study as the admin list returns it, as far as a page's story card is concerned. */
export interface FeaturedRow {
  slug: string | null;
  title: string;
  summary?: string | null;
  image: string | null;
  category: string | null;
  results?: ServiceStatData[] | null;
  status: string;
}

/** The case studies a page's case-studies block names (published ones, in the order named) as story cards; the first supplies the left column's figures, as on the website. */
export function featuredStories(blocks: PageBlockData[] | undefined, rows: FeaturedRow[] | undefined): IndustryStoryData[] {
  const block = blocks?.find((b) => b.type === "case-studies");
  const featured = (block?.type === "case-studies" ? (block.props.caseStudies ?? []) : [])
    .map((slug) => rows?.find((c) => c.slug === slug && c.status === "PUBLISHED"))
    .filter((c): c is FeaturedRow => !!c);
  return featured.map((c, i) => ({
    tag: c.category ?? undefined,
    title: c.title,
    body: c.summary ?? undefined,
    image: mediaSrc(c.image),
    linkLabel: "Read the case study →",
    linkUrl: `/case-studies/${c.slug}`,
    ...(i === 0 ? { metrics: featured[0].results ?? undefined } : {}),
  }));
}

export type { SectionIntroData };
