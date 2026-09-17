package com.takypok.contentservice.repository;

import com.takypok.contentservice.model.entity.Banner;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BannerRepository extends R2dbcRepository<Banner, Long> {
  Flux<Banner> findAllBySiteIdOrderByDisplayOrderAsc(Long siteId);

  Flux<Banner> findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
      Long siteId, String status, Boolean active);

  Mono<Banner> findByIdAndSiteId(Long id, Long siteId);
}
