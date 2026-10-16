package com.takypok.contentservice.model.response;

/** Card shape for the Case Studies listing: what one card shows, without the body. */
public record CaseStudyCardResponse(
    Long id,
    String slug,
    String title,
    String image,
    String date,
    String category,
    String summary) {

  public static CaseStudyCardResponse from(CaseStudyResponse caseStudy) {
    return new CaseStudyCardResponse(
        caseStudy.id(),
        caseStudy.slug(),
        caseStudy.title(),
        caseStudy.image(),
        caseStudy.date(),
        caseStudy.category(),
        caseStudy.summary());
  }
}
