package com.takypok.contentservice.repository;

import com.takypok.contentservice.model.response.PageResponses;
import com.takypok.contentservice.search.SearchQuery;
import com.takypok.contentservice.util.PageTypes;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * The Pages list and Trash queries: one result set over the three page types, filtered and sorted
 * in the database.
 */
@Repository
@RequiredArgsConstructor
public class PageRepository {
  private static final String TYPES = "('service_page', 'industry_page', 'about_page')";

  /**
   * What the list filters on; every field is optional. {@code publishedFrom} and {@code
   * publishedTo} bound the date it last went live.
   */
  public record Filter(
      String q,
      String status,
      String type,
      String author,
      OffsetDateTime publishedFrom,
      OffsetDateTime publishedTo) {}

  public record Result(List<PageResponses.Row> rows, long total) {}

  private final DatabaseClient databaseClient;

  private static final String ROWS =
      "SELECT ci.id, ci.content_type AS type,"
          + " COALESCE(ci.data ->> 'navLabel', ci.data ->> 'heroTitle') AS title,"
          + " ci.data ->> 'slug' AS slug, ci.display_order,"
          + " NULLIF(ci.data ->> 'parentId', '')::bigint AS parent_id,"
          + " (SELECT COALESCE(p.data ->> 'navLabel', p.data ->> 'heroTitle') FROM content_item p"
          + " WHERE p.id = NULLIF(ci.data ->> 'parentId', '')::bigint) AS parent_title,"
          + " CASE ci.content_type WHEN 'service_page' THEN '/services/' || (ci.data ->> 'category') || '/' || (ci.data ->> 'slug')"
          + " WHEN 'industry_page' THEN '/industries/' || (ci.data ->> 'slug') ELSE '/about' END AS path,"
          + " ci.status, (ci.draft IS NOT NULL) AS has_draft, ci.publish_at, ci.created_by ->> 'sub' AS author_sub,"
          + " audit_actor_label(ci.created_by) AS author,"
          + " (SELECT max(a.occurred_at) FROM content_audit a WHERE a.item_id = ci.id AND a.action = 'PUBLISH') AS published_at,"
          + " COALESCE(ci.modified_at, ci.created_at) AS updated_at,"
          + " audit_actor_label(COALESCE(ci.modified_by, ci.created_by)) AS updated_by,"
          + " ci.version FROM content_item ci WHERE ci.site_id = :site AND ci.content_type IN "
          + TYPES;

  /** One page of rows. {@code size} 0 means all of them. */
  public Mono<Result> page(long siteId, Filter f, String sort, int page, int size) {
    List<String> conditions = new ArrayList<>();
    Map<String, Object> params = new LinkedHashMap<>();
    if (f.q() != null && !f.q().isBlank()) {
      conditions.add("(x.title ILIKE :like ESCAPE '\\' OR x.path ILIKE :like ESCAPE '\\')");
      params.put("like", "%" + SearchQuery.likeEscape(f.q().trim()) + "%");
    }
    if (f.status() != null && !f.status().isBlank()) {
      conditions.add("x.status = :status");
      params.put("status", f.status());
    }
    if (f.type() != null && !f.type().isBlank()) {
      conditions.add("x.type = :type");
      params.put("type", f.type());
    }
    if (f.author() != null && !f.author().isBlank()) {
      conditions.add("x.author_sub = :author");
      params.put("author", f.author());
    }
    if (f.publishedFrom() != null) {
      conditions.add("x.published_at >= :from");
      params.put("from", f.publishedFrom());
    }
    if (f.publishedTo() != null) {
      conditions.add("x.published_at < :to");
      params.put("to", f.publishedTo());
    }
    String where = conditions.isEmpty() ? "" : " WHERE " + String.join(" AND ", conditions);
    String from = " FROM (" + ROWS + ") x" + where;
    String order = " ORDER BY " + orderBy(sort) + ", x.id";
    String paging = size > 0 ? " LIMIT :limit OFFSET :offset" : "";

    DatabaseClient.GenericExecuteSpec rows =
        databaseClient.sql("SELECT x.*" + from + order + paging).bind("site", siteId);
    DatabaseClient.GenericExecuteSpec count =
        databaseClient.sql("SELECT COUNT(*) AS n" + from).bind("site", siteId);
    for (Map.Entry<String, Object> e : params.entrySet()) {
      rows = rows.bind(e.getKey(), e.getValue());
      count = count.bind(e.getKey(), e.getValue());
    }
    if (size > 0) {
      rows = rows.bind("limit", size).bind("offset", (long) (page - 1) * size);
    }
    Mono<List<PageResponses.Row>> items =
        rows.map(
                (row, md) ->
                    new PageResponses.Row(
                        row.get("id", Long.class),
                        row.get("type", String.class),
                        row.get("title", String.class),
                        row.get("slug", String.class),
                        row.get("path", String.class),
                        row.get("parent_id", Long.class),
                        row.get("parent_title", String.class),
                        row.get("status", String.class),
                        Boolean.TRUE.equals(row.get("has_draft", Boolean.class)),
                        instant(row.get("publish_at", OffsetDateTime.class)),
                        row.get("author", String.class),
                        instant(row.get("published_at", OffsetDateTime.class)),
                        instant(row.get("updated_at", OffsetDateTime.class)),
                        row.get("updated_by", String.class),
                        row.get("version", Integer.class)))
            .all()
            .collectList();
    return Mono.zip(items, count.map((row, md) -> row.get("n", Long.class)).one())
        .map(t -> new Result(t.getT1(), t.getT2()));
  }

