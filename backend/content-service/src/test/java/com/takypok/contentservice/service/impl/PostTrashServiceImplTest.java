package com.takypok.contentservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.model.response.TrashResponses;
import com.takypok.contentservice.repository.ContentItemRepository;
import com.takypok.contentservice.repository.TrashRepository;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.authentication.User;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.ReactiveAuditorAware;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class PostTrashServiceImplTest {
  private static final User ADA = new User("ada", "Ada", null, null, null, null);

  @Mock private TrashRepository trash;
  @Mock private ContentItemRepository items;
  @Mock private ReactiveAuditorAware<User> auditor;

  private PostTrashServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new PostTrashServiceImpl(trash, items, auditor);
  }

  private void signedIn() {
    when(auditor.getCurrentAuditor()).thenReturn(Mono.just(ADA));
  }

  private static ContentItem post(long id, String status) throws Exception {
    ContentItem item = new ContentItem();
    item.setId(id);
    item.setContentType("post");
    item.setStatus(status);
    item.setData(new ObjectMapper().readTree("{\"title\":\"Post " + id + "\"}"));
    return item;
  }

  @Test
  void aDraftScheduledOrArchivedPostGoesToTheTrashAsTheUser() throws Exception {
    signedIn();
    for (long id = 5; id <= 7; id++) {
      String status = id == 5 ? "DRAFT" : id == 6 ? "SCHEDULED" : "ARCHIVED";
      when(items.findByIdAndSiteId(id, 1L)).thenReturn(Mono.just(post(id, status)));
      when(trash.trash(1L, id, ADA)).thenReturn(Mono.just(true));
    }

    StepVerifier.create(service.trash(1L, List.of(5L, 6L, 7L)))
        .assertNext(
            r -> {
              assertEquals(List.of(5L, 6L, 7L), r.updated());
              assertTrue(r.failed().isEmpty());
            })
        .verifyComplete();
  }

  @Test
  void aPublishedPostCannotBeTrashedAndTheOthersStillGo() throws Exception {
    signedIn();
    when(items.findByIdAndSiteId(5L, 1L)).thenReturn(Mono.just(post(5L, "PUBLISHED")));
    when(items.findByIdAndSiteId(6L, 1L)).thenReturn(Mono.just(post(6L, "DRAFT")));
    when(trash.trash(1L, 6L, ADA)).thenReturn(Mono.just(true));

    StepVerifier.create(service.trash(1L, List.of(5L, 6L)))
        .assertNext(
            r -> {
              assertEquals(List.of(6L), r.updated());
              assertEquals(1, r.failed().size());
              assertEquals("Post 5", r.failed().get(0).title());
              assertTrue(r.failed().get(0).reason().contains("archive it"));
            })
        .verifyComplete();
    verify(trash, never()).trash(anyLong(), eq(5L), any());
  }

  @Test
  void aMissingOrOtherSitePostIsReportedNotThrown() {
    when(items.findByIdAndSiteId(9L, 1L)).thenReturn(Mono.empty());

    StepVerifier.create(service.trash(1L, List.of(9L)))
        .assertNext(
            r -> {
              assertTrue(r.updated().isEmpty());
              assertEquals("This post no longer exists", r.failed().get(0).reason());
            })
        .verifyComplete();
  }

  @Test
  void repeatedIdsAreDoneOnce() throws Exception {
    signedIn();
    when(items.findByIdAndSiteId(5L, 1L)).thenReturn(Mono.just(post(5L, "DRAFT")));
    when(trash.trash(1L, 5L, ADA)).thenReturn(Mono.just(true));

    StepVerifier.create(service.trash(1L, List.of(5L, 5L)))
        .assertNext(r -> assertEquals(List.of(5L), r.updated()))
        .verifyComplete();
    verify(trash).trash(1L, 5L, ADA);
  }

  @Test
  void restoreUsesTheGivenAddressForThePostItIsGivenFor() {
    signedIn();
    when(trash.find(1L, 5L))
        .thenReturn(Mono.just(new TrashRepository.Trashed(5, "post", "A", "a")));
    when(trash.find(1L, 6L))
        .thenReturn(Mono.just(new TrashRepository.Trashed(6, "post", "B", "b")));
    when(trash.restore(1L, 5L, ADA, "a-2"))
        .thenReturn(Mono.just(new TrashRepository.Restored(5, "a-2", 0)));
    when(trash.restore(1L, 6L, ADA, null))
        .thenReturn(Mono.just(new TrashRepository.Restored(6, "b", 0)));

    StepVerifier.create(service.restore(1L, List.of(5L, 6L), Map.of(5L, "a-2")))
        .assertNext(r -> assertEquals(List.of(5L, 6L), r.updated()))
        .verifyComplete();
  }

  @Test
  void aTypedAddressIsNormalisedAndAnUnusableOneIsReported() {
    signedIn();
    when(trash.find(1L, 5L))
        .thenReturn(Mono.just(new TrashRepository.Trashed(5, "post", "A", "a")));
    when(trash.find(1L, 6L))
        .thenReturn(Mono.just(new TrashRepository.Trashed(6, "post", "B", "b")));
    when(trash.restore(1L, 5L, ADA, "new-address"))
        .thenReturn(Mono.just(new TrashRepository.Restored(5, "new-address", 0)));

    StepVerifier.create(
            service.restore(1L, List.of(5L, 6L), Map.of(5L, "  New Address ", 6L, "!!!")))
        .assertNext(
            r -> {
              assertEquals(List.of(5L), r.updated());
              assertTrue(r.failed().get(0).reason().contains("letters, numbers and hyphens"));
            })
        .verifyComplete();
  }

  @Test
  void aTakenAddressIsReportedWithTheReasonAndThePostStaysInTheTrash() {
    signedIn();
    when(trash.find(1L, 5L))
        .thenReturn(Mono.just(new TrashRepository.Trashed(5, "post", "A", "a")));
    when(trash.restore(1L, 5L, ADA, null))
        .thenReturn(
            Mono.error(
                new IllegalStateException("ERROR: Slug already in use: a (SQLSTATE 23505)")));

    StepVerifier.create(service.restore(1L, List.of(5L), null))
        .assertNext(
            r -> {
              assertTrue(r.updated().isEmpty());
              assertTrue(r.failed().get(0).reason().contains("/insights/a is already used"));
            })
        .verifyComplete();
  }

  @Test
  void aPostNotInTheTrashIsReportedOnRestoreAndDelete() {
    when(trash.find(1L, 5L)).thenReturn(Mono.empty());

    StepVerifier.create(service.restore(1L, List.of(5L), null))
        .assertNext(r -> assertEquals("This post is not in the Trash", r.failed().get(0).reason()))
        .verifyComplete();
    StepVerifier.create(service.deleteForever(1L, List.of(5L)))
        .assertNext(r -> assertEquals("This post is not in the Trash", r.failed().get(0).reason()))
        .verifyComplete();
  }

  @Test
  void deleteForeverDeletesFromTheTrashAsTheUser() {
    signedIn();
    when(trash.find(1L, 5L))
        .thenReturn(Mono.just(new TrashRepository.Trashed(5, "post", "A", "a")));
    when(trash.deleteForever(1L, List.of(5L), ADA)).thenReturn(Mono.just(1));

    StepVerifier.create(service.deleteForever(1L, List.of(5L)))
        .assertNext(r -> assertEquals(List.of(5L), r.updated()))
        .verifyComplete();
  }

  @Test
  void theListValidatesPageAndSize() {
    org.junit.jupiter.api.Assertions.assertThrows(
        ApplicationException.class, () -> service.list(1L, null, 0, 20));
    org.junit.jupiter.api.Assertions.assertThrows(
        ApplicationException.class, () -> service.list(1L, null, 1, 101));
  }

  @Test
  void restoreCheckOffersAFreeAddressWhenTheOwnIsTaken() {
    when(trash.find(1L, 5L))
        .thenReturn(Mono.just(new TrashRepository.Trashed(5, "post", "A", "a")));
    when(trash.find(1L, 6L))
        .thenReturn(Mono.just(new TrashRepository.Trashed(6, "post", "B", "b")));
    when(trash.slugHolder(1L, "a"))
        .thenReturn(Mono.just(new TrashResponses.SlugHolder(9L, "Other")));
    when(trash.slugHolder(1L, "b")).thenReturn(Mono.empty());
    when(trash.freeSlugLike(1L, "a")).thenReturn(Mono.just("a-2"));

    StepVerifier.create(service.restoreCheck(1L, List.of(5L, 6L)))
        .assertNext(
            checks -> {
              assertEquals("Other", checks.get(0).holder().title());
              assertEquals("a-2", checks.get(0).suggestion());
              assertEquals(null, checks.get(1).holder());
            })
        .verifyComplete();
  }

  @Test
  void linkCheckAddsTheTitleOfEachLinkedPost() throws Exception {
    when(trash.linksTo(1L, List.of(5L)))
        .thenReturn(
            reactor.core.publisher.Flux.just(
                new TrashResponses.LinkCheck(
                    5L,
                    null,
                    2,
                    List.of(new TrashResponses.Referrer(8L, "post", "PUBLISHED", "Other")))));
    when(items.findByIdAndSiteId(5L, 1L)).thenReturn(Mono.just(post(5L, "DRAFT")));

    StepVerifier.create(service.linkCheck(1L, List.of(5L)))
        .assertNext(
            checks -> {
              assertEquals("Post 5", checks.get(0).title());
              assertEquals(2, checks.get(0).referrerCount());
            })
        .verifyComplete();
  }
}
