package com.takypok.contentservice.service;

import com.takypok.contentservice.model.entity.Post;
import com.takypok.contentservice.model.request.PostCreateRequest;
import com.takypok.contentservice.model.request.PostUpdateRequest;
import com.takypok.contentservice.model.response.PostDetailResponse;
import com.takypok.contentservice.model.response.PostSummaryResponse;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostService {
  Mono<List<Post>> getAll(Long siteId);

  Mono<Post> getById(Long id, Long siteId);

  Mono<Post> create(Long siteId, PostCreateRequest request);

  Mono<Post> update(Long siteId, PostUpdateRequest request);

  Mono<Void> delete(Long id, Long siteId);

  Flux<Post> getPublished(Long siteId);

  Mono<PostDetailResponse> getPublishedDetailBySlug(Long siteId, String slug);

  Mono<List<PostSummaryResponse>> searchPublished(Long siteId, String tag, String q);
}
