package com.takypok.contentservice.service;

import com.takypok.contentservice.model.entity.LogoBadge;
import com.takypok.contentservice.model.request.LogoBadgeCreateRequest;
import com.takypok.contentservice.model.request.LogoBadgeUpdateRequest;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LogoBadgeService {
  Mono<List<LogoBadge>> getAll(Long siteId, String type);

  Mono<LogoBadge> getById(Long id, Long siteId);

  Mono<LogoBadge> create(Long siteId, LogoBadgeCreateRequest request);

  Mono<LogoBadge> update(Long siteId, LogoBadgeUpdateRequest request);

  Mono<Void> delete(Long id, Long siteId);

  Flux<LogoBadge> getPublished(Long siteId, String type);
}
