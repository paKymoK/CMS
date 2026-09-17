package com.takypok.contentservice.repository;

import com.takypok.contentservice.model.entity.ServiceCard;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ServiceCardRepository extends R2dbcRepository<ServiceCard, Long> {
  Flux<ServiceCard> findAllBySiteIdOrderByDisplayOrderAsc(Long siteId);

  Flux<ServiceCard> findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
      Long siteId, String status, Boolean active);

  Mono<ServiceCard> findByIdAndSiteId(Long id, Long siteId);
}
