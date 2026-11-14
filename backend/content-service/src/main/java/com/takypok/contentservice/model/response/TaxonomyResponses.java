package com.takypok.contentservice.model.response;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/** What the category and tag screens show. */
public final class TaxonomyResponses {
  private TaxonomyResponses() {}

  /**
   * A category or tag as a list row. {@code status} is ACTIVE or INACTIVE; {@code publishedCount}
   * is how many published posts carry it; {@code isDefault} marks "Uncategorized".
   */
  public record Row(
      Long id,
      String name,
      String slug,
      String description,
      Long parentId,
      String parentName,
      String status,
      long publishedCount,
      boolean isDefault,
      Instant updatedAt,
      String updatedBy,
      Integer version) {}

  /** One page of rows. {@code page} counts from 1. */
  public record Page(
      List<Row> items,
      int page,
      int size,
      long totalItems,
      int totalPages,
      Map<String, Long> statusCounts,
      long trashCount) {}

  public record TrashRow(
      Long id, String name, String slug, Instant trashedAt, String trashedBy, int postCount) {}

  public record TrashPage(
      List<TrashRow> items, int page, int size, long totalItems, int totalPages) {}

  /** A category or tag on the public site, with how many published posts it has. */
  public record PublicTerm(
      Long id, String name, String slug, String description, String parentName, long count) {}

  public record PublicTermPage(PublicTerm term, List<PostSummaryResponse> posts) {}
}
