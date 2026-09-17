package com.takypok.contentservice.service;

import com.takypok.contentservice.model.entity.Stat;
import com.takypok.contentservice.model.request.StatCreateRequest;
import com.takypok.contentservice.model.request.StatUpdateRequest;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StatService {
  Mono<List<Stat>> getAll(Long siteId);

  Mono<Stat> getById(Long id, Long siteId);

  Mono<Stat> create(Long siteId, StatCreateRequest request);

  Mono<Stat> update(Long siteId, StatUpdateRequest request);

  Mono<Void> delete(Long id, Long siteId);

  Flux<Stat> getPublished(Long siteId);
}
