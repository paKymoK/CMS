package com.takypok.contentservice.model.response;

import com.takypok.contentservice.model.entity.ContentSlug;
import java.time.Instant;

/** One address of an item: its current slug, or an old one that redirects to it. */
public record SlugResponse(Long id, String path, boolean primary, Instant createdAt) {
  public static SlugResponse from(ContentSlug row) {
    return new SlugResponse(row.getId(), row.path(), row.isPrimary(), row.getCreatedAt());
  }
}
