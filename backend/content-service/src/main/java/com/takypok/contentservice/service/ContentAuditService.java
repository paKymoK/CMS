package com.takypok.contentservice.service;

import com.takypok.contentservice.model.response.AuditResponses;
import java.time.LocalDate;
import reactor.core.publisher.Mono;

/** Reads the content audit trail and restores items from it. */
public interface ContentAuditService {
  /** Every field is optional; the ones given are combined (AND). */
  record Filter(
      Long itemId,
      String contentType,
      /** An {@code AuditAction} name. */
      String action,
      /** Part of the actor's name. */
      String actor,
      LocalDate from,
      /** Inclusive: the whole of this day is included. */
      LocalDate to) {}

  /** A page of entries, newest first. {@code page} is 1-based; out-of-range values are clamped. */
  Mono<AuditResponses.Page> list(Long siteId, Filter filter, int page, int size);

  Mono<AuditResponses.Detail> get(Long siteId, Long entryId);

  /**
   * Puts an existing item back to the content an entry recorded (the state after it). The item
   * keeps its current status, and the restore is itself recorded. A deleted item is never brought
   * back: there is nothing to restore in place, and its last content is available from the entry
   * (see {@link #get}) to start a new item from.
   */
  Mono<AuditResponses.Restored> restore(Long siteId, Long entryId);
}
