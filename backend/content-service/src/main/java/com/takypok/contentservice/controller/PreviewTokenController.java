package com.takypok.contentservice.controller;

import com.takypok.contentservice.config.CmsAdminGuard;
import com.takypok.contentservice.model.entity.Site;
import com.takypok.contentservice.model.response.PreviewTokenResponse;
import com.takypok.contentservice.service.PreviewTokenService;
import com.takypok.contentservice.service.SiteService;
import com.takypok.core.model.ResultMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Mint-only — admin-authenticated the same way every other /v1/admin/** controller is
 * (CmsAdminGuard.requireSiteAccess). The token this hands back is what admin-app opens the
 * website's preview URL with; refreshing it after that point goes through PreviewController
 * instead, since the website tab holds the preview token, never this admin session.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/preview-tokens")
public class PreviewTokenController {
  private final PreviewTokenService previewTokenService;
  private final SiteService siteService;
  private final CmsAdminGuard cmsAdminGuard;

  @PostMapping("")
  public Mono<ResultMessage<PreviewTokenResponse>> mint(
      @RequestParam String site, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> previewTokenService.mint(resolved.getId()))
        .map(PreviewTokenResponse::from)
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
