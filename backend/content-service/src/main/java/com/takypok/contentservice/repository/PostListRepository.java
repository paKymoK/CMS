package com.takypok.contentservice.repository;

import com.takypok.contentservice.search.SearchQuery;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * The reads behind the dedicated post list. Everything is scoped to one site and to posts, filtered
 * and sorted in the database (a site can have many posts, so the browser never gets them all), and
 * built only from fixed SQL pieces: what a person types is always a bound parameter, and the sort
 * is picked from a fixed set, never pasted into the query.
 *
 * <p>"Updated" is the row's own modified time (falling back to when it was created), which is what
 * the audit trail and the list's default order mean by it. Month and year are in UTC.
 */
@Repository
@RequiredArgsConstructor
public class PostListRepository {

  /** The orders the list offers. */
  public enum Sort {
    UPDATED_DESC("COALESCE(ci.modified_at, ci.created_at) DESC NULLS LAST, ci.id DESC"),
    UPDATED_ASC("COALESCE(ci.modified_at, ci.created_at) ASC NULLS LAST, ci.id ASC"),
    TITLE_ASC("LOWER(COALESCE(ci.data ->> 'title', '')) ASC, ci.id ASC"),
    TITLE_DESC("LOWER(COALESCE(ci.data ->> 'title', '')) DESC, ci.id DESC");

    private final String sql;

    Sort(String sql) {
      this.sql = sql;
    }
  }

  /**
   * Any field may be null (no condition). {@code year} alone filters the year; with {@code month}
   * one month.
   */
  public record Filter(
      String q, String status, Long categoryId, Long tagId, Integer year, Integer month) {}

  /** One row as read; tags arrive as the JSON text of the array. */
  public record Row(
      long id,
      String title,
      String slug,
      String category,
      String tagsJson,
      String authorName,
      String status,
      OffsetDateTime publishAt,
      OffsetDateTime updatedAt,
      String updatedBy,
      Integer version) {}

  public record Page(List<Row> rows, long total) {}

  public record CategoryCount(String name, long count) {}

  private static final String UPDATED = "COALESCE(ci.modified_at, ci.created_at)";

  private static final String SELECT =
      "SELECT ci.id, ci.data ->> 'title' AS title, ci.data ->> 'slug' AS slug,"
          + " ci.data ->> 'category' AS category, (ci.data -> 'tags')::text AS tags,"
          + " ci.data ->> 'authorName' AS author_name, ci.status, ci.publish_at,"
          + " "
          + UPDATED
          + " AS updated_at, audit_actor_label(ci.modified_by) AS updated_by, ci.version"
          + " FROM content_item ci";

  private final DatabaseClient databaseClient;

  public Mono<Page> page(long siteId, Filter filter, Sort sort, int page, int size) {
    List<String> conditions = new ArrayList<>();
    Map<String, Object> params = new LinkedHashMap<>();
    scope(siteId, filter, conditions, params);
    String where = " WHERE " + String.join(" AND ", conditions);

    DatabaseClient.GenericExecuteSpec count =
        bindAll(databaseClient.sql("SELECT COUNT(*) FROM content_item ci" + where), params);
    DatabaseClient.GenericExecuteSpec rows =
        bindAll(
                databaseClient.sql(
                    SELECT + where + " ORDER BY " + sort.sql + " LIMIT :limit OFFSET :offset"),
                params)
            .bind("limit", size)
            .bind("offset", (long) (page - 1) * size);

    return count
        .map((row, md) -> row.get(0, Long.class))
        .one()
        .flatMap(
            total ->
                rows.map(
                        (row, md) ->
                            new Row(
                                row.get("id", Long.class),
                                row.get("title", String.class),
                                row.get("slug", String.class),
                                row.get("category", String.class),
                                row.get("tags", String.class),
                                row.get("author_name", String.class),
                                row.get("status", String.class),
                                row.get("publish_at", OffsetDateTime.class),
                                row.get("updated_at", OffsetDateTime.class),
                                row.get("updated_by", String.class),
                                row.get("version", Integer.class)))
                    .all()
                    .collectList()
                    .map(list -> new Page(list, total)));
  }

