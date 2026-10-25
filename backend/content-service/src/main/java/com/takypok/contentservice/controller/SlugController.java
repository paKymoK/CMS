package com.takypok.contentservice.controller;

import com.takypok.contentservice.config.CmsAdminGuard;
import com.takypok.contentservice.model.entity.Site;
import com.takypok.contentservice.model.response.SlugResponse;
import com.takypok.contentservice.service.SiteService;
import com.takypok.contentservice.service.SlugRegistry;
import com.takypok.core.model.ResultMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/** The addresses of an item (current + old) for the editor's "Previous URLs", and releasing one. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/slugs")
public class SlugController {
  private final SlugRegistry slugRegistry;
  private final SiteService siteService;
  private final CmsAdminGuard cmsAdminGuard;

  @GetMapping("")
  public Mono<ResultMessage<List<SlugResponse>>> list(
      @RequestParam String site, @RequestParam Long itemId, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(
            resolved ->
                slugRegistry
                    .forItem(resolved.getId(), itemId)
                    .map(SlugResponse::from)
                    .collectList())
        .map(ResultMessage::success);
  }

  @DeleteMapping("/{id}")
  public Mono<ResultMessage<Void>> removeAlias(
      @PathVariable Long id, @RequestParam String site, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> slugRegistry.removeAlias(resolved.getId(), id))
        .then(Mono.just(ResultMessage.success(null)));
  }

  private Mono<Site> authorizedSite(String code, Authentication authentication) {
    return siteService
        .resolveByCode(code)
        .flatMap(
            resolved ->
                cmsAdminGuard
                    .requireSiteAccess(authentication, resolved.getCode())
                    .thenReturn(resolved));
  }
}
