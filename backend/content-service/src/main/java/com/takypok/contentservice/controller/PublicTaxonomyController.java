package com.takypok.contentservice.controller;

import com.takypok.contentservice.model.TaxonomyKind;
import com.takypok.contentservice.model.response.TaxonomyResponses;
import com.takypok.contentservice.service.PostService;
import com.takypok.contentservice.service.SiteService;
import com.takypok.contentservice.service.TaxonomyService;
import com.takypok.core.model.ResultMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Public, unauthenticated, read-only: the Active categories and tags and the published posts of
 * each (/category/{slug}, /tag/{slug} on the website). The site comes from the visitor's Host
 * header, as for the other public reads. An Inactive or unknown one answers "not found".
 */
@RestController
@RequiredArgsConstructor
public class PublicTaxonomyController {
  private final TaxonomyService taxonomyService;
  private final PostService postService;
  private final SiteService siteService;

  @GetMapping("/v1/post-categories")
  public Mono<ResultMessage<List<TaxonomyResponses.PublicTerm>>> categories(
      ServerWebExchange exchange) {
    return list(TaxonomyKind.CATEGORY, exchange);
  }

  @GetMapping("/v1/post-tags")
  public Mono<ResultMessage<List<TaxonomyResponses.PublicTerm>>> tags(ServerWebExchange exchange) {
    return list(TaxonomyKind.TAG, exchange);
  }

  @GetMapping("/v1/post-categories/{slug}")
  public Mono<ResultMessage<TaxonomyResponses.PublicTermPage>> category(
      @PathVariable String slug, ServerWebExchange exchange) {
    return page(TaxonomyKind.CATEGORY, slug, exchange);
  }

  @GetMapping("/v1/post-tags/{slug}")
  public Mono<ResultMessage<TaxonomyResponses.PublicTermPage>> tag(
      @PathVariable String slug, ServerWebExchange exchange) {
    return page(TaxonomyKind.TAG, slug, exchange);
  }

  private Mono<ResultMessage<List<TaxonomyResponses.PublicTerm>>> list(
      TaxonomyKind kind, ServerWebExchange exchange) {
    return siteService
        .resolveFromHost(exchange)
        .flatMap(site -> taxonomyService.publicList(site.getId(), kind))
        .map(ResultMessage::success);
  }

  private Mono<ResultMessage<TaxonomyResponses.PublicTermPage>> page(
      TaxonomyKind kind, String slug, ServerWebExchange exchange) {
    return siteService
        .resolveFromHost(exchange)
        .flatMap(
            site ->
                taxonomyService
                    .publicTerm(site.getId(), kind, slug)
                    .flatMap(
                        term ->
                            postService
                                .publishedWith(site.getId(), kind, term.id())
                                .map(posts -> new TaxonomyResponses.PublicTermPage(term, posts))))
        .map(ResultMessage::success);
  }
}
