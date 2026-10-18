package com.takypok.contentservice.model.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.takypok.contentservice.audit.ContentAuditEntry;
import java.time.OffsetDateTime;
import java.util.List;

/** Response shapes of the audit endpoints. */
public final class AuditResponses {
  private AuditResponses() {}

  /** One line of the history or the activity feed: what happened, to what, by whom, and when. */
  public record Entry(
      Long id,
      Long itemId,
      String contentType,
      String action,
      String title,
      String actorName,
      JsonNode actor,
      OffsetDateTime occurredAt,
      /** Names of the fields that changed (a JSON array of strings). */
      JsonNode changed) {
    public static Entry from(ContentAuditEntry e) {
      return new Entry(
          e.getId(),
          e.getItemId(),
          e.getContentType(),
          e.getAction(),
          e.getTitle(),
          e.getActorName(),
          e.getActor(),
          e.getOccurredAt(),
          e.getChanged());
    }
  }

  /** One entry with the item's whole state before and after — what the diff view is drawn from. */
  public record Detail(Entry entry, JsonNode before, JsonNode after) {
    public static Detail from(ContentAuditEntry e) {
      return new Detail(Entry.from(e), e.getBeforeData(), e.getAfterData());
    }
  }

  public record Page(List<Entry> items, int page, int size, long totalItems, int totalPages) {}

  /** The outcome of a restore: which item now holds the old content. */
  public record Restored(Long itemId, String contentType) {}
}
