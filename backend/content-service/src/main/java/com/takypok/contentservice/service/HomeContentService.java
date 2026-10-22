package com.takypok.contentservice.service;

import com.takypok.contentservice.model.response.HomeContentResponse;
import reactor.core.publisher.Mono;

public interface HomeContentService {
  Mono<HomeContentResponse> getHomeContent(Long siteId);
}
