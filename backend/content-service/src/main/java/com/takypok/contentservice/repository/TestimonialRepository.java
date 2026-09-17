package com.takypok.contentservice.repository;

import com.takypok.contentservice.model.entity.Testimonial;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TestimonialRepository extends R2dbcRepository<Testimonial, Long> {
  Flux<Testimonial> findAllBySiteIdOrderByDisplayOrderAsc(Long siteId);

  Flux<Testimonial> findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
      Long siteId, String status, Boolean active);

  Mono<Testimonial> findByIdAndSiteId(Long id, Long siteId);
}
