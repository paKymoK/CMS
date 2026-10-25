package com.takypok.contentservice.model.entity;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * One address an item has had: its current slug (primary) or an old one that still redirects to it.
 * Rows are written by the content_item_slug trigger, never by the application (see
 * init-schema.sql), which only reads them and removes aliases.
 */
@Getter
@Setter
@Table("content_slug")
public class ContentSlug {
  @Id private Long id;
  private Long siteId;
  private String contentType;

  /** The category slug for a service page, empty for the other types. */
  private String scope;

  private String slug;
  private Long itemId;

  @Column("is_primary")
  private boolean primary;

  private Instant createdAt;

  /** The address as it appears after the section prefix: {@code scope/slug}, or just the slug. */
  public String path() {
    return scope == null || scope.isEmpty() ? slug : scope + "/" + slug;
  }
}
