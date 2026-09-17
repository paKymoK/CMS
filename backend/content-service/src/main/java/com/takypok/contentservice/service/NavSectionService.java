package com.takypok.contentservice.service;

import com.takypok.contentservice.model.entity.NavSection;
import com.takypok.contentservice.model.request.NavSectionCreateRequest;
import com.takypok.contentservice.model.request.NavSectionUpdateRequest;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NavSectionService {
  Mono<List<NavSection>> getAll(Long siteId);

  Mono<NavSection> getById(Long id, Long siteId);

  Mono<NavSection> create(Long siteId, NavSectionCreateRequest request);

  Mono<NavSection> update(Long siteId, NavSectionUpdateRequest request);

  Mono<Void> delete(Long id, Long siteId);

  Flux<NavSection> getPublished(Long siteId);
}
