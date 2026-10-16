package com.takypok.contentservice.service;

import com.takypok.contentservice.model.request.PostCreateRequest;
import com.takypok.contentservice.model.request.PostUpdateRequest;
import com.takypok.contentservice.model.response.PostDetailResponse;
import com.takypok.contentservice.model.response.PostResponse;
import com.takypok.contentservice.model.response.PostSummaryResponse;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostService {
  Mono<List<PostResponse>> getAll(Long siteId);

  Mono<PostResponse> getById(Long id, Long siteId);

  Mono<PostResponse> create(Long siteId, PostCreateRequest request);

  Mono<PostResponse> update(Long siteId, PostUpdateRequest request);

  Mono<Void> delete(Long id, Long siteId);

  Flux<PostResponse> getPublished(Long siteId);

  Mono<PostDetailResponse> getPublishedDetailBySlug(Long siteId, String slug);

  /**
   * Draft-inclusive counterpart of getPublishedDetailBySlug for the token-gated preview path —
   * ignores status/active, but still filters by siteId. "Related" stays published-only so the
   * preview shows what visitors will actually see in that section.
   */
  Mono<PostDetailResponse> getPreviewDetailBySlug(Long siteId, String slug);

  Mono<List<PostSummaryResponse>> searchPublished(Long siteId, String tag, String q);
}
