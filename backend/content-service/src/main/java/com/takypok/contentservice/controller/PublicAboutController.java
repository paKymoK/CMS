package com.takypok.contentservice.controller;

import com.takypok.contentservice.model.response.AboutPageResponse;
import com.takypok.contentservice.service.AboutPageService;
import com.takypok.contentservice.service.SiteService;
import com.takypok.core.model.ResultMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Public, unauthenticated, read-only — the About page. Site is resolved from the visitor's Host
 * header like every other public read endpoint. See core-v1 SecurityConfig's GET /v1/about
 * carve-out.
 */
@RestController
@RequiredArgsConstructor
public class PublicAboutController {
  private final AboutPageService aboutPageService;
  private final SiteService siteService;

  @GetMapping("/v1/about")
  public Mono<ResultMessage<AboutPageResponse>> page(ServerWebExchange exchange) {
    return siteService
        .resolveFromHost(exchange)
        .flatMap(site -> aboutPageService.getPage(site.getId()))
        .map(ResultMessage::success);
  }
}
