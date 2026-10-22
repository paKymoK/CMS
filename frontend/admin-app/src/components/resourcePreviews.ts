import type { ComponentType } from "react";
import CaseStudyPreview, { type ResourcePreviewProps } from "./CaseStudyPreview";
import IndustryPreview from "./IndustryPreview";
import ServicePreview from "./ServicePreview";

/** A resource's preview, by resource key. A resource with no entry has no Preview button in the full-page editor. */
export const RESOURCE_PREVIEWS: Record<string, ComponentType<ResourcePreviewProps>> = {
  "case-studies": CaseStudyPreview,
  "service-pages": ServicePreview,
  "industry-pages": IndustryPreview,
};
