package com.takypok.contentservice.search;

import com.takypok.contentservice.model.entity.ContentItem;
import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Keeps the search text in step with {@code data}: runs just before any insert or update of a
 * {@link ContentItem} — whichever service saves it — and recomputes the three search columns, so
 * nothing that writes content can forget to. (Rows written by plain SQL, like the seed data, are
 * indexed at startup by {@link SearchIndexBackfill}.)
 */
@Component
public class ContentItemSearchCallback implements BeforeConvertCallback<ContentItem> {

  @Override
  public Publisher<ContentItem> onBeforeConvert(ContentItem item, SqlIdentifier table) {
    SearchTextExtractor.SearchText text = SearchTextExtractor.extract(item.getData());
    item.setSearchTitle(text.title());
    item.setSearchMeta(text.meta());
    item.setSearchBody(text.body());
    return Mono.just(item);
  }
}
