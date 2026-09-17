package com.takypok.contentservice.service;

import com.takypok.contentservice.model.entity.ServiceCard;
import com.takypok.contentservice.model.request.ServiceCardCreateRequest;
import com.takypok.contentservice.model.request.ServiceCardUpdateRequest;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ServiceCardService {
  Mono<List<ServiceCard>> getAll(Long siteId);

  Mono<ServiceCard> getById(Long id, Long siteId);

  Mono<ServiceCard> create(Long siteId, ServiceCardCreateRequest request);

  Mono<ServiceCard> update(Long siteId, ServiceCardUpdateRequest request);

  Mono<Void> delete(Long id, Long siteId);

  Flux<ServiceCard> getPublished(Long siteId);
}
