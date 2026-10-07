package com.takypok.contentservice.controller;

import com.takypok.contentservice.config.CmsAdminGuard;
import com.takypok.contentservice.model.entity.PostTemplate;
import com.takypok.contentservice.model.entity.Site;
import com.takypok.contentservice.model.request.PostTemplateCreateRequest;
import com.takypok.contentservice.service.PostTemplateService;
import com.takypok.contentservice.service.SiteService;
import com.takypok.core.model.ResultMessage;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/** Admin-only: templates are never exposed on the public site API. */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/post-templates")
public class PostTemplateController {
  private final PostTemplateService postTemplateService;
  private final SiteService siteService;
  private final CmsAdminGuard cmsAdminGuard;

  @GetMapping("")
  public Mono<ResultMessage<List<PostTemplate>>> get(
      @RequestParam String site, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> postTemplateService.getAll(resolved.getId()))
        .map(ResultMessage::success);
  }

  @PostMapping("")
  public Mono<ResultMessage<PostTemplate>> create(
      @RequestParam String site,
      @Valid @RequestBody PostTemplateCreateRequest request,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> postTemplateService.create(resolved.getId(), request))
        .map(ResultMessage::success);
  }

  @DeleteMapping("/{id}")
  public Mono<ResultMessage<Void>> delete(
      @PathVariable Long id, @RequestParam String site, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> postTemplateService.delete(id, resolved.getId()))
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
