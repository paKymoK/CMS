package com.takypok.contentservice.controller;

import com.takypok.contentservice.config.CmsAdminGuard;
import com.takypok.contentservice.model.entity.Site;
import com.takypok.contentservice.model.response.AuditResponses;
import com.takypok.contentservice.service.ContentAuditService;
import com.takypok.contentservice.service.SiteService;
import com.takypok.core.model.ResultMessage;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * The content audit trail for the admin app: the site-wide activity feed, one item's history (the
 * same list filtered by {@code itemId}), the full before/after of an entry, and restoring an item
 * to what an entry recorded. Authenticated and site-access-checked like the rest of /v1/admin.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/audit")
public class AuditController {
  private final ContentAuditService auditService;
  private final SiteService siteService;
  private final CmsAdminGuard cmsAdminGuard;

  @GetMapping("")
  public Mono<ResultMessage<AuditResponses.Page>> list(
      @RequestParam String site,
      @RequestParam(required = false) Long itemId,
      @RequestParam(required = false) String type,
      @RequestParam(required = false) String action,
      @RequestParam(required = false) String actor,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int size,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(
            resolved ->
                auditService.list(
                    resolved.getId(),
                    new ContentAuditService.Filter(itemId, type, action, actor, from, to),
                    page,
                    size))
        .map(ResultMessage::success);
  }

  @GetMapping("/{id}")
  public Mono<ResultMessage<AuditResponses.Detail>> get(
      @PathVariable Long id, @RequestParam String site, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> auditService.get(resolved.getId(), id))
        .map(ResultMessage::success);
  }

  @PostMapping("/{id}/restore")
  public Mono<ResultMessage<AuditResponses.Restored>> restore(
      @PathVariable Long id, @RequestParam String site, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> auditService.restore(resolved.getId(), id))
        .map(ResultMessage::success);
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
