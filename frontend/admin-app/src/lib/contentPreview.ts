import type {
  CaseStudyArticleData,
  CaseStudyCardData,
  CaseStudyResultData,
  CaseStudyTestimonialData,
  PostArticleData,
  PostCardData,
} from "@cms/content-renderer";
import { resolveMediaHtml, mediaSrc } from "./media";

/** What the editor form holds, as far as the post page is concerned. */
export interface PostDraft {
  id?: number;
  title: string;
  image: string | null;
  date: string;
  category: string;
  body: string;
  authorName: string;
  authorRole: string;
  layout: string;
}

/** A published post as the admin list returns it. */
export interface PostListItem {
  id: number;
  slug: string | null;
  title: string;
  image: string | null;
  category: string | null;
  tags: string[] | null;
  status: string;
  active: boolean;
  displayOrder: number;
}

const MAX_RELATED = 3;
const WORDS_PER_MINUTE = 200;

/** Same estimate content-service makes (ReadTimeCalculator). */
export function readMinutes(body: string): number {
  const text = body.replace(/<[^>]*>/g, " ").trim();
  if (!text) return 1;
  return Math.max(1, Math.round(text.split(/\s+/).length / WORDS_PER_MINUTE));
}

/** Same pick content-service makes (PostServiceImpl.relatedTo): same category first, then the rest in order. */
export function relatedFor(draft: Pick<PostDraft, "id" | "category">, all: PostListItem[]): PostCardData[] {
  const published = all
    .filter((p) => p.status === "PUBLISHED" && p.active && p.slug && p.id !== draft.id)
    .sort((a, b) => a.displayOrder - b.displayOrder);
  const same = published.filter((p) => p.category && p.category === draft.category);
  const others = published.filter((p) => !(p.category && p.category === draft.category));
  return [...same, ...others]
    .slice(0, MAX_RELATED)
    .map((p) => ({ id: p.id, slug: p.slug!, title: p.title, image: mediaSrc(p.image) ?? null, tags: p.tags ?? [] }));
}

/** Blank display date means "the day it is published", so the preview shows today. */
function displayDate(date: string): string {
  return date.trim() || new Date().toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" });
}

export function buildPreviewPost(draft: PostDraft, related: PostCardData[]): PostArticleData {
  return {
    title: draft.title || "Untitled post",
    image: mediaSrc(draft.image) ?? null,
    authorName: draft.authorName || null,
    authorRole: draft.authorRole || null,
    date: displayDate(draft.date),
    readMinutes: readMinutes(draft.body),
    body: resolveMediaHtml(draft.body),
    layout: draft.layout,
    related,
  };
}

/** What the case study editor form holds, as far as the detail page is concerned. */
export interface CaseStudyDraft {
  id?: number;
  title: string;
  summary: string;
  category: string;
  date: string;
  image: string | null;
  body: string;
  results: CaseStudyResultData[];
  testimonial: CaseStudyTestimonialData | null;
}

/** A case study as the admin list returns it. */
export interface CaseStudyListItem {
  id: number;
  slug: string | null;
  title: string;
  image: string | null;
  category: string | null;
  status: string;
  active: boolean;
  displayOrder: number;
}

/** Same pick content-service makes (CaseStudyServiceImpl.relatedTo). */
export function relatedCaseStudiesFor(
  draft: Pick<CaseStudyDraft, "id" | "category">,
  all: CaseStudyListItem[],
): CaseStudyCardData[] {
  const published = all
    .filter((c) => c.status === "PUBLISHED" && c.active && c.slug && c.id !== draft.id)
    .sort((a, b) => a.displayOrder - b.displayOrder);
  const same = published.filter((c) => c.category && c.category === draft.category);
  const others = published.filter((c) => !(c.category && c.category === draft.category));
  return [...same, ...others]
    .slice(0, MAX_RELATED)
    .map((c) => ({ id: c.id, slug: c.slug!, title: c.title, image: mediaSrc(c.image) ?? null, category: c.category }));
}

export function buildPreviewCaseStudy(draft: CaseStudyDraft, related: CaseStudyCardData[]): CaseStudyArticleData {
  return {
    title: draft.title || "Untitled case study",
    summary: draft.summary,
    category: draft.category,
    date: draft.date,
    image: mediaSrc(draft.image) ?? null,
    body: resolveMediaHtml(draft.body),
    results: draft.results.filter((r) => r.value.trim() || r.label.trim()),
    testimonial: draft.testimonial
      ? { ...draft.testimonial, photo: mediaSrc(draft.testimonial.photo) ?? null }
      : null,
    related,
  };
}
