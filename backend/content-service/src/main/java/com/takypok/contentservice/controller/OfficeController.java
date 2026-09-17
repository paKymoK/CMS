package com.takypok.contentservice.controller;

import com.takypok.contentservice.config.CmsAdminGuard;
import com.takypok.contentservice.model.entity.Office;
import com.takypok.contentservice.model.entity.Site;
import com.takypok.contentservice.model.request.OfficeCreateRequest;
import com.takypok.contentservice.model.request.OfficeUpdateRequest;
import com.takypok.contentservice.service.OfficeService;
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
@RequestMapping("/v1/admin/offices")
public class OfficeController {
  private final OfficeService officeService;
  private final SiteService siteService;
  private final CmsAdminGuard cmsAdminGuard;

  @GetMapping("")
  public Mono<ResultMessage<List<Office>>> get(
      @RequestParam String site, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> officeService.getAll(resolved.getId()))
        .map(ResultMessage::success);
  }

  @GetMapping("/{id}")
  public Mono<ResultMessage<Office>> getById(
      @PathVariable Long id, @RequestParam String site, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> officeService.getById(id, resolved.getId()))
        .map(ResultMessage::success);
  }

  @PostMapping("")
  public Mono<ResultMessage<Office>> create(
      @RequestParam String site,
      @Valid @RequestBody OfficeCreateRequest request,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> officeService.create(resolved.getId(), request))
        .map(ResultMessage::success);
  }

  @PutMapping("")
  public Mono<ResultMessage<Office>> update(
      @RequestParam String site,
      @Valid @RequestBody OfficeUpdateRequest request,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> officeService.update(resolved.getId(), request))
        .map(ResultMessage::success);
  }

  @DeleteMapping("/{id}")
  public Mono<ResultMessage<Void>> delete(
      @PathVariable Long id, @RequestParam String site, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> officeService.delete(id, resolved.getId()))
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
