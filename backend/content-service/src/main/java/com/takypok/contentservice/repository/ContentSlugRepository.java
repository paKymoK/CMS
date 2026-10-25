package com.takypok.contentservice.repository;

import com.takypok.contentservice.model.entity.ContentSlug;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ContentSlugRepository extends R2dbcRepository<ContentSlug, Long> {
  Mono<ContentSlug> findBySiteIdAndContentTypeAndScopeAndSlug(
      Long siteId, String contentType, String scope, String slug);

  Mono<ContentSlug> findByItemIdAndPrimaryTrue(Long itemId);

  Flux<ContentSlug> findAllBySiteIdAndItemIdOrderByPrimaryDescIdAsc(Long siteId, Long itemId);
}
