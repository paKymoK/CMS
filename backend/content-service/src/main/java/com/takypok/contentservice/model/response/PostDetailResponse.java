package com.takypok.contentservice.model.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.takypok.contentservice.model.entity.Post;
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

  public static PostDetailResponse from(Post post, List<PostSummaryResponse> related) {
    return new PostDetailResponse(
        post.getId(),
        post.getSlug(),
        post.getTitle(),
        post.getExcerpt(),
        post.getBody(),
        post.getImage(),
        post.getDate(),
        post.getCategory(),
        post.getTags(),
        post.getAuthorName(),
        post.getAuthorRole(),
        post.getAuthorBio(),
        post.getAuthorAvatar(),
        post.getLayout() != null ? post.getLayout() : "default",
        ReadTimeCalculator.minutesFor(post.getBody()),
        related);
  }
}
