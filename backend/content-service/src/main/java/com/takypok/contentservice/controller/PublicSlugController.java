package com.takypok.contentservice.controller;

import com.takypok.contentservice.model.response.SlugRedirectResponse;
import com.takypok.contentservice.service.SiteService;
import com.takypok.contentservice.service.SlugRegistry;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import com.takypok.core.model.ResultMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Where a renamed page now lives. The website asks this when a detail page is not found: if the
 * address is an old slug of a published item, it answers with the current address to redirect to.
 * Site resolved from the Host header, like the other public reads.
 */
@RestController
@RequiredArgsConstructor
public class PublicSlugController {
  private final SlugRegistry slugRegistry;
  private final SiteService siteService;

  /**
   * @param section the URL section: posts, case-studies, industries or services
   * @param path the address after the section: {@code slug}, or {@code category/slug} for services
   */
  @GetMapping("/v1/slug-redirect")
  public Mono<ResultMessage<SlugRedirectResponse>> redirect(
      @RequestParam String section, @RequestParam String path, ServerWebExchange exchange) {
    return siteService
        .resolveFromHost(exchange)
        .flatMap(site -> slugRegistry.currentPathOfAlias(site.getId(), section, path))
        .map(current -> ResultMessage.success(new SlugRedirectResponse(current)))
        .switchIfEmpty(
            Mono.error(new ApplicationException(Message.Application.ERROR, "Page not found")));
  }
}
