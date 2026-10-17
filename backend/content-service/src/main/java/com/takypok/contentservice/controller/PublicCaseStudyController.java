package com.takypok.contentservice.controller;

import com.takypok.contentservice.model.response.CaseStudyDetailResponse;
import com.takypok.contentservice.model.response.CaseStudyPageResponse;
import com.takypok.contentservice.service.CaseStudyService;
import com.takypok.contentservice.service.SiteService;
import com.takypok.core.model.ResultMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Public, unauthenticated, read-only — Case Studies listing + Case Study Detail. Site resolved from
 * the visitor's own Host header, same as PublicPostController/HomeController.
 * /v1/admin/case-studies is a different path prefix and untouched by this — still authenticated +
 * site-access-checked.
 */
@RestController
@RequiredArgsConstructor
public class PublicCaseStudyController {
  private static final int MAX_PAGE_SIZE = 24;

  private final CaseStudyService caseStudyService;
  private final SiteService siteService;

  @GetMapping("/v1/case-studies")
  public Mono<ResultMessage<CaseStudyPageResponse>> list(
      @RequestParam(required = false) String category,
      @RequestParam(required = false) String q,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "6") int size,
      ServerWebExchange exchange) {
    int pageSize = Math.min(Math.max(1, size), MAX_PAGE_SIZE);
    return siteService
        .resolveFromHost(exchange)
        .flatMap(
            site -> caseStudyService.searchPublished(site.getId(), category, q, page, pageSize))
        .map(ResultMessage::success);
  }

  @GetMapping("/v1/case-studies/{slug}")
  public Mono<ResultMessage<CaseStudyDetailResponse>> getBySlug(
      @PathVariable String slug, ServerWebExchange exchange) {
    return siteService
        .resolveFromHost(exchange)
        .flatMap(site -> caseStudyService.getPublishedDetailBySlug(site.getId(), slug))
        .map(ResultMessage::success);
  }
}