  /** How many posts the site has in each status. */
  public Mono<Map<String, Long>> statusCounts(long siteId) {
    return databaseClient
        .sql(
            "SELECT status, COUNT(*) AS n FROM content_item WHERE site_id = :site"
                + " AND content_type = 'post' GROUP BY status")
        .bind("site", siteId)
        .map((row, md) -> Map.entry(row.get("status", String.class), row.get("n", Long.class)))
        .all()
        .collectMap(Map.Entry::getKey, Map.Entry::getValue);
  }

  /** The categories in use on the site's posts, with how many posts each has. */
  public Mono<List<CategoryCount>> categories(long siteId) {
    return databaseClient
        .sql(
            "SELECT data ->> 'category' AS name, COUNT(*) AS n FROM content_item"
                + " WHERE site_id = :site AND content_type = 'post'"
                + " AND COALESCE(data ->> 'category', '') <> '' GROUP BY 1 ORDER BY 1")
        .bind("site", siteId)
        .map(
            (row, md) -> new CategoryCount(row.get("name", String.class), row.get("n", Long.class)))
        .all()
        .collectList();
  }

  /** The years in which posts were last updated, newest first. */
  public Mono<List<Integer>> years(long siteId) {
    return databaseClient
        .sql(
            "SELECT DISTINCT EXTRACT(YEAR FROM (COALESCE(modified_at, created_at)"
                + " AT TIME ZONE 'UTC'))::int AS y FROM content_item"
                + " WHERE site_id = :site AND content_type = 'post'"
                + " AND COALESCE(modified_at, created_at) IS NOT NULL ORDER BY y DESC")
        .bind("site", siteId)
        .map((row, md) -> row.get("y", Integer.class))
        .all()
        .collectList();
  }

  // ── building the condition list ──

  private static void scope(
      long siteId, Filter f, List<String> conditions, Map<String, Object> params) {
    conditions.add("ci.site_id = :site");
    params.put("site", siteId);
    conditions.add("ci.content_type = 'post'");
    if (f.q() != null && !f.q().isBlank()) {
      // Part of a word, any case. search_title is the title with accents folded (and has a trigram
      // index), so "viet" also finds "Việt"; the slug is matched as typed.
      conditions.add(
          "(ci.search_title ILIKE :like ESCAPE '\\' OR ci.data ->> 'slug' ILIKE :like ESCAPE '\\')");
      params.put("like", "%" + SearchQuery.likeEscape(f.q().trim()) + "%");
    }
    if (f.status() != null && !f.status().isBlank()) {
      conditions.add("ci.status = :status");
      params.put("status", f.status());
    }
    if (f.categoryId() != null) {
      conditions.add("ci.data -> 'categoryIds' @> to_jsonb(CAST(:categoryId AS bigint))");
      params.put("categoryId", f.categoryId());
    }
    if (f.tagId() != null) {
      conditions.add("ci.data -> 'tagIds' @> to_jsonb(CAST(:tagId AS bigint))");
      params.put("tagId", f.tagId());
    }
    if (f.year() != null) {
      OffsetDateTime from =
          OffsetDateTime.of(
              f.year(), f.month() == null ? 1 : f.month(), 1, 0, 0, 0, 0, ZoneOffset.UTC);
      OffsetDateTime to = f.month() == null ? from.plusYears(1) : from.plusMonths(1);
      conditions.add(UPDATED + " >= :from AND " + UPDATED + " < :to");
      params.put("from", from);
      params.put("to", to);
    }
  }

  private static DatabaseClient.GenericExecuteSpec bindAll(
      DatabaseClient.GenericExecuteSpec spec, Map<String, Object> params) {
    DatabaseClient.GenericExecuteSpec bound = spec;
    for (Map.Entry<String, Object> e : params.entrySet()) {
      bound = bound.bind(e.getKey(), e.getValue());
    }
    return bound;
  }
}
