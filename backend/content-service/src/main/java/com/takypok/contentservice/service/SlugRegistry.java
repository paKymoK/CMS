package com.takypok.contentservice.service;

import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.model.entity.ContentSlug;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The addresses (slugs) of the content types that have a detail page. A renamed item keeps its old
 * slugs as aliases that redirect to the current one; the content_item_slug trigger maintains them,
 * this service reads them and lets an editor release one.
 */
public interface SlugRegistry {
  /**
   * Whether {@code slug} is an old slug (alias) of an item other than {@code excludeId}. Current
   * slugs are checked against content_item itself; together the two make "slug already in use".
   */
  Mono<Boolean> isAliasOfOther(
      Long siteId, String contentType, String scope, String slug, Long excludeId);

  /**
   * The published item an old slug belongs to, or empty when {@code slug} is not an alias (or the
   * item is not published).
   */
  Mono<ContentItem> findPublishedByAlias(
      Long siteId, String contentType, String scope, String slug);

  /**
   * Where an old address now lives: the item's current {@code scope/slug}. Empty when {@code path}
   * is not an alias, or its item is not published, so the caller answers "not found".
   */
  Mono<String> currentPathOfAlias(Long siteId, String section, String path);

  /** Every slug of an item, the current one first. */
  Flux<ContentSlug> forItem(Long siteId, Long itemId);

  /** Releases an old slug so it can be used again; the current slug cannot be removed. */
  Mono<Void> removeAlias(Long siteId, Long slugId);
}
