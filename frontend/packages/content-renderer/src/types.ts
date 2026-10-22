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
  capabilitiesEyebrow?: string;
  capabilitiesTitle?: string;
  capabilitiesSubtitle?: string;
  capabilities?: ServiceCapabilityData[];
  processTitle?: string;
  processSubtitle?: string;
  processSteps?: ServiceStepData[];
  storiesHeading?: string;
  caseStudies?: string[];
  reasonsTitle?: string;
  reasonsSubtitle?: string;
  reasons?: ServiceReasonData[];
  hiddenSections?: string[];
  seoTitle?: string;
  seoDescription?: string;
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
  solutionsEyebrow?: string;
  solutionsTitle?: string;
  solutionsBody?: string;
  solutionGroups?: IndustrySolutionGroupData[];
  reasonsEyebrow?: string;
  reasonsTitle?: string;
  reasonsBody?: string;
  reasons?: IndustryReasonData[];
  storiesEyebrow?: string;
  storiesTitle?: string;
  storiesBody?: string;
  storiesHeading?: string;
  storiesMetrics?: ServiceStatData[];
  stories?: IndustryStoryData[];
  insightsEyebrow?: string;
  insightsTitle?: string;
  insightsBody?: string;
  insightsLinkLabel?: string;
  insightsLinkUrl?: string;
  hiddenSections?: string[];
  seoTitle?: string;
  seoDescription?: string;
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

/** The content of the About page (content_item "about_page"); the website and the admin preview both fill it. */
export interface AboutPageData {
  heroBreadcrumb?: string;
  heroTitle: string;
  heroSubtitle?: string;
  ctaLabel?: string;
  ctaUrl?: string;
  heroImage?: string;
  heroStats?: ServiceStatData[];
  introEyebrow?: string;
  introTitle?: string;
  introBody?: string;
  introImage?: string;
  introDecorImage?: string;
  growthTitle?: string;
  growthStats?: ServiceStatData[];
  visionEyebrow?: string;
  visionTitle?: string;
  visionImage?: string;
  missionEyebrow?: string;
  missionBody?: string;
  missionImage?: string;
  leadersEyebrow?: string;
  leadersTitle?: string;
  leadersRingImage?: string;
  leaders?: AboutLeaderData[];
  valuesEyebrow?: string;
  valuesTitle?: string;
  valuesBody?: string;
  values?: AboutValueData[];
  hiddenSections?: string[];
  seoTitle?: string;
  seoDescription?: string;
}
