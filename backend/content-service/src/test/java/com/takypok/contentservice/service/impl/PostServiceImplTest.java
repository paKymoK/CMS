package com.takypok.contentservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.model.mapper.ContentItemMapper;
import com.takypok.contentservice.model.request.PostCreateRequest;
import com.takypok.contentservice.repository.ContentItemRepository;
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

  private PostServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new PostServiceImpl(contentItemRepository, contentItemMapper, objectMapper);
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
    when(contentItemRepository.save(any(ContentItem.class)))
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
    verify(contentItemRepository, times(2)).save(captor.capture());
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
    verify(contentItemRepository, never()).save(any(ContentItem.class));
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
    verify(contentItemRepository).save(captor.capture());
    assertEquals("<p>ok</p>", captor.getValue().getData().get("body").asText());

    request.setBody("<p>ok</p><script>alert(1)</script>");
    StepVerifier.create(service.create(1L, request))
        .expectError(ApplicationException.class)
        .verify();
    // the rejected body was never saved
    verify(contentItemRepository, times(1)).save(any(ContentItem.class));
  }

  @Test
  void previewDetailIncludesDraftsButNeverLeaksAnotherSitesPostForTheSameSlug() throws Exception {
    ContentItem siteOnesDraft =
        post(
            1L,
            1L,
            "{\"slug\":\"shared-slug\",\"title\":\"Site 1's draft\",\"category\":\"insight\"}");
    siteOnesDraft.setStatus("DRAFT");
    siteOnesDraft.setActive(false);

    when(contentItemRepository.findBySlug(1L, "post", "shared-slug"))
        .thenReturn(Mono.just(siteOnesDraft));
    when(contentItemRepository
            .findAllBySiteIdAndContentTypeAndStatusAndActiveOrderByDisplayOrderAsc(
                1L, "post", "PUBLISHED", true))
        .thenReturn(Flux.empty());
    when(contentItemRepository.findBySlug(2L, "post", "shared-slug")).thenReturn(Mono.empty());

    StepVerifier.create(service.getPreviewDetailBySlug(1L, "shared-slug"))
        .assertNext(detail -> assertEquals("Site 1's draft", detail.title()))
        .verifyComplete();

    StepVerifier.create(service.getPreviewDetailBySlug(2L, "shared-slug"))
        .expectError(ApplicationException.class)
        .verify();
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
}
