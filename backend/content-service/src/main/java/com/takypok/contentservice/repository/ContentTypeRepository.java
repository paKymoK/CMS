package com.takypok.contentservice.repository;

import com.takypok.contentservice.model.entity.ContentType;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ContentTypeRepository extends R2dbcRepository<ContentType, Long> {
  Mono<ContentType> findByKey(String key);

  Flux<ContentType> findAllByActiveTrueOrderByLabelAsc();
}
