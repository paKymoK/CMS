package com.takypok.contentservice.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * What a visitor typed, made safe and searchable: split into plain lowercase words (accents folded,
 * everything else dropped) and turned into a Postgres tsquery that matches every word and treats
 * the last one as a prefix — so "cloud migrat" finds "cloud migration" while it is still being
 * typed. Only [a-z0-9] ever reaches the database, so there is no tsquery syntax to escape.
 */
public final class SearchQuery {
  /** More than this many words is a paragraph, not a search; the rest are ignored. */
  public static final int MAX_TOKENS = 8;

  private static final int MAX_TOKEN_LENGTH = 40;
  private static final Pattern NON_ALNUM = Pattern.compile("[^a-z0-9]+");

  /** Words too common to correct towards or to offer as a suggestion. */
  public static final Set<String> STOPWORDS =
      Set.of(
          "the", "and", "for", "with", "from", "that", "this", "are", "was", "were", "has", "have",
          "had", "its", "our", "your", "their", "into", "onto", "over", "under", "about", "than",
          "then", "them", "they", "you", "can", "will", "not", "but", "all", "any", "how", "what",
          "when", "where", "who", "why");

  private SearchQuery() {}

  /** The words of a query, lowercase and accent-free, in order. */
  public static List<String> tokens(String query) {
    List<String> tokens = new ArrayList<>();
    if (query == null || query.isBlank()) {
      return tokens;
    }
    String folded = SearchTextExtractor.fold(query).toLowerCase(Locale.ROOT);
    for (String word : NON_ALNUM.split(folded)) {
      if (!word.isEmpty() && tokens.size() < MAX_TOKENS) {
        tokens.add(word.length() > MAX_TOKEN_LENGTH ? word.substring(0, MAX_TOKEN_LENGTH) : word);
      }
    }
    return tokens;
  }

  /** {@code "cloud & migrat:*"} — every word, the last as a prefix. Empty for no words. */
  public static String toTsQuery(List<String> tokens) {
    if (tokens.isEmpty()) {
      return "";
    }
    StringBuilder query = new StringBuilder();
    for (int i = 0; i < tokens.size(); i++) {
      if (i > 0) {
        query.append(" & ");
      }
      query.append(tokens.get(i));
      if (i == tokens.size() - 1) {
        query.append(":*");
      }
    }
    return query.toString();
  }

  /** A query back as one string, for showing and for a corrected re-search. */
  public static String join(List<String> tokens) {
    return String.join(" ", tokens);
  }

  /** Escapes LIKE wildcards so a typed % or _ is matched literally. */
  public static String likeEscape(String text) {
    return text.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
  }
}
