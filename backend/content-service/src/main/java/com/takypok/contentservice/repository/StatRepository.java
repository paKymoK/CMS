package com.takypok.contentservice.repository;

import com.takypok.contentservice.model.entity.Stat;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StatRepository extends R2dbcRepository<Stat, Long> {
  Flux<Stat> findAllBySiteIdOrderByDisplayOrderAsc(Long siteId);

  Flux<Stat> findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
      Long siteId, String status, Boolean active);

  Mono<Stat> findByIdAndSiteId(Long id, Long siteId);
}
