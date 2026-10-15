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
  /** Decorative artwork used by the section. */
  image?: string;
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

/** A headline figure shown verbatim. */
export type StatFigure = { value: string; label: string };

/** content_item type "about_hero" — see content-service's AboutHeroFields. */
export type AboutHero = {
  breadcrumb?: string;
  title: string;
  subtitle?: string;
  ctaLabel?: string;
  ctaUrl?: string;
  image?: string;
  stats?: StatFigure[];
};

/** content_item type "about_intro" — see content-service's AboutIntroFields. */
export type AboutIntro = {
  eyebrow?: string;
  title: string;
  /** Paragraphs separated by blank lines. */
  body?: string;
  image?: string;
  decorImage?: string;
  /** Heading above the growth figures, which come from the Stats CMS screen. */
  statsTitle?: string;
};

/** content_item type "about_statement" — the Vision (dark) and Mission (light) bands. */
export type AboutStatement = {
  variant: "vision" | "mission";
  eyebrow?: string;
  title?: string;
  body?: string;
  image?: string;
};

/** content_item type "about_value" — one "Our Values 4C" photo card. */
export type AboutValue = { title: string; image?: string };

/** content_item type "leader" — one member of the leadership team. */
export type Leader = { name: string; title?: string; photo?: string };

/** content_item type "industry_hero" — the Banking, Finance & Insurance page's hero. */
export type IndustryHero = {
  breadcrumb?: string;
  title: string;
  subtitle?: string;
  ctaLabel?: string;
  ctaUrl?: string;
  /** Artwork on the right (transparent). */
  image?: string;
  stats?: StatFigure[];
};

/** content_item type "industry_solution" — one slide (and tab) of the solutions carousel. */
export type IndustrySolution = {
  /** Carousel page this slide belongs to. */
  group: string;
  tabLabel: string;
  title?: string;
  logo?: string;
  image?: string;
  /** One bullet per line. */
  columnA?: string;
  columnB?: string;
};

/** content_item type "industry_reason" — one "reason to partner with us" card. */
export type IndustryReason = { title: string; image?: string };

/** content_item type "industry_story" — one success-story card. */
export type IndustryStory = {
  tag?: string;
  title: string;
  body?: string;
  image?: string;
  linkLabel?: string;
  linkUrl?: string;
  /** Left-column heading and figures (read from the first story). */
  heading?: string;
  metrics?: StatFigure[];
};

/** content_item type "industry_card" — one card of the Industries strip on service pages. */
export type IndustryCard = { label: string; image?: string; url?: string };

/** Generic content_item types, keyed by content_type; each list is in display order. */
export type HomeSections = {
  hero_slide?: HeroSlide[];
  assistant_band?: AssistantBand[];
  section_intro?: SectionIntro[];
  feature_band?: FeatureBand[];
  site_footer?: SiteFooterContent[];
  about_hero?: AboutHero[];
  about_intro?: AboutIntro[];
  about_statement?: AboutStatement[];
  leader?: Leader[];
  about_value?: AboutValue[];
  industry_card?: IndustryCard[];
  industry_hero?: IndustryHero[];
  industry_solution?: IndustrySolution[];
  industry_reason?: IndustryReason[];
  industry_story?: IndustryStory[];
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
