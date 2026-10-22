package com.takypok.contentservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.contentservice.audit.ContentItemWriter;
import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.model.mapper.ContentItemMapper;
import com.takypok.contentservice.model.request.PostCreateRequest;
import com.takypok.contentservice.model.request.PostUpdateRequest;
import com.takypok.contentservice.repository.ContentItemRepository;
import com.takypok.contentservice.service.SearchService;
import com.takypok.core.config.ConfigObjectMapper;
import com.takypok.core.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Every content/media/chat-touching endpoint must be proven cross-site-safe before it's done, per
 * CLAUDE.md. These exercise PostServiceImpl's site scoping directly (no real DB — same Mockito
 * approach as ContentItemMapperTest) rather than the controller, since that's where the guarantee
 * actually lives: the controller only ever supplies a Host-resolved siteId, never a client value.
 */
@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {
  private final ObjectMapper objectMapper = ConfigObjectMapper.objectMapper();

  @Mock private ContentItemRepository contentItemRepository;
  @Mock private ContentItemMapper contentItemMapper;
  @Mock private SearchService searchService;
  @Mock private ContentItemWriter contentItemWriter;

  private PostServiceImpl service;

  @BeforeEach
  void setUp() {
    service =
        new PostServiceImpl(
            contentItemRepository,
            contentItemMapper,
            objectMapper,
            searchService,
            contentItemWriter);
    // validation itself is ContentItemMapperTest's job: pass the data through unchanged
    lenient()
        .when(contentItemMapper.resolveAndValidate(eq("post"), any(JsonNode.class)))
        .thenAnswer(invocation -> Mono.just(invocation.getArgument(1)));
  }

  private ContentItem post(long id, long siteId, String json) throws Exception {
    ContentItem item = new ContentItem();
    item.setId(id);
    item.setSiteId(siteId);
    item.setContentType("post");
    item.setData(objectMapper.readTree(json));
    item.setDisplayOrder(0);
    item.setActive(true);
    item.setStatus("PUBLISHED");
    return item;
  }

  private void saveEchoesItem() {
    when(contentItemWriter.save(any(ContentItem.class)))
        .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
  }

  @Test
  void publishedDetailLookupNeverLeaksAnotherSitesPostForTheSameSlug() throws Exception {
    ContentItem siteOnesPost =
        post(
            1L,
            1L,
            "{\"slug\":\"shared-slug\",\"title\":\"Site 1's post\",\"category\":\"insight\"}");

    // Site 1 has a published post at this slug...
    when(contentItemRepository.findBySlug(1L, "post", "shared-slug"))
        .thenReturn(Mono.just(siteOnesPost));
    when(contentItemRepository
            .findAllBySiteIdAndContentTypeAndStatusAndActiveOrderByDisplayOrderAsc(
                1L, "post", "PUBLISHED", true))
        .thenReturn(Flux.just(siteOnesPost));
    // ...but site 2 does not, even though the slug string is identical.
    when(contentItemRepository.findBySlug(2L, "post", "shared-slug")).thenReturn(Mono.empty());

    StepVerifier.create(service.getPublishedDetailBySlug(1L, "shared-slug"))
        .assertNext(detail -> assertEquals("Site 1's post", detail.title()))
        .verifyComplete();

    StepVerifier.create(service.getPublishedDetailBySlug(2L, "shared-slug"))
        .expectError(ApplicationException.class)
        .verify();
  }

  @Test
  void publishedDetailHidesADraftPost() throws Exception {
    ContentItem draft = post(1L, 1L, "{\"slug\":\"wip\",\"title\":\"Work in progress\"}");
    draft.setStatus("DRAFT");
    when(contentItemRepository.findBySlug(1L, "post", "wip")).thenReturn(Mono.just(draft));

    StepVerifier.create(service.getPublishedDetailBySlug(1L, "wip"))
        .expectError(ApplicationException.class)
        .verify();
  }

  @Test
  void listingOnlyEverQueriesTheResolvedSiteNeverAnother() {
    when(contentItemRepository
            .findAllBySiteIdAndContentTypeAndStatusAndActiveOrderByDisplayOrderAsc(
                2L, "post", "PUBLISHED", true))
        .thenReturn(Flux.empty());

    StepVerifier.create(service.searchPublished(2L, null, null))
        .assertNext(list -> assertEquals(0, list.size()))
        .verifyComplete();

    verify(contentItemRepository)
        .findAllBySiteIdAndContentTypeAndStatusAndActiveOrderByDisplayOrderAsc(
            2L, "post", "PUBLISHED", true);
    verify(contentItemRepository, never())
        .findAllBySiteIdAndContentTypeAndStatusAndActiveOrderByDisplayOrderAsc(
            1L, "post", "PUBLISHED", true);
  }

  @Test
  void theSameSlugStringCanIndependentlyExistInTwoDifferentSites() {
    when(contentItemRepository.findBySlug(1L, "post", "same-slug")).thenReturn(Mono.empty());
    when(contentItemRepository.findBySlug(2L, "post", "same-slug")).thenReturn(Mono.empty());
    saveEchoesItem();

    PostCreateRequest request = new PostCreateRequest();
    request.setTitle("Some Title");
    request.setSlug("same-slug");

    StepVerifier.create(service.create(1L, request)).expectNextCount(1).verifyComplete();
    StepVerifier.create(service.create(2L, request)).expectNextCount(1).verifyComplete();

    ArgumentCaptor<ContentItem> captor = ArgumentCaptor.forClass(ContentItem.class);
    verify(contentItemWriter, times(2)).save(captor.capture());
    assertEquals("same-slug", captor.getAllValues().get(0).getData().get("slug").asText());
    assertEquals(1L, captor.getAllValues().get(0).getSiteId());
    assertEquals("same-slug", captor.getAllValues().get(1).getData().get("slug").asText());
    assertEquals(2L, captor.getAllValues().get(1).getSiteId());
  }

  @Test
  void aSlugAlreadyUsedByAnotherPostInTheSameSiteIsRejected() throws Exception {
    when(contentItemRepository.findBySlug(1L, "post", "taken"))
        .thenReturn(Mono.just(post(9L, 1L, "{\"slug\":\"taken\",\"title\":\"x\"}")));

    PostCreateRequest request = new PostCreateRequest();
    request.setTitle("Some Title");
    request.setSlug("taken");

    StepVerifier.create(service.create(1L, request))
        .expectError(ApplicationException.class)
        .verify();
    verify(contentItemWriter, never()).save(any(ContentItem.class));
  }

  @Test
  void createCleansBenignLeftoversAndRejectsDangerousBodies() {
    when(contentItemRepository.findBySlug(1L, "post", "s")).thenReturn(Mono.empty());
    saveEchoesItem();

    PostCreateRequest request = new PostCreateRequest();
    request.setTitle("T");
    request.setSlug("s");
    request.setBody("<p style=\"color:red\">ok</p>");

    StepVerifier.create(service.create(1L, request)).expectNextCount(1).verifyComplete();

    ArgumentCaptor<ContentItem> captor = ArgumentCaptor.forClass(ContentItem.class);
    verify(contentItemWriter).save(captor.capture());
    assertEquals("<p>ok</p>", captor.getValue().getData().get("body").asText());

    request.setBody("<p>ok</p><script>alert(1)</script>");
    StepVerifier.create(service.create(1L, request))
        .expectError(ApplicationException.class)
        .verify();
    // the rejected body was never saved
    verify(contentItemWriter, times(1)).save(any(ContentItem.class));
  }

  @Test
  void blankExcerptDateAndAuthorAreFilledOrLeftEmptyInsteadOfRejected() {
    when(contentItemRepository.findBySlug(1L, "post", "just-a-title")).thenReturn(Mono.empty());
    saveEchoesItem();

    PostCreateRequest request = new PostCreateRequest();
    request.setTitle("Just a title");
    request.setStatus("PUBLISHED");
    request.setBody("<h2>Hi</h2><p>First paragraph of the post.</p>");

    StepVerifier.create(service.create(1L, request))
        .assertNext(
            post -> {
              assertEquals("Hi First paragraph of the post.", post.excerpt());
              assertNotNull(post.date());
              assertEquals("default", post.layout());
              assertEquals("insight", post.category());
              assertEquals("PUBLISHED", post.status());
              assertNull(post.authorName());
            })
        .verifyComplete();
  }

  @Test
  void unknownLayoutIsRejected() {
    PostCreateRequest request = new PostCreateRequest();
    request.setTitle("T");
    request.setLayout("fancy");

    StepVerifier.create(service.create(1L, request))
        .expectError(ApplicationException.class)
        .verify();
  }

  private static final String SEO_POST =
      "{\"slug\":\"a-post\",\"title\":\"A post\",\"category\":\"insight\",\"layout\":\"default\","
          + "\"authorBio\":\"Long bio\",\"authorAvatar\":\"/media-service/images/a.webp\","
          + "\"seoTitle\":\"Stored SEO title\",\"seoDescription\":\"Stored SEO description\","
          + "\"seoImage\":\"/media-service/images/seo.webp\",\"seoNoindex\":true}";

  private PostUpdateRequest updateOf(long id) {
    PostUpdateRequest request = new PostUpdateRequest();
    request.setId(id);
    request.setTitle("A post");
    request.setSlug("a-post");
    return request;
  }

  @Test
  void aNewPostStoresItsSeoFieldsAndBlankOnesStayEmpty() {
    when(contentItemRepository.findBySlug(1L, "post", "a-post")).thenReturn(Mono.empty());
    saveEchoesItem();
    PostCreateRequest request = new PostCreateRequest();
    request.setTitle("A post");
    request.setSlug("a-post");
    request.setSeoTitle("Custom title");
    request.setSeoDescription("   ");
    request.setSeoNoindex(true);

    StepVerifier.create(service.create(1L, request))
        .assertNext(
            post -> {
              assertEquals("Custom title", post.seoTitle());
              assertNull(post.seoDescription(), "a blank value is stored as empty");
              assertNull(post.seoImage());
              assertTrue(post.seoNoindex());
            })
        .verifyComplete();
  }

  @Test
  void aNewPostIsIndexableByDefault() {
    when(contentItemRepository.findBySlug(1L, "post", "a-post")).thenReturn(Mono.empty());
    saveEchoesItem();
    PostCreateRequest request = new PostCreateRequest();
    request.setTitle("A post");
    request.setSlug("a-post");

    StepVerifier.create(service.create(1L, request))
        .assertNext(post -> assertFalse(post.seoNoindex()))
        .verifyComplete();
  }

  @Test
  void anUpdateThatLeavesTheSeoFieldsOutKeepsTheStoredOnes() throws Exception {
    when(contentItemRepository.findByIdAndSiteId(5L, 1L))
        .thenReturn(Mono.just(post(5L, 1L, SEO_POST)));
    when(contentItemRepository.findBySlug(1L, "post", "a-post")).thenReturn(Mono.empty());
    saveEchoesItem();

    StepVerifier.create(service.update(1L, updateOf(5L)))
        .assertNext(
            post -> {
              assertEquals("Stored SEO title", post.seoTitle());
              assertEquals("Stored SEO description", post.seoDescription());
              assertEquals("/media-service/images/seo.webp", post.seoImage());
              assertTrue(post.seoNoindex());
            })
        .verifyComplete();
  }

  @Test
  void anUpdateWithBlankSeoFieldsClearsThemAndANewValueReplacesTheOldOne() throws Exception {
    when(contentItemRepository.findByIdAndSiteId(5L, 1L))
        .thenReturn(Mono.just(post(5L, 1L, SEO_POST)));
    when(contentItemRepository.findBySlug(1L, "post", "a-post")).thenReturn(Mono.empty());
    saveEchoesItem();
    PostUpdateRequest request = updateOf(5L);
    request.setSeoTitle("");
    request.setSeoDescription("New description");
    request.setSeoImage("");
    request.setSeoNoindex(false);

    StepVerifier.create(service.update(1L, request))
        .assertNext(
            post -> {
              assertNull(post.seoTitle());
              assertEquals("New description", post.seoDescription());
              assertNull(post.seoImage());
              assertFalse(post.seoNoindex());
            })
        .verifyComplete();
  }

  @Test
  void anUpdateFromAClientThatDoesNotSendTheAuthorBioOrAvatarKeepsThem() throws Exception {
    when(contentItemRepository.findByIdAndSiteId(5L, 1L))
        .thenReturn(Mono.just(post(5L, 1L, SEO_POST)));
    when(contentItemRepository.findBySlug(1L, "post", "a-post")).thenReturn(Mono.empty());
    saveEchoesItem();

    StepVerifier.create(service.update(1L, updateOf(5L)))
        .assertNext(
            post -> {
              assertEquals("Long bio", post.authorBio());
              assertEquals("/media-service/images/a.webp", post.authorAvatar());
            })
        .verifyComplete();
  }

  @Test
  void aBlankAuthorBioClearsIt() throws Exception {
    when(contentItemRepository.findByIdAndSiteId(5L, 1L))
        .thenReturn(Mono.just(post(5L, 1L, SEO_POST)));
    when(contentItemRepository.findBySlug(1L, "post", "a-post")).thenReturn(Mono.empty());
    saveEchoesItem();
    PostUpdateRequest request = updateOf(5L);
    request.setAuthorBio("");

    StepVerifier.create(service.update(1L, request))
        .assertNext(post -> assertNull(post.authorBio()))
        .verifyComplete();
  }

  @Test
  void thePublicDetailCarriesTheSeoFieldsAndNoindexDefaultsToFalse() throws Exception {
    ContentItem withSeo = post(1L, 1L, SEO_POST);
    ContentItem without =
        post(2L, 1L, "{\"slug\":\"plain\",\"title\":\"Plain\",\"category\":\"insight\"}");
    when(contentItemRepository.findBySlug(1L, "post", "a-post")).thenReturn(Mono.just(withSeo));
    when(contentItemRepository.findBySlug(1L, "post", "plain")).thenReturn(Mono.just(without));
    when(contentItemRepository
            .findAllBySiteIdAndContentTypeAndStatusAndActiveOrderByDisplayOrderAsc(
                1L, "post", "PUBLISHED", true))
        .thenReturn(Flux.just(withSeo, without));

    StepVerifier.create(service.getPublishedDetailBySlug(1L, "a-post"))
        .assertNext(
            detail -> {
              assertEquals("Stored SEO title", detail.seoTitle());
              assertEquals("/media-service/images/seo.webp", detail.seoImage());
              assertTrue(detail.seoNoindex());
            })
        .verifyComplete();
    StepVerifier.create(service.getPublishedDetailBySlug(1L, "plain"))
        .assertNext(
            detail -> {
              assertNull(detail.seoTitle());
              assertFalse(detail.seoNoindex());
            })
        .verifyComplete();
  }
}
