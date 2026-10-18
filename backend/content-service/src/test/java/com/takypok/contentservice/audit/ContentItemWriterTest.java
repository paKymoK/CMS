package com.takypok.contentservice.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.repository.ContentItemRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ContentItemWriterTest {
  private static final String RESTORE_HINT =
      "SELECT set_config('cms.audit_action', 'RESTORE', true)";

  @Mock private ContentItemRepository items;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private DatabaseClient databaseClient;

  @Mock private TransactionalOperator transaction;

  private ContentItemWriter writer;

  /** What happened, in order, so the tests can check the sequence and not just the calls. */
  private final List<String> events = new ArrayList<>();

  @BeforeEach
  @SuppressWarnings("unchecked")
  void setUp() {
    writer = new ContentItemWriter(items, databaseClient, transaction);
    // The transaction wrapper is a pass-through here; its atomicity is Spring's job.
    lenient()
        .when(transaction.transactional(any(Mono.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
  }

  @Test
  void anOrdinarySaveIsJustASaveTheTriggerDoesTheRest() {
    ContentItem item = new ContentItem();
    when(items.save(item)).thenReturn(Mono.just(item));

    StepVerifier.create(writer.save(item)).expectNext(item).verifyComplete();

    verifyNoInteractions(databaseClient);
  }

  @Test
  void aRestoreSetsTheAuditHintBeforeItSaves() {
    ContentItem item = new ContentItem();
    when(databaseClient.sql(RESTORE_HINT).fetch().all())
        .thenReturn(Flux.<Map<String, Object>>empty().doOnComplete(() -> events.add("hint")));
    when(items.save(item))
        .thenReturn(
            Mono.fromSupplier(
                () -> {
                  events.add("save");
                  return item;
                }));

    StepVerifier.create(writer.restore(item)).expectNext(item).verifyComplete();

    assertEquals(List.of("hint", "save"), events);
  }

  @Test
  void aDeleteReSavesTheItemFirstThenDeletesTheReSavedCopy() {
    ContentItem item = new ContentItem();
    ContentItem reSaved = new ContentItem();
    when(items.save(item))
        .thenReturn(
            Mono.fromSupplier(
                () -> {
                  events.add("re-save");
                  return reSaved;
                }));
    when(items.delete(reSaved)).thenReturn(Mono.fromRunnable(() -> events.add("delete")));

    StepVerifier.create(writer.delete(item)).verifyComplete();

    assertEquals(List.of("re-save", "delete"), events);
    // The optimistic-locking delete needs the copy the re-save returned, not the stale original.
    verify(items).delete(reSaved);
  }
}
