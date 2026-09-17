package com.takypok.contentservice.controller;

import com.takypok.contentservice.config.CmsAdminGuard;
import com.takypok.contentservice.model.entity.LogoBadge;
import com.takypok.contentservice.model.entity.Site;
import com.takypok.contentservice.model.request.LogoBadgeCreateRequest;
import com.takypok.contentservice.model.request.LogoBadgeUpdateRequest;
import com.takypok.contentservice.service.LogoBadgeService;
import com.takypok.contentservice.service.SiteService;
import com.takypok.core.model.ResultMessage;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/** Covers award/certification/partner logos through one controller, filtered by ?type=. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/logo-badges")
public class LogoBadgeController {
  private final LogoBadgeService logoBadgeService;
  private final SiteService siteService;
  private final CmsAdminGuard cmsAdminGuard;

  @GetMapping("")
  public Mono<ResultMessage<List<LogoBadge>>> get(
      @RequestParam String site,
      @RequestParam(required = false) String type,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> logoBadgeService.getAll(resolved.getId(), type))
        .map(ResultMessage::success);
  }

  @GetMapping("/{id}")
  public Mono<ResultMessage<LogoBadge>> getById(
      @PathVariable Long id, @RequestParam String site, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> logoBadgeService.getById(id, resolved.getId()))
        .map(ResultMessage::success);
  }

  @PostMapping("")
  public Mono<ResultMessage<LogoBadge>> create(
      @RequestParam String site,
      @Valid @RequestBody LogoBadgeCreateRequest request,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> logoBadgeService.create(resolved.getId(), request))
        .map(ResultMessage::success);
  }

  @PutMapping("")
  public Mono<ResultMessage<LogoBadge>> update(
      @RequestParam String site,
      @Valid @RequestBody LogoBadgeUpdateRequest request,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> logoBadgeService.update(resolved.getId(), request))
        .map(ResultMessage::success);
  }

  @DeleteMapping("/{id}")
  public Mono<ResultMessage<Void>> delete(
      @PathVariable Long id, @RequestParam String site, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> logoBadgeService.delete(id, resolved.getId()))
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
