package com.takypok.contentservice.repository;

import com.takypok.contentservice.audit.ContentAuditEntry;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface ContentAuditRepository extends R2dbcRepository<ContentAuditEntry, Long> {
  Mono<ContentAuditEntry> findByIdAndSiteId(Long id, Long siteId);
}
