package com.takypok.contentservice.model.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.takypok.contentservice.model.entity.Post;

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

  public static PostSummaryResponse from(Post post) {
    return new PostSummaryResponse(
        post.getId(),
        post.getSlug(),
        post.getTitle(),
        post.getExcerpt(),
        post.getImage(),
        post.getDate(),
        post.getCategory(),
        post.getTags(),
        post.getAuthorName(),
        ReadTimeCalculator.minutesFor(post.getBody()),
        post.getFeatured());
  }
}
