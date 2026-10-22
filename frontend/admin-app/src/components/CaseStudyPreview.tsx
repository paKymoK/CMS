import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import { contentApi } from "../lib/api";
import { buildPreviewCaseStudy, relatedCaseStudiesFor, type CaseStudyDraft, type CaseStudyListItem } from "../lib/contentPreview";
import type { PreviewPage } from "../lib/previewProtocol";
import PreviewPanel from "./PreviewPanel";

/** Preview of the case study being edited: the form state, plus the related strip from the site's published case studies. */
export default function CaseStudyPreview({ site, draft, onClose }: { site: string; draft: CaseStudyDraft; onClose: () => void }) {
  const listQuery = useQuery({
    queryKey: ["preview-related", "case-studies", site],
    queryFn: async () => {
      const { data } = await contentApi.get("/v1/admin/case-studies", { params: { site } });
      return data.data as CaseStudyListItem[];
    },
    staleTime: 60_000,
  });

  const page = useMemo<PreviewPage>(
    () => ({ kind: "case-study", caseStudy: buildPreviewCaseStudy(draft, relatedCaseStudiesFor(draft, listQuery.data ?? [])) }),
    [draft, listQuery.data],
  );

  return <PreviewPanel page={page} onClose={onClose} />;
}