  private static String orderBy(String sort) {
    return switch (sort == null ? "updated_desc" : sort) {
      case "title_asc" -> "lower(x.title) ASC";
      case "title_desc" -> "lower(x.title) DESC";
      case "updated_asc" -> "x.updated_at ASC NULLS FIRST";
      case "order_asc" -> "x.display_order ASC, lower(x.title) ASC";
      case "published_desc" -> "x.published_at DESC NULLS LAST";
      case "published_asc" -> "x.published_at ASC NULLS LAST";
      default -> "x.updated_at DESC NULLS LAST";
    };
  }

  private static java.time.Instant instant(OffsetDateTime t) {
    return t == null ? null : t.toInstant();
  }

  /** How many pages the site has in each status (the Trash is counted apart). */
  public Mono<Map<String, Long>> statusCounts(long siteId) {
    return databaseClient
        .sql(
            "SELECT status, COUNT(*) AS n FROM content_item WHERE site_id = :site AND content_type IN "
                + TYPES
                + " GROUP BY status")
        .bind("site", siteId)
        .map((row, md) -> Map.entry(row.get("status", String.class), row.get("n", Long.class)))
        .all()
        .collectMap(Map.Entry::getKey, Map.Entry::getValue);
  }

  public Mono<Long> trashCount(long siteId) {
    return databaseClient
        .sql(
            "SELECT COUNT(*) AS n FROM content_trash WHERE site_id = :site AND content_type IN "
                + TYPES)
        .bind("site", siteId)
        .map((row, md) -> row.get("n", Long.class))
        .one();
  }

  /** The people who created the site's pages, for the Author filter. */
  public Mono<List<PageResponses.Author>> authors(long siteId) {
    return databaseClient
        .sql(
            "SELECT DISTINCT created_by ->> 'sub' AS sub, audit_actor_label(created_by) AS name FROM content_item"
                + " WHERE site_id = :site AND content_type IN "
                + TYPES
                + " AND created_by ->> 'sub' IS NOT NULL ORDER BY 2")
        .bind("site", siteId)
        .map(
            (row, md) ->
                new PageResponses.Author(
                    row.get("sub", String.class), row.get("name", String.class)))
        .all()
        .collectList();
  }

  /**
   * The pages (any type) whose parent is one of the given pages: parent id, then the child's id,
   * title and status.
   */
  public Mono<List<ChildRow>> childrenOf(long siteId, List<Long> parentIds) {
    return databaseClient
        .sql(
            "SELECT NULLIF(ci.data ->> 'parentId', '')::bigint AS parent_id, ci.id, ci.status,"
                + " COALESCE(ci.data ->> 'navLabel', ci.data ->> 'heroTitle') AS title FROM content_item ci"
                + " WHERE ci.site_id = :site AND ci.content_type IN "
                + TYPES
                + " AND NULLIF(ci.data ->> 'parentId', '')::bigint = ANY (CAST(:ids AS bigint[]))"
                + " ORDER BY ci.display_order, ci.id")
        .bind("site", siteId)
        .bind("ids", parentIds.toArray(new Long[0]))
        .map(
            (row, md) ->
                new ChildRow(
                    row.get("parent_id", Long.class),
                    row.get("id", Long.class),
                    row.get("status", String.class),
                    row.get("title", String.class)))
        .all()
        .collectList();
  }

  public record ChildRow(Long parentId, Long id, String status, String title) {}

