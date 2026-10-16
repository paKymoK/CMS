package com.takypok.contentservice.model.response;

/** Card shape for a Case Study Detail page's "related" rail. */
public record CaseStudySummaryResponse(
    Long id, String slug, String title, String image, String date, String category) {

  public static CaseStudySummaryResponse from(CaseStudyResponse caseStudy) {
    return new CaseStudySummaryResponse(
        caseStudy.id(),
        caseStudy.slug(),
        caseStudy.title(),
        caseStudy.image(),
        caseStudy.date(),
        caseStudy.category());
  }
}
