package com.takypok.contentservice.service.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.takypok.contentservice.audit.ContentItemWriter;
import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.model.request.ContentItemUpdateRequest;
import com.takypok.contentservice.model.request.PageQuickEditRequest;
import com.takypok.contentservice.model.response.PageResponses;
import com.takypok.contentservice.model.response.PostListResponses;
import com.takypok.contentservice.model.response.TrashResponses;
import com.takypok.contentservice.repository.ContentItemRepository;
import com.takypok.contentservice.repository.PageRepository;
import com.takypok.contentservice.repository.TrashRepository;
import com.takypok.contentservice.service.ContentItemService;
import com.takypok.contentservice.service.PageService;
import com.takypok.contentservice.util.PageTypes;
import com.takypok.contentservice.util.ScheduleRules;
import com.takypok.contentservice.util.StaleEdit;
import com.takypok.contentservice.util.StatusRules;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import com.takypok.core.model.authentication.User;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PageServiceImpl implements PageService {
  private static final int DEFAULT_SIZE = 20;
  private static final int MAX_SIZE = 100;
  private static final int MAX_REFERRERS_LISTED = 5;
  private static final int MAX_REFERRER_ROWS = 500;
  private static final Set<String> SORTS =
      Set.of(
          "updated_desc",
          "updated_asc",
          "title_asc",
          "title_desc",
          "published_desc",
          "published_asc");
  private static final Set<String> STATUSES = Set.of("DRAFT", "SCHEDULED", "PUBLISHED", "ARCHIVED");

  private final PageRepository pages;
  private final ContentItemRepository items;
  private final ContentItemService contentItemService;
  private final ContentItemWriter writer;
  private final TrashRepository trash;
  private final ReactiveAuditorAware<User> auditor;

  private record Outcome(Long id, PostListResponses.Failure failure) {}

  // ---- the list --------------------------------------------------------------------------------

  @Override
  public Mono<PageResponses.Page> page(
      Long siteId,
      String q,
      String status,
      String type,
      String author,
      LocalDate publishedFrom,
      LocalDate publishedTo,
      String sort,
      Integer page,
      Integer size) {
    return Mono.defer(
        () -> {
          String statusValue = blankToNull(status);
          if (statusValue != null && !STATUSES.contains(statusValue)) {
            throw fail("status must be DRAFT, SCHEDULED, PUBLISHED or ARCHIVED");
          }
          String typeValue = blankToNull(type);
          if (typeValue != null && !PageTypes.is(typeValue)) throw fail("Unknown page type");
          String sortValue = blankToNull(sort);
          if (sortValue != null && !SORTS.contains(sortValue)) throw fail("Unknown sort: " + sort);
          int pageNo = page == null ? 1 : page;
          int pageSize = size == null ? DEFAULT_SIZE : size;
          if (pageNo < 1) throw fail("page starts at 1");
          if (pageSize < 0 || pageSize > MAX_SIZE)
            throw fail("size must be 0 (all) or up to " + MAX_SIZE);
          String query = blankToNull(q);
          if (query != null && query.length() > 200) throw fail("The search text is too long");
          PageRepository.Filter filter =
              new PageRepository.Filter(
                  query,
                  statusValue,
                  typeValue,
                  blankToNull(author),
                  publishedFrom == null
                      ? null
                      : publishedFrom.atStartOfDay().atOffset(ZoneOffset.UTC),
                  publishedTo == null
                      ? null
                      : publishedTo.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC));
          return pages
              .page(siteId, filter, sortValue, pageNo, pageSize)
              .map(
                  r ->
                      new PageResponses.Page(
                          r.rows(),
                          pageNo,
                          pageSize,
                          r.total(),
                          pageSize == 0
                              ? 1
                              : (int) Math.max(1, Math.ceil(r.total() / (double) pageSize))));
        });
  }

  @Override
  public Mono<PageResponses.Facets> facets(Long siteId) {
    return Mono.zip(pages.statusCounts(siteId), pages.trashCount(siteId), pages.authors(siteId))
        .map(t -> new PageResponses.Facets(withZeros(t.getT1()), t.getT2(), t.getT3()));
  }

  private static Map<String, Long> withZeros(Map<String, Long> counts) {
    Map<String, Long> out = new java.util.LinkedHashMap<>();
    for (String s : List.of("DRAFT", "SCHEDULED", "PUBLISHED", "ARCHIVED"))
      out.put(s, counts.getOrDefault(s, 0L));
    return out;
  }

  // ---- bulk status and quick edit --------------------------------------------------------------

  @Override
  public Mono<PostListResponses.BulkResult> bulkStatus(Long siteId, List<Long> ids, String action) {
    return Mono.defer(
        () -> {
          String target = targetOf(action);
          return run(ids, id -> changeStatus(siteId, id, target));
        });
  }

  private static String targetOf(String action) {
    if (action == null) throw fail("Choose an action");
    return switch (action.trim().toUpperCase()) {
      case "PUBLISH" -> "PUBLISHED";
      case "UNPUBLISH" -> "DRAFT";
      case "ARCHIVE" -> "ARCHIVED";
      default -> throw fail("Unknown action: " + action);
    };
  }

  private Mono<Outcome> changeStatus(Long siteId, Long id, String status) {
    return findPage(siteId, id)
        .flatMap(
            item -> {
              String title = titleOf(item);
              // Publishing a live page that has saved edits makes them live; taking it off the site
              // keeps them as its content.
              boolean applyDraft = item.getDraft() != null;
              if (status.equals(item.getStatus()) && !applyDraft)
                return Mono.just(new Outcome(id, null));
              StatusRules.requireAllowed(item.getContentType(), status);
              if (applyDraft) {
                item.setData(item.getDraft());
                item.setDraft(null);
              }
              ScheduleRules.Times times =
                  ScheduleRules.resolve(
                      status,
                      null,
                      null,
                      item.getStatus(),
                      item.getPublishAt(),
                      item.getUnpublishAt(),
                      Instant.now());
              item.setStatus(status);
              item.setPublishAt(times.publishAt());
              item.setUnpublishAt(times.unpublishAt());
              return writer
                  .save(item)
                  .thenReturn(new Outcome(id, null))
                  .onErrorResume(
                      ex -> !(ex instanceof ApplicationException),
                      ex -> Mono.just(failed(id, title, "It could not be changed")));
            });
  }

  @Override
  public Mono<PageResponses.Row> quickEdit(Long siteId, Long id, PageQuickEditRequest request) {
    return findPage(siteId, id)
        .flatMap(
            item -> {
              StaleEdit.check(item, request.getVersion());
              String status =
                  request.getStatus() == null || request.getStatus().isBlank()
                      ? item.getStatus()
                      : request.getStatus();
              // A page being taken off the site keeps its saved edits as its content: the edit goes
              // into those.
              boolean offLiveWithDraft = item.getDraft() != null && !"PUBLISHED".equals(status);
              ObjectNode data = (offLiveWithDraft ? item.getDraft() : item.getData()).deepCopy();
              if (request.getTitle() != null) {
                String title = request.getTitle().trim();
                if (title.isEmpty()) throw fail("The title cannot be empty");
                data.put(PageTypes.titleKey(item.getContentType()), title);
              }
              if (request.getSlug() != null && !request.getSlug().isBlank()) {
                if (PageTypes.ABOUT.equals(item.getContentType())) {
                  throw fail("The About page lives at /about and has no address to change");
                }
                String slug = Slugs.slugify(request.getSlug(), null);
                if (slug == null)
                  throw fail("Enter an address made of letters, numbers and hyphens");
                data.put("slug", slug);
              }
              if ("SCHEDULED".equals(status) && !"SCHEDULED".equals(item.getStatus())) {
                throw fail("Scheduling needs a time: use the page editor");
              }
              ContentItemUpdateRequest update = new ContentItemUpdateRequest();
              update.setId(id);
              update.setContentType(item.getContentType());
              update.setData(data);
              update.setStatus(status);
              update.setPublishAt(item.getPublishAt());
              update.setUnpublishAt(item.getUnpublishAt());
              update.setVersion(item.getVersion());
              update.setKeepDraft(!offLiveWithDraft);
              Mono<ContentItem> saved = contentItemService.update(siteId, update);
              if (item.getDraft() == null || offLiveWithDraft) return saved;
              // A live page with saved edits: the same change goes into the draft, so publishing it
              // later does not undo this.
              return saved.flatMap(
                  live -> {
                    ObjectNode draft = item.getDraft().deepCopy();
                    if (request.getTitle() != null)
                      draft.set(
                          PageTypes.titleKey(item.getContentType()),
                          data.get(PageTypes.titleKey(item.getContentType())));
                    if (data.hasNonNull("slug")
                        && request.getSlug() != null
                        && !request.getSlug().isBlank()) draft.set("slug", data.get("slug"));
                    ContentItemUpdateRequest forDraft = new ContentItemUpdateRequest();
                    forDraft.setId(id);
                    forDraft.setContentType(item.getContentType());
                    forDraft.setData(draft);
                    forDraft.setStatus(live.getStatus());
                    forDraft.setVersion(live.getVersion());
                    forDraft.setSaveAsDraft(true);
                    return "PUBLISHED".equals(live.getStatus())
                        ? contentItemService.update(siteId, forDraft)
                        : Mono.just(live);
                  });
            })
        .flatMap(saved -> rowOf(siteId, saved.getId()));
  }

  private Mono<PageResponses.Row> rowOf(Long siteId, Long id) {
    return pages
        .page(siteId, new PageRepository.Filter(null, null, null, null, null, null), null, 1, 0)
        .flatMap(
            r ->
                r.rows().stream()
                    .filter(row -> row.id().equals(id))
                    .findFirst()
                    .map(Mono::just)
                    .orElse(Mono.empty()));
  }

  // ---- Trash -----------------------------------------------------------------------------------

  @Override
  public Mono<PostListResponses.BulkResult> trash(Long siteId, List<Long> ids) {
    return run(
        ids,
        id ->
            findPage(siteId, id)
                .flatMap(
                    item -> {
                      String title = titleOf(item);
                      if (PageTypes.ABOUT.equals(item.getContentType())) {
                        return Mono.just(
                            failed(
                                id,
                                title,
                                "The About page cannot be moved to the Trash: every site has exactly one"));
                      }
                      return actor()
                          .flatMap(who -> trash.trash(siteId, id, who))
                          .map(
                              moved ->
                                  moved
                                      ? new Outcome(id, null)
                                      : failed(id, title, "This page no longer exists"));
                    }));
  }

  @Override
  public Mono<PostListResponses.BulkResult> restore(
      Long siteId, List<Long> ids, Map<Long, String> slugs) {
    return run(
        ids,
        id ->
            trash
                .find(siteId, id)
                .filter(t -> PageTypes.is(t.contentType()))
                .switchIfEmpty(Mono.error(fail("This page is not in the Trash")))
                .flatMap(
                    t -> {
                      String typed = slugs == null ? null : slugs.get(id);
                      String slug =
                          typed == null || typed.isBlank() ? null : Slugs.slugify(typed, null);
                      return actor()
                          .flatMap(who -> trash.restore(siteId, id, who, slug))
                          .map(restored -> new Outcome(id, null))
                          .switchIfEmpty(
                              Mono.just(failed(id, t.title(), "This page is not in the Trash")))
                          .onErrorResume(
                              ex ->
                                  PostTrashServiceImpl.isSlugConflict(ex)
                                      ? Mono.just(
                                          failed(
                                              id,
                                              t.title(),
                                              "Its address /"
                                                  + t.slug()
                                                  + " is already used: give it another address and restore again"))
                                      : Mono.error(ex));
                    }));
  }

  @Override
  public Mono<PostListResponses.BulkResult> deleteForever(Long siteId, List<Long> ids) {
    return run(
        ids,
        id ->
            trash
                .find(siteId, id)
                .filter(t -> PageTypes.is(t.contentType()))
                .switchIfEmpty(Mono.error(fail("This page is not in the Trash")))
                .flatMap(
                    t ->
                        actor()
                            .flatMap(who -> trash.deleteForever(siteId, List.of(id), who))
                            .map(
                                n ->
                                    n > 0
                                        ? new Outcome(id, null)
                                        : failed(id, t.title(), "This page is not in the Trash"))));
  }

  @Override
  public Mono<PageResponses.TrashPage> trashList(
      Long siteId, String q, Integer page, Integer size) {
    int pageNo = page == null ? 1 : page;
    int pageSize = size == null ? DEFAULT_SIZE : size;
    if (pageNo < 1) throw fail("page starts at 1");
    if (pageSize < 1 || pageSize > MAX_SIZE) throw fail("size must be between 1 and " + MAX_SIZE);
    return pages.trashPage(siteId, q, pageNo, pageSize);
  }

  @Override
  public Mono<List<TrashResponses.LinkCheck>> linkCheck(Long siteId, List<Long> ids) {
    List<Long> unique = ids.stream().distinct().toList();
    return Mono.zip(
            pages.referrers(siteId, unique, MAX_REFERRER_ROWS),
            Flux.fromIterable(unique)
                .concatMap(
                    id ->
                        items
                            .findByIdAndSiteId(id, siteId)
                            .filter(i -> PageTypes.is(i.getContentType())))
                .collectList())
        .map(
            t -> {
              List<TrashResponses.LinkCheck> out = new ArrayList<>();
              for (ContentItem page : t.getT2()) {
                List<TrashResponses.Referrer> refs =
                    new ArrayList<>(t.getT1().getOrDefault(page.getId(), List.of()));
                // A published Service page is in the header's Services menu: trashing it takes it
                // out.
                if (PageTypes.SERVICE.equals(page.getContentType())
                    && "PUBLISHED".equals(page.getStatus())) {
                  refs.add(
                      0, new TrashResponses.Referrer(0L, "menu", "PUBLISHED", "the Services menu"));
                }
                if (!refs.isEmpty()) {
                  out.add(
                      new TrashResponses.LinkCheck(
                          page.getId(),
                          titleOf(page),
                          refs.size(),
                          refs.stream().limit(MAX_REFERRERS_LISTED).toList()));
                }
              }
              return out;
            });
  }

  // ---- helpers ---------------------------------------------------------------------------------

  private Mono<ContentItem> findPage(Long siteId, Long id) {
    return items
        .findByIdAndSiteId(id, siteId)
        .filter(i -> PageTypes.is(i.getContentType()))
        .switchIfEmpty(Mono.error(fail("This page no longer exists")));
  }

  private static String titleOf(ContentItem item) {
    return item.getData().path(PageTypes.titleKey(item.getContentType())).asText("");
  }

  private Mono<User> actor() {
    return auditor.getCurrentAuditor();
  }

  private Mono<PostListResponses.BulkResult> run(
      List<Long> ids, Function<Long, Mono<Outcome>> action) {
    return Flux.fromIterable(ids.stream().distinct().toList())
        .concatMap(
            id ->
                action
                    .apply(id)
                    .onErrorResume(
                        ApplicationException.class,
                        ex ->
                            Mono.just(
                                failed(
                                    id,
                                    null,
                                    ex.getArgs() != null
                                            && ex.getArgs().length > 0
                                            && ex.getArgs()[0] != null
                                        ? ex.getArgs()[0].toString()
                                        : "It could not be changed"))))
        .collectList()
        .map(
            outcomes ->
                new PostListResponses.BulkResult(
                    outcomes.stream().filter(o -> o.failure() == null).map(Outcome::id).toList(),
                    outcomes.stream().map(Outcome::failure).filter(Objects::nonNull).toList()));
  }

  private static Outcome failed(Long id, String title, String reason) {
    return new Outcome(id, new PostListResponses.Failure(id, title, reason));
  }

  private static String blankToNull(String s) {
    return s == null || s.isBlank() ? null : s.trim();
  }

  private static ApplicationException fail(String message) {
    return new ApplicationException(Message.Application.ERROR, message);
  }
}
