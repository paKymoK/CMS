package com.takypok.contentservice.service;

import com.takypok.contentservice.model.entity.CaseStudy;
import com.takypok.contentservice.model.request.CaseStudyCreateRequest;
import com.takypok.contentservice.model.request.CaseStudyUpdateRequest;
import com.takypok.contentservice.model.response.CaseStudyDetailResponse;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CaseStudyService {
  Mono<List<CaseStudy>> getAll(Long siteId);

  Mono<CaseStudy> getById(Long id, Long siteId);

  Mono<CaseStudy> create(Long siteId, CaseStudyCreateRequest request);

  Mono<CaseStudy> update(Long siteId, CaseStudyUpdateRequest request);

  Mono<Void> delete(Long id, Long siteId);

  Flux<CaseStudy> getPublished(Long siteId);

  Mono<CaseStudyDetailResponse> getPublishedDetailBySlug(Long siteId, String slug);

  /**
   * Draft-inclusive counterpart of getPublishedDetailBySlug for the token-gated preview path —
   * ignores status/active (including on the linked testimonial), but still filters by siteId.
   * "Related" stays published-only so the preview shows what visitors will actually see.
   */
  Mono<CaseStudyDetailResponse> getPreviewDetailBySlug(Long siteId, String slug);
}
