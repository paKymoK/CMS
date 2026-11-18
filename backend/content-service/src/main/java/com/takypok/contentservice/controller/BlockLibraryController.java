package com.takypok.contentservice.controller;

import com.takypok.contentservice.config.CmsAdminGuard;
import com.takypok.contentservice.model.entity.BlockLibraryItem;
import com.takypok.contentservice.model.entity.Site;
import com.takypok.contentservice.model.request.BlockLibraryCreateRequest;
import com.takypok.contentservice.service.BlockLibraryService;
import com.takypok.contentservice.service.SiteService;
import com.takypok.core.model.ResultMessage;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/** Admin-only: the saved page layouts and blocks are never exposed on the public site API. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/block-library")
public class BlockLibraryController {
  private final BlockLibraryService blockLibraryService;
  private final SiteService siteService;
  private final CmsAdminGuard cmsAdminGuard;

  @GetMapping("")
  public Mono<ResultMessage<List<BlockLibraryItem>>> get(
      @RequestParam String site,
      @RequestParam(defaultValue = "PAGE") String kind,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> blockLibraryService.getAll(resolved.getId(), kind))
        .map(ResultMessage::success);
  }

  @PostMapping("")
  public Mono<ResultMessage<BlockLibraryItem>> create(
      @RequestParam String site,
      @Valid @RequestBody BlockLibraryCreateRequest request,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> blockLibraryService.create(resolved.getId(), request))
        .map(ResultMessage::success);
  }

  @DeleteMapping("/{id}")
  public Mono<ResultMessage<Void>> delete(
      @PathVariable Long id, @RequestParam String site, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> blockLibraryService.delete(id, resolved.getId()))
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
