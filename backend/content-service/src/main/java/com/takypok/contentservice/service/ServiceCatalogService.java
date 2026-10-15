package com.takypok.contentservice.service;

import com.takypok.contentservice.model.response.ServiceNavResponse;
import com.takypok.contentservice.model.response.ServicePageResponse;
import reactor.core.publisher.Mono;

/** Read side of the Services section: the header menu tree and individual service pages. */
public interface ServiceCatalogService {
  /** {@code preview} includes drafts and inactive rows (token-gated preview path only). */
  Mono<ServiceNavResponse> getNav(Long siteId, boolean preview);

  Mono<ServicePageResponse> getPage(Long siteId, String category, String slug, boolean preview);
}
