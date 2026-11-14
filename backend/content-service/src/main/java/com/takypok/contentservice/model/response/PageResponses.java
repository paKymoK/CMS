package com.takypok.contentservice.model.response;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * What the Pages list (PAGE-01) shows: one slim row per page of any page type, plus the filter
 * choices.
 */
public final class PageResponses {
  private PageResponses() {}

  /**
   * A page as a list row. {@code type} is service_page, industry_page or about_page; {@code path}
   * is its public address; {@code author} is who created it and {@code updatedBy} who last changed
   * it ("Display Name (sub)"); {@code publishedAt} is when it last went live.
   */
  public record Row(
      Long id,
      String type,
      String title,
      String slug,
      String path,
      Long parentId,
      String parentTitle,
      String status,
      boolean hasDraft,
      Instant publishAt,
      String author,
      Instant publishedAt,
      Instant updatedAt,
      String updatedBy,
      Integer version) {}

  /**
   * One page of rows. {@code page} counts from 1; {@code size} is 0 when everything is on one page.
   */
  public record Page(List<Row> items, int page, int size, long totalItems, int totalPages) {}

  public record Author(String sub, String name) {}

  /** Counts per status, how many are in the Trash, and who the authors are. */
  public record Facets(Map<String, Long> statusCounts, long trashCount, List<Author> authors) {}

  /**
   * A trashed page. {@code previousStatus} is what it was before; restoring always gives a draft.
   */
  public record TrashRow(
      Long id,
      String type,
      String title,
      String slug,
      String path,
      String previousStatus,
      Instant trashedAt,
      String trashedBy) {}

  public record TrashPage(
      List<TrashRow> items, int page, int size, long totalItems, int totalPages) {}
}
