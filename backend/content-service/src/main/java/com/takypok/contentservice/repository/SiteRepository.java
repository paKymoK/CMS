package com.takypok.contentservice.repository;

import com.takypok.contentservice.model.entity.Site;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface SiteRepository extends R2dbcRepository<Site, Long> {
  Mono<Site> findByCode(String code);

  Mono<Site> findBySubdomain(String subdomain);
}
