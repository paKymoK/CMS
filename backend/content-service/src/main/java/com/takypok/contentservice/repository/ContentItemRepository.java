package com.takypok.contentservice.repository;

import com.takypok.contentservice.model.entity.ContentItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ContentItemRepository extends R2dbcRepository<ContentItem, Long> {
  Flux<ContentItem> findAllBySiteIdAndContentTypeOrderByDisplayOrderAsc(
      Long siteId, String contentType);

  Flux<ContentItem> findAllBySiteIdAndContentTypeAndStatusAndActiveOrderByDisplayOrderAsc(
      Long siteId, String contentType, String status, Boolean active);

  Flux<ContentItem> findAllBySiteIdOrderByDisplayOrderAsc(Long siteId);

  Flux<ContentItem> findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
      Long siteId, String status, Boolean active);

  Mono<ContentItem> findByIdAndSiteId(Long id, Long siteId);

  /**
   * Items keyed by a slug stored inside {@code data} (case studies). At most one row can match: the
   * uq_content_item_case_study_slug index backs it for case_study, and the service-catalog types
   * check their own uniqueness on write.
   */
  @Query(
      "SELECT * FROM content_item WHERE site_id = :siteId AND content_type = :contentType"
          + " AND data ->> 'slug' = :slug")
  Mono<ContentItem> findBySlug(Long siteId, String contentType, String slug);
}
