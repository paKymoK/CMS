package com.takypok.contentservice.controller;

import com.takypok.contentservice.model.response.PostDetailResponse;
import com.takypok.contentservice.model.response.PostSummaryResponse;
import com.takypok.contentservice.service.PostService;
import com.takypok.contentservice.service.SiteService;
import com.takypok.core.model.ResultMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Public, unauthenticated, read-only — Insights listing + Post Detail. Site is resolved from the
 * visitor's own Host header, exactly like HomeController's /v1/home, and for the same reason: never
 * a query param, since subdomain routing already tells us the site from the request itself. See
 * core-v1 SecurityConfig's GET /v1/posts, /v1/posts/** permitAll carve-out. /v1/admin/posts is a
 * different path prefix and untouched by this — still authenticated + site-access-checked.
 *
 * <p>The design mockup this page was built from (Insights.dc.html) annotates its own proposed API
 * as {@code GET /v1/posts?site=en&tag=&q=&page=} — the {@code site} query param is deliberately NOT
 * implemented here; it would violate the Host-header-only site resolution rule this codebase
 * already enforces for every other public read endpoint.
 */
@RestController
@RequiredArgsConstructor
public class PublicPostController {
  private final PostService postService;
  private final SiteService siteService;

  @GetMapping("/v1/posts")
  public Mono<ResultMessage<List<PostSummaryResponse>>> list(
      @RequestParam(required = false) String tag,
      @RequestParam(required = false) String q,
      ServerWebExchange exchange) {
    return siteService
        .resolveFromHost(exchange)
        .flatMap(site -> postService.searchPublished(site.getId(), tag, q))
        .map(ResultMessage::success);
  }

  @GetMapping("/v1/posts/{slug}")
  public Mono<ResultMessage<PostDetailResponse>> getBySlug(
      @PathVariable String slug, ServerWebExchange exchange) {
    return siteService
        .resolveFromHost(exchange)
        .flatMap(site -> postService.getPublishedDetailBySlug(site.getId(), slug))
        .map(ResultMessage::success);
  }
}
