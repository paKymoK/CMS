package com.takypok.contentservice.service.impl;

import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.model.response.PostListResponses;
import com.takypok.contentservice.model.response.TrashResponses;
import com.takypok.contentservice.repository.ContentItemRepository;
import com.takypok.contentservice.repository.TrashRepository;
import com.takypok.contentservice.service.PostTrashService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import com.takypok.core.model.authentication.User;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PostTrashServiceImpl implements PostTrashService {
  static final String TYPE = "post";
  private static final int DEFAULT_SIZE = 20;
  private static final int MAX_SIZE = 100;

  private final TrashRepository trash;
  private final ContentItemRepository items;
  private final ReactiveAuditorAware<User> auditor;

  private record Outcome(Long id, PostListResponses.Failure failure) {}

  @Override
  public Mono<PostListResponses.BulkResult> trash(Long siteId, List<Long> ids) {
    return run(
        ids,
        id ->
            items
                .findByIdAndSiteId(id, siteId)
                .filter(item -> TYPE.equals(item.getContentType()))
                .switchIfEmpty(Mono.error(fail("This post no longer exists")))
                .flatMap(
                    item -> {
                      String title = titleOf(item);
                      if ("PUBLISHED".equals(item.getStatus())) {
                        return Mono.just(
                            failed(
                                id,
                                title,
                                "A published post cannot be moved to Trash: archive it or move it"
                                    + " to draft first"));
                      }
                      return actor()
                          .flatMap(who -> trash.trash(siteId, id, who))
                          .map(
                              moved ->
                                  moved
                                      ? new Outcome(id, null)
                                      : failed(id, title, "This post no longer exists"));
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
                .filter(t -> TYPE.equals(t.contentType()))
                .switchIfEmpty(Mono.error(fail("This post is not in the Trash")))
                .flatMap(
                    t ->
                        Mono.fromSupplier(() -> chosenSlug(slugs, id))
                            .flatMap(
                                slug ->
                                    actor()
                                        .flatMap(
                                            who ->
                                                trash.restore(siteId, id, who, slug.orElse(null))))
                            .map(restored -> new Outcome(id, null))
                            .switchIfEmpty(
                                Mono.just(failed(id, t.title(), "This post is not in the Trash")))
                            .onErrorResume(
                                ex ->
                                    isSlugConflict(ex)
                                        ? Mono.just(
                                            failed(
                                                id,
                                                t.title(),
                                                "Its address /insights/"
                                                    + t.slug()
                                                    + " is already used: restore it under another"
                                                    + " address"))
                                        : Mono.error(ex))));
  }

  @Override
  public Mono<PostListResponses.BulkResult> deleteForever(Long siteId, List<Long> ids) {
    return run(
        ids,
        id ->
            trash
                .find(siteId, id)
                .filter(t -> TYPE.equals(t.contentType()))
                .switchIfEmpty(Mono.error(fail("This post is not in the Trash")))
                .flatMap(
                    t ->
                        actor()
                            .flatMap(who -> trash.deleteForever(siteId, List.of(id), who))
                            .map(
                                n ->
                                    n > 0
                                        ? new Outcome(id, null)
                                        : failed(id, t.title(), "This post is not in the Trash"))));
  }

  @Override
  public Mono<TrashResponses.Page> list(Long siteId, String q, Integer page, Integer size) {
    int p = page == null ? 1 : page;
    int s = size == null ? DEFAULT_SIZE : size;
    if (p < 1) throw new ApplicationException(Message.Application.ERROR, "page starts at 1");
    if (s < 1 || s > MAX_SIZE) {
      throw new ApplicationException(
          Message.Application.ERROR, "size must be between 1 and " + MAX_SIZE);
    }
    return trash.page(siteId, TYPE, q, p, s);
  }

  @Override
  public Mono<List<TrashResponses.LinkCheck>> linkCheck(Long siteId, List<Long> ids) {
    List<Long> unique = ids.stream().distinct().toList();
    return trash
        .linksTo(siteId, unique)
        .collectList()
        .flatMap(
            links ->
                links.isEmpty()
                    ? Mono.just(List.<TrashResponses.LinkCheck>of())
                    : Flux.fromIterable(links)
                        .concatMap(
                            link ->
                                items
                                    .findByIdAndSiteId(link.id(), siteId)
                                    .map(
                                        item ->
                                            new TrashResponses.LinkCheck(
                                                link.id(),
                                                titleOf(item),
                                                link.referrerCount(),
                                                link.referrers())))
                        .collectList());
  }

  @Override
  public Mono<List<TrashResponses.RestoreCheck>> restoreCheck(Long siteId, List<Long> ids) {
    return Flux.fromIterable(ids.stream().distinct().toList())
        .concatMap(
            id ->
                trash
                    .find(siteId, id)
                    .filter(t -> TYPE.equals(t.contentType()) && t.slug() != null)
                    .flatMap(
                        t ->
                            trash
                                .slugHolder(siteId, t.slug())
                                .flatMap(
                                    holder ->
                                        trash
                                            .freeSlugLike(siteId, t.slug())
                                            .map(
                                                free ->
                                                    new TrashResponses.RestoreCheck(
                                                        id, t.title(), t.slug(), holder, free))
                                            .defaultIfEmpty(
                                                new TrashResponses.RestoreCheck(
                                                    id, t.title(), t.slug(), holder, null)))
                                .switchIfEmpty(
                                    Mono.just(
                                        new TrashResponses.RestoreCheck(
                                            id, t.title(), t.slug(), null, null)))))
        .collectList();
  }

  // ---- helpers ------------------------------------------------------------------------------

  /** One at a time, so a failure of one does not stop the rest. */
  private Mono<PostListResponses.BulkResult> run(
      List<Long> ids, Function<Long, Mono<Outcome>> action) {
    return Flux.fromIterable(ids.stream().distinct().toList())
        .concatMap(
            id ->
                action
                    .apply(id)
                    .onErrorResume(
                        ex ->
                            Mono.just(
                                failed(
                                    id,
                                    null,
                                    ex instanceof ApplicationException app
                                            && app.getArgs() != null
                                            && app.getArgs().length > 0
                                            && app.getArgs()[0] != null
                                        ? app.getArgs()[0]
                                        : "It could not be changed"))))
        .collectList()
        .map(
            outcomes ->
                new PostListResponses.BulkResult(
                    outcomes.stream().filter(o -> o.failure() == null).map(Outcome::id).toList(),
                    outcomes.stream().map(Outcome::failure).filter(Objects::nonNull).toList()));
  }

  /** The address typed for the post, in slug form; empty when none was given. */
  private static java.util.Optional<String> chosenSlug(Map<Long, String> slugs, Long id) {
    String typed = slugs == null ? null : slugs.get(id);
    if (typed == null || typed.isBlank()) return java.util.Optional.empty();
    String slug = Slugs.slugify(typed, null);
    if (slug == null) throw fail("Enter an address made of letters, numbers and hyphens");
    return java.util.Optional.of(slug);
  }

  private Mono<User> actor() {
    return auditor.getCurrentAuditor();
  }

  private static Outcome failed(Long id, String title, String reason) {
    return new Outcome(id, new PostListResponses.Failure(id, title, reason));
  }

  private static ApplicationException fail(String message) {
    return new ApplicationException(Message.Application.ERROR, message);
  }

  private static String titleOf(ContentItem item) {
    return item.getData() == null ? null : item.getData().path("title").asText(null);
  }

  /** The schema reports a taken address as "Slug already in use". */
  static boolean isSlugConflict(Throwable ex) {
    for (Throwable t = ex; t != null; t = t.getCause()) {
      if (t.getMessage() != null && t.getMessage().contains("Slug already in use")) return true;
      if (t.getCause() == t) break;
    }
    return false;
  }
}
