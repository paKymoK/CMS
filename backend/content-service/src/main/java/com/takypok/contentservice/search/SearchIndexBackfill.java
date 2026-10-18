package com.takypok.contentservice.search;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * Indexes the rows that reached content_item without going through the application — the seed data
 * in init-data.sql, or anything inserted by hand — by filling their search columns at startup. A
 * row counts as not yet indexed when all three are still null; rows the application writes always
 * have them set (an item with no text gets empty strings, not null), so a normal start finds
 * nothing to do.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SearchIndexBackfill implements ApplicationRunner {
  private final DatabaseClient databaseClient;
  private final ObjectMapper mapper;

  @Override
  public void run(ApplicationArguments args) {
    Long indexed =
        databaseClient
            .sql(
                "SELECT id, data FROM content_item"
                    + " WHERE search_title IS NULL AND search_meta IS NULL AND search_body IS NULL")
            .map((row, metadata) -> new Row(row.get("id", Long.class), row.get("data", Json.class)))
            .all()
            .concatMap(this::index)
            .count()
            .block();
    if (indexed != null && indexed > 0) {
      log.info("Indexed {} content item(s) for search", indexed);
    }
  }

  private Flux<Long> index(Row row) {
    JsonNode data;
    try {
      data = mapper.readTree(row.data().asString());
    } catch (Exception e) {
      log.warn("Skipping content item {}: unreadable data ({})", row.id(), e.getMessage());
      return Flux.empty();
    }
    SearchTextExtractor.SearchText text = SearchTextExtractor.extract(data);
    return databaseClient
        .sql(
            "UPDATE content_item SET search_title = :title, search_meta = :meta,"
                + " search_body = :body WHERE id = :id")
        .bind("title", text.title())
        .bind("meta", text.meta())
        .bind("body", text.body())
        .bind("id", row.id())
        .fetch()
        .rowsUpdated()
        .flux()
        .map(n -> row.id());
  }

  private record Row(Long id, Json data) {}
}
