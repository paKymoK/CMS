package com.takypok.contentservice.service;

import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.model.request.ContentItemCreateRequest;
import com.takypok.contentservice.model.request.ContentItemUpdateRequest;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ContentItemService {
  Mono<List<ContentItem>> getAll(Long siteId, String contentType);

  Mono<ContentItem> getById(Long id, Long siteId);

  Mono<ContentItem> create(Long siteId, ContentItemCreateRequest request);

  Mono<ContentItem> update(Long siteId, ContentItemUpdateRequest request);

  Mono<Void> delete(Long id, Long siteId);

  Flux<ContentItem> getPublished(Long siteId, String contentType);
}
