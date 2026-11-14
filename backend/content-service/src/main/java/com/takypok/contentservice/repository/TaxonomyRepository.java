package com.takypok.contentservice.repository;

import com.takypok.contentservice.model.TaxonomyKind;
import com.takypok.contentservice.model.response.TaxonomyResponses;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The category and tag queries that are about posts or the Trash, which the generic item repository
 * cannot express.
 */
@Repository
@RequiredArgsConstructor
public class TaxonomyRepository {
  private final DatabaseClient databaseClient;

  /** How many published posts carry each category (or tag), by id. */
  public Mono<Map<Long, Long>> publishedCounts(long siteId, TaxonomyKind kind) {
    return databaseClient
        .sql(
            "SELECT e.v::bigint AS id, COUNT(*) AS n FROM content_item p,"
                + " jsonb_array_elements_text(coalesce(p.data -> '"
                + kind.postField()
                + "', '[]'::jsonb)) AS e (v)"
                + " WHERE p.site_id = :site AND p.content_type = 'post' AND p.status = 'PUBLISHED'"
                + " GROUP BY 1")
        .bind("site", siteId)
        .map((row, md) -> Map.entry(row.get("id", Long.class), row.get("n", Long.class)))
        .all()
        .collectMap(Map.Entry::getKey, Map.Entry::getValue);
  }

  /** The ids of every post (any status) that carries the category or tag. */
  public Flux<Long> postIdsWith(long siteId, TaxonomyKind kind, long id) {
    return databaseClient
        .sql(
            "SELECT p.id FROM content_item p WHERE p.site_id = :site AND p.content_type = 'post'"
                + " AND p.data -> '"
                + kind.postField()
                + "' @> to_jsonb(CAST(:id AS bigint)) ORDER BY p.id")
        .bind("site", siteId)
        .bind("id", id)
        .map((row, md) -> row.get("id", Long.class))
        .all();
  }

  /** One page of the Trash for a kind, newest first. */
  public Mono<TaxonomyResponses.TrashPage> trashPage(
      long siteId, TaxonomyKind kind, String q, int page, int size) {
    String like =
        q == null || q.isBlank()
            ? null
            : "%" + q.trim().replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
    String where =
        " WHERE site_id = :site AND content_type = :type"
            + (like == null ? "" : " AND title ILIKE :like ESCAPE '\\'");
    DatabaseClient.GenericExecuteSpec rows =
        databaseClient
            .sql(
                "SELECT id, title, snapshot -> 'data' ->> 'slug' AS slug, trashed_at,"
                    + " audit_actor_label(trashed_by) AS trashed_by,"
                    + " jsonb_array_length(coalesce(snapshot -> 'data' -> 'trashedPostIds', '[]'::jsonb)) AS posts"
                    + " FROM content_trash"
                    + where
                    + " ORDER BY trashed_at DESC, id DESC LIMIT :limit OFFSET :offset")
            .bind("site", siteId)
            .bind("type", kind.type())
            .bind("limit", size)
            .bind("offset", (long) (page - 1) * size);
    DatabaseClient.GenericExecuteSpec count =
        databaseClient
            .sql("SELECT COUNT(*) AS n FROM content_trash" + where)
            .bind("site", siteId)
            .bind("type", kind.type());
    if (like != null) {
      rows = rows.bind("like", like);
      count = count.bind("like", like);
    }
    Mono<List<TaxonomyResponses.TrashRow>> items =
        rows.map(
                (row, md) ->
                    new TaxonomyResponses.TrashRow(
                        row.get("id", Long.class),
                        row.get("title", String.class),
                        row.get("slug", String.class),
                        row.get("trashed_at", OffsetDateTime.class).toInstant(),
                        row.get("trashed_by", String.class),
                        row.get("posts", Integer.class)))
            .all()
            .collectList();
    Mono<Long> total = count.map((row, md) -> row.get("n", Long.class)).one();
    return Mono.zip(items, total)
        .map(
            t ->
                new TaxonomyResponses.TrashPage(
                    t.getT1(),
                    page,
                    size,
                    t.getT2(),
                    (int) Math.max(1, Math.ceil(t.getT2() / (double) size))));
  }
}
