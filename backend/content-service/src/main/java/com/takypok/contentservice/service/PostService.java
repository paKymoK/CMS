package com.takypok.contentservice.service;

import com.takypok.contentservice.model.request.PostCreateRequest;
import com.takypok.contentservice.model.request.PostUpdateRequest;
import com.takypok.contentservice.model.response.PostDetailResponse;
import com.takypok.contentservice.model.response.PostListResponses;
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

  /**
   * Changes the status of several posts at once with the same rules as saving one: PUBLISH,
   * UNPUBLISH (back to draft) or ARCHIVE. A post that cannot be changed (it fails validation, say)
   * is reported and the others still go ahead; one that is already in the status is left as it is
   * and counts as done. Only posts of this site are ever touched.
   */
  Mono<PostListResponses.BulkResult> bulkStatus(
      Long siteId, java.util.List<Long> ids, String action);

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
