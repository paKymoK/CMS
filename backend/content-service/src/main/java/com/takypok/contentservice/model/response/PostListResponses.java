package com.takypok.contentservice.model.response;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/** What the dedicated post list (POST-01) shows: one slim row per post, plus the filter choices. */
public final class PostListResponses {
  private PostListResponses() {}

  /**
   * A post as a list row. {@code updatedAt} is when it last changed and {@code updatedBy} who did
   * it ("Display Name (sub)"); both come from the row's audit stamps.
   */
  public record Row(
      Long id,
      String title,
      String slug,
      String category,
      List<String> tags,
      String authorName,
      String status,
      Instant publishAt,
      Instant updatedAt,
      String updatedBy,
      Integer version) {}

  /** One page of rows. {@code page} counts from 1. */
  public record Page(List<Row> items, int page, int size, long totalItems, int totalPages) {}

  /** What the filters can offer for a site: counts per status, the categories in use, the years. */
  public record Facets(
      Map<String, Long> statusCounts, List<CategoryCount> categories, List<Integer> years) {}

  public record CategoryCount(String name, long count) {}

  /** The outcome of a bulk action: what changed, and what could not and why. */
  public record BulkResult(List<Long> updated, List<Failure> failed) {}

  public record Failure(Long id, String title, String reason) {}
}
