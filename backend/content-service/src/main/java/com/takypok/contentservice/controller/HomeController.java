package com.takypok.contentservice.controller;

import com.takypok.contentservice.model.response.HomeContentResponse;
import com.takypok.contentservice.service.HomeContentService;
import com.takypok.contentservice.service.SiteService;
import com.takypok.core.model.ResultMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Public, unauthenticated, read-only. Site is resolved from the visitor's own Host header — never a
 * query param, since the whole point of subdomain routing is that the site is already known from
 * the request itself. See core-v1 SecurityConfig's GET /v1/home/** permitAll carve-out.
 */
@RestController
@RequiredArgsConstructor
public class HomeController {
  private final HomeContentService homeContentService;
  private final SiteService siteService;

  @GetMapping("/v1/home")
  public Mono<ResultMessage<HomeContentResponse>> getHome(ServerWebExchange exchange) {
    return siteService
        .resolveFromHost(exchange)
        .flatMap(site -> homeContentService.getHomeContent(site.getId()))
        .map(ResultMessage::success);
  }
}
