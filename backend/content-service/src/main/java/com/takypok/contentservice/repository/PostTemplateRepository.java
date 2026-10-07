package com.takypok.contentservice.repository;

import com.takypok.contentservice.model.entity.PostTemplate;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostTemplateRepository extends R2dbcRepository<PostTemplate, Long> {
  Flux<PostTemplate> findAllBySiteIdOrderByNameAsc(Long siteId);

  Mono<PostTemplate> findByIdAndSiteId(Long id, Long siteId);
}
