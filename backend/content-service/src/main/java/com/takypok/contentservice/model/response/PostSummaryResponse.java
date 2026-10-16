package com.takypok.contentservice.model.response;

import com.fasterxml.jackson.databind.JsonNode;

/** Card shape for the Insights listing and for a detail page's "related" rail. */
public record PostSummaryResponse(
    Long id,
    String slug,
    String title,
    String excerpt,
    String image,
    String date,
    String category,
    JsonNode tags,
    String authorName,
    int readMinutes,
    Boolean featured) {

  public static PostSummaryResponse from(PostResponse post) {
    return new PostSummaryResponse(
        post.id(),
        post.slug(),
        post.title(),
        post.excerpt(),
        post.image(),
        post.date(),
        post.category(),
        post.tags(),
        post.authorName(),
        ReadTimeCalculator.minutesFor(post.body()),
        post.featured());
  }
}
