export type Stat = {
  value: string;
  label: string;
  note: string;
};

export type CaseStudy = {
  slug?: string;
  title: string;
  date: string;
  category: string;
  image?: string;
};

export type InsightItem = {
  slug?: string;
  title: string;
  date?: string;
  image?: string;
};

export type LogoBadge = {
  name: string;
  logo?: string;
};

export type Testimonial = {
  name: string;
  title: string;
  company: string;
  quote?: string;
  /** Portrait on the right half of the quote card. */
  photo?: string;
  /** Client logo on the quote card (dark artwork, light background). */
  logo?: string;
  /** Abstract artwork behind the quote text. */
  backgroundImage?: string;
  /** Logo variant for the brand card shown beside neighbouring slides, and that card's gradient. */
  flankLogo?: string;
  flankFrom?: string;
  flankTo?: string;
};

export type FooterNavCategory = {
  label: string;
  links: string[];
};

export type ServiceCard = {
  name: string;
  /** Revealed on hover/tap over the card. */
  description?: string;
  image?: string;
};

export type Office = {
  city: string;
  lat: number;
  lon: number;
  /** Flag swatch color (placeholder, per handoff — real flag assets pending). */
  flag: string;
  /** Renders larger with a white border (Hanoi HQ, Osaka). */
  big?: boolean;
  /** Multi-line address, \n-separated. */
  address: string;
  /** Shown in the globe hover card; falls back to the design placeholder when absent. */
  image?: string;
  /** Optional "more" link rendered in the hover card; the card stays open while hovered so it is clickable. */
  link?: { label: string; href: string };
};

/** content_item type "hero_slide" — see content-service's HeroSlideFields. */
export type HeroSlide = {
  title: string;
  subtitle?: string;
  ctaLabel?: string;
  ctaUrl?: string;
  /** Text-placement preset from the design. */
  layout?: "center" | "left" | "right" | "left-wide" | "bottom-left";
  ctaStyle?: "white" | "blue" | "light";
  backgroundImage?: string;
  /** Transparent artwork drawn over the background colour/image. */
  overlayImage?: string;
  backgroundColor?: string;
  backgroundColorEnd?: string;
};

/** content_item type "assistant_band" — see content-service's AssistantBandFields. */
export type AssistantBand = {
  title: string;
  greeting: string;
  welcome?: string;
  suggestions?: string[];
  backdropImage?: string;
  orbImage?: string;
};

/** content_item type "section_intro" — see content-service's SectionIntroFields. */
export type SectionIntro = {
  /** The section's anchor id (whatwedo, testimonials, ...). */
  section: string;
  eyebrow?: string;
  title: string;
  body?: string;
  /** Small label above the section's list (e.g. "Latest Case Study"). */
  kicker?: string;
  /** The section's "see all" link. */
  linkLabel?: string;
  linkUrl?: string;
  /** Label of the section's primary button (e.g. "Read this letter"). */
  buttonLabel?: string;
};

/** content_item type "feature_band" — see content-service's FeatureBandFields. */
export type FeatureBand = {
  eyebrow?: string;
  title: string;
  body?: string;
  ctaLabel?: string;
  ctaUrl?: string;
  image?: string;
};

/** content_item type "site_footer" — see content-service's SiteFooterFields. */
export type SiteFooterContent = {
  title: string;
  ctaLabel?: string;
  ctaUrl?: string;
  copyright?: string;
  privacyLabel?: string;
  privacyUrl?: string;
  termsLabel?: string;
  termsUrl?: string;
  facebookUrl?: string;
  twitterUrl?: string;
  instagramUrl?: string;
};

/** Generic content_item types, keyed by content_type; each list is in display order. */
export type HomeSections = {
  hero_slide?: HeroSlide[];
  assistant_band?: AssistantBand[];
  section_intro?: SectionIntro[];
  feature_band?: FeatureBand[];
  site_footer?: SiteFooterContent[];
};

export type HomeContent = {
  stats: Stat[];
  services: ServiceCard[];
  offices: Office[];
  caseStudies: CaseStudy[];
  insights: InsightItem[];
  awards: LogoBadge[];
  certifications: LogoBadge[];
  partners: LogoBadge[];
  testimonials: Testimonial[];
  footerNav: FooterNavCategory[];
  sections?: HomeSections;
};
