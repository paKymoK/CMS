package com.takypok.contentservice.repository;

import com.takypok.contentservice.model.entity.BlockLibraryItem;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BlockLibraryRepository extends R2dbcRepository<BlockLibraryItem, Long> {
  Flux<BlockLibraryItem> findAllBySiteIdAndKindOrderByNameAsc(Long siteId, String kind);

  Mono<BlockLibraryItem> findByIdAndSiteId(Long id, Long siteId);
}
