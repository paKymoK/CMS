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
  testimonial: CaseStudyTestimonial | null;
  related: RelatedCaseStudy[];
};
