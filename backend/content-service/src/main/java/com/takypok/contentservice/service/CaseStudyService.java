package com.takypok.contentservice.service;

import com.takypok.contentservice.model.request.CaseStudyCreateRequest;
import com.takypok.contentservice.model.request.CaseStudyUpdateRequest;
import com.takypok.contentservice.model.response.CaseStudyDetailResponse;
import com.takypok.contentservice.model.response.CaseStudyPageResponse;
import com.takypok.contentservice.model.response.CaseStudyResponse;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CaseStudyService {
  Mono<List<CaseStudyResponse>> getAll(Long siteId);

  Mono<CaseStudyResponse> getById(Long id, Long siteId);

  Mono<CaseStudyResponse> create(Long siteId, CaseStudyCreateRequest request);

  Mono<CaseStudyResponse> update(Long siteId, CaseStudyUpdateRequest request);

  Mono<Void> delete(Long id, Long siteId);

  Flux<CaseStudyResponse> getPublished(Long siteId);

  default Mono<CaseStudyPageResponse> searchPublished(
      Long siteId, String category, String q, int page, int size) {
    return searchPublished(siteId, category, q, page, size, false);
  }

  /**
   * One page of the published listing, optionally narrowed to a category and a text query. The
   * query goes through {@link SearchService}: best match first, and a misspelt query is searched as
   * corrected unless {@code exact} is set. {@code page} is 1-based; out-of-range values are
   * clamped.
   */
  Mono<CaseStudyPageResponse> searchPublished(
      Long siteId, String category, String q, int page, int size, boolean exact);

  Mono<CaseStudyDetailResponse> getPublishedDetailBySlug(Long siteId, String slug);

}
