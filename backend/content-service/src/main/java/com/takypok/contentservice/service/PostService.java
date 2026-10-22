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

  default Mono<List<PostSummaryResponse>> searchPublished(Long siteId, String tag, String q) {
    return searchPublished(siteId, tag, q, false);
  }

  /**
   * The published listing, narrowed to a tag and a text query. The query goes through {@link
   * SearchService}: best match first, and a misspelt query is searched as corrected unless {@code
   * exact} is set.
   */
  Mono<List<PostSummaryResponse>> searchPublished(Long siteId, String tag, String q, boolean exact);
}