  /** One page of the Trash, newest first. */
  public Mono<PageResponses.TrashPage> trashPage(long siteId, String q, int page, int size) {
    boolean search = q != null && !q.isBlank();
    String where =
        " WHERE t.site_id = :site AND t.content_type IN "
            + TYPES
            + (search ? " AND t.title ILIKE :like ESCAPE '\\'" : "");
    DatabaseClient.GenericExecuteSpec rows =
        databaseClient
            .sql(
                "SELECT t.id, t.content_type AS type, t.title, t.snapshot -> 'data' ->> 'slug' AS slug,"
                    + " t.snapshot -> 'data' ->> 'category' AS category, t.snapshot ->> 'status' AS previous_status,"
                    + " t.trashed_at, audit_actor_label(t.trashed_by) AS trashed_by FROM content_trash t"
                    + where
                    + " ORDER BY t.trashed_at DESC, t.id DESC LIMIT :limit OFFSET :offset")
            .bind("site", siteId)
            .bind("limit", size)
            .bind("offset", (long) (page - 1) * size);
    DatabaseClient.GenericExecuteSpec count =
        databaseClient
            .sql("SELECT COUNT(*) AS n FROM content_trash t" + where)
            .bind("site", siteId);
    if (search) {
      String like = "%" + SearchQuery.likeEscape(q.trim()) + "%";
      rows = rows.bind("like", like);
      count = count.bind("like", like);
    }
    Mono<List<PageResponses.TrashRow>> items =
        rows.map(
                (row, md) -> {
                  String type = row.get("type", String.class);
                  return new PageResponses.TrashRow(
                      row.get("id", Long.class),
                      type,
                      row.get("title", String.class),
                      row.get("slug", String.class),
                      PageTypes.path(
                          type, row.get("category", String.class), row.get("slug", String.class)),
                      row.get("previous_status", String.class),
                      row.get("trashed_at", OffsetDateTime.class).toInstant(),
                      row.get("trashed_by", String.class));
                })
            .all()
            .collectList();
    return Mono.zip(items, count.map((row, md) -> row.get("n", Long.class)).one())
        .map(
            t ->
                new PageResponses.TrashPage(
                    t.getT1(),
                    page,
                    size,
                    t.getT2(),
                    (int) Math.max(1, Math.ceil(t.getT2() / (double) size))));
  }

  /**
   * Where the pages (by id) are linked from: other items whose text holds the page's public
   * address.
   */
  public Mono<Map<Long, List<com.takypok.contentservice.model.response.TrashResponses.Referrer>>>
      referrers(long siteId, List<Long> ids, int limit) {
    return databaseClient
        .sql(
            "WITH targets AS (SELECT ci.id, CASE ci.content_type WHEN 'service_page' THEN '/services/' || (ci.data ->> 'category') || '/' || (ci.data ->> 'slug')"
                + " WHEN 'industry_page' THEN '/industries/' || (ci.data ->> 'slug') ELSE '/about' END AS path"
                + " FROM content_item ci WHERE ci.site_id = :site AND ci.id = ANY (CAST(:ids AS bigint[]))),"
                + " docs AS (SELECT ci.id, ci.content_type, ci.status, COALESCE(ci.data ->> 'title', ci.data ->> 'navLabel',"
                + " ci.data ->> 'heroTitle', ci.data ->> 'name', ci.data ->> 'label') AS title, ci.data::text AS txt FROM content_item ci"
                + " WHERE ci.site_id = :site AND ci.id <> ALL (CAST(:ids AS bigint[])))"
                + " SELECT DISTINCT t.id AS target_id, d.id, d.content_type, d.status, d.title FROM docs d JOIN targets t"
                + " ON strpos(d.txt, t.path) > 0 AND d.txt ~ (regexp_replace(t.path, '([^a-zA-Z0-9])', '\\\\\\1', 'g') || '([^a-z0-9/-]|$)')"
                + " ORDER BY t.id, d.id LIMIT "
                + limit)
        .bind("site", siteId)
        .bind("ids", ids.toArray(new Long[0]))
        .map(
            (row, md) ->
                Map.entry(
                    row.get("target_id", Long.class),
                    new com.takypok.contentservice.model.response.TrashResponses.Referrer(
                        row.get("id", Long.class),
                        row.get("content_type", String.class),
                        row.get("status", String.class),
                        row.get("title", String.class))))
        .all()
        .collectList()
        .map(
            entries -> {
              Map<Long, List<com.takypok.contentservice.model.response.TrashResponses.Referrer>>
                  by = new LinkedHashMap<>();
              entries.forEach(
                  e -> by.computeIfAbsent(e.getKey(), k -> new ArrayList<>()).add(e.getValue()));
              return by;
            });
  }
}
