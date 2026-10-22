export type PostSummary = {
  id: number;
  slug: string;
  title: string;
  excerpt?: string | null;
  image?: string | null;
  date: string;
  category: string;
  tags: string[];
  authorName?: string | null;
  readMinutes: number;
  featured: boolean;
};

export type PostDetail = Omit<PostSummary, "featured"> & {
  body: string;
  /** default | focused | wide | landing — picked by the author per post. */
  layout?: string;
  authorRole?: string;
  authorBio?: string;
  authorAvatar?: string;
  /** Search-engine fields: each falls back to the title / excerpt / cover image when blank. */
  seoTitle?: string | null;
  seoDescription?: string | null;
  seoImage?: string | null;
  seoNoindex?: boolean;
  related: PostSummary[];
};

export type CaseStudyResult = {
  value: string;
  label: string;
};

export type CaseStudyTestimonial = {
  name: string;
  title: string;
  company: string;
  quote?: string;
  photo?: string;
};

export type RelatedCaseStudy = {
  id: number;
  slug: string;
  title: string;
  image?: string;
  date: string;
  category: string;
};

export type CaseStudyDetail = {
  id: number;
  slug: string;
  title: string;
  summary: string;
  body: string;
  image?: string;
  date: string;
  category: string;
  results: CaseStudyResult[];
  /** Search-engine fields: each falls back to the title / summary / cover image when blank. */
  seoTitle?: string | null;
  seoDescription?: string | null;
  seoImage?: string | null;
  seoNoindex?: boolean;
  testimonial: CaseStudyTestimonial | null;
  related: RelatedCaseStudy[];
};

/** One card on the Case Studies listing. */
export type CaseStudyCard = {
  id: number;
  slug: string;
  title: string;
  image?: string | null;
  date: string;
  category?: string | null;
  summary?: string | null;
};

/** One server-side page of the Case Studies listing. */
export type CaseStudyPage = {
  items: CaseStudyCard[];
  /** 1-based; the server clamps an out-of-range request. */
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
  /** Every category in use on the site — feeds the Capabilities filter. */
  categories: string[];
};
