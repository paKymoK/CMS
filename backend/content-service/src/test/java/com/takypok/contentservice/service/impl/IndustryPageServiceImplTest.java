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
  @Mock private com.takypok.contentservice.service.SlugRegistry slugRegistry;

  private IndustryPageServiceImpl service() {
    return new IndustryPageServiceImpl(
        contentItemRepository,
        new FeaturedCaseStudies(contentItemRepository, slugRegistry),
        new GrowthStats(contentItemRepository));
  }

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

    StepVerifier.create(service().getPage(1L, "bfsi"))
        .assertNext(response -> assertEquals("Banking", response.data().get("navLabel").asText()))
        .verifyComplete();
  }

  @Test
  void aDraftPageIsNotFound() {
    when(contentItemRepository.findBySlug(1L, "industry_page", "bfsi"))
        .thenReturn(Mono.just(page("DRAFT")));

    StepVerifier.create(service().getPage(1L, "bfsi"))
        .expectError(ApplicationException.class)
        .verify();
  }

  @Test
  void anUnknownSlugIsNotFound() {
    when(contentItemRepository.findBySlug(1L, "industry_page", "nope")).thenReturn(Mono.empty());

    StepVerifier.create(service().getPage(1L, "nope"))
        .expectError(ApplicationException.class)
        .verify();
  }

  @Test
  void aCaseStudiesBlockFeaturesTheNamedPublishedCaseStudies() {
    ContentItem item = page("PUBLISHED");
    var block = objectMapper.createObjectNode().put("id", "b1").put("type", "case-studies");
    block.putObject("props").putArray("caseStudies").add("acme");
    ((com.fasterxml.jackson.databind.node.ObjectNode) item.getData()).putArray("blocks").add(block);
    ContentItem study = new ContentItem();
    study.setStatus("PUBLISHED");
    study.setData(objectMapper.createObjectNode().put("slug", "acme").put("title", "Acme"));
    when(contentItemRepository.findBySlug(1L, "industry_page", "bfsi")).thenReturn(Mono.just(item));
    when(contentItemRepository.findBySlug(1L, CaseStudyServiceImpl.TYPE, "acme"))
        .thenReturn(Mono.just(study));
    when(slugRegistry.findPublishedByAlias(1L, CaseStudyServiceImpl.TYPE, "", "acme"))
        .thenReturn(Mono.empty());

    StepVerifier.create(service().getPage(1L, "bfsi"))
        .assertNext(
            response -> {
              assertEquals(1, response.caseStudies().size());
              assertEquals("Acme", response.caseStudies().get(0).title());
            })
        .verifyComplete();
  }

  @Test
  void anIntroBlockOnAnIndustryPageGetsTheLibraryGrowthFigures() {
    ContentItem item = page("PUBLISHED");
    var block = objectMapper.createObjectNode().put("id", "i1").put("type", "intro");
    block.putObject("props").put("growthSource", "library");
    ((com.fasterxml.jackson.databind.node.ObjectNode) item.getData()).putArray("blocks").add(block);
    ContentItem stat = new ContentItem();
    stat.setData(objectMapper.createObjectNode().put("value", "32+").put("label", "Years"));
    when(contentItemRepository.findBySlug(1L, "industry_page", "bfsi")).thenReturn(Mono.just(item));
    when(contentItemRepository.findAllBySiteIdAndContentTypeAndStatusOrderByDisplayOrderAsc(
            1L, "stat", "PUBLISHED"))
        .thenReturn(reactor.core.publisher.Flux.just(stat));

    StepVerifier.create(service().getPage(1L, "bfsi"))
        .assertNext(
            response ->
                assertEquals(
                    "32+", response.data().at("/blocks/0/props/growthStats/0/value").asText()))
        .verifyComplete();
  }
}
