package com.takypok.contentservice.service;

import com.takypok.contentservice.model.entity.Office;
import com.takypok.contentservice.model.request.OfficeCreateRequest;
import com.takypok.contentservice.model.request.OfficeUpdateRequest;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OfficeService {
  Mono<List<Office>> getAll(Long siteId);

  Mono<Office> getById(Long id, Long siteId);

  Mono<Office> create(Long siteId, OfficeCreateRequest request);

  Mono<Office> update(Long siteId, OfficeUpdateRequest request);

  Mono<Void> delete(Long id, Long siteId);

  Flux<Office> getPublished(Long siteId);
}
