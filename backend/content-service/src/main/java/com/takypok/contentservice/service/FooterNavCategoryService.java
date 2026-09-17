package com.takypok.contentservice.service;

import com.takypok.contentservice.model.entity.FooterNavCategory;
import com.takypok.contentservice.model.request.FooterNavCategoryCreateRequest;
import com.takypok.contentservice.model.request.FooterNavCategoryUpdateRequest;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FooterNavCategoryService {
  Mono<List<FooterNavCategory>> getAll(Long siteId);

  Mono<FooterNavCategory> getById(Long id, Long siteId);

  Mono<FooterNavCategory> create(Long siteId, FooterNavCategoryCreateRequest request);

  Mono<FooterNavCategory> update(Long siteId, FooterNavCategoryUpdateRequest request);

  Mono<Void> delete(Long id, Long siteId);

  Flux<FooterNavCategory> getPublished(Long siteId);
}
