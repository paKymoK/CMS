package com.takypok.contentservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.takypok.contentservice.model.entity.Post;
import com.takypok.contentservice.model.request.PostCreateRequest;
import com.takypok.contentservice.repository.PostRepository;
import com.takypok.core.exception.ApplicationException;
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

  @Mock private PostRepository postRepository;

  @Test
  void publishedDetailLookupNeverLeaksAnotherSitesPostForTheSameSlug() {
    PostServiceImpl service = new PostServiceImpl(postRepository);
    Post siteOnesPost = new Post();
    siteOnesPost.setId(1L);
    siteOnesPost.setSiteId(1L);
    siteOnesPost.setSlug("shared-slug");
    siteOnesPost.setTitle("Site 1's post");
    siteOnesPost.setCategory("insight");

    // Site 1 has a published post at this slug...
    when(postRepository.findBySiteIdAndSlugAndStatusAndActive(1L, "shared-slug", "PUBLISHED", true))
        .thenReturn(Mono.just(siteOnesPost));
    when(postRepository.findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
            1L, "PUBLISHED", true))
        .thenReturn(Flux.just(siteOnesPost));
    // ...but site 2 does not, even though the slug string is identical.
    when(postRepository.findBySiteIdAndSlugAndStatusAndActive(2L, "shared-slug", "PUBLISHED", true))
        .thenReturn(Mono.empty());

    StepVerifier.create(service.getPublishedDetailBySlug(1L, "shared-slug"))
        .assertNext(detail -> assertEquals("Site 1's post", detail.title()))
        .verifyComplete();

    StepVerifier.create(service.getPublishedDetailBySlug(2L, "shared-slug"))
        .expectError(ApplicationException.class)
        .verify();
  }

  @Test
  void listingOnlyEverQueriesTheResolvedSiteNeverAnother() {
    PostServiceImpl service = new PostServiceImpl(postRepository);
    when(postRepository.findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
            2L, "PUBLISHED", true))
        .thenReturn(Flux.empty());

    StepVerifier.create(service.searchPublished(2L, null, null))
        .assertNext(list -> assertEquals(0, list.size()))
        .verifyComplete();

    verify(postRepository)
        .findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(2L, "PUBLISHED", true);
    verify(postRepository, never())
        .findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(1L, "PUBLISHED", true);
  }

  @Test
  void theSameSlugStringCanIndependentlyExistInTwoDifferentSites() {
    PostServiceImpl service = new PostServiceImpl(postRepository);
    when(postRepository.existsBySiteIdAndSlug(1L, "same-slug")).thenReturn(Mono.just(false));
    when(postRepository.existsBySiteIdAndSlug(2L, "same-slug")).thenReturn(Mono.just(false));
    when(postRepository.save(any(Post.class)))
        .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

    PostCreateRequest request = new PostCreateRequest();
    request.setTitle("Some Title");
    request.setSlug("same-slug");

    StepVerifier.create(service.create(1L, request)).expectNextCount(1).verifyComplete();
    StepVerifier.create(service.create(2L, request)).expectNextCount(1).verifyComplete();

    ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
    verify(postRepository, times(2)).save(captor.capture());
    assertEquals("same-slug", captor.getAllValues().get(0).getSlug());
    assertEquals(1L, captor.getAllValues().get(0).getSiteId());
    assertEquals("same-slug", captor.getAllValues().get(1).getSlug());
    assertEquals(2L, captor.getAllValues().get(1).getSiteId());
  }
}
