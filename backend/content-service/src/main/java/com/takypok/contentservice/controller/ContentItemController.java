package com.takypok.contentservice.controller;

import com.takypok.contentservice.config.CmsAdminGuard;
import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.model.entity.Site;
import com.takypok.contentservice.model.request.ContentItemCreateRequest;
import com.takypok.contentservice.model.request.ContentItemUpdateRequest;
import com.takypok.contentservice.service.ContentItemService;
import com.takypok.contentservice.service.SiteService;
import com.takypok.core.model.ResultMessage;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Admin CRUD for any content type registered in content_type — covers every future
 * content_item-backed section through one controller, the same way LogoBadgeController covers
 * award/certification/partner through one controller filtered by ?type=. ?type= here is mandatory
 * (not optional like LogoBadgeController's), since content_item spans arbitrarily many unrelated
 * content types and "every type for this site" isn't a meaningful admin-app screen.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/content-items")
public class ContentItemController {
  private final ContentItemService contentItemService;
  private final SiteService siteService;
  private final CmsAdminGuard cmsAdminGuard;

  @GetMapping("")
  public Mono<ResultMessage<List<ContentItem>>> get(
      @RequestParam String site, @RequestParam String type, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> contentItemService.getAll(resolved.getId(), type))
        .map(ResultMessage::success);
  }

  @GetMapping("/{id}")
  public Mono<ResultMessage<ContentItem>> getById(
      @PathVariable Long id, @RequestParam String site, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> contentItemService.getById(id, resolved.getId()))
        .map(ResultMessage::success);
  }

  @PostMapping("")
  public Mono<ResultMessage<ContentItem>> create(
      @RequestParam String site,
      @Valid @RequestBody ContentItemCreateRequest request,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> contentItemService.create(resolved.getId(), request))
        .map(ResultMessage::success);
  }

  @PutMapping("")
  public Mono<ResultMessage<ContentItem>> update(
      @RequestParam String site,
      @Valid @RequestBody ContentItemUpdateRequest request,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> contentItemService.update(resolved.getId(), request))
        .map(ResultMessage::success);
  }

  @DeleteMapping("/{id}")
  public Mono<ResultMessage<Void>> delete(
      @PathVariable Long id, @RequestParam String site, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> contentItemService.delete(id, resolved.getId()))
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
