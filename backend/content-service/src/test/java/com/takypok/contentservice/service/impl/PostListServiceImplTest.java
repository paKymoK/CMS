package com.takypok.contentservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.contentservice.repository.PostListRepository;
import com.takypok.core.exception.ApplicationException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class PostListServiceImplTest {

  @Mock private PostListRepository repository;
  private PostListServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new PostListServiceImpl(repository, new ObjectMapper());
  }

  private void repositoryReturns(long total, PostListRepository.Row... rows) {
    when(repository.page(any(Long.class), any(), any(), anyInt(), anyInt()))
        .thenReturn(Mono.just(new PostListRepository.Page(List.of(rows), total)));
  }

  private static PostListRepository.Row row(long id, String tagsJson) {
    OffsetDateTime t = OffsetDateTime.of(2026, 11, 7, 9, 30, 0, 0, ZoneOffset.UTC);
    return new PostListRepository.Row(
        id,
        "Title " + id,
        "slug-" + id,
        "insight",
        tagsJson,
        "Lan",
        "PUBLISHED",
        null,
        t,
        "Ha (nttha5)",
        3);
  }

  @Test
  void defaultsAreTheFirstPageOfTwentyNewestFirst() {
    repositoryReturns(45, row(1, "[\"ai\",\"cloud\"]"));
    var filter = ArgumentCaptor.forClass(PostListRepository.Filter.class);
    var sort = ArgumentCaptor.forClass(PostListRepository.Sort.class);

    StepVerifier.create(service.page(1L, null, null, null, null, null, null, null, null))
        .assertNext(
            page -> {
              assertEquals(1, page.page());
              assertEquals(20, page.size());
              assertEquals(45, page.totalItems());
              assertEquals(3, page.totalPages());
              assertEquals(List.of("ai", "cloud"), page.items().get(0).tags());
              assertEquals("Ha (nttha5)", page.items().get(0).updatedBy());
            })
        .verifyComplete();
    verify(repository).page(eq(1L), filter.capture(), sort.capture(), eq(1), eq(20));
    assertEquals(PostListRepository.Sort.UPDATED_DESC, sort.getValue());
    assertEquals(new PostListRepository.Filter(null, null, null, null, null), filter.getValue());
  }

  @Test
  void pageSizeIsCappedAndBlankFiltersMeanNoFilter() {
    repositoryReturns(0);
    var filter = ArgumentCaptor.forClass(PostListRepository.Filter.class);

    StepVerifier.create(service.page(1L, "  ", " ", "", 2026, 11, "title_asc", 0, 5000))
        .assertNext(page -> assertEquals(100, page.size()))
        .verifyComplete();
    verify(repository)
        .page(eq(1L), filter.capture(), eq(PostListRepository.Sort.TITLE_ASC), eq(1), eq(100));
    assertEquals(new PostListRepository.Filter(null, null, null, 2026, 11), filter.getValue());
  }

  @Test
  void theSearchTextIsTrimmedAndCut() {
    repositoryReturns(0);
    var filter = ArgumentCaptor.forClass(PostListRepository.Filter.class);

    StepVerifier.create(service.page(1L, "  hello  ", null, null, null, null, null, null, null))
        .expectNextCount(1)
        .verifyComplete();
    StepVerifier.create(service.page(1L, "x".repeat(500), null, null, null, null, null, null, null))
        .expectNextCount(1)
        .verifyComplete();
    verify(repository, org.mockito.Mockito.times(2))
        .page(eq(1L), filter.capture(), any(), anyInt(), anyInt());
    assertEquals("hello", filter.getAllValues().get(0).q());
    assertEquals(200, filter.getAllValues().get(1).q().length());
  }

  @Test
  void badInputIsRefusedBeforeTheDatabaseIsAsked() {
    for (Object[] bad :
        new Object[][] {
          {"BOGUS", null, null, null}, // status
          {null, null, 13, 2026}, // month out of range
          {null, null, 5, null}, // month without a year
          {null, "sideways", null, null}, // sort
          {null, null, null, 1500}, // year
        }) {
      assertThrows(
          ApplicationException.class,
          () ->
              service
                  .page(
                      1L,
                      null,
                      (String) bad[0],
                      null,
                      (Integer) bad[3],
                      (Integer) bad[2],
                      (String) bad[1],
                      null,
                      null)
                  .block());
    }
    verify(repository, never()).page(any(Long.class), any(), any(), anyInt(), anyInt());
  }

  @Test
  void tagsThatAreNotAnArrayOfTextAreIgnored() {
    repositoryReturns(
        1, row(1, "{\"a\":1}"), row(2, null), row(3, "[\"ok\",1,\"\"]"), row(4, "not json"));

    StepVerifier.create(service.page(1L, null, null, null, null, null, null, null, null))
        .assertNext(
            page -> {
              assertEquals(List.of(), page.items().get(0).tags());
              assertEquals(List.of(), page.items().get(1).tags());
              assertEquals(List.of("ok"), page.items().get(2).tags());
              assertEquals(List.of(), page.items().get(3).tags());
            })
        .verifyComplete();
  }

  @Test
  void facetsAlwaysListEveryStatusEvenAtZero() {
    when(repository.statusCounts(1L)).thenReturn(Mono.just(Map.of("DRAFT", 4L, "PUBLISHED", 9L)));
    when(repository.categories(1L))
        .thenReturn(Mono.just(List.of(new PostListRepository.CategoryCount("insight", 7))));
    when(repository.years(1L)).thenReturn(Mono.just(List.of(2026, 2025)));

    StepVerifier.create(service.facets(1L))
        .assertNext(
            f -> {
              assertEquals(
                  Map.of("DRAFT", 4L, "PUBLISHED", 9L, "SCHEDULED", 0L, "ARCHIVED", 0L),
                  f.statusCounts());
              assertEquals("insight", f.categories().get(0).name());
              assertEquals(List.of(2026, 2025), f.years());
            })
        .verifyComplete();
  }
}
