package com.takypok.contentservice.search;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The SQL behind search. Everything here reads content_item's search columns and the search_term
 * view (see init-schema.sql); nothing writes. {@code publicOnly} restricts every query to what the
 * public site may show — PUBLISHED — which is the only thing the visitor-facing endpoints ever pass
 * as true; admin search passes false and sees drafts too.
 */
@Repository
@RequiredArgsConstructor
public class SearchRepository {
  /**
   * A matching content item. {@code title} is the readable one (not the accent-folded index text).
   */
  public record Hit(long id, String type, String status, String slug, String title, float rank) {}

  /** A word of the content's vocabulary and how many items use it. */
  public record Term(String word, long docCount) {}

  private static final String TITLE =
      "COALESCE(ci.data ->> 'title', ci.data ->> 'name', ci.data ->> 'navLabel',"
          + " ci.data ->> 'heroTitle', ci.search_title)";

  private static final String SELECT_HIT =
      "SELECT ci.id, ci.content_type, ci.status, ci.data ->> 'slug' AS slug, "
          + TITLE
          + " AS title, ";

  /** Site, optional type list and visibility — the scope every item query shares. */
  private static final String SCOPE =
      " ci.site_id = :site AND (:allTypes = true OR ci.content_type = ANY(:types))"
          + " AND (:publicOnly = false OR ci.status = 'PUBLISHED')";

  private final DatabaseClient databaseClient;

  /** Items matching every word, the last as a prefix, best first (title outranks body). */
  public Flux<Hit> fullText(
      long siteId, Collection<String> types, String tsQuery, boolean publicOnly, int limit) {
    return scoped(
            databaseClient.sql(
                SELECT_HIT
                    + "ts_rank_cd(CAST('{0.1,0.2,0.4,1.0}' AS float4[]), ci.search_vec, q.tq) AS rank"
                    + " FROM content_item ci, to_tsquery('english', :tsq) AS q(tq)"
                    + " WHERE"
                    + SCOPE
                    + " AND ci.search_vec @@ q.tq"
                    + " ORDER BY rank DESC, ci.display_order, ci.id LIMIT :limit"),
            siteId,
            types,
            publicOnly)
        .bind("tsq", tsQuery)
        .bind("limit", limit)
        .map((row, md) -> hit(row))
        .all();
  }

  /** Items whose title resembles the text — typos and half-typed words the index can't match. */
  public Flux<Hit> fuzzyTitle(
      long siteId, Collection<String> types, String text, boolean publicOnly, int limit) {
    return scoped(
            databaseClient.sql(
                SELECT_HIT
                    + "word_similarity(:text, ci.search_title) AS rank"
                    + " FROM content_item ci WHERE"
                    + SCOPE
                    + " AND word_similarity(:text, ci.search_title) >= 0.45"
                    + " ORDER BY rank DESC, ci.id LIMIT :limit"),
            siteId,
            types,
            publicOnly)
        .bind("text", text)
        .bind("limit", limit)
        .map((row, md) -> hit(row))
        .all();
  }

  /**
   * The vocabulary word closest to {@code token}: the word itself when it is in the vocabulary,
   * else the most similar one that is not a stopword. Empty when nothing is similar enough.
   */
  public Mono<String> closestWord(long siteId, String token, boolean publicOnly) {
    return databaseClient
        .sql(
            "SELECT word FROM (SELECT word, sum(doc_count) AS dc FROM search_term"
                + " WHERE site_id = :site AND (visible = true OR :publicOnly = false)"
                + " GROUP BY word) t"
                + " WHERE word = :token OR (word % :token AND NOT (word = ANY(:stop)))"
                + " ORDER BY (word = :token) DESC, similarity(word, :token) DESC, dc DESC, word"
                + " LIMIT 1")
        .bind("site", siteId)
        .bind("publicOnly", publicOnly)
        .bind("token", token)
        .bind("stop", SearchQuery.STOPWORDS.toArray(new String[0]))
        .map((row, md) -> row.get("word", String.class))
        .one();
  }

  /** Words to offer while {@code token} is being typed: completions first, then close spellings. */
  public Flux<Term> suggestTerms(long siteId, String token, boolean publicOnly, int limit) {
    return databaseClient
        .sql(
            "SELECT word, dc FROM (SELECT word, sum(doc_count) AS dc FROM search_term"
                + " WHERE site_id = :site AND (visible = true OR :publicOnly = false)"
                + " AND NOT (word = ANY(:stop)) GROUP BY word) t"
                + " WHERE word <> :token"
                + " AND (word LIKE :prefix ESCAPE '\\' OR word % :token)"
                + " ORDER BY (word LIKE :prefix ESCAPE '\\') DESC, similarity(word, :token) DESC,"
                + " dc DESC, word LIMIT :limit")
        .bind("site", siteId)
        .bind("publicOnly", publicOnly)
        .bind("token", token)
        .bind("prefix", SearchQuery.likeEscape(token) + "%")
        .bind("stop", SearchQuery.STOPWORDS.toArray(new String[0]))
        .bind("limit", limit)
        .map((row, md) -> new Term(row.get("word", String.class), row.get("dc", Long.class)))
        .all();
  }

  /** Items whose title contains the text, starting with it first, then close matches. */
  public Flux<Hit> suggestTitles(
      long siteId, Collection<String> types, String text, boolean publicOnly, int limit) {
    String escaped = SearchQuery.likeEscape(text);
    return scoped(
            databaseClient.sql(
                SELECT_HIT
                    + "word_similarity(:text, ci.search_title) AS rank"
                    + " FROM content_item ci WHERE"
                    + SCOPE
                    + " AND (ci.search_title ILIKE :contains ESCAPE '\\'"
                    + " OR word_similarity(:text, ci.search_title) >= 0.45)"
                    + " ORDER BY (ci.search_title ILIKE :starts ESCAPE '\\') DESC, rank DESC, ci.id"
                    + " LIMIT :limit"),
            siteId,
            types,
            publicOnly)
        .bind("text", text)
        .bind("contains", "%" + escaped + "%")
        .bind("starts", escaped + "%")
        .bind("limit", limit)
        .map((row, md) -> hit(row))
        .all();
  }

  private static DatabaseClient.GenericExecuteSpec scoped(
      DatabaseClient.GenericExecuteSpec spec,
      long siteId,
      Collection<String> types,
      boolean publicOnly) {
    return spec.bind("site", siteId)
        .bind("allTypes", types.isEmpty())
        // ANY(empty array) is false, so an empty list is handled by allTypes; bind a placeholder.
        .bind("types", types.isEmpty() ? new String[] {""} : types.toArray(new String[0]))
        .bind("publicOnly", publicOnly);
  }

  private static Hit hit(io.r2dbc.spi.Readable row) {
    Float rank = row.get("rank", Float.class);
    return new Hit(
        row.get("id", Long.class),
        row.get("content_type", String.class),
        row.get("status", String.class),
        row.get("slug", String.class),
        row.get("title", String.class),
        rank != null ? rank : 0f);
  }
}
