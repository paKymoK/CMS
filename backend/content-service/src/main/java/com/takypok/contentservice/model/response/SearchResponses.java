package com.takypok.contentservice.model.response;

import com.takypok.contentservice.search.SearchRepository;
import com.takypok.contentservice.service.SearchService;
import java.util.List;

/** Response shapes of the search endpoints (public and admin). */
public final class SearchResponses {
  private SearchResponses() {}

  /** As-you-type suggestions: completed queries ("term") and matching items ("title"). */
  public record Suggest(List<SearchService.Suggestion> suggestions) {}

  /**
   * What a search for {@code query} amounted to: the spelling it was corrected to, or the spelling
   * it could have been, and how many items matched. The items themselves come from the listing
   * endpoints (which apply the same correction).
   */
  public record Resolve(String query, String correctedQuery, String didYouMean, int total) {
    public static Resolve from(SearchService.Resolution resolution) {
      return new Resolve(
          resolution.query(),
          resolution.correctedQuery(),
          resolution.didYouMean(),
          resolution.hits().size());
    }
  }

  /** The admin search result: the matching items, with the same correction fields. */
  public record Admin(
      String query, String correctedQuery, String didYouMean, List<SearchRepository.Hit> hits) {
    public static Admin from(SearchService.Resolution resolution) {
      return new Admin(
          resolution.query(),
          resolution.correctedQuery(),
          resolution.didYouMean(),
          resolution.hits());
    }
  }
}
