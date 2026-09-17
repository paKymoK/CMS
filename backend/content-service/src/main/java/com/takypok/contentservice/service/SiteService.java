package com.takypok.contentservice.service;

import com.takypok.contentservice.model.entity.Site;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface SiteService {
  /** Admin API: caller specifies which site's content it's managing via ?site=<code>. */
  Mono<Site> resolveByCode(String code);

  /** Public API: site is derived from the visitor's own Host header, never a query param. */
  Mono<Site> resolveFromHost(ServerWebExchange exchange);
}
