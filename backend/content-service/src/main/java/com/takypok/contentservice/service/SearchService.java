package com.takypok.contentservice.service;

import com.takypok.contentservice.search.SearchRepository;
import java.util.Collection;
import java.util.List;
import reactor.core.publisher.Mono;

/**
 * Forgiving text search over content: finds items by their words (title first, then tags and
 * summary, then the body), copes with a half-typed last word, and when nothing matches tries the
 * words a visitor probably meant — the closest words that really occur in the content. See
 * init-schema.sql for the index and the vocabulary it works from.
 */
public interface SearchService {
  /** One suggestion in the as-you-type dropdown. */
  record Suggestion(String kind, String text, String type, String slug, Long id) {}

  /**
   * The outcome of a search. {@code correctedQuery} is set when the query as typed found nothing
   * and these hits are for a corrected spelling; {@code didYouMean} when even then there were no
   * hits but a likelier spelling exists. At most one of the two is set.
   */
  record Resolution(
      String query, String correctedQuery, String didYouMean, List<SearchRepository.Hit> hits) {}

  /**
   * @param types content types to search; empty means every type
   * @param publicOnly only published, active items (the public site) — false adds drafts (admin)
   * @param allowCorrection false for "search exactly what I typed": no spelling correction
   */
  Mono<Resolution> resolve(
      long siteId,
      Collection<String> types,
      String query,
      boolean publicOnly,
      boolean allowCorrection,
      int limit);

  /** Completions and matching titles for a query that is still being typed. */
  Mono<List<Suggestion>> suggest(
      long siteId, Collection<String> types, String query, boolean publicOnly);
}
