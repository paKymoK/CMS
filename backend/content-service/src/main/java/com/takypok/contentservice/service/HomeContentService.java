package com.takypok.contentservice.service;

import com.takypok.contentservice.model.response.HomeContentResponse;
import reactor.core.publisher.Mono;

public interface HomeContentService {
  Mono<HomeContentResponse> getHomeContent(Long siteId);

  /**
   * Same composition as {@link #getHomeContent}, but every source's getAll(siteId) instead of
   * getPublished(siteId) — draft and inactive rows included, matching what the admin list screens
   * already show. Used only by the token-gated preview path; never cached, never reached without a
   * valid preview_token.
   */
  Mono<HomeContentResponse> getPreviewContent(Long siteId);
}
