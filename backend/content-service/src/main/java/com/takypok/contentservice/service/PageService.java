package com.takypok.contentservice.service;

import com.takypok.contentservice.model.request.PageQuickEditRequest;
import com.takypok.contentservice.model.response.PageResponses;
import com.takypok.contentservice.model.response.PostListResponses;
import com.takypok.contentservice.model.response.TrashResponses;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import reactor.core.publisher.Mono;

/**
 * The pages (Service, Industry and About) as one list (PAGE-01): search, filters, sort, paging,
 * bulk status changes, quick edit and the Trash. Editing a page's content stays with the generic
 * item API and its editor.
 */
public interface PageService {
  /**
   * One page of the site's pages. {@code sort} is updated_desc (default), updated_asc, title_asc,
   * title_desc, published_desc or published_asc; {@code size} 20 by default, at most 100, 0 for
   * all. The published dates bound when the page last went live (UTC, whole days, both ends
   * included).
   */
  Mono<PageResponses.Page> page(
      Long siteId,
      String q,
      String status,
      String type,
      String author,
      LocalDate publishedFrom,
      LocalDate publishedTo,
      String sort,
      Integer page,
      Integer size);

  Mono<PageResponses.Facets> facets(Long siteId);

  /** PUBLISH, UNPUBLISH (back to draft) or ARCHIVE each; one failing does not stop the rest. */
  Mono<PostListResponses.BulkResult> bulkStatus(Long siteId, List<Long> ids, String action);

  /**
   * Edits the title, address or status on the list, with the same rules as saving in the editor.
   */
  Mono<PageResponses.Row> quickEdit(Long siteId, Long id, PageQuickEditRequest request);

  /**
   * A new draft with the same content as the page (as the editor shows it, pending edits included):
   * its title gets "(copy)" and its address "-copy" (or "-copy-2", ...). The copy has no schedule,
   * no pending draft and no canonical address of its own to point back at the original. The About
   * page cannot be copied.
   */
  Mono<PageResponses.Row> duplicate(Long siteId, Long id);

  Mono<PostListResponses.BulkResult> trash(Long siteId, List<Long> ids);

  Mono<PostListResponses.BulkResult> restore(Long siteId, List<Long> ids, Map<Long, String> slugs);

  Mono<PostListResponses.BulkResult> deleteForever(Long siteId, List<Long> ids);

  Mono<PageResponses.TrashPage> trashList(Long siteId, String q, Integer page, Integer size);

  /**
   * Before trashing: what links to these pages (including a published Service page's place in the
   * menu).
   */
  Mono<List<TrashResponses.LinkCheck>> linkCheck(Long siteId, List<Long> ids);
}
