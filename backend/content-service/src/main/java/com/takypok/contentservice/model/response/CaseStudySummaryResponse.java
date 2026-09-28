package com.takypok.contentservice.model.response;

import com.takypok.contentservice.model.entity.CaseStudy;

/** Card shape for a Case Study Detail page's "related" rail. */
public record CaseStudySummaryResponse(
    Long id, String slug, String title, String image, String date, String category) {

  public static CaseStudySummaryResponse from(CaseStudy caseStudy) {
    return new CaseStudySummaryResponse(
        caseStudy.getId(),
        caseStudy.getSlug(),
        caseStudy.getTitle(),
        caseStudy.getImage(),
        caseStudy.getDate(),
        caseStudy.getCategory());
  }
}
