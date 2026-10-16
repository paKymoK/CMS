package com.takypok.contentservice.model.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.takypok.contentservice.model.content.types.CaseStudyFields;
import com.takypok.contentservice.model.entity.ContentItem;

/**
 * A case_study content_item flattened back to the shape the admin editor and the public pages use:
 * its {@link CaseStudyFields} plus the row's envelope.
 */
public record CaseStudyResponse(
    Long id,
    String slug,
    String title,
    String date,
    String image,
    String category,
    String summary,
    String body,
    JsonNode results,
    Long testimonialId,
    Integer displayOrder,
    Boolean active,
    String status,
    Integer version) {

  public static CaseStudyResponse from(ContentItem item, CaseStudyFields fields) {
    return new CaseStudyResponse(
        item.getId(),
        fields.getSlug(),
        fields.getTitle(),
        fields.getDate(),
        fields.getImage(),
        fields.getCategory(),
        fields.getSummary(),
        fields.getBody(),
        fields.getResults(),
        fields.getTestimonialId(),
        item.getDisplayOrder(),
        item.getActive(),
        item.getStatus(),
        item.getVersion());
  }
}
