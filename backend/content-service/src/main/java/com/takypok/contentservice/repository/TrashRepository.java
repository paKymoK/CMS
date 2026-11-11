package com.takypok.contentservice.repository;

import com.takypok.contentservice.model.response.TrashResponses;
import com.takypok.contentservice.search.SearchQuery;
import com.takypok.core.model.authentication.User;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The Trash (content_trash and the SQL functions in init-schema.sql). Moving an item in and out is
 * done by those functions, each in one statement, so it is atomic and credited to the user the
 * audit trail should name; this class only calls them and reads the Trash. SQL is fixed text;
 * everything variable is a bound parameter.
 */
@Repository
@RequiredArgsConstructor
public class TrashRepository {
  /** How long an item stays in the Trash; the same 30 as purge_trashed_content() in the schema. */
  public static final int RETENTION_DAYS = 30;

  /** Most referrers listed per post in a link check. */
  private static final int MAX_REFERRERS_LISTED = 5;

  private static final int MAX_REFERRER_ROWS = 500;
  private static final int MAX_SUGGESTION = 20;

  private final DatabaseClient databaseClient;

  /** What the restore function reports. */
  public record Restored(long id, String slug, int skippedAliases) {}

  /** A row of the Trash, as much as the checks need. */
  public record Trashed(long id, String contentType, String title, String slug) {}

  /** Moves the item to the Trash. False when the site has no such item. */
  public Mono<Boolean> trash(long siteId, long id, User actor) {
    return databaseClient
        .sql("SELECT trash_content_item(:id, :site, CAST(:actor AS jsonb)) AS ok")
        .bind("id", id)
        .bind("site", siteId)
        .bind("actor", actorJson(actor))
        .map((row, md) -> Boolean.TRUE.equals(row.get("ok", Boolean.class)))
        .one();
  }

  /**
   * Brings the item back as a draft, under {@code slug} when given. Empty when it is not in the
   * site's Trash. Fails with an error whose message contains "Slug already in use" when the address
   * is taken.
   */
  public Mono<Restored> restore(long siteId, long id, User actor, String slug) {
    return databaseClient
        .sql(
            "WITH x AS MATERIALIZED (SELECT restore_trashed_item(:id, :site,"
                + " CAST(:actor AS jsonb), NULLIF(CAST(:slug AS text), '')) AS r)"
                + " SELECT r ->> 'slug' AS slug, (r ->> 'skippedAliases')::int AS skipped"
                + " FROM x WHERE r IS NOT NULL")
        .bind("id", id)
        .bind("site", siteId)
        .bind("actor", actorJson(actor))
        .bind("slug", slug == null ? "" : slug)
        .map(
            (row, md) ->
                new Restored(id, row.get("slug", String.class), row.get("skipped", Integer.class)))
        .one();
  }

  /** Deletes items from the Trash for good; returns how many were deleted. */
  public Mono<Integer> deleteForever(long siteId, List<Long> ids, User actor) {
    return databaseClient
        .sql(
            "SELECT delete_trashed_items(CAST(:ids AS bigint[]), :site, CAST(:actor AS jsonb)) AS n")
        .bind("ids", ids.toArray(new Long[0]))
        .bind("site", siteId)
        .bind("actor", actorJson(actor))
        .map((row, md) -> row.get("n", Integer.class))
        .one();
  }

  /** One trashed item of the site, or empty. */
  public Mono<Trashed> find(long siteId, long id) {
    return databaseClient
        .sql(
            "SELECT id, content_type, title, snapshot -> 'data' ->> 'slug' AS slug"
                + " FROM content_trash WHERE site_id = :site AND id = :id")
        .bind("site", siteId)
        .bind("id", id)
        .map(
            (row, md) ->
                new Trashed(
                    row.get("id", Long.class),
                    row.get("content_type", String.class),
                    row.get("title", String.class),
                    row.get("slug", String.class)))
        .one();
  }

  public Mono<Long> count(long siteId, String contentType) {
    return databaseClient
        .sql(
            "SELECT COUNT(*) AS n FROM content_trash WHERE site_id = :site"
                + " AND content_type = :type")
        .bind("site", siteId)
        .bind("type", contentType)
        .map((row, md) -> row.get("n", Long.class))
        .one();
  }

  /** One page of the site's trashed items of a type, newest first; {@code q} is part of a title. */
  public Mono<TrashResponses.Page> page(
      long siteId, String contentType, String q, int page, int size) {
    boolean search = q != null && !q.isBlank();
    String where =
        " WHERE t.site_id = :site AND t.content_type = :type"
            + (search
                ? " AND (t.snapshot ->> 'search_title' ILIKE :like ESCAPE '\\'"
                    + " OR t.snapshot -> 'data' ->> 'slug' ILIKE :like ESCAPE '\\')"
                : "");
    DatabaseClient.GenericExecuteSpec rowsQuery =
        databaseClient
            .sql(
                "SELECT t.id, t.title, t.snapshot -> 'data' ->> 'slug' AS slug,"
                    + " t.snapshot -> 'data' ->> 'category' AS category,"
                    + " t.snapshot -> 'data' ->> 'authorName' AS author_name,"
                    + " t.snapshot ->> 'status' AS previous_status, t.trashed_at,"
                    + " audit_actor_label(t.trashed_by) AS trashed_by,"
                    + " GREATEST(0, CEIL(EXTRACT(EPOCH FROM (t.trashed_at"
                    + " + make_interval(days => :days) - now())) / 86400))::int AS days_left"
                    + " FROM content_trash t"
                    + where
                    + " ORDER BY t.trashed_at DESC, t.id DESC LIMIT :limit OFFSET :offset")
            .bind("site", siteId)
            .bind("type", contentType)
            .bind("days", RETENTION_DAYS)
            .bind("limit", size)
            .bind("offset", (long) (page - 1) * size);
    DatabaseClient.GenericExecuteSpec countQuery =
        databaseClient
            .sql("SELECT COUNT(*) AS n FROM content_trash t" + where)
            .bind("site", siteId)
            .bind("type", contentType);
    DatabaseClient.GenericExecuteSpec rows = rowsQuery;
    DatabaseClient.GenericExecuteSpec count = countQuery;
    if (search) {
      String like = "%" + SearchQuery.likeEscape(q.trim()) + "%";
      rows = rowsQuery.bind("like", like);
      count = countQuery.bind("like", like);
    }
    DatabaseClient.GenericExecuteSpec finalRows = rows;
    return count
        .map((row, md) -> row.get("n", Long.class))
        .one()
        .flatMap(
            total ->
                finalRows
                    .map(
                        (row, md) ->
                            new TrashResponses.Row(
                                row.get("id", Long.class),
                                row.get("title", String.class),
                                row.get("slug", String.class),
                                row.get("category", String.class),
                                row.get("author_name", String.class),
                                row.get("previous_status", String.class),
                                row.get("trashed_at", OffsetDateTime.class).toInstant(),
                                row.get("trashed_by", String.class),
                                row.get("days_left", Integer.class)))
                    .all()
                    .collectList()
                    .map(
                        list ->
                            new TrashResponses.Page(
                                list, page, size, total, (int) ((total + size - 1) / size))));
  }

