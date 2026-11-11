package com.takypok.contentservice.service;

import com.takypok.contentservice.model.response.PostListResponses;
import com.takypok.contentservice.model.response.TrashResponses;
import java.util.List;
import java.util.Map;
import reactor.core.publisher.Mono;

/**
 * The Trash for posts (BRD POST-01): a post moved there leaves the post table, is kept 30 days and
 * can be restored (as a draft) or deleted for good; after 30 days the database deletes it. Every
 * action works on several posts and reports per post, like the other bulk actions: one that cannot
 * be done is listed with a reason and the rest still go ahead.
 */
public interface PostTrashService {

  /** Moves posts to the Trash. A published post cannot go: archive it or move it to draft first. */
  Mono<PostListResponses.BulkResult> trash(Long siteId, List<Long> ids);

  /**
   * Restores posts as drafts, each under its own address or, from {@code slugs} (post id to
   * address), another one. A post whose address is taken is reported with that reason and stays in
   * the Trash.
   */
  Mono<PostListResponses.BulkResult> restore(Long siteId, List<Long> ids, Map<Long, String> slugs);

  /** Deletes posts from the Trash for good. */
  Mono<PostListResponses.BulkResult> deleteForever(Long siteId, List<Long> ids);

  /** One page of the site's trashed posts, newest first; {@code q} is part of a title or slug. */
  Mono<TrashResponses.Page> list(Long siteId, String q, Integer page, Integer size);

  /**
   * Before trashing: which posts have other items linking to them. Posts nothing links to are left
   * out of the answer, so an empty list means nothing to warn about.
   */
  Mono<List<TrashResponses.LinkCheck>> linkCheck(Long siteId, List<Long> ids);

  /**
   * Before restoring: for each trashed post whether its address is free; when taken, who holds it
   * and a free address to offer instead.
   */
  Mono<List<TrashResponses.RestoreCheck>> restoreCheck(Long siteId, List<Long> ids);
}
