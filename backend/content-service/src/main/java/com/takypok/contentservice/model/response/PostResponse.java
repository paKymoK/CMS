package com.takypok.contentservice.model.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.takypok.contentservice.model.content.types.PostFields;
import com.takypok.contentservice.model.entity.ContentItem;
import java.time.Instant;

/**
 * A post content_item flattened back to the shape the admin editor and the public pages use: its
 * {@link PostFields} plus the row's envelope.
 */
public record PostResponse(
    Long id,
    String slug,
    String category,
    String image,
    String title,
    String excerpt,
    String date,
    String body,
    String authorName,
    String authorRole,
    String authorBio,
    String authorAvatar,
    JsonNode tags,
    Boolean featured,
    String layout,
    String seoTitle,
    String seoDescription,
    String seoImage,
    Boolean seoNoindex,
    Integer displayOrder,
    String status,
    Instant publishAt,
    Instant unpublishAt,
    Integer version) {

  public static PostResponse from(ContentItem item, PostFields fields) {
    return new PostResponse(
        item.getId(),
        fields.getSlug(),
        fields.getCategory(),
        fields.getImage(),
        fields.getTitle(),
        fields.getExcerpt(),
        fields.getDate(),
        fields.getBody(),
        fields.getAuthorName(),
        fields.getAuthorRole(),
        fields.getAuthorBio(),
        fields.getAuthorAvatar(),
        fields.getTags(),
        fields.getFeatured(),
        fields.getLayout(),
        fields.getSeoTitle(),
        fields.getSeoDescription(),
        fields.getSeoImage(),
        fields.getSeoNoindex(),
        item.getDisplayOrder(),
        item.getStatus(),
        item.getPublishAt(),
        item.getUnpublishAt(),
        item.getVersion());
  }
}
