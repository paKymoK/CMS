package com.takypok.contentservice.controller;

import com.takypok.contentservice.config.CmsAdminGuard;
import com.takypok.contentservice.model.entity.Site;
import com.takypok.contentservice.model.request.PageBulkRequest;
import com.takypok.contentservice.model.request.PageQuickEditRequest;
import com.takypok.contentservice.model.request.PostIdsRequest;
import com.takypok.contentservice.model.response.PageResponses;
import com.takypok.contentservice.model.response.PostListResponses;
import com.takypok.contentservice.model.response.TrashResponses;
import com.takypok.contentservice.service.PageService;
import com.takypok.contentservice.service.SiteService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import com.takypok.core.model.ResultMessage;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * The Pages list (PAGE-01): the Service, Industry and About pages as one list, with bulk actions,
 * quick edit and Trash.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/pages")
public class PageController {
  private final PageService pageService;
  private final SiteService siteService;
  private final CmsAdminGuard cmsAdminGuard;

  @GetMapping("/page")
  public Mono<ResultMessage<PageResponses.Page>> page(
      @RequestParam String site,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String type,
      @RequestParam(required = false) String author,
      @RequestParam(required = false) String publishedFrom,
      @RequestParam(required = false) String publishedTo,
      @RequestParam(required = false) String sort,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer size,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(
            s ->
                pageService.page(
                    s.getId(),
                    q,
                    status,
                    type,
                    author,
                    date(publishedFrom),
                    date(publishedTo),
                    sort,
                    page,
                    size))
        .map(ResultMessage::success);
  }

  @GetMapping("/facets")
  public Mono<ResultMessage<PageResponses.Facets>> facets(
      @RequestParam String site, Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(s -> pageService.facets(s.getId()))
        .map(ResultMessage::success);
  }

  /**
   * PUBLISH, UNPUBLISH, ARCHIVE and TRASH for the list; RESTORE and DELETE_FOREVER for the Trash.
   */
  @PostMapping("/bulk")
  public Mono<ResultMessage<PostListResponses.BulkResult>> bulk(
      @RequestParam String site,
      @Valid @RequestBody PageBulkRequest request,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(
            s -> {
              String action =
                  request.getAction() == null ? "" : request.getAction().trim().toUpperCase();
              return switch (action) {
                case "TRASH" -> pageService.trash(s.getId(), request.getIds());
                case "RESTORE" ->
                    pageService.restore(s.getId(), request.getIds(), request.getSlugs());
                case "DELETE_FOREVER" -> pageService.deleteForever(s.getId(), request.getIds());
                default -> pageService.bulkStatus(s.getId(), request.getIds(), request.getAction());
              };
            })
        .map(ResultMessage::success);
  }

  /** Quick edit on the list: title, address or status. */
  @PutMapping("/{id}")
  public Mono<ResultMessage<PageResponses.Row>> quickEdit(
      @PathVariable Long id,
      @RequestParam String site,
      @RequestBody PageQuickEditRequest request,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(s -> pageService.quickEdit(s.getId(), id, request))
        .map(ResultMessage::success);
  }

  @GetMapping("/trash")
  public Mono<ResultMessage<PageResponses.TrashPage>> trash(
      @RequestParam String site,
      @RequestParam(required = false) String q,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer size,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(s -> pageService.trashList(s.getId(), q, page, size))
        .map(ResultMessage::success);
  }

  /**
   * Before trashing: what links to these pages (and whether they are in a menu). Empty means
   * nothing to warn of.
   */
  @PostMapping("/trash/links")
  public Mono<ResultMessage<List<TrashResponses.LinkCheck>>> trashLinks(
      @RequestParam String site,
      @Valid @RequestBody PostIdsRequest request,
      Authentication authentication) {
    return authorizedSite(site, authentication)
        .flatMap(s -> pageService.linkCheck(s.getId(), request.getIds()))
        .map(ResultMessage::success);
  }

  private static LocalDate date(String value) {
    if (value == null || value.isBlank()) return null;
    try {
      return LocalDate.parse(value.trim());
    } catch (java.time.format.DateTimeParseException e) {
      throw new ApplicationException(Message.Application.ERROR, "Dates are written yyyy-mm-dd");
    }
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
