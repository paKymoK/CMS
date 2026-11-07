package com.takypok.contentservice.service;

import com.takypok.contentservice.model.response.PostListResponses;
import reactor.core.publisher.Mono;

/** The dedicated post list (BRD POST-01): search, filters, sort and pages, done in the database. */
public interface PostListService {

  /**
   * One page of a site's posts. {@code page} counts from 1 (default 1), {@code size} defaults to 20
   * (at most 100), {@code sort} is updated_desc (default), updated_asc, title_asc or title_desc.
   * Month and year are of the last update, in UTC; a month needs a year.
   */
  Mono<PostListResponses.Page> page(
      Long siteId,
      String q,
      String status,
      String category,
      Integer year,
      Integer month,
      String sort,
      Integer page,
      Integer size);

  /** What the filters can offer for the site. */
  Mono<PostListResponses.Facets> facets(Long siteId);
}
