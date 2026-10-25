package com.takypok.contentservice.repository;

import com.takypok.contentservice.model.entity.ContentItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ContentItemRepository extends R2dbcRepository<ContentItem, Long> {
  Flux<ContentItem> findAllBySiteIdAndContentTypeOrderByDisplayOrderAsc(
      Long siteId, String contentType);

  Flux<ContentItem> findAllBySiteIdAndContentTypeAndStatusOrderByDisplayOrderAsc(
      Long siteId, String contentType, String status);

  Flux<ContentItem> findAllBySiteIdAndStatusOrderByDisplayOrderAsc(Long siteId, String status);

  Mono<ContentItem> findByIdAndSiteId(Long id, Long siteId);

  /**
   * Items keyed by their current slug stored inside {@code data}. At most one row can match: the
   * content_slug table keeps a slug unique per site and type for the types with a detail page. Old
   * slugs are not found here; see SlugRegistry.
   */
  @Query(
      "SELECT * FROM content_item WHERE site_id = :siteId AND content_type = :contentType"
          + " AND data ->> 'slug' = :slug")
  Mono<ContentItem> findBySlug(Long siteId, String contentType, String slug);
}
