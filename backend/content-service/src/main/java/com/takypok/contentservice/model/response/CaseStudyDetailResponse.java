package com.takypok.contentservice.model.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.takypok.contentservice.model.entity.CaseStudy;
import com.takypok.contentservice.model.entity.Testimonial;
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
    TestimonialSummary testimonial,
    List<CaseStudySummaryResponse> related) {

  public record TestimonialSummary(
      String name, String title, String company, String quote, String photo) {
    public static TestimonialSummary from(Testimonial testimonial) {
      return new TestimonialSummary(
          testimonial.getName(),
          testimonial.getTitle(),
          testimonial.getCompany(),
          testimonial.getQuote(),
          testimonial.getPhoto());
    }
  }

  public static CaseStudyDetailResponse from(
      CaseStudy caseStudy, TestimonialSummary testimonial, List<CaseStudySummaryResponse> related) {
    return new CaseStudyDetailResponse(
        caseStudy.getId(),
        caseStudy.getSlug(),
        caseStudy.getTitle(),
        caseStudy.getSummary(),
        caseStudy.getBody(),
        caseStudy.getImage(),
        caseStudy.getDate(),
        caseStudy.getCategory(),
        caseStudy.getResults(),
        testimonial,
        related);
  }
}
