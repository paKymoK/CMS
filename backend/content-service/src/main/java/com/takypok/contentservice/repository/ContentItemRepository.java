package com.takypok.contentservice.repository;

import com.takypok.contentservice.model.entity.ContentItem;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ContentItemRepository extends R2dbcRepository<ContentItem, Long> {
  Flux<ContentItem> findAllBySiteIdAndContentTypeOrderByDisplayOrderAsc(
      Long siteId, String contentType);

  Flux<ContentItem> findAllBySiteIdAndContentTypeAndStatusAndActiveOrderByDisplayOrderAsc(
      Long siteId, String contentType, String status, Boolean active);

  Mono<ContentItem> findByIdAndSiteId(Long id, Long siteId);
}
