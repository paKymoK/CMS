package com.takypok.contentservice.service;

import com.takypok.contentservice.model.response.IndustryPageResponse;
import reactor.core.publisher.Mono;

/** Read side of the Industries section: individual industry pages. */
public interface IndustryPageService {
  Mono<IndustryPageResponse> getPage(Long siteId, String slug);
}
