package com.takypok.contentservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.model.mapper.ContentItemMapper;
import com.takypok.contentservice.model.request.CaseStudyCreateRequest;
import com.takypok.contentservice.repository.ContentItemRepository;
import com.takypok.core.config.ConfigObjectMapper;
import com.takypok.core.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Cross-site-safety tests for CaseStudyServiceImpl — see PostServiceImplTest for the same on Post.
 */
@ExtendWith(MockitoExtension.class)
class CaseStudyServiceImplTest {
  private final ObjectMapper objectMapper = ConfigObjectMapper.objectMapper();

  @Mock private ContentItemRepository contentItemRepository;
  @Mock private ContentItemMapper contentItemMapper;

  private CaseStudyServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new CaseStudyServiceImpl(contentItemRepository, contentItemMapper, objectMapper);
    // validation itself is ContentItemMapperTest's job: pass the data through unchanged
    lenient()
        .when(contentItemMapper.resolveAndValidate(eq("case_study"), any(JsonNode.class)))
        .thenAnswer(invocation -> Mono.just(invocation.getArgument(1)));
  }

  private ContentItem caseStudy(long id, long siteId, String json) throws Exception {
    ContentItem item = new ContentItem();
    item.setId(id);
    item.setSiteId(siteId);
    item.setContentType("case_study");
    item.setData(objectMapper.readTree(json));
    item.setDisplayOrder(0);
    item.setActive(true);
    item.setStatus("PUBLISHED");
    return item;
  }

  private ContentItem testimonial(long id, long siteId) {
    ContentItem item = new ContentItem();
    item.setId(id);
    item.setSiteId(siteId);
    item.setContentType("testimonial");
    item.setData(objectMapper.createObjectNode().put("name", "A client"));
    return item;
  }

  @Test
  void publishedDetailLookupNeverLeaksAnotherSitesCaseStudyForTheSameSlug() throws Exception {
    ContentItem siteOnes =
        caseStudy(
            1L,
            1L,
            "{\"slug\":\"shared-slug\",\"title\":\"Site 1's case study\",\"category\":\"cloud\"}");

    when(contentItemRepository.findBySlug(1L, "case_study", "shared-slug"))
        .thenReturn(Mono.just(siteOnes));
    when(contentItemRepository
            .findAllBySiteIdAndContentTypeAndStatusAndActiveOrderByDisplayOrderAsc(
                1L, "case_study", "PUBLISHED", true))
        .thenReturn(Flux.just(siteOnes));
    when(contentItemRepository.findBySlug(2L, "case_study", "shared-slug"))
        .thenReturn(Mono.empty());

    StepVerifier.create(service.getPublishedDetailBySlug(1L, "shared-slug"))
        .assertNext(detail -> assertEquals("Site 1's case study", detail.title()))
        .verifyComplete();

    StepVerifier.create(service.getPublishedDetailBySlug(2L, "shared-slug"))
        .expectError(ApplicationException.class)
        .verify();
  }

  @Test
  void publishedDetailHidesADraftCaseStudy() throws Exception {
    ContentItem draft = caseStudy(1L, 1L, "{\"slug\":\"wip\",\"title\":\"Work in progress\"}");
    draft.setStatus("DRAFT");
    when(contentItemRepository.findBySlug(1L, "case_study", "wip")).thenReturn(Mono.just(draft));

    StepVerifier.create(service.getPublishedDetailBySlug(1L, "wip"))
        .expectError(ApplicationException.class)
        .verify();
  }

  @Test
  void creatingACaseStudyWithAnotherSitesTestimonialIdIsRejected() {
    // testimonial 5 belongs to site 1, not site 2
    when(contentItemRepository.findByIdAndSiteId(5L, 2L)).thenReturn(Mono.empty());

    CaseStudyCreateRequest request = new CaseStudyCreateRequest();
    request.setTitle("A case study");
    request.setTestimonialId(5L);

    StepVerifier.create(service.create(2L, request))
        .expectError(ApplicationException.class)
        .verify();

    verify(contentItemRepository, never()).save(any(ContentItem.class));
  }

  @Test
  void creatingACaseStudyLinkedToAnItemThatIsNotATestimonialIsRejected() throws Exception {
    when(contentItemRepository.findByIdAndSiteId(5L, 1L))
        .thenReturn(Mono.just(caseStudy(5L, 1L, "{\"title\":\"Not a quote\"}")));

    CaseStudyCreateRequest request = new CaseStudyCreateRequest();
    request.setTitle("A case study");
    request.setTestimonialId(5L);

    StepVerifier.create(service.create(1L, request))
        .expectError(ApplicationException.class)
        .verify();

    verify(contentItemRepository, never()).save(any(ContentItem.class));
  }

  @Test
  void creatingACaseStudyWithItsOwnSitesTestimonialSucceeds() {
    when(contentItemRepository.findByIdAndSiteId(5L, 1L))
        .thenReturn(Mono.just(testimonial(5L, 1L)));
    when(contentItemRepository.findBySlug(1L, "case_study", "a-case-study"))
        .thenReturn(Mono.empty());
    when(contentItemRepository.save(any(ContentItem.class)))
        .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

    CaseStudyCreateRequest request = new CaseStudyCreateRequest();
    request.setTitle("A case study");
    request.setTestimonialId(5L);

    StepVerifier.create(service.create(1L, request))
        .assertNext(
            created -> {
              assertEquals("a-case-study", created.slug());
              assertEquals(5L, created.testimonialId());
              assertEquals("DRAFT", created.status());
              assertEquals(true, created.active());
            })
        .verifyComplete();
  }

  @Test
  void aTakenSlugGetsANumericSuffixWhenGeneratedFromTheTitle() throws Exception {
    when(contentItemRepository.findBySlug(1L, "case_study", "a-case-study"))
        .thenReturn(Mono.just(caseStudy(9L, 1L, "{\"slug\":\"a-case-study\",\"title\":\"x\"}")));
    when(contentItemRepository.findBySlug(1L, "case_study", "a-case-study-2"))
        .thenReturn(Mono.empty());
    when(contentItemRepository.save(any(ContentItem.class)))
        .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

    CaseStudyCreateRequest request = new CaseStudyCreateRequest();
    request.setTitle("A case study");

    StepVerifier.create(service.create(1L, request))
        .assertNext(created -> assertEquals("a-case-study-2", created.slug()))
        .verifyComplete();
  }

  @Test
  void anExplicitlyRequestedTakenSlugIsRejected() throws Exception {
    when(contentItemRepository.findBySlug(1L, "case_study", "taken"))
        .thenReturn(Mono.just(caseStudy(9L, 1L, "{\"slug\":\"taken\",\"title\":\"x\"}")));

    CaseStudyCreateRequest request = new CaseStudyCreateRequest();
    request.setTitle("A case study");
    request.setSlug("taken");

    StepVerifier.create(service.create(1L, request))
        .expectError(ApplicationException.class)
        .verify();
  }

  @Test
  void previewDetailIncludesDraftsButNeverLeaksAnotherSitesCaseStudyForTheSameSlug()
      throws Exception {
    ContentItem siteOnesDraft =
        caseStudy(
            1L,
            1L,
            "{\"slug\":\"shared-slug\",\"title\":\"Site 1's draft\",\"category\":\"cloud\"}");
    siteOnesDraft.setStatus("DRAFT");
    siteOnesDraft.setActive(false);

    when(contentItemRepository.findBySlug(1L, "case_study", "shared-slug"))
        .thenReturn(Mono.just(siteOnesDraft));
    when(contentItemRepository
            .findAllBySiteIdAndContentTypeAndStatusAndActiveOrderByDisplayOrderAsc(
                1L, "case_study", "PUBLISHED", true))
        .thenReturn(Flux.empty());
    when(contentItemRepository.findBySlug(2L, "case_study", "shared-slug"))
        .thenReturn(Mono.empty());

    StepVerifier.create(service.getPreviewDetailBySlug(1L, "shared-slug"))
        .assertNext(detail -> assertEquals("Site 1's draft", detail.title()))
        .verifyComplete();

    StepVerifier.create(service.getPreviewDetailBySlug(2L, "shared-slug"))
        .expectError(ApplicationException.class)
        .verify();
  }
}
