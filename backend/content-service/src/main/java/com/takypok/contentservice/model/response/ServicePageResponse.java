package com.takypok.contentservice.model.response;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/**
 * One service page: its authored content (the service_page item's data, as stored), the category it
 * sits in, and the case studies it features, already resolved from their slugs.
 */
public record ServicePageResponse(
    JsonNode data, ServiceNavResponse.Category category, List<FeaturedCaseStudy> caseStudies) {

  public record FeaturedCaseStudy(
      String slug, String title, String summary, String image, String category, JsonNode results) {}
}
