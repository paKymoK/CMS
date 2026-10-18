package com.takypok.contentservice.audit;

import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.repository.ContentItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

/**
 * How the application writes content items so the audit trail comes out right. The trail itself is
 * recorded by a trigger on content_item (see init-schema.sql), which reads who made the change from
 * the row's {@code modified_by} — Spring Data auditing stamps it on every save. Two things the
 * trigger cannot see on its own are passed to it from here:
 *
 * <ul>
 *   <li>A <b>delete</b>: the row only knows its last editor, so the item is first re-saved as the
 *       current user and then deleted, in one transaction — the trigger recognises the row version
 *       written by its own transaction and credits the delete to whoever just re-saved it.
 *   <li>A <b>restore</b>: marked with a transaction-local setting, so the trail says "restored"
 *       rather than "updated".
 * </ul>
 *
 * Anything else — an ordinary create or update — needs no help: use the repository's save. This
 * writer exists for the two cases above; routing every write through it keeps them from being
 * forgotten.
 */
@Component
@RequiredArgsConstructor
public class ContentItemWriter {
  private final ContentItemRepository items;
  private final DatabaseClient databaseClient;
  private final TransactionalOperator transaction;

  /** An ordinary create or update; the trigger records it, credited to the signed-in user. */
  public Mono<ContentItem> save(ContentItem item) {
    return items.save(item);
  }

  /** Saves the item and has the audit trail record the write as a restore. */
  public Mono<ContentItem> restore(ContentItem item) {
    return transaction.transactional(
        databaseClient
            .sql("SELECT set_config('cms.audit_action', 'RESTORE', true)")
            .fetch()
            .all()
            .then(Mono.defer(() -> items.save(item))));
  }

  /**
   * Deletes the item, credited to the current user: it is re-saved first (Spring stamps the user on
   * the row; the trigger sees no change to record), then deleted — using the re-saved copy, whose
   * version the optimistic-locking delete expects.
   */
  public Mono<Void> delete(ContentItem item) {
    return transaction.transactional(items.save(item).flatMap(items::delete));
  }
}
