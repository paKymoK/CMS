export type Stat = {
  value: string;
  label: string;
  note: string;
};

export type CaseStudy = {
  title: string;
  date: string;
  category: string;
  image?: string;
};

export type InsightItem = {
  title: string;
  image?: string;
};

export type LogoBadge = {
  name: string;
  logo: string;
};

export type FlankLogo = {
  name: string;
  /** Card background color behind the logo wordmark (placeholder, per handoff). */
  color: string;
};

export type Testimonial = {
  name: string;
  title: string;
  company: string;
  quote?: string;
  photo?: string;
  /** The two client-logo cards flanking the centre quote card on this slide. */
  flankLogos?: [FlankLogo, FlankLogo];
};

export type FooterNavCategory = {
  label: string;
  links: string[];
};

export type ServiceCard = {
  name: string;
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
};
