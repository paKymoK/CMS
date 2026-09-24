package com.takypok.contentservice.service;

import com.takypok.contentservice.model.entity.PreviewToken;
import reactor.core.publisher.Mono;

public interface PreviewTokenService {
  Mono<PreviewToken> mint(Long siteId);

  /**
   * Rotates: validates the current token, deletes it, and mints a fresh one for the same site with
   * a new 15-minute expiry — a captured-but-unused old token stops working the moment a real
   * refresh happens, rather than staying valid for as long as someone keeps extending it.
   */
  Mono<PreviewToken> refresh(String token);
}
