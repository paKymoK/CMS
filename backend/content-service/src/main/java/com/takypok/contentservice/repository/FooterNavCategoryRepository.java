package com.takypok.contentservice.repository;

import com.takypok.contentservice.model.entity.FooterNavCategory;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FooterNavCategoryRepository extends R2dbcRepository<FooterNavCategory, Long> {
  Flux<FooterNavCategory> findAllBySiteIdOrderByDisplayOrderAsc(Long siteId);

  Flux<FooterNavCategory> findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
      Long siteId, String status, Boolean active);

  Mono<FooterNavCategory> findByIdAndSiteId(Long id, Long siteId);
}
