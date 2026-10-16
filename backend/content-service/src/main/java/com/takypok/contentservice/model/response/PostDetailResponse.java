package com.takypok.contentservice.model.response;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

public record PostDetailResponse(
    Long id,
    String slug,
    String title,
    String excerpt,
    String body,
    String image,
    String date,
    String category,
    JsonNode tags,
    String authorName,
    String authorRole,
    String authorBio,
    String authorAvatar,
    String layout,
    int readMinutes,
    List<PostSummaryResponse> related) {

  public static PostDetailResponse from(PostResponse post, List<PostSummaryResponse> related) {
    return new PostDetailResponse(
        post.id(),
        post.slug(),
        post.title(),
        post.excerpt(),
        post.body(),
        post.image(),
        post.date(),
        post.category(),
        post.tags(),
        post.authorName(),
        post.authorRole(),
        post.authorBio(),
        post.authorAvatar(),
        post.layout() != null ? post.layout() : "default",
        ReadTimeCalculator.minutesFor(post.body()),
        related);
  }
}
