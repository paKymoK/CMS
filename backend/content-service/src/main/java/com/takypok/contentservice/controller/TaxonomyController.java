package com.takypok.contentservice.controller;

import com.takypok.contentservice.config.CmsAdminGuard;
import com.takypok.contentservice.model.TaxonomyKind;
import com.takypok.contentservice.model.entity.Site;
import com.takypok.contentservice.model.request.TaxonomyBulkRequest;
import com.takypok.contentservice.model.request.TaxonomyRequest;
import com.takypok.contentservice.model.response.PostListResponses;
import com.takypok.contentservice.model.response.TaxonomyResponses;
import com.takypok.contentservice.service.SiteService;
import com.takypok.contentservice.service.TaxonomyService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import com.takypok.core.model.ResultMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * The admin side of post categories ({@code /v1/admin/post-categories}) and tags ({@code
 * /v1/admin/post-tags}): the same screens and rules, so one controller serves both.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/{kind:post-categories|post-tags}")
public class TaxonomyController {
  private final TaxonomyService taxonomyService;
  private final SiteService siteService;
  private final CmsAdminGuard cmsAdminGuard;

  @GetMapping("")
  public Mono<ResultMessage<TaxonomyResponses.Page>> list(
      @PathVariable String kind,
      @RequestParam String site,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) Long parentId,
      @RequestParam(required = false) String sort,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer size,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(
            s ->
                taxonomyService.list(
                    s.getId(), kindOf(kind), q, status, parentId, sort, page, size))
        .map(ResultMessage::success);
  }

  @GetMapping("/{id}")
  public Mono<ResultMessage<TaxonomyResponses.Row>> get(
      @PathVariable String kind,
      @PathVariable Long id,
      @RequestParam String site,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(s -> taxonomyService.get(s.getId(), kindOf(kind), id))
        .map(ResultMessage::success);
  }

  @PostMapping("")
  public Mono<ResultMessage<TaxonomyResponses.Row>> create(
      @PathVariable String kind,
      @RequestParam String site,
      @Valid @RequestBody TaxonomyRequest request,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(s -> taxonomyService.create(s.getId(), kindOf(kind), request))
        .map(ResultMessage::success);
  }

  @PutMapping("/{id}")
  public Mono<ResultMessage<TaxonomyResponses.Row>> update(
      @PathVariable String kind,
      @PathVariable Long id,
      @RequestParam String site,
      @Valid @RequestBody TaxonomyRequest request,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(s -> taxonomyService.update(s.getId(), kindOf(kind), id, request))
        .map(ResultMessage::success);
  }

  /**
   * ACTIVATE, DEACTIVATE, DELETE (to the Trash) and REMOVE_FROM_POSTS for the list and its quick
   * actions; RESTORE and DELETE_FOREVER for the Trash. The answer says what changed and what could
   * not.
   */
  @PostMapping("/bulk")
  public Mono<ResultMessage<PostListResponses.BulkResult>> bulk(
      @PathVariable String kind,
      @RequestParam String site,
      @Valid @RequestBody TaxonomyBulkRequest request,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(
            s -> {
              TaxonomyKind k = kindOf(kind);
              String action =
                  request.getAction() == null ? "" : request.getAction().trim().toUpperCase();
              return switch (action) {
                case "ACTIVATE" -> taxonomyService.setActive(s.getId(), k, request.getIds(), true);
                case "DEACTIVATE" ->
                    taxonomyService.setActive(s.getId(), k, request.getIds(), false);
                case "DELETE" -> taxonomyService.delete(s.getId(), k, request.getIds());
                case "REMOVE_FROM_POSTS" ->
                    taxonomyService.removeFromPosts(s.getId(), k, request.getIds());
                case "RESTORE" ->
                    taxonomyService.restore(s.getId(), k, request.getIds(), request.getSlugs());
                case "DELETE_FOREVER" ->
                    taxonomyService.deleteForever(s.getId(), k, request.getIds());
                default ->
                    Mono.error(
                        new ApplicationException(
                            Message.Application.ERROR, "Unknown action: " + request.getAction()));
              };
            })
        .map(ResultMessage::success);
  }

  /** The Trash for this kind, newest first. */
  @GetMapping("/trash")
  public Mono<ResultMessage<TaxonomyResponses.TrashPage>> trash(
      @PathVariable String kind,
      @RequestParam String site,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer size,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(s -> taxonomyService.trashList(s.getId(), kindOf(kind), q, page, size))
        .map(ResultMessage::success);
  }

  private static TaxonomyKind kindOf(String path) {
    return TaxonomyKind.fromPath(path)
        .orElseThrow(() -> new ApplicationException(Message.Application.ERROR, "Unknown kind"));
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
