package com.takypok.contentservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    when(contentItemRepository
            .findAllBySiteIdAndContentTypeAndStatusAndActiveOrderByDisplayOrderAsc(
                1L, "about_page", "PUBLISHED", true))
        .thenReturn(Flux.just(item));

    StepVerifier.create(new AboutPageServiceImpl(contentItemRepository).getPage(1L))
        .assertNext(response -> assertEquals("Hello", response.data().get("heroTitle").asText()))
        .verifyComplete();
  }

  @Test
  void aSiteWithoutAPublishedPageHasNone() {
    when(contentItemRepository
            .findAllBySiteIdAndContentTypeAndStatusAndActiveOrderByDisplayOrderAsc(
                2L, "about_page", "PUBLISHED", true))
        .thenReturn(Flux.empty());

    StepVerifier.create(new AboutPageServiceImpl(contentItemRepository).getPage(2L))
        .expectError(ApplicationException.class)
        .verify();
  }
}
