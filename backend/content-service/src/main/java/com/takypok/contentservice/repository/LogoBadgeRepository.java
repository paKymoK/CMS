package com.takypok.contentservice.repository;

import com.takypok.contentservice.model.entity.LogoBadge;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LogoBadgeRepository extends R2dbcRepository<LogoBadge, Long> {
  Flux<LogoBadge> findAllBySiteIdOrderByDisplayOrderAsc(Long siteId);

  Flux<LogoBadge> findAllBySiteIdAndTypeOrderByDisplayOrderAsc(Long siteId, String type);

  Flux<LogoBadge> findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
      Long siteId, String status, Boolean active);

  Flux<LogoBadge> findAllBySiteIdAndTypeAndStatusAndActiveOrderByDisplayOrderAsc(
      Long siteId, String type, String status, Boolean active);

  Mono<LogoBadge> findByIdAndSiteId(Long id, Long siteId);
}
