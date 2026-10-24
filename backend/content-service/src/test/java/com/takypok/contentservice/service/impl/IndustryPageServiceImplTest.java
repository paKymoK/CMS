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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class IndustryPageServiceImplTest {
  private final ObjectMapper objectMapper = ConfigObjectMapper.objectMapper();

  @Mock private ContentItemRepository contentItemRepository;

  private ContentItem page(String status) {
    ContentItem item = new ContentItem();
    item.setId(1L);
    item.setSiteId(1L);
    item.setContentType("industry_page");
    item.setData(objectMapper.createObjectNode().put("slug", "bfsi").put("navLabel", "Banking"));
    item.setStatus(status);
    return item;
  }

  @Test
  void aPublishedPageIsReturnedAsItsStoredData() {
    when(contentItemRepository.findBySlug(1L, "industry_page", "bfsi"))
        .thenReturn(Mono.just(page("PUBLISHED")));

    StepVerifier.create(new IndustryPageServiceImpl(contentItemRepository).getPage(1L, "bfsi"))
        .assertNext(response -> assertEquals("Banking", response.data().get("navLabel").asText()))
        .verifyComplete();
  }

  @Test
  void aDraftPageIsNotFound() {
    when(contentItemRepository.findBySlug(1L, "industry_page", "bfsi"))
        .thenReturn(Mono.just(page("DRAFT")));

    StepVerifier.create(new IndustryPageServiceImpl(contentItemRepository).getPage(1L, "bfsi"))
        .expectError(ApplicationException.class)
        .verify();
  }

  @Test
  void anUnknownSlugIsNotFound() {
    when(contentItemRepository.findBySlug(1L, "industry_page", "nope")).thenReturn(Mono.empty());

    StepVerifier.create(new IndustryPageServiceImpl(contentItemRepository).getPage(1L, "nope"))
        .expectError(ApplicationException.class)
        .verify();
  }
}
