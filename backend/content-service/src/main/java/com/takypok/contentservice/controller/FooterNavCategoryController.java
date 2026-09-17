package com.takypok.contentservice.controller;

import com.takypok.contentservice.config.CmsAdminGuard;
import com.takypok.contentservice.model.entity.FooterNavCategory;
import com.takypok.contentservice.model.entity.Site;
import com.takypok.contentservice.model.request.FooterNavCategoryCreateRequest;
import com.takypok.contentservice.model.request.FooterNavCategoryUpdateRequest;
import com.takypok.contentservice.service.FooterNavCategoryService;
import com.takypok.contentservice.service.SiteService;
import com.takypok.core.model.ResultMessage;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/footer-nav-categories")
public class FooterNavCategoryController {
  private final FooterNavCategoryService footerNavCategoryService;
  private final SiteService siteService;
  private final CmsAdminGuard cmsAdminGuard;

  @GetMapping("")
  public Mono<ResultMessage<List<FooterNavCategory>>> get(
      @RequestParam String site, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> footerNavCategoryService.getAll(resolved.getId()))
        .map(ResultMessage::success);
  }

  @GetMapping("/{id}")
  public Mono<ResultMessage<FooterNavCategory>> getById(
      @PathVariable Long id, @RequestParam String site, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> footerNavCategoryService.getById(id, resolved.getId()))
        .map(ResultMessage::success);
  }

  @PostMapping("")
  public Mono<ResultMessage<FooterNavCategory>> create(
      @RequestParam String site,
      @Valid @RequestBody FooterNavCategoryCreateRequest request,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> footerNavCategoryService.create(resolved.getId(), request))
        .map(ResultMessage::success);
  }

  @PutMapping("")
  public Mono<ResultMessage<FooterNavCategory>> update(
      @RequestParam String site,
      @Valid @RequestBody FooterNavCategoryUpdateRequest request,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> footerNavCategoryService.update(resolved.getId(), request))
        .map(ResultMessage::success);
  }

  @DeleteMapping("/{id}")
  public Mono<ResultMessage<Void>> delete(
      @PathVariable Long id, @RequestParam String site, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> footerNavCategoryService.delete(id, resolved.getId()))
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
