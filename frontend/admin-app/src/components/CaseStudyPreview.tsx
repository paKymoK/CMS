import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import { contentApi } from "../lib/api";
import {
  buildPreviewCaseStudy,
  relatedCaseStudiesFor,
  type CaseStudyDraft,
  type CaseStudyListItem,
} from "../lib/contentPreview";
import type { PreviewPage } from "../lib/previewProtocol";
import type { CaseStudyResultData, CaseStudyTestimonialData } from "@cms/content-renderer";
import PreviewPanel from "./PreviewPanel";

export interface ResourcePreviewProps {
  site: string;
  id?: number;
  values: Record<string, unknown>;
  onClose: () => void;
}

const str = (v: unknown) => (typeof v === "string" ? v : "");

/** Preview of the case study being edited: the form values, plus the related strip from the site's published case studies. */
export default function CaseStudyPreview({ site, id, values, onClose }: ResourcePreviewProps) {
  const listQuery = useQuery({
    queryKey: ["preview-related", "case-studies", site],
    queryFn: async () => {
      const { data } = await contentApi.get("/v1/admin/case-studies", { params: { site } });
      return data.data as CaseStudyListItem[];
    },
    staleTime: 60_000,
  });
  const testimonialId = typeof values.testimonialId === "number" ? values.testimonialId : null;
  const testimonialsQuery = useQuery({
    queryKey: ["preview-testimonials", site],
    queryFn: async () => {
      // Testimonials are generic content items: the fields live inside `data`.
      const { data } = await contentApi.get("/v1/admin/content-items", { params: { site, type: "testimonial" } });
      return (data.data as { id: number; data: Omit<CaseStudyTestimonialData, "id"> }[]).map((row) => ({ ...row.data, id: row.id }));
    },
    staleTime: 60_000,
    enabled: testimonialId !== null,
  });

  const page = useMemo<PreviewPage>(() => {
    const draft: CaseStudyDraft = {
      id,
      title: str(values.title),
      summary: str(values.summary),
      category: str(values.category),
      date: str(values.date),
      image: str(values.image) || null,
      body: str(values.body),
      results: Array.isArray(values.results) ? (values.results as CaseStudyResultData[]).map((r) => ({ value: str(r?.value), label: str(r?.label) })) : [],
      testimonial: (testimonialsQuery.data?.find((t) => t.id === testimonialId) as CaseStudyTestimonialData | undefined) ?? null,
    };
    return { kind: "case-study", caseStudy: buildPreviewCaseStudy(draft, relatedCaseStudiesFor(draft, listQuery.data ?? [])) };
  }, [id, values, testimonialId, testimonialsQuery.data, listQuery.data]);

  return <PreviewPanel page={page} onClose={onClose} />;
}
