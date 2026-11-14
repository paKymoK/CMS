package com.takypok.contentservice.service;

import com.takypok.contentservice.model.TaxonomyKind;
import com.takypok.contentservice.model.request.TaxonomyRequest;
import com.takypok.contentservice.model.response.PostListResponses;
import com.takypok.contentservice.model.response.TaxonomyResponses;
import java.util.List;
import java.util.Map;
import reactor.core.publisher.Mono;

/**
 * The categories and tags a post is filed under (CAT-01 to CAT-06, TAG-01 to TAG-06). Both are
 * content items of their own type, per site: Active is PUBLISHED and Inactive is DRAFT, so audit,
 * addresses (with the old ones kept as aliases) and the Trash come from the generic machinery.
 */
public interface TaxonomyService {
  /**
   * One page of the site's categories or tags. {@code sort} is name_asc (default), name_desc,
   * updated_desc, updated_asc, posts_desc or posts_asc; {@code size} 20 by default, at most 100, 0
   * for all.
   */
  Mono<TaxonomyResponses.Page> list(
      Long siteId,
      TaxonomyKind kind,
      String q,
      String status,
      Long parentId,
      String sort,
      Integer page,
      Integer size);

  Mono<TaxonomyResponses.Row> get(Long siteId, TaxonomyKind kind, Long id);

  Mono<TaxonomyResponses.Row> create(Long siteId, TaxonomyKind kind, TaxonomyRequest request);

  Mono<TaxonomyResponses.Row> update(
      Long siteId, TaxonomyKind kind, Long id, TaxonomyRequest request);

  /** ACTIVATE or DEACTIVATE each; one failing does not stop the rest. */
  Mono<PostListResponses.BulkResult> setActive(
      Long siteId, TaxonomyKind kind, List<Long> ids, boolean active);

  /**
   * Moves each to the Trash; its posts lose it (a category's posts fall back to "Uncategorized").
   */
  Mono<PostListResponses.BulkResult> delete(Long siteId, TaxonomyKind kind, List<Long> ids);

  /** Takes each off every post, keeping it. */
  Mono<PostListResponses.BulkResult> removeFromPosts(
      Long siteId, TaxonomyKind kind, List<Long> ids);

  Mono<TaxonomyResponses.TrashPage> trashList(
      Long siteId, TaxonomyKind kind, String q, Integer page, Integer size);

  /** Brings each back (Inactive), with its posts and sub-categories. */
  Mono<PostListResponses.BulkResult> restore(
      Long siteId, TaxonomyKind kind, List<Long> ids, Map<Long, String> slugs);

  Mono<PostListResponses.BulkResult> deleteForever(Long siteId, TaxonomyKind kind, List<Long> ids);

  /**
   * The ids a post will carry after a save, and what is derived from them. {@code
   * requestedCategories} and {@code requestedTags} are null when the client did not send them (the
   * stored ones are kept; a new post gets none). Refuses unknown ids, an Inactive item that is
   * newly added, and more than 15 in total; a post with no category gets "Uncategorized".
   */
  Mono<PostTaxonomy> resolveForPost(
      Long siteId,
      List<Long> requestedCategories,
      List<Long> requestedTags,
      List<Long> storedCategories,
      List<Long> storedTags);

  /** What a post stores: the ids and the names derived from them. */
  record PostTaxonomy(
      List<Long> categoryIds, List<Long> tagIds, String category, List<String> tags) {}

  /** Rewrites every post that carries the item so the names derived in it are current. */
  Mono<Void> syncPosts(Long siteId, TaxonomyKind kind, Long itemId);

  /**
   * Adds the category or tag to each post, or takes it off ({@code remove}); a post that would go
   * over 15 is reported, the rest still change.
   */
  Mono<PostListResponses.BulkResult> assignToPosts(
      Long siteId, TaxonomyKind kind, Long itemId, List<Long> postIds, boolean remove);

  /** The site's Active categories or tags for the public site, with their published post counts. */
  Mono<List<TaxonomyResponses.PublicTerm>> publicList(Long siteId, TaxonomyKind kind);

  /** One Active category or tag by address; "not found" when it is Inactive or unknown. */
  Mono<TaxonomyResponses.PublicTerm> publicTerm(Long siteId, TaxonomyKind kind, String slug);
}
