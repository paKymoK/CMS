package com.takypok.contentservice.service;

import com.takypok.contentservice.model.response.AboutPageResponse;
import reactor.core.publisher.Mono;

/** Read side of the About page, of which a site has exactly one. */
public interface AboutPageService {
  Mono<AboutPageResponse> getPage(Long siteId);
}
