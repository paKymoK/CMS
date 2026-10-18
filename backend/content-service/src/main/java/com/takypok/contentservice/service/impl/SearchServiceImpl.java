package com.takypok.contentservice.service.impl;

import com.takypok.contentservice.search.SearchQuery;
import com.takypok.contentservice.search.SearchRepository;
import com.takypok.contentservice.service.SearchService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
  /** Below this a word is too short to be corrected without guessing wildly. */
  private static final int MIN_CORRECTABLE_LENGTH = 4;

  private static final int SUGGESTED_TERMS = 4;
  private static final int SUGGESTED_TITLES = 4;

  private final SearchRepository repository;

  @Override
  public Mono<Resolution> resolve(
      long siteId,
      Collection<String> types,
      String query,
      boolean publicOnly,
      boolean allowCorrection,
      int limit) {
    List<String> tokens = SearchQuery.tokens(query);
    if (tokens.isEmpty()) {
      return Mono.just(new Resolution(query, null, null, List.of()));
    }
    return repository
        .fullText(siteId, types, SearchQuery.toTsQuery(tokens), publicOnly, limit)
        .collectList()
        .flatMap(
            hits -> {
              if (!hits.isEmpty() || !allowCorrection) {
                return Mono.just(new Resolution(query, null, null, hits));
              }
              return correct(siteId, tokens, publicOnly)
                  .flatMap(
                      corrected ->
                          searchCorrected(
                              siteId, types, query, tokens, corrected, publicOnly, limit));
            });
  }

  /** Re-searches with the spelling corrected; failing that, falls back to similar titles. */
  private Mono<Resolution> searchCorrected(
      long siteId,
      Collection<String> types,
      String query,
      List<String> tokens,
      List<String> corrected,
      boolean publicOnly,
      int limit) {
    boolean changed = !corrected.equals(tokens);
    Mono<List<SearchRepository.Hit>> correctedHits =
        changed
            ? repository
                .fullText(siteId, types, SearchQuery.toTsQuery(corrected), publicOnly, limit)
                .collectList()
            : Mono.just(List.<SearchRepository.Hit>of());
    return correctedHits.flatMap(
        hits -> {
          if (!hits.isEmpty()) {
            return Mono.just(new Resolution(query, SearchQuery.join(corrected), null, hits));
          }
          String typed = SearchQuery.join(tokens);
          return repository
              .fuzzyTitle(siteId, types, typed, publicOnly, limit)
              .collectList()
              .map(
                  similar ->
                      new Resolution(
                          query,
                          null,
                          similar.isEmpty() && changed ? SearchQuery.join(corrected) : null,
                          similar));
        });
  }

  /** Each word swapped for the closest word that really occurs in the content (or kept). */
  private Mono<List<String>> correct(long siteId, List<String> tokens, boolean publicOnly) {
    return Flux.fromIterable(tokens)
        .concatMap(
            token ->
                token.length() < MIN_CORRECTABLE_LENGTH || SearchQuery.STOPWORDS.contains(token)
                    ? Mono.just(token)
                    : repository.closestWord(siteId, token, publicOnly).defaultIfEmpty(token))
        .collectList();
  }

  @Override
  public Mono<List<Suggestion>> suggest(
      long siteId, Collection<String> types, String query, boolean publicOnly) {
    List<String> tokens = SearchQuery.tokens(query);
    if (tokens.isEmpty()) {
      return Mono.just(List.of());
    }
    String last = tokens.get(tokens.size() - 1);
    String typed = SearchQuery.join(tokens);

    // The words before the one being typed are corrected too, so a suggestion never carries a
    // misspelling along: "digitl transf" offers "digital transformation", not "digitl
    // transformation".
    Mono<String> head =
        correct(siteId, tokens.subList(0, tokens.size() - 1), publicOnly).map(SearchQuery::join);
    Mono<List<Suggestion>> terms =
        head.flatMap(
            prefix ->
                repository
                    .suggestTerms(siteId, last, publicOnly, SUGGESTED_TERMS)
                    .map(
                        term ->
                            new Suggestion(
                                "term",
                                prefix.isEmpty() ? term.word() : prefix + " " + term.word(),
                                null,
                                null,
                                null))
                    .collectList());
    Mono<List<Suggestion>> titles =
        repository
            .suggestTitles(siteId, types, typed, publicOnly, SUGGESTED_TITLES)
            .map(hit -> new Suggestion("title", hit.title(), hit.type(), hit.slug(), hit.id()))
            .collectList();

    return Mono.zip(terms, titles)
        .map(
            both -> {
              List<Suggestion> all = new ArrayList<>(both.getT1());
              all.addAll(both.getT2());
              return all;
            });
  }
}
