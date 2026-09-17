package com.takypok.contentservice.repository;

import com.takypok.contentservice.model.entity.Office;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OfficeRepository extends R2dbcRepository<Office, Long> {
  Flux<Office> findAllBySiteIdOrderByDisplayOrderAsc(Long siteId);

  Flux<Office> findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
      Long siteId, String status, Boolean active);

  Mono<Office> findByIdAndSiteId(Long id, Long siteId);
}
