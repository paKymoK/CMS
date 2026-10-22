import type { ComponentType, ReactNode } from "react";

/** What a related-article card needs. */
export interface PostCardData {
  id: number;
  slug: string;
  title: string;
  image?: string | null;
  tags: string[];
}

/** Everything the post page draws. The website fills it from content-service, the admin from the editor form. */
export interface PostArticleData {
  title: string;
  image?: string | null;
  authorName?: string | null;
  authorRole?: string | null;
  date?: string | null;
  readMinutes: number;
  /** Sanitized rich-text HTML, with media URLs already resolved. */
  body: string;
  /** default | focused | wide | landing */
  layout?: string | null;
  related: PostCardData[];
}

/** Already-translated UI strings, so this package needs no i18n library. */
export interface PostArticleLabels {
  eyebrow: string;
  minRead: string;
  onThisPage: string;
  share: string;
  keepReading: string;
  relatedInsights: string;
  viewAllInsights: string;
}

/** The two things that differ per host: the router link and the (optimised) image. */
export interface PostArticleSlots {
  Link: ComponentType<{ href: string; className?: string; children: ReactNode }>;
  /** Fills its (relative) parent. */
  Image: ComponentType<{ src: string; sizes: string; priority?: boolean; className?: string }>;
}

export interface CaseStudyResultData {
  value: string;
  label: string;
}

export interface CaseStudyTestimonialData {
  name: string;
  title?: string | null;
  company?: string | null;
  quote?: string | null;
  photo?: string | null;
}

export interface CaseStudyCardData {
  id: number;
  slug: string;
  title: string;
  image?: string | null;
  category?: string | null;
}

/** Everything the case study detail page draws. */
export interface CaseStudyArticleData {
  title: string;
  summary: string;
  category?: string | null;
  date?: string | null;
  image?: string | null;
  /** Sanitized rich-text HTML, with media URLs already resolved. */
  body: string;
  results: CaseStudyResultData[];
  testimonial: CaseStudyTestimonialData | null;
  related: CaseStudyCardData[];
}

export interface CaseStudyArticleLabels {
  home: string;
  eyebrow: string;
  discussProject: string;
  eventPhotography: string;
  results: string;
  moreWork: string;
  relatedCaseStudies: string;
}
