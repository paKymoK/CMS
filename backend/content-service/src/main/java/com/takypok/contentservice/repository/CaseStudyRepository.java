package com.takypok.contentservice.repository;

import com.takypok.contentservice.model.entity.CaseStudy;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CaseStudyRepository extends R2dbcRepository<CaseStudy, Long> {
  Flux<CaseStudy> findAllBySiteIdOrderByDisplayOrderAsc(Long siteId);

  Flux<CaseStudy> findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
      Long siteId, String status, Boolean active);

  Mono<CaseStudy> findByIdAndSiteId(Long id, Long siteId);
}
