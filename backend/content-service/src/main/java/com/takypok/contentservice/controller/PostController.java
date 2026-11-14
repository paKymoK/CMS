package com.takypok.contentservice.controller;

import com.takypok.contentservice.config.CmsAdminGuard;
import com.takypok.contentservice.model.TaxonomyKind;
import com.takypok.contentservice.model.entity.Site;
import com.takypok.contentservice.model.request.PostBulkRequest;
import com.takypok.contentservice.model.request.PostCreateRequest;
import com.takypok.contentservice.model.request.PostIdsRequest;
import com.takypok.contentservice.model.request.PostUpdateRequest;
import com.takypok.contentservice.model.response.PostListResponses;
import com.takypok.contentservice.model.response.PostResponse;
import com.takypok.contentservice.model.response.TrashResponses;
import com.takypok.contentservice.service.PostListService;
import com.takypok.contentservice.service.PostService;
import com.takypok.contentservice.service.PostTrashService;
import com.takypok.contentservice.service.SiteService;
import com.takypok.contentservice.service.TaxonomyService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
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
  private final PostTrashService postTrashService;
  private final TaxonomyService taxonomyService;
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
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) Long tagId,
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
                    resolved.getId(), q, status, categoryId, tagId, year, month, sort, page, size))
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

  /**
   * Change several posts at once: publish, unpublish (back to draft) or archive; or for the Trash,
   * trash, restore (as drafts, optionally under other addresses) or delete for good.
   */
  @PostMapping("/bulk")
  public Mono<ResultMessage<PostListResponses.BulkResult>> bulk(
      @RequestParam String site,
      @Valid @RequestBody PostBulkRequest request,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(
            resolved -> {
              String action = request.getAction() == null ? "" : request.getAction().trim();
              return switch (action.toUpperCase()) {
                case "TRASH" -> postTrashService.trash(resolved.getId(), request.getIds());
                case "RESTORE" ->
                    postTrashService.restore(
                        resolved.getId(), request.getIds(), request.getSlugs());
                case "DELETE_FOREVER" ->
                    postTrashService.deleteForever(resolved.getId(), request.getIds());
                case "ASSIGN_CATEGORY", "REMOVE_CATEGORY", "ASSIGN_TAG", "REMOVE_TAG" ->
                    assign(resolved.getId(), action.toUpperCase(), request);
                default ->
                    postService.bulkStatus(resolved.getId(), request.getIds(), request.getAction());
              };
            })
        .map(ResultMessage::success);
  }

  /** The Trash: the site's trashed posts, newest first. */
  @GetMapping("/trash")
  public Mono<ResultMessage<TrashResponses.Page>> trash(
      @RequestParam String site,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer size,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> postTrashService.list(resolved.getId(), q, page, size))
        .map(ResultMessage::success);
  }

  /** Before trashing: which of these posts other items link to. Empty means nothing to warn of. */
  @PostMapping("/trash/links")
  public Mono<ResultMessage<List<TrashResponses.LinkCheck>>> trashLinks(
      @RequestParam String site,
      @Valid @RequestBody PostIdsRequest request,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> postTrashService.linkCheck(resolved.getId(), request.getIds()))
        .map(ResultMessage::success);
  }

  /** Before restoring: whether each trashed post's address is free, and what to offer if not. */
  @PostMapping("/trash/restore-check")
  public Mono<ResultMessage<List<TrashResponses.RestoreCheck>>> restoreCheck(
      @RequestParam String site,
      @Valid @RequestBody PostIdsRequest request,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> postTrashService.restoreCheck(resolved.getId(), request.getIds()))
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

  /** Moves the post to the Trash (the same as the bulk TRASH action, for one post). */
  @DeleteMapping("/{id}")
  public Mono<ResultMessage<Void>> delete(
      @PathVariable Long id, @RequestParam String site, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(resolved -> postTrashService.trash(resolved.getId(), List.of(id)))
        .flatMap(
            result ->
                result.failed().isEmpty()
                    ? Mono.just(ResultMessage.<Void>success(null))
                    : Mono.error(
                        new ApplicationException(
                            Message.Application.ERROR, result.failed().get(0).reason())));
  }

  private Mono<PostListResponses.BulkResult> assign(
      Long siteId, String action, PostBulkRequest request) {
    if (request.getTaxonomyId() == null) {
      return Mono.error(
          new ApplicationException(Message.Application.ERROR, "Choose a category or tag"));
    }
    TaxonomyKind kind = action.endsWith("CATEGORY") ? TaxonomyKind.CATEGORY : TaxonomyKind.TAG;
    return taxonomyService.assignToPosts(
        siteId, kind, request.getTaxonomyId(), request.getIds(), action.startsWith("REMOVE"));
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
