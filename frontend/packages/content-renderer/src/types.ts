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
  Image: ComponentType<{ src: string; sizes: string; priority?: boolean; className?: string; alt?: string }>;
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

export interface ServiceStatData {
  value: string;
  label: string;
}

export interface ServiceCapabilityData {
  title: string;
  body?: string;
  image?: string;
}

export interface ServiceStepData {
  title: string;
  image?: string;
}

export interface ServiceReasonData {
  title: string;
  body?: string;
  image?: string;
  sideImage?: string;
}

export interface CapabilitiesBlockProps {
  eyebrow?: string;
  title?: string;
  subtitle?: string;
  items?: ServiceCapabilityData[];
}

export interface ProcessBlockProps {
  title?: string;
  subtitle?: string;
  steps?: ServiceStepData[];
}

export interface StoriesBlockProps {
  /** Heading of the left column; the case studies themselves are resolved by the host. */
  heading?: string;
  caseStudies?: string[];
}

export interface ReasonsBlockProps {
  title?: string;
  subtitle?: string;
  items?: ServiceReasonData[];
}

/** One block of a service page: where it sits in the list is where it is drawn. */
export type ServiceBlockData = { id: string; hidden?: boolean } & (
  | { type: "capabilities"; props: CapabilitiesBlockProps }
  | { type: "process"; props: ProcessBlockProps }
  | { type: "stories"; props: StoriesBlockProps }
  | { type: "reasons"; props: ReasonsBlockProps }
  | { type: "industries"; props: Record<string, never> }
  | { type: "highlights"; props: Record<string, never> }
);

/** The content of one service page (content_item "service_page"); the website and the admin preview both fill it. */
export interface ServicePageData {
  slug: string;
  category: string;
  navLabel: string;
  shortLabel?: string;
  heroTitle: string;
  heroSubtitle?: string;
  ctaLabel?: string;
  ctaUrl?: string;
  heroImage?: string;
  heroStats?: ServiceStatData[];
  /** The sections below the hero, in page order. */
  blocks?: ServiceBlockData[];
  seoTitle?: string;
  seoDescription?: string;
  /** Social-share image; falls back to the page's main image. */
  seoImage?: string;
  seoNoindex?: boolean;
}

/** One card of the success-stories stack; the first card carries the left-column heading and figures. */
export interface IndustryStoryData {
  tag?: string;
  title: string;
  body?: string;
  image?: string;
  linkLabel?: string;
  linkUrl?: string;
  heading?: string;
  metrics?: ServiceStatData[];
}

/** Eyebrow, title and one-line description above a section. */
export interface SectionIntroData {
  eyebrow?: string;
  title: string;
  body?: string;
}

export interface IndustrySlideData {
  tabLabel: string;
  title?: string;
  logo?: string;
  image?: string;
  /** One bullet per line. */
  columnA?: string;
  columnB?: string;
}

/** One page of the solutions carousel; the arrows move between groups, the tabs between a group's slides. */
export interface IndustrySolutionGroupData {
  name: string;
  slides?: IndustrySlideData[];
}

export interface IndustryReasonData {
  title: string;
  image?: string;
}

export interface SolutionsBlockProps {
  eyebrow?: string;
  title?: string;
  body?: string;
  groups?: IndustrySolutionGroupData[];
}

export interface IndustryReasonsBlockProps {
  eyebrow?: string;
  title?: string;
  body?: string;
  items?: IndustryReasonData[];
}

export interface IndustryStoriesBlockProps {
  eyebrow?: string;
  title?: string;
  body?: string;
  /** Heading and figures of the left column; they belong to the block, not to a card. */
  heading?: string;
  metrics?: ServiceStatData[];
  items?: IndustryStoryData[];
}

export interface InsightsBlockProps {
  eyebrow?: string;
  title?: string;
  body?: string;
  linkLabel?: string;
  linkUrl?: string;
}

/** One block of an industry page: where it sits in the list is where it is drawn. */
export type IndustryBlockData = { id: string; hidden?: boolean } & (
  | { type: "solutions"; props: SolutionsBlockProps }
  | { type: "reasons"; props: IndustryReasonsBlockProps }
  | { type: "stories"; props: IndustryStoriesBlockProps }
  | { type: "insights"; props: InsightsBlockProps }
);

/** The content of one industry page (content_item "industry_page"); the website and the admin preview both fill it. */
export interface IndustryPageData {
  slug: string;
  navLabel: string;
  heroBreadcrumb?: string;
  heroTitle: string;
  heroSubtitle?: string;
  ctaLabel?: string;
  ctaUrl?: string;
  heroImage?: string;
  heroStats?: ServiceStatData[];
  /** The sections below the hero, in page order. */
  blocks?: IndustryBlockData[];
  seoTitle?: string;
  seoDescription?: string;
  /** Social-share image; falls back to the page's main image. */
  seoImage?: string;
  seoNoindex?: boolean;
}

export interface AboutLeaderData {
  name: string;
  title?: string;
  photo?: string;
}

export interface AboutValueData {
  title: string;
  image?: string;
}

export interface AboutIntroBlockProps {
  eyebrow?: string;
  title?: string;
  /** Paragraphs separated by blank lines. */
  body?: string;
  image?: string;
  decorImage?: string;
  growthTitle?: string;
  growthStats?: ServiceStatData[];
}

export interface AboutVisionBlockProps {
  eyebrow?: string;
  title?: string;
  image?: string;
}

export interface AboutMissionBlockProps {
  eyebrow?: string;
  body?: string;
  image?: string;
}

export interface AboutLeadersBlockProps {
  eyebrow?: string;
  title?: string;
  ringImage?: string;
  items?: AboutLeaderData[];
}

export interface AboutValuesBlockProps {
  eyebrow?: string;
  title?: string;
  body?: string;
  items?: AboutValueData[];
}

/** One block of the About page: where it sits in the list is where it is drawn. */
export type AboutBlockData = { id: string; hidden?: boolean } & (
  | { type: "intro"; props: AboutIntroBlockProps }
  | { type: "vision"; props: AboutVisionBlockProps }
  | { type: "mission"; props: AboutMissionBlockProps }
  | { type: "leaders"; props: AboutLeadersBlockProps }
  | { type: "values"; props: AboutValuesBlockProps }
);

/** The content of the About page (content_item "about_page"); the website and the admin preview both fill it. */
export interface AboutPageData {
  heroBreadcrumb?: string;
  heroTitle: string;
  heroSubtitle?: string;
  ctaLabel?: string;
  ctaUrl?: string;
  heroImage?: string;
  heroStats?: ServiceStatData[];
  /** The sections below the hero, in page order. */
  blocks?: AboutBlockData[];
  seoTitle?: string;
  seoDescription?: string;
  /** Social-share image; falls back to the page's main image. */
  seoImage?: string;
  seoNoindex?: boolean;
}
