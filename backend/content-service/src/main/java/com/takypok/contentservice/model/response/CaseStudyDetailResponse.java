package com.takypok.contentservice.model.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.takypok.contentservice.model.content.types.TestimonialFields;
import java.util.List;

public record CaseStudyDetailResponse(
    Long id,
    String slug,
    String title,
    String summary,
    String body,
    String image,
    String date,
    String category,
    JsonNode results,
    String seoTitle,
    String seoDescription,
    String seoImage,
    Boolean seoNoindex,
    TestimonialSummary testimonial,
    List<CaseStudySummaryResponse> related) {

  public record TestimonialSummary(
      String name, String title, String company, String quote, String photo) {
    public static TestimonialSummary from(TestimonialFields testimonial) {
      return new TestimonialSummary(
          testimonial.getName(),
          testimonial.getTitle(),
          testimonial.getCompany(),
          testimonial.getQuote(),
          testimonial.getPhoto());
    }
  }

  public static CaseStudyDetailResponse from(
      CaseStudyResponse caseStudy,
      TestimonialSummary testimonial,
      List<CaseStudySummaryResponse> related) {
    return new CaseStudyDetailResponse(
        caseStudy.id(),
        caseStudy.slug(),
        caseStudy.title(),
        caseStudy.summary(),
        caseStudy.body(),
        caseStudy.image(),
        caseStudy.date(),
        caseStudy.category(),
        caseStudy.results(),
        caseStudy.seoTitle(),
        caseStudy.seoDescription(),
        caseStudy.seoImage(),
        Boolean.TRUE.equals(caseStudy.seoNoindex()),
        testimonial,
        related);
  }
}