  /**
   * What links to each of these items (of the site, any type): other items whose text holds
   * "/insights/<one of its addresses, current or old>". It reads every item's JSON text, so it is
   * meant for the moment someone asks to trash, not for rendering a list. Items being trashed
   * together do not count as referrers of each other.
   */
  public Flux<TrashResponses.LinkCheck> linksTo(long siteId, List<Long> ids) {
    Long[] idArray = ids.toArray(new Long[0]);
    return databaseClient
        .sql(
            "WITH targets AS (SELECT s.item_id AS id, s.slug FROM content_slug s"
                + " WHERE s.site_id = :site AND s.content_type = 'post'"
                + " AND s.item_id = ANY (CAST(:ids AS bigint[]))),"
                + " docs AS (SELECT ci.id, ci.content_type, ci.status,"
                + " ci.data ->> 'title' AS title, ci.data::text AS txt FROM content_item ci"
                + " WHERE ci.site_id = :site AND ci.id <> ALL (CAST(:ids AS bigint[])))"
                + " SELECT DISTINCT t.id AS target_id, d.id, d.content_type, d.status, d.title"
                + " FROM docs d JOIN targets t ON d.txt ~ ('/insights/'"
                + " || regexp_replace(t.slug, '([^a-zA-Z0-9])', '\\\\\\1', 'g')"
                + " || '([^a-z0-9-]|$)')"
                + " WHERE strpos(d.txt, '/insights/') > 0 ORDER BY t.id, d.id LIMIT "
                + MAX_REFERRER_ROWS)
        .bind("site", siteId)
        .bind("ids", idArray)
        .map(
            (row, md) ->
                Map.entry(
                    row.get("target_id", Long.class),
                    new TrashResponses.Referrer(
                        row.get("id", Long.class),
                        row.get("content_type", String.class),
                        row.get("status", String.class),
                        row.get("title", String.class))))
        .all()
        .collectList()
        .flatMapMany(
            entries -> {
              Map<Long, List<TrashResponses.Referrer>> byTarget = new LinkedHashMap<>();
              entries.forEach(
                  e ->
                      byTarget
                          .computeIfAbsent(e.getKey(), k -> new ArrayList<>())
                          .add(e.getValue()));
              return Flux.fromIterable(byTarget.entrySet())
                  .map(
                      e ->
                          new TrashResponses.LinkCheck(
                              e.getKey(),
                              null,
                              e.getValue().size(),
                              e.getValue().stream().limit(MAX_REFERRERS_LISTED).toList()));
            });
  }

  /** Who holds this address on the site (as a current or an old address of a post), or empty. */
  public Mono<TrashResponses.SlugHolder> slugHolder(long siteId, String slug) {
    return databaseClient
        .sql(
            "SELECT s.item_id, ci.data ->> 'title' AS title FROM content_slug s"
                + " JOIN content_item ci ON ci.id = s.item_id WHERE s.site_id = :site"
                + " AND s.content_type = 'post' AND s.scope = '' AND s.slug = :slug")
        .bind("site", siteId)
        .bind("slug", slug)
        .map(
            (row, md) ->
                new TrashResponses.SlugHolder(
                    row.get("item_id", Long.class), row.get("title", String.class)))
        .one();
  }

  /** The first of slug-2, slug-3, … that no post of the site holds. */
  public Mono<String> freeSlugLike(long siteId, String slug) {
    List<String> candidates = new ArrayList<>();
    for (int i = 2; i < 2 + MAX_SUGGESTION; i++) {
      candidates.add(slug + "-" + i);
    }
    return databaseClient
        .sql(
            "SELECT slug FROM content_slug WHERE site_id = :site AND content_type = 'post'"
                + " AND scope = '' AND slug = ANY (CAST(:candidates AS text[]))")
        .bind("site", siteId)
        .bind("candidates", candidates.toArray(new String[0]))
        .map((row, md) -> row.get("slug", String.class))
        .all()
        .collectList()
        .map(taken -> candidates.stream().filter(c -> !taken.contains(c)).findFirst().orElse(null));
  }

  private static String actorJson(User actor) {
    return "{\"sub\":" + quote(actor.getSub()) + ",\"name\":" + quote(actor.getName()) + "}";
  }

  private static String quote(String value) {
    if (value == null) return "null";
    return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
  }
}
