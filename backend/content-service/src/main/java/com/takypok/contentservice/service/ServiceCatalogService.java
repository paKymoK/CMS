package com.takypok.contentservice.service;

import com.takypok.contentservice.model.response.ServiceNavResponse;
import com.takypok.contentservice.model.response.ServicePageResponse;
import reactor.core.publisher.Mono;

/** Read side of the Services section: the header menu tree and individual service pages. */
public interface ServiceCatalogService {
  Mono<ServiceNavResponse> getNav(Long siteId);

  Mono<ServicePageResponse> getPage(Long siteId, String category, String slug);
}
