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
import com.takypok.contentservice.search.SearchRepository;
import com.takypok.contentservice.service.SearchService;
import com.takypok.core.config.ConfigObjectMapper;
import com.takypok.core.exception.ApplicationException;
import java.util.List;
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
  @Mock private SearchService searchService;

  private CaseStudyServiceImpl service;

  @BeforeEach
  void setUp() {
    service =
        new CaseStudyServiceImpl(
            contentItemRepository, contentItemMapper, objectMapper, searchService);
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

  private void publishedFor(long siteId, ContentItem... items) {
    when(contentItemRepository
            .findAllBySiteIdAndContentTypeAndStatusAndActiveOrderByDisplayOrderAsc(
                siteId, "case_study", "PUBLISHED", true))
        .thenReturn(Flux.just(items));
  }

  private ContentItem listed(long id, String title, String category, String summary)
      throws Exception {
    return caseStudy(
        id,
        1L,
        "{\"slug\":\"s-"
            + id
            + "\",\"title\":\""
            + title
            + "\",\"category\":\""
            + category
            + "\",\"summary\":\""
            + summary
            + "\"}");
  }

  @Test
  void searchPublishedPagesTheListingAndReportsTotals() throws Exception {
    publishedFor(
        1L,
        listed(1, "One", "Cloud", "a"),
        listed(2, "Two", "AI", "b"),
        listed(3, "Three", "Cloud", "c"),
        listed(4, "Four", "Data", "d"),
        listed(5, "Five", "Cloud", "e"));

    StepVerifier.create(service.searchPublished(1L, null, null, 2, 2))
        .assertNext(
            page -> {
              assertEquals(5, page.totalItems());
              assertEquals(3, page.totalPages());
              assertEquals(2, page.page());
              assertEquals(
                  java.util.List.of("Three", "Four"),
                  page.items().stream().map(i -> i.title()).toList());
              // every category on the site, not only this page's, sorted
              assertEquals(java.util.List.of("AI", "Cloud", "Data"), page.categories());
            })
        .verifyComplete();
  }

  @Test
  void searchPublishedFiltersByCategoryAndTextAndClampsAnOutOfRangePage() throws Exception {
    publishedFor(
        1L,
        listed(1, "Cloud migration", "Cloud", "moved a bank"),
        listed(2, "Chatbot", "AI", "support bot"),
        listed(3, "Cloud cost", "Cloud", "saved money"));

    StepVerifier.create(service.searchPublished(1L, "cloud", null, 1, 6))
        .assertNext(page -> assertEquals(2, page.totalItems()))
        .verifyComplete();

    publishedFor(
        1L,
        listed(1, "Cloud migration", "Cloud", "moved a bank"),
        listed(2, "Chatbot", "AI", "support bot"));
    // The text query goes through SearchService: only the items it returns, best first.
    when(searchService.resolve(
            eq(1L),
            eq(List.of("case_study")),
            eq("SUPPORT"),
            eq(true),
            eq(true),
            any(Integer.class)))
        .thenReturn(
            Mono.just(
                new SearchService.Resolution(
                    "SUPPORT",
                    null,
                    null,
                    List.of(
                        new SearchRepository.Hit(
                            2, "case_study", "PUBLISHED", "x", "Chatbot", 1f)))));
    StepVerifier.create(service.searchPublished(1L, null, "SUPPORT", 1, 6))
        .assertNext(
            page -> {
              assertEquals(1, page.totalItems());
              assertEquals("Chatbot", page.items().get(0).title());
            })
        .verifyComplete();

    publishedFor(1L, listed(1, "Only", "Cloud", "x"));
    StepVerifier.create(service.searchPublished(1L, null, null, 99, 6))
        .assertNext(
            page -> {
              assertEquals(1, page.page());
              assertEquals(1, page.items().size());
            })
        .verifyComplete();
  }

  @Test
  void searchPublishedWithNoMatchesStillReturnsOneEmptyPage() throws Exception {
    publishedFor(1L, listed(1, "One", "Cloud", "a"));

    StepVerifier.create(service.searchPublished(1L, "Nope", null, 1, 6))
        .assertNext(
            page -> {
              assertEquals(0, page.totalItems());
              assertEquals(1, page.totalPages());
              assertEquals(0, page.items().size());
            })
        .verifyComplete();
  }
}
