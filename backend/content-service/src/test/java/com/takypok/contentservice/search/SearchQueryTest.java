package com.takypok.contentservice.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class SearchQueryTest {
  @Test
  void tokensAreLowercaseAccentFreeWords() {
    assertEquals(List.of("ha", "noi", "ai", "2026"), SearchQuery.tokens("  Hà Nội, AI & 2026!! "));
  }

  @Test
  void nothingSearchableGivesNoTokens() {
    assertTrue(SearchQuery.tokens(null).isEmpty());
    assertTrue(SearchQuery.tokens("   ").isEmpty());
    assertTrue(SearchQuery.tokens("!!! ???").isEmpty());
  }

  @Test
  void aLongQueryIsCutAtTheWordLimit() {
    assertEquals(SearchQuery.MAX_TOKENS, SearchQuery.tokens("a b c d e f g h i j k l").size());
  }

  @Test
  void theTsQueryRequiresEveryWordAndTreatsTheLastAsAPrefix() {
    assertEquals("cloud & migrat:*", SearchQuery.toTsQuery(List.of("cloud", "migrat")));
    assertEquals("kubernetes:*", SearchQuery.toTsQuery(List.of("kubernetes")));
    assertEquals("", SearchQuery.toTsQuery(List.of()));
  }

  @Test
  void tsQuerySyntaxTypedByTheVisitorNeverSurvives() {
    // Operators and quotes are separators, so they can't reach to_tsquery.
    assertEquals(
        List.of("cloud", "drop", "table", "x"),
        SearchQuery.tokens("cloud' | !drop <-> table:* & (x)"));
    assertEquals(
        "cloud & drop & table & x:*",
        SearchQuery.toTsQuery(SearchQuery.tokens("cloud' | !drop <-> table:* & (x)")));
  }

  @Test
  void likeWildcardsAreEscaped() {
    assertEquals("100\\% a\\_b \\\\", SearchQuery.likeEscape("100% a_b \\"));
  }
}
