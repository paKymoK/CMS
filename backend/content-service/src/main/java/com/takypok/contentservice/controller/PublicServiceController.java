package com.takypok.contentservice.controller;

import com.takypok.contentservice.model.response.ServiceNavResponse;
import com.takypok.contentservice.model.response.ServicePageResponse;
import com.takypok.contentservice.service.ServiceCatalogService;
import com.takypok.contentservice.service.SiteService;
import com.takypok.core.model.ResultMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Public, unauthenticated, read-only — the Services menu tree and individual service pages. Site is
 * resolved from the visitor's Host header like every other public read endpoint. See core-v1
 * SecurityConfig's GET /v1/services/** carve-out; /v1/admin/content-items is untouched.
 */
@RestController
@RequiredArgsConstructor
public class PublicServiceController {
  private final ServiceCatalogService serviceCatalogService;
  private final SiteService siteService;

  @GetMapping("/v1/services")
  public Mono<ResultMessage<ServiceNavResponse>> nav(ServerWebExchange exchange) {
    return siteService
        .resolveFromHost(exchange)
        .flatMap(site -> serviceCatalogService.getNav(site.getId(), false))
        .map(ResultMessage::success);
  }

  @GetMapping("/v1/services/{category}/{slug}")
  public Mono<ResultMessage<ServicePageResponse>> page(
      @PathVariable String category, @PathVariable String slug, ServerWebExchange exchange) {
    return siteService
        .resolveFromHost(exchange)
        .flatMap(site -> serviceCatalogService.getPage(site.getId(), category, slug, false))
        .map(ResultMessage::success);
  }
}
