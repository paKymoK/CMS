package com.takypok.contentservice.controller;

import com.takypok.contentservice.config.CmsAdminGuard;
import com.takypok.contentservice.model.entity.Site;
import com.takypok.contentservice.model.request.PostBulkRequest;
import com.takypok.contentservice.model.request.PostCreateRequest;
import com.takypok.contentservice.model.request.PostUpdateRequest;
import com.takypok.contentservice.model.response.PostListResponses;
import com.takypok.contentservice.model.response.PostResponse;
import com.takypok.contentservice.service.PostListService;
import com.takypok.contentservice.service.PostService;
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
@RequestMapping("/v1/admin/posts")
public class PostController {
  private final PostService postService;
  private final PostListService postListService;
  private final SiteService siteService;
  private final CmsAdminGuard cmsAdminGuard;

  @GetMapping("")
  public Mono<ResultMessage<List<PostResponse>>> get(
      @RequestParam String site, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> postService.getAll(resolved.getId()))
        .map(ResultMessage::success);
  }

  /**
   * The dedicated list: one page, filtered and sorted in the database. All parameters are optional;
   * see {@link PostListService#page}.
   */
  @GetMapping("/page")
  public Mono<ResultMessage<PostListResponses.Page>> page(
      @RequestParam String site,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) Integer year,
      @RequestParam(required = false) Integer month,
      @RequestParam(required = false) String sort,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer size,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(
            resolved ->
                postListService.page(
                    resolved.getId(), q, status, category, year, month, sort, page, size))
        .map(ResultMessage::success);
  }

  /** Counts per status, the categories in use and the years, for the list's filters. */
  @GetMapping("/facets")
  public Mono<ResultMessage<PostListResponses.Facets>> facets(
      @RequestParam String site, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> postListService.facets(resolved.getId()))
        .map(ResultMessage::success);
  }

  /** Publish, unpublish or archive several posts at once. */
  @PostMapping("/bulk")
  public Mono<ResultMessage<PostListResponses.BulkResult>> bulk(
      @RequestParam String site,
      @Valid @RequestBody PostBulkRequest request,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(
            resolved ->
                postService.bulkStatus(resolved.getId(), request.getIds(), request.getAction()))
        .map(ResultMessage::success);
  }

  @GetMapping("/{id}")
  public Mono<ResultMessage<PostResponse>> getById(
      @PathVariable Long id, @RequestParam String site, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> postService.getById(id, resolved.getId()))
        .map(ResultMessage::success);
  }

  @PostMapping("")
  public Mono<ResultMessage<PostResponse>> create(
      @RequestParam String site,
      @Valid @RequestBody PostCreateRequest request,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> postService.create(resolved.getId(), request))
        .map(ResultMessage::success);
  }

  @PutMapping("")
  public Mono<ResultMessage<PostResponse>> update(
      @RequestParam String site,
      @Valid @RequestBody PostUpdateRequest request,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> postService.update(resolved.getId(), request))
        .map(ResultMessage::success);
  }

  @DeleteMapping("/{id}")
  public Mono<ResultMessage<Void>> delete(
      @PathVariable Long id, @RequestParam String site, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> postService.delete(id, resolved.getId()))
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
