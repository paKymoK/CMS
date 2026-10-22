package com.takypok.contentservice.controller;

import com.takypok.contentservice.model.response.IndustryPageResponse;
import com.takypok.contentservice.service.IndustryPageService;
import com.takypok.contentservice.service.SiteService;
import com.takypok.core.model.ResultMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Public, unauthenticated, read-only — individual industry pages. Site is resolved from the
 * visitor's Host header like every other public read endpoint. See core-v1 SecurityConfig's GET
 * /v1/industries/** carve-out.
 */
@RestController
@RequiredArgsConstructor
public class PublicIndustryController {
  private final IndustryPageService industryPageService;
  private final SiteService siteService;

  @GetMapping("/v1/industries/{slug}")
  public Mono<ResultMessage<IndustryPageResponse>> page(
      @PathVariable String slug, ServerWebExchange exchange) {
    return siteService
        .resolveFromHost(exchange)
        .flatMap(site -> industryPageService.getPage(site.getId(), slug))
        .map(ResultMessage::success);
  }
}
