package com.takypok.contentservice.controller;

import com.takypok.contentservice.config.PreviewTokenGuard;
import com.takypok.contentservice.model.response.HomeContentResponse;
import com.takypok.contentservice.model.response.PreviewTokenResponse;
import com.takypok.contentservice.service.HomeContentService;
import com.takypok.contentservice.service.PreviewTokenService;
import com.takypok.core.model.ResultMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Unauthenticated at the Spring Security layer (see core-v1 SecurityConfig's /v1/preview/**
 * permitAll carve-out) — every request here is instead validated against a minted, site-scoped,
 * time-limited preview_token, by PreviewTokenGuard or PreviewTokenService.refresh. siteId always
 * comes from the resolved token, never a caller-supplied parameter — there's no ?site= here.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/preview")
public class PreviewController {
  private final PreviewTokenGuard previewTokenGuard;
  private final PreviewTokenService previewTokenService;
  private final HomeContentService homeContentService;

  @GetMapping("/home")
  public Mono<ResultMessage<HomeContentResponse>> getHome(@RequestParam String token) {
    return previewTokenGuard
        .requireValidToken(token)
        .flatMap(previewToken -> homeContentService.getPreviewContent(previewToken.getSiteId()))
        .map(ResultMessage::success);
  }

  @PostMapping("/tokens/refresh")
  public Mono<ResultMessage<PreviewTokenResponse>> refresh(@RequestParam String token) {
    return previewTokenService
        .refresh(token)
        .map(PreviewTokenResponse::from)
        .map(ResultMessage::success);
  }
}
