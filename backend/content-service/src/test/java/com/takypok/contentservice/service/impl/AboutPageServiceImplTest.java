package com.takypok.contentservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.repository.ContentItemRepository;
import com.takypok.core.config.ConfigObjectMapper;
import com.takypok.core.exception.ApplicationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class AboutPageServiceImplTest {
  private final ObjectMapper objectMapper = ConfigObjectMapper.objectMapper();

  @Mock private ContentItemRepository contentItemRepository;

  @Test
  void thePublishedPageIsReturnedAsItsStoredData() {
    ContentItem item = new ContentItem();
    item.setData(objectMapper.createObjectNode().put("heroTitle", "Hello"));
    when(contentItemRepository.findAllBySiteIdAndContentTypeAndStatusOrderByDisplayOrderAsc(
            1L, "about_page", "PUBLISHED"))
        .thenReturn(Flux.just(item));

    StepVerifier.create(new AboutPageServiceImpl(contentItemRepository).getPage(1L))
        .assertNext(response -> assertEquals("Hello", response.data().get("heroTitle").asText()))
        .verifyComplete();
  }

  @Test
  void aSiteWithoutAPublishedPageHasNone() {
    when(contentItemRepository.findAllBySiteIdAndContentTypeAndStatusOrderByDisplayOrderAsc(
            2L, "about_page", "PUBLISHED"))
        .thenReturn(Flux.empty());

    StepVerifier.create(new AboutPageServiceImpl(contentItemRepository).getPage(2L))
        .expectError(ApplicationException.class)
        .verify();
  }

  private ContentItem aboutWith(String growthSource) {
    ContentItem item = new ContentItem();
    var data = objectMapper.createObjectNode().put("heroTitle", "Hello");
    if (growthSource != null) data.put("growthSource", growthSource);
    data.putArray("growthStats").addObject().put("value", "1+").put("label", "Embedded");
    item.setData(data);
    return item;
  }

  private ContentItem stat(String value, String label) {
    ContentItem item = new ContentItem();
    item.setData(
        objectMapper.createObjectNode().put("value", value).put("label", label).put("note", "n"));
    return item;
  }

  private void aboutPageIs(ContentItem about) {
    when(contentItemRepository.findAllBySiteIdAndContentTypeAndStatusOrderByDisplayOrderAsc(
            1L, "about_page", "PUBLISHED"))
        .thenReturn(Flux.just(about));
  }

  @Test
  void libraryModeServesThePublishedStatsInsteadOfTheEmbeddedFigures() {
    aboutPageIs(aboutWith("library"));
    when(contentItemRepository.findAllBySiteIdAndContentTypeAndStatusOrderByDisplayOrderAsc(
            1L, "stat", "PUBLISHED"))
        .thenReturn(Flux.just(stat("32+", "Years"), stat("3000+", "Employees")));

    StepVerifier.create(new AboutPageServiceImpl(contentItemRepository).getPage(1L))
        .assertNext(
            response -> {
              var figures = response.data().get("growthStats");
              assertEquals(2, figures.size());
              assertEquals("32+", figures.get(0).get("value").asText());
              assertEquals("Employees", figures.get(1).get("label").asText());
              assertTrue(figures.get(0).path("note").isMissingNode(), "only value and label");
              assertEquals("Hello", response.data().get("heroTitle").asText());
            })
        .verifyComplete();
  }

  @Test
  void customModeKeepsTheEmbeddedFiguresAndNeverReadsTheLibrary() {
    aboutPageIs(aboutWith("custom"));

    StepVerifier.create(new AboutPageServiceImpl(contentItemRepository).getPage(1L))
        .assertNext(
            response ->
                assertEquals(
                    "Embedded", response.data().get("growthStats").get(0).get("label").asText()))
        .verifyComplete();

    verify(contentItemRepository, never())
        .findAllBySiteIdAndContentTypeAndStatusOrderByDisplayOrderAsc(
            eq(1L), eq("stat"), eq("PUBLISHED"));
  }

  @Test
  void aPageWithoutASourceIsCustom() {
    aboutPageIs(aboutWith(null));

    StepVerifier.create(new AboutPageServiceImpl(contentItemRepository).getPage(1L))
        .assertNext(
            response ->
                assertEquals(
                    "Embedded", response.data().get("growthStats").get(0).get("label").asText()))
        .verifyComplete();
  }

  @Test
  void libraryModeWithNoPublishedStatsShowsNoFigures() {
    aboutPageIs(aboutWith("library"));
    when(contentItemRepository.findAllBySiteIdAndContentTypeAndStatusOrderByDisplayOrderAsc(
            1L, "stat", "PUBLISHED"))
        .thenReturn(Flux.empty());

    StepVerifier.create(new AboutPageServiceImpl(contentItemRepository).getPage(1L))
        .assertNext(response -> assertEquals(0, response.data().get("growthStats").size()))
        .verifyComplete();
  }
}
