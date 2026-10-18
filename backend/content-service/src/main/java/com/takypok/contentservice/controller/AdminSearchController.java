package com.takypok.contentservice.controller;

import com.takypok.contentservice.config.CmsAdminGuard;
import com.takypok.contentservice.model.entity.Site;
import com.takypok.contentservice.model.response.SearchResponses;
import com.takypok.contentservice.service.SearchService;
import com.takypok.contentservice.service.SiteService;
import com.takypok.core.model.ResultMessage;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Search for the admin app: the same forgiving search as the public site, but over every status
 * (drafts included) and every content type, and authenticated + site-access-checked like the rest
 * of /v1/admin.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/search")
public class AdminSearchController {
  private static final int MAX_QUERY_LENGTH = 100;
  private static final int MAX_LIMIT = 100;

  private final SearchService searchService;
  private final SiteService siteService;
  private final CmsAdminGuard cmsAdminGuard;

  @GetMapping("")
  public Mono<ResultMessage<SearchResponses.Admin>> search(
      @RequestParam String site,
      @RequestParam String q,
      @RequestParam(required = false) String type,
      @RequestParam(defaultValue = "false") boolean exact,
      @RequestParam(defaultValue = "30") int limit,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(
            resolved ->
                searchService.resolve(
                    resolved.getId(),
                    types(type),
                    clamp(q),
                    false,
                    !exact,
                    Math.min(Math.max(1, limit), MAX_LIMIT)))
        .map(SearchResponses.Admin::from)
        .map(ResultMessage::success);
  }

  @GetMapping("/suggest")
  public Mono<ResultMessage<SearchResponses.Suggest>> suggest(
      @RequestParam String site,
      @RequestParam String q,
      @RequestParam(required = false) String type,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> searchService.suggest(resolved.getId(), types(type), clamp(q), false))
        .map(SearchResponses.Suggest::new)
        .map(ResultMessage::success);
  }

  /** Comma-separated content types; none means every type. */
  private static List<String> types(String type) {
    if (type == null || type.isBlank()) {
      return List.of();
    }
    return Arrays.stream(type.split(",")).map(String::trim).filter(t -> !t.isEmpty()).toList();
  }

  private static String clamp(String q) {
    return q.length() > MAX_QUERY_LENGTH ? q.substring(0, MAX_QUERY_LENGTH) : q;
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
