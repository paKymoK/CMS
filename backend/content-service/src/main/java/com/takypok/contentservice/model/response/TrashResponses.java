package com.takypok.contentservice.model.response;

import java.time.Instant;
import java.util.List;

/** What the Trash area shows for posts, and the warnings asked for before trashing or restoring. */
public final class TrashResponses {
  private TrashResponses() {}

  /**
   * One trashed post. {@code previousStatus} is what it was before (restoring always gives a
   * draft); {@code trashedBy} is "Display Name (sub)"; {@code daysLeft} counts whole days to the
   * automatic deletion, rounded up, never below 0.
   */
  public record Row(
      Long id,
      String title,
      String slug,
      String category,
      String authorName,
      String previousStatus,
      Instant trashedAt,
      String trashedBy,
      int daysLeft) {}

  public record Page(List<Row> items, int page, int size, long totalItems, int totalPages) {}

  /** Something that links to a post ("/insights/<slug>" in its text). */
  public record Referrer(Long id, String contentType, String status, String title) {}

  /**
   * The items that link to one post that is about to be trashed. {@code referrers} is the first
   * few; {@code referrerCount} is all of them.
   */
  public record LinkCheck(Long id, String title, int referrerCount, List<Referrer> referrers) {}

  /** What already holds the address a trashed post wants back. */
  public record SlugHolder(Long id, String title) {}

  /**
   * Whether a trashed post can come back under its own address. When {@code holder} is set the
   * address is taken, and {@code suggestion} is a free one to offer instead.
   */
  public record RestoreCheck(
      Long id, String title, String slug, SlugHolder holder, String suggestion) {}
}
