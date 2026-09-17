package com.takypok.contentservice.repository;

import com.takypok.contentservice.model.entity.Post;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostRepository extends R2dbcRepository<Post, Long> {
  Flux<Post> findAllBySiteIdOrderByDisplayOrderAsc(Long siteId);

  Flux<Post> findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
      Long siteId, String status, Boolean active);

  Mono<Post> findByIdAndSiteId(Long id, Long siteId);
}
