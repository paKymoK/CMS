package com.takypok.contentservice.repository;

import com.takypok.contentservice.model.entity.PreviewToken;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface PreviewTokenRepository extends R2dbcRepository<PreviewToken, Long> {
  Mono<PreviewToken> findByToken(String token);

  Mono<Void> deleteByToken(String token);
}
