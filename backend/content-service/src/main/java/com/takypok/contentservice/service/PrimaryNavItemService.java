package com.takypok.contentservice.service;

import com.takypok.contentservice.model.entity.PrimaryNavItem;
import com.takypok.contentservice.model.request.PrimaryNavItemCreateRequest;
import com.takypok.contentservice.model.request.PrimaryNavItemUpdateRequest;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PrimaryNavItemService {
  Mono<List<PrimaryNavItem>> getAll(Long siteId);

  Mono<PrimaryNavItem> getById(Long id, Long siteId);

  Mono<PrimaryNavItem> create(Long siteId, PrimaryNavItemCreateRequest request);

  Mono<PrimaryNavItem> update(Long siteId, PrimaryNavItemUpdateRequest request);

  Mono<Void> delete(Long id, Long siteId);

  Flux<PrimaryNavItem> getPublished(Long siteId);
}
