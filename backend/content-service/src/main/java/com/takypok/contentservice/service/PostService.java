package com.takypok.contentservice.service;

import com.takypok.contentservice.model.entity.Post;
import com.takypok.contentservice.model.request.PostCreateRequest;
import com.takypok.contentservice.model.request.PostUpdateRequest;
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
}
