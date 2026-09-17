package com.takypok.contentservice.repository;

import com.takypok.contentservice.model.entity.PrimaryNavItem;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PrimaryNavItemRepository extends R2dbcRepository<PrimaryNavItem, Long> {
  Flux<PrimaryNavItem> findAllBySiteIdOrderByDisplayOrderAsc(Long siteId);

  Flux<PrimaryNavItem> findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
      Long siteId, String status, Boolean active);

  Mono<PrimaryNavItem> findByIdAndSiteId(Long id, Long siteId);
}
