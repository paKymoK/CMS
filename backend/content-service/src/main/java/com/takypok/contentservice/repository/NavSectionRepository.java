package com.takypok.contentservice.repository;

import com.takypok.contentservice.model.entity.NavSection;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NavSectionRepository extends R2dbcRepository<NavSection, Long> {
  Flux<NavSection> findAllBySiteIdOrderByDisplayOrderAsc(Long siteId);

  Flux<NavSection> findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
      Long siteId, String status, Boolean active);

  Mono<NavSection> findByIdAndSiteId(Long id, Long siteId);
}
