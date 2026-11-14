package com.takypok.contentservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.takypok.contentservice.audit.ContentItemWriter;
import com.takypok.contentservice.model.TaxonomyKind;
import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.model.mapper.ContentItemMapper;
import com.takypok.contentservice.model.request.TaxonomyRequest;
import com.takypok.contentservice.model.response.PostListResponses;
import com.takypok.contentservice.model.response.TaxonomyResponses;
import com.takypok.contentservice.repository.ContentItemRepository;
import com.takypok.contentservice.repository.TaxonomyRepository;
import com.takypok.contentservice.repository.TrashRepository;
import com.takypok.contentservice.service.SlugRegistry;
import com.takypok.contentservice.service.TaxonomyService;
import com.takypok.contentservice.util.StaleEdit;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import com.takypok.core.model.authentication.User;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TaxonomyServiceImpl implements TaxonomyService {
  static final String POST = "post";
  static final String UNCATEGORIZED = "uncategorized";

  /** A post carries at most this many categories and tags together. */
  static final int MAX_PER_POST = 15;

  private static final int DEFAULT_SIZE = 20;
  private static final int MAX_SIZE = 100;

  private final ContentItemRepository items;
  private final ContentItemMapper mapper;
  private final ContentItemWriter writer;
  private final ObjectMapper objectMapper;
  private final TaxonomyRepository repository;
  private final TrashRepository trash;
  private final SlugRegistry slugRegistry;
  private final ReactiveAuditorAware<User> auditor;
  private final TransactionalOperator transaction;

  // ---- reading -------------------------------------------------------------------------------

  @Override
  public Mono<TaxonomyResponses.Page> list(
      Long siteId,
      TaxonomyKind kind,
      String q,
      String status,
      Long parentId,
      String sort,
      Integer page,
      Integer size) {
    return Mono.zip(
            ensureDefaults(siteId, kind).then(allOf(siteId, kind)),
            repository.publishedCounts(siteId, kind),
            trash.count(siteId, kind.type()))
        .map(
            t -> {
              List<ContentItem> all = t.getT1();
              Map<Long, Long> counts = t.getT2();
              Map<Long, String> names = namesOf(all);
              String needle = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
              List<TaxonomyResponses.Row> rows =
                  all.stream()
                      .map(i -> row(i, names, counts))
                      .filter(
                          r ->
                              needle.isEmpty()
                                  || r.name().toLowerCase(Locale.ROOT).contains(needle)
                                  || r.slug().toLowerCase(Locale.ROOT).contains(needle))
                      .filter(
                          r ->
                              status == null
                                  || status.isBlank()
                                  || status.equalsIgnoreCase(r.status()))
                      .filter(r -> parentId == null || parentId.equals(r.parentId()))
                      .sorted(comparator(sort))
                      .toList();
              Map<String, Long> statusCounts = new LinkedHashMap<>();
              statusCounts.put(
                  "ACTIVE", all.stream().filter(TaxonomyServiceImpl::isActive).count());
              statusCounts.put("INACTIVE", all.stream().filter(i -> !isActive(i)).count());
              int pageSize =
                  size == null
                      ? DEFAULT_SIZE
                      : size <= 0 ? Integer.MAX_VALUE : Math.min(size, MAX_SIZE);
              int pageNo = Math.max(1, page == null ? 1 : page);
              int totalPages = Math.max(1, (int) Math.ceil(rows.size() / (double) pageSize));
              pageNo = Math.min(pageNo, totalPages);
              int from = (int) Math.min((long) (pageNo - 1) * pageSize, rows.size());
              int to = (int) Math.min((long) from + pageSize, rows.size());
              return new TaxonomyResponses.Page(
                  rows.subList(from, to),
                  pageNo,
                  pageSize == Integer.MAX_VALUE ? 0 : pageSize,
                  rows.size(),
                  totalPages,
                  statusCounts,
                  t.getT3());
            });
  }

  @Override
  public Mono<TaxonomyResponses.Row> get(Long siteId, TaxonomyKind kind, Long id) {
    return find(siteId, kind, id).flatMap(item -> rowOf(siteId, kind, item));
  }

  private Mono<TaxonomyResponses.Row> rowOf(Long siteId, TaxonomyKind kind, ContentItem item) {
    return Mono.zip(allOf(siteId, kind), repository.publishedCounts(siteId, kind))
        .map(t -> row(item, namesOf(t.getT1()), t.getT2()));
  }

  private static Comparator<TaxonomyResponses.Row> comparator(String sort) {
    String s = sort == null ? "name_asc" : sort;
    Comparator<TaxonomyResponses.Row> byName =
        Comparator.comparing(r -> r.name().toLowerCase(Locale.ROOT));
    Comparator<TaxonomyResponses.Row> byUpdated =
        Comparator.comparing(
            TaxonomyResponses.Row::updatedAt, Comparator.nullsFirst(Comparator.naturalOrder()));
    Comparator<TaxonomyResponses.Row> byCount =
        Comparator.comparingLong(TaxonomyResponses.Row::publishedCount);
    return switch (s) {
      case "name_desc" -> byName.reversed();
      case "updated_desc" -> byUpdated.reversed().thenComparing(byName);
      case "updated_asc" -> byUpdated.thenComparing(byName);
      case "posts_desc" -> byCount.reversed().thenComparing(byName);
      case "posts_asc" -> byCount.thenComparing(byName);
      default -> byName;
    };
  }

  private static TaxonomyResponses.Row row(
      ContentItem item, Map<Long, String> names, Map<Long, Long> counts) {
    JsonNode d = item.getData();
    Long parentId = d.hasNonNull("parentId") ? d.get("parentId").asLong() : null;
    ZonedDateTime when = item.getModifiedAt() != null ? item.getModifiedAt() : item.getCreatedAt();
    User who = item.getModifiedBy() != null ? item.getModifiedBy() : item.getCreatedBy();
    return new TaxonomyResponses.Row(
        item.getId(),
        d.path("name").asText(""),
        d.path("slug").asText(""),
        d.hasNonNull("description") ? d.get("description").asText() : null,
        parentId,
        parentId == null ? null : names.get(parentId),
        isActive(item) ? "ACTIVE" : "INACTIVE",
        counts.getOrDefault(item.getId(), 0L),
        UNCATEGORIZED.equals(d.path("slug").asText())
            && "post_category".equals(item.getContentType()),
        when == null ? null : when.toInstant(),
        who == null ? null : label(who),
        item.getVersion());
  }

  /** "Display Name (sub)", as the audit trail writes a person. */
  private static String label(User who) {
    String name = who.getName();
    String sub = who.getSub();
    if (name == null || name.isBlank()) return sub;
    if (sub == null || "system".equals(sub) || name.equals(sub)) return name;
    return name + " (" + sub + ")";
  }

  private static boolean isActive(ContentItem item) {
    return "PUBLISHED".equals(item.getStatus());
  }

  private static Map<Long, String> namesOf(List<ContentItem> all) {
    Map<Long, String> names = new LinkedHashMap<>();
    all.forEach(i -> names.put(i.getId(), i.getData().path("name").asText("")));
    return names;
  }

  private Mono<List<ContentItem>> allOf(Long siteId, TaxonomyKind kind) {
    return items
        .findAllBySiteIdAndContentTypeOrderByDisplayOrderAsc(siteId, kind.type())
        .collectList();
  }

  private Mono<ContentItem> find(Long siteId, TaxonomyKind kind, Long id) {
    return items
        .findByIdAndSiteId(id, siteId)
        .filter(i -> kind.type().equals(i.getContentType()))
        .switchIfEmpty(Mono.error(fail("This " + kind.noun() + " no longer exists")));
  }

  // ---- create and edit -----------------------------------------------------------------------

  @Override
  public Mono<TaxonomyResponses.Row> create(
      Long siteId, TaxonomyKind kind, TaxonomyRequest request) {
    return Mono.defer(
        () -> {
          String name = request.getName().trim();
          return allOf(siteId, kind)
              .flatMap(
                  all -> {
                    requireUniqueName(all, name, null, kind);
                    Long parentId = parentFor(kind, all, null, request);
                    boolean parentActive = parentId == null || activeIn(all, parentId);
                    boolean active = wantsActive(request.getStatus(), parentActive, true);
                    ObjectNode data = objectMapper.createObjectNode();
                    data.put("name", name);
                    putOrRemove(data, "description", cleanDescription(request.getDescription()));
                    if (parentId != null) data.put("parentId", parentId);
                    return slugFor(siteId, kind, null, request.getSlug(), name)
                        .flatMap(
                            slug -> {
                              data.put("slug", slug);
                              ContentItem item = new ContentItem();
                              item.setSiteId(siteId);
                              item.setContentType(kind.type());
                              item.setStatus(active ? "PUBLISHED" : "DRAFT");
                              item.setDisplayOrder(all.size());
                              return save(kind, item, data);
                            });
                  })
              .flatMap(item -> get(siteId, kind, item.getId()));
        });
  }

  @Override
  public Mono<TaxonomyResponses.Row> update(
      Long siteId, TaxonomyKind kind, Long id, TaxonomyRequest request) {
    return Mono.defer(
            () ->
                find(siteId, kind, id)
                    .flatMap(
                        item ->
                            allOf(siteId, kind)
                                .flatMap(all -> applyEdit(siteId, kind, item, all, request))))
        .flatMap(item -> get(siteId, kind, item.getId()));
  }

  private Mono<ContentItem> applyEdit(
      Long siteId,
      TaxonomyKind kind,
      ContentItem item,
      List<ContentItem> all,
      TaxonomyRequest request) {
    StaleEdit.check(item, request.getVersion());
    ObjectNode data = item.getData().deepCopy();
    boolean isDefault = isDefault(item);
    String oldName = data.path("name").asText("");
    String name = request.getName().trim();
    requireUniqueName(all, name, item.getId(), kind);
    boolean nameChanged = !name.equals(oldName);
    data.put("name", name);
    if (request.getDescription() != null)
      putOrRemove(data, "description", cleanDescription(request.getDescription()));
    String requestedSlug = request.getSlug();
    if (isDefault
        && requestedSlug != null
        && !requestedSlug.isBlank()
        && !UNCATEGORIZED.equals(requestedSlug.trim())) {
      throw fail("The address of \"Uncategorized\" cannot be changed");
    }
    boolean wasActive = isActive(item);
    boolean wantActive = wasActive;
    if (request.getStatus() != null && !request.getStatus().isBlank()) {
      wantActive = "ACTIVE".equalsIgnoreCase(request.getStatus());
      if (!wantActive && isDefault) throw fail("\"Uncategorized\" must stay active");
    }
    if (kind == TaxonomyKind.CATEGORY && request.isParentIdSent()) {
      Long parentId = parentFor(kind, all, item, request);
      if (parentId == null) data.remove("parentId");
      else data.put("parentId", parentId);
      if (wantActive && parentId != null && !activeIn(all, parentId)) {
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
          throw fail("Activate the parent category first");
        }
        wantActive = false;
      }
    } else if (wantActive
        && !wasActive
        && kind == TaxonomyKind.CATEGORY
        && data.hasNonNull("parentId")
        && !activeIn(all, data.get("parentId").asLong())) {
      throw fail("Activate the parent category first");
    }
    final boolean active = wantActive;
    final boolean statusChanged = active != wasActive;
    String currentSlug = data.path("slug").asText("");
    boolean slugRequested =
        requestedSlug != null
            && !requestedSlug.isBlank()
            && !requestedSlug.trim().equals(currentSlug);
    Mono<String> slug =
        slugRequested
            ? slugFor(siteId, kind, item.getId(), requestedSlug, name)
            : Mono.just(currentSlug);
    return slug.flatMap(
        s -> {
          data.put("slug", s);
          item.setStatus(active ? "PUBLISHED" : "DRAFT");
          Mono<ContentItem> saved = save(kind, item, data);
          Mono<ContentItem> done =
              saved.flatMap(
                  it ->
                      (!active && statusChanged && kind == TaxonomyKind.CATEGORY
                              ? deactivateChildren(siteId, it)
                              : Mono.<Void>empty())
                          .thenReturn(it));
          return transaction.transactional(
              done.flatMap(
                  it ->
                      nameChanged || statusChanged
                          ? syncPosts(siteId, kind, it.getId()).thenReturn(it)
                          : Mono.just(it)));
        });
  }

  /**
   * A category switched off takes its sub-categories with it: a child can be Active only under an
   * Active parent.
   */
  private Mono<Void> deactivateChildren(Long siteId, ContentItem parent) {
    return allOf(siteId, TaxonomyKind.CATEGORY)
        .flatMapMany(Flux::fromIterable)
        .filter(
            c ->
                isActive(c)
                    && c.getData().hasNonNull("parentId")
                    && c.getData().get("parentId").asLong() == parent.getId())
        .concatMap(
            child -> {
              child.setStatus("DRAFT");
              return writer
                  .save(child)
                  .flatMap(c -> syncPosts(siteId, TaxonomyKind.CATEGORY, c.getId()));
            })
        .then();
  }

  private Long parentFor(
      TaxonomyKind kind, List<ContentItem> all, ContentItem self, TaxonomyRequest request) {
    if (kind != TaxonomyKind.CATEGORY || request.getParentId() == null) return null;
    Long parentId = request.getParentId();
    if (self != null && parentId.equals(self.getId()))
      throw fail("A category cannot be its own parent");
    ContentItem parent =
        all.stream()
            .filter(i -> i.getId().equals(parentId))
            .findFirst()
            .orElseThrow(() -> fail("The parent category no longer exists"));
    if (parent.getData().hasNonNull("parentId")) {
      throw fail("A sub-category cannot have its own sub-categories: choose a top-level parent");
    }
    if (UNCATEGORIZED.equals(parent.getData().path("slug").asText())) {
      throw fail("\"Uncategorized\" cannot have sub-categories");
    }
    if (self != null) {
      boolean hasChildren =
          all.stream()
              .anyMatch(
                  c ->
                      c.getData().hasNonNull("parentId")
                          && c.getData().get("parentId").asLong() == self.getId());
      if (hasChildren) throw fail("A category with sub-categories cannot become a sub-category");
      if (isDefault(self)) throw fail("\"Uncategorized\" cannot be a sub-category");
    }
    return parentId;
  }

  private static boolean activeIn(List<ContentItem> all, Long id) {
    return all.stream().anyMatch(i -> i.getId().equals(id) && isActive(i));
  }

  private static boolean wantsActive(String requested, boolean parentActive, boolean dflt) {
    if (requested == null || requested.isBlank()) return dflt && parentActive;
    boolean active = "ACTIVE".equalsIgnoreCase(requested);
    if (active && !parentActive) throw fail("Activate the parent category first");
    return active;
  }

  private static boolean isDefault(ContentItem item) {
    return "post_category".equals(item.getContentType())
        && UNCATEGORIZED.equals(item.getData().path("slug").asText());
  }

  private static void requireUniqueName(
      List<ContentItem> all, String name, Long selfId, TaxonomyKind kind) {
    boolean taken =
        all.stream()
            .anyMatch(
                i ->
                    !i.getId().equals(selfId)
                        && i.getData().path("name").asText("").equalsIgnoreCase(name));
    if (taken) throw fail("A " + kind.noun() + " named \"" + name + "\" already exists");
  }

  private static String cleanDescription(String description) {
    return description == null || description.isBlank() ? null : description.strip();
  }

  private static void putOrRemove(ObjectNode data, String key, String value) {
    if (value == null) data.remove(key);
    else data.put(key, value);
  }

  private Mono<String> slugFor(
      Long siteId, TaxonomyKind kind, Long selfId, String requested, String name) {
    return Slugs.resolve(
        requested, name, kind.noun(), slug -> slugTaken(siteId, kind, slug, selfId));
  }

  private Mono<Boolean> slugTaken(Long siteId, TaxonomyKind kind, String slug, Long selfId) {
    return items
        .findBySlug(siteId, kind.type(), slug)
        .filter(other -> selfId == null || !other.getId().equals(selfId))
        .hasElement()
        .flatMap(
            current ->
                current
                    ? Mono.just(true)
                    : slugRegistry.isAliasOfOther(siteId, kind.type(), "", slug, selfId));
  }

  private Mono<ContentItem> save(TaxonomyKind kind, ContentItem item, ObjectNode data) {
    return mapper
        .resolveAndValidate(kind.type(), data)
        .flatMap(
            valid -> {
              item.setData(valid);
              return writer.save(item);
            });
  }

  // ---- bulk actions --------------------------------------------------------------------------

  private record Outcome(Long id, PostListResponses.Failure failure) {}

  private Mono<PostListResponses.BulkResult> run(
      List<Long> ids, Function<Long, Mono<Outcome>> each) {
    return Flux.fromIterable(ids.stream().distinct().toList())
        .concatMap(
            id ->
                each.apply(id)
                    .onErrorResume(
                        ApplicationException.class, e -> Mono.just(failed(id, null, messageOf(e)))))
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

  private static String messageOf(ApplicationException e) {
    if (e.getArgs() != null && e.getArgs().length > 0 && e.getArgs()[0] != null) {
      return e.getArgs()[0].toString();
    }
    return "It could not be changed";
  }

  @Override
  public Mono<PostListResponses.BulkResult> setActive(
      Long siteId, TaxonomyKind kind, List<Long> ids, boolean active) {
    return run(
        ids,
        id ->
            find(siteId, kind, id)
                .flatMap(
                    item -> {
                      String name = item.getData().path("name").asText("");
                      if (isActive(item) == active) return Mono.just(new Outcome(id, null));
                      if (!active && isDefault(item)) {
                        return Mono.just(failed(id, name, "\"Uncategorized\" must stay active"));
                      }
                      return allOf(siteId, kind)
                          .flatMap(
                              all -> {
                                if (active
                                    && item.getData().hasNonNull("parentId")
                                    && !activeIn(all, item.getData().get("parentId").asLong())) {
                                  return Mono.just(
                                      failed(id, name, "Activate its parent category first"));
                                }
                                item.setStatus(active ? "PUBLISHED" : "DRAFT");
                                return transaction.transactional(
                                    writer
                                        .save(item)
                                        .flatMap(
                                            it ->
                                                (!active && kind == TaxonomyKind.CATEGORY
                                                        ? deactivateChildren(siteId, it)
                                                        : Mono.<Void>empty())
                                                    .then(syncPosts(siteId, kind, it.getId())))
                                        .thenReturn(new Outcome(id, null)));
                              });
                    }));
  }

  @Override
  public Mono<PostListResponses.BulkResult> removeFromPosts(
      Long siteId, TaxonomyKind kind, List<Long> ids) {
    return run(
        ids,
        id ->
            find(siteId, kind, id)
                .flatMap(
                    item ->
                        isDefault(item)
                            ? Mono.just(
                                failed(
                                    id,
                                    "Uncategorized",
                                    "Posts keep \"Uncategorized\" when they have no other category"))
                            : transaction.transactional(
                                rewritePostsWith(siteId, kind, id, true)
                                    .thenReturn(new Outcome(id, null)))));
  }

  @Override
  public Mono<PostListResponses.BulkResult> delete(Long siteId, TaxonomyKind kind, List<Long> ids) {
    return run(
        ids,
        id ->
            find(siteId, kind, id)
                .flatMap(
                    item -> {
                      String name = item.getData().path("name").asText("");
                      if (isDefault(item)) {
                        return Mono.just(
                            failed(
                                id,
                                name,
                                "\"Uncategorized\" cannot be deleted: it holds posts that have no other category"));
                      }
                      return transaction.transactional(
                          trashItem(siteId, kind, item).thenReturn(new Outcome(id, null)));
                    }));
  }

  /**
   * To the Trash: the posts that carry it (and a category's sub-categories) are noted in the item
   * first, so a restore can give them back; then the posts lose it. Nothing but the item is
   * deleted.
   */
  private Mono<Void> trashItem(Long siteId, TaxonomyKind kind, ContentItem item) {
    Mono<List<Long>> postIds = repository.postIdsWith(siteId, kind, item.getId()).collectList();
    Mono<List<Long>> childIds =
        kind == TaxonomyKind.CATEGORY
            ? allOf(siteId, kind)
                .map(
                    all ->
                        all.stream()
                            .filter(
                                c ->
                                    c.getData().hasNonNull("parentId")
                                        && c.getData().get("parentId").asLong() == item.getId())
                            .map(ContentItem::getId)
                            .toList())
            : Mono.just(List.of());
    return Mono.zip(postIds, childIds)
        .flatMap(
            t -> {
              ObjectNode data = item.getData().deepCopy();
              data.set("trashedPostIds", idArray(t.getT1()));
              if (kind == TaxonomyKind.CATEGORY) data.set("trashedChildIds", idArray(t.getT2()));
              Mono<Void> orphan =
                  Flux.fromIterable(t.getT2())
                      .concatMap(
                          cid ->
                              items
                                  .findById(cid)
                                  .flatMap(
                                      child -> {
                                        ObjectNode cd = child.getData().deepCopy();
                                        cd.remove("parentId");
                                        return save(kind, child, cd);
                                      }))
                      .then();
              return save(kind, item, data)
                  .then(orphan)
                  .then(actor())
                  .flatMap(who -> trash.trash(siteId, item.getId(), who))
                  .flatMap(
                      moved ->
                          moved
                              ? rewritePosts(
                                  siteId, t.getT1(), ids -> remove(ids, item.getId()), kind)
                              : Mono.error(fail("This " + kind.noun() + " no longer exists")));
            });
  }

  @Override
  public Mono<TaxonomyResponses.TrashPage> trashList(
      Long siteId, TaxonomyKind kind, String q, Integer page, Integer size) {
    int pageSize = size == null ? DEFAULT_SIZE : Math.max(1, Math.min(size, MAX_SIZE));
    return repository.trashPage(siteId, kind, q, Math.max(1, page == null ? 1 : page), pageSize);
  }

  @Override
  public Mono<PostListResponses.BulkResult> restore(
      Long siteId, TaxonomyKind kind, List<Long> ids, Map<Long, String> slugs) {
    return run(
        ids,
        id ->
            trash
                .find(siteId, id)
                .filter(t -> kind.type().equals(t.contentType()))
                .switchIfEmpty(Mono.error(fail("This " + kind.noun() + " is not in the Trash")))
                .flatMap(
                    t -> {
                      String slug = slugs == null ? null : slugs.get(id);
                      return actor()
                          .flatMap(
                              who ->
                                  transaction.transactional(
                                      trash
                                          .restore(
                                              siteId,
                                              id,
                                              who,
                                              slug == null || slug.isBlank() ? null : slugify(slug))
                                          .flatMap(r -> giveBack(siteId, kind, id))))
                          .thenReturn(new Outcome(id, null))
                          .onErrorResume(
                              ex ->
                                  PostTrashServiceImpl.isSlugConflict(ex)
                                      ? Mono.just(
                                          failed(
                                              id,
                                              t.title(),
                                              "Its address /"
                                                  + kind.noun()
                                                  + "/"
                                                  + t.slug()
                                                  + " is already used: restore it under another address"))
                                      : Mono.error(ex));
                    }));
  }

  /**
   * After a restore: the posts and sub-categories it had come back to it, and the notes are
   * cleared.
   */
  private Mono<Void> giveBack(Long siteId, TaxonomyKind kind, Long id) {
    return items
        .findById(id)
        .flatMap(
            item -> {
              JsonNode d = item.getData();
              List<Long> posts = longs(d.get("trashedPostIds"));
              List<Long> children = longs(d.get("trashedChildIds"));
              ObjectNode cleaned = d.deepCopy();
              cleaned.remove("trashedPostIds");
              cleaned.remove("trashedChildIds");
              Mono<Void> relink =
                  Flux.fromIterable(children)
                      .concatMap(
                          cid ->
                              items
                                  .findByIdAndSiteId(cid, siteId)
                                  .filter(c -> !c.getData().hasNonNull("parentId"))
                                  .flatMap(
                                      c -> {
                                        ObjectNode cd = c.getData().deepCopy();
                                        cd.put("parentId", id);
                                        return save(kind, c, cd);
                                      }))
                      .then();
              return save(kind, item, cleaned)
                  .then(relink)
                  .then(rewritePosts(siteId, posts, ids -> add(ids, id), kind));
            });
  }

  @Override
  public Mono<PostListResponses.BulkResult> deleteForever(
      Long siteId, TaxonomyKind kind, List<Long> ids) {
    return run(
        ids,
        id ->
            trash
                .find(siteId, id)
                .filter(t -> kind.type().equals(t.contentType()))
                .switchIfEmpty(Mono.error(fail("This " + kind.noun() + " is not in the Trash")))
                .flatMap(
                    t ->
                        actor()
                            .flatMap(who -> trash.deleteForever(siteId, List.of(id), who))
                            .map(
                                n ->
                                    n > 0
                                        ? new Outcome(id, null)
                                        : failed(
                                            id,
                                            t.title(),
                                            "This " + kind.noun() + " is not in the Trash"))));
  }

  // ---- what a post stores --------------------------------------------------------------------

  private record Maps(
      Map<Long, ContentItem> cats, Map<Long, ContentItem> tags, Long uncategorized) {}

  private Mono<Maps> maps(Long siteId) {
    return Mono.zip(
            allOf(siteId, TaxonomyKind.CATEGORY),
            allOf(siteId, TaxonomyKind.TAG),
            uncategorizedId(siteId))
        .map(
            t -> {
              Map<Long, ContentItem> cats = new LinkedHashMap<>();
              t.getT1().forEach(i -> cats.put(i.getId(), i));
              Map<Long, ContentItem> tags = new LinkedHashMap<>();
              t.getT2().forEach(i -> tags.put(i.getId(), i));
              return new Maps(cats, tags, t.getT3());
            });
  }

  /** The default category's id; made if the site has none yet. */
  private Mono<Long> uncategorizedId(Long siteId) {
    return items
        .findBySlug(siteId, TaxonomyKind.CATEGORY.type(), UNCATEGORIZED)
        .map(ContentItem::getId)
        .switchIfEmpty(
            Mono.defer(
                () -> {
                  ObjectNode data = objectMapper.createObjectNode();
                  data.put("name", "Uncategorized");
                  data.put("slug", UNCATEGORIZED);
                  data.put("description", "Posts that have no other category.");
                  ContentItem item = new ContentItem();
                  item.setSiteId(siteId);
                  item.setContentType(TaxonomyKind.CATEGORY.type());
                  item.setStatus("PUBLISHED");
                  item.setDisplayOrder(0);
                  return save(TaxonomyKind.CATEGORY, item, data)
                      .map(ContentItem::getId)
                      .onErrorResume(
                          e ->
                              items
                                  .findBySlug(siteId, TaxonomyKind.CATEGORY.type(), UNCATEGORIZED)
                                  .map(ContentItem::getId));
                }));
  }

  private Mono<Void> ensureDefaults(Long siteId, TaxonomyKind kind) {
    return kind == TaxonomyKind.CATEGORY ? uncategorizedId(siteId).then() : Mono.empty();
  }

  private static PostTaxonomy derive(List<Long> catIds, List<Long> tagIds, Maps m) {
    List<Long> cats =
        new ArrayList<>(new LinkedHashSet<>(catIds))
            .stream().filter(m.cats()::containsKey).toList();
    List<Long> tags =
        new ArrayList<>(new LinkedHashSet<>(tagIds))
            .stream().filter(m.tags()::containsKey).toList();
    // "Uncategorized" is only for a post with nothing else: it drops out once a real category is
    // there.
    if (cats.size() > 1) cats = cats.stream().filter(id -> !id.equals(m.uncategorized())).toList();
    if (cats.isEmpty()) cats = List.of(m.uncategorized());
    String category =
        cats.stream()
            .map(m.cats()::get)
            .filter(TaxonomyServiceImpl::isActive)
            .findFirst()
            .map(i -> i.getData().path("name").asText(""))
            .orElse("");
    List<String> names =
        tags.stream()
            .map(m.tags()::get)
            .filter(TaxonomyServiceImpl::isActive)
            .map(i -> i.getData().path("name").asText(""))
            .toList();
    return new PostTaxonomy(cats, tags, category, names);
  }

  @Override
  public Mono<PostTaxonomy> resolveForPost(
      Long siteId,
      List<Long> requestedCategories,
      List<Long> requestedTags,
      List<Long> storedCategories,
      List<Long> storedTags) {
    List<Long> storedCats = storedCategories == null ? List.of() : storedCategories;
    List<Long> storedTgs = storedTags == null ? List.of() : storedTags;
    return maps(siteId)
        .map(
            m -> {
              List<Long> cats =
                  requestedCategories == null ? storedCats : distinct(requestedCategories);
              List<Long> tags = requestedTags == null ? storedTgs : distinct(requestedTags);
              check(cats, storedCats, m.cats(), TaxonomyKind.CATEGORY);
              check(tags, storedTgs, m.tags(), TaxonomyKind.TAG);
              PostTaxonomy derived = derive(cats, tags, m);
              if (derived.categoryIds().size() + derived.tagIds().size() > MAX_PER_POST) {
                throw fail(
                    "A post can have at most " + MAX_PER_POST + " categories and tags together");
              }
              return derived;
            });
  }

  private static void check(
      List<Long> chosen, List<Long> stored, Map<Long, ContentItem> live, TaxonomyKind kind) {
    for (Long id : chosen) {
      ContentItem item = live.get(id);
      if (item == null) {
        if (stored.contains(id)) continue; // a stale id on the post is dropped by derive()
        throw fail("Unknown " + kind.noun() + " " + id);
      }
      if (!isActive(item) && !stored.contains(id)) {
        throw fail(
            "\""
                + item.getData().path("name").asText("")
                + "\" is inactive and cannot be added to a post");
      }
    }
  }

  private static List<Long> distinct(List<Long> ids) {
    return new ArrayList<>(new LinkedHashSet<>(ids));
  }

  // ---- rewriting posts -----------------------------------------------------------------------

  private Mono<Void> rewritePostsWith(Long siteId, TaxonomyKind kind, Long itemId, boolean remove) {
    return repository
        .postIdsWith(siteId, kind, itemId)
        .collectList()
        .flatMap(
            ids ->
                rewritePosts(
                    siteId, ids, list -> remove ? remove(list, itemId) : add(list, itemId), kind));
  }

  private Mono<Void> rewritePosts(
      Long siteId, List<Long> postIds, UnaryOperator<List<Long>> change, TaxonomyKind kind) {
    if (postIds.isEmpty()) return Mono.empty();
    return maps(siteId)
        .flatMapMany(
            m ->
                Flux.fromIterable(postIds)
                    .concatMap(
                        pid ->
                            items
                                .findByIdAndSiteId(pid, siteId)
                                .filter(p -> POST.equals(p.getContentType()))
                                .flatMap(p -> rewritePost(p, m, kind, change))))
        .then();
  }

  /**
   * Re-saves one post with its ids changed and its derived names recomputed; false when nothing
   * changed.
   */
  private Mono<Boolean> rewritePost(
      ContentItem post, Maps m, TaxonomyKind kind, UnaryOperator<List<Long>> change) {
    ObjectNode d = post.getData().deepCopy();
    List<Long> cats = longs(d.get("categoryIds"));
    List<Long> tags = longs(d.get("tagIds"));
    if (kind == TaxonomyKind.CATEGORY) cats = change.apply(cats);
    else tags = change.apply(tags);
    PostTaxonomy t = derive(cats, tags, m);
    ObjectNode next = d.deepCopy();
    next.set("categoryIds", idArray(t.categoryIds()));
    next.set("tagIds", idArray(t.tagIds()));
    next.put("category", t.category());
    ArrayNode names = next.putArray("tags");
    t.tags().forEach(names::add);
    if (next.equals(post.getData())) return Mono.just(false);
    return mapper
        .resolveAndValidate(POST, next)
        .flatMap(
            valid -> {
              post.setData(valid);
              return writer.save(post);
            })
        .thenReturn(true);
  }

  @Override
  public Mono<Void> syncPosts(Long siteId, TaxonomyKind kind, Long itemId) {
    return rewritePostsWith(siteId, kind, itemId, false);
  }

  @Override
  public Mono<PostListResponses.BulkResult> assignToPosts(
      Long siteId, TaxonomyKind kind, Long itemId, List<Long> postIds, boolean remove) {
    return find(siteId, kind, itemId)
        .flatMap(
            item -> {
              if (!remove && !isActive(item)) {
                return Mono.error(
                    fail(
                        "\""
                            + item.getData().path("name").asText("")
                            + "\" is inactive and cannot be added to a post"));
              }
              return maps(siteId)
                  .flatMap(
                      m ->
                          run(
                              postIds,
                              pid ->
                                  items
                                      .findByIdAndSiteId(pid, siteId)
                                      .filter(p -> POST.equals(p.getContentType()))
                                      .switchIfEmpty(Mono.error(fail("This post no longer exists")))
                                      .flatMap(
                                          p -> {
                                            String title = p.getData().path("title").asText("");
                                            List<Long> cats = longs(p.getData().get("categoryIds"));
                                            List<Long> tags = longs(p.getData().get("tagIds"));
                                            List<Long> own =
                                                kind == TaxonomyKind.CATEGORY ? cats : tags;
                                            if (!remove
                                                && !own.contains(itemId)
                                                && cats.size() + tags.size() >= MAX_PER_POST) {
                                              return Mono.just(
                                                  failed(
                                                      pid,
                                                      title,
                                                      "It already has "
                                                          + MAX_PER_POST
                                                          + " categories and tags"));
                                            }
                                            return rewritePost(
                                                    p,
                                                    m,
                                                    kind,
                                                    ids ->
                                                        remove
                                                            ? remove(ids, itemId)
                                                            : add(ids, itemId))
                                                .thenReturn(new Outcome(pid, null));
                                          })));
            });
  }

  // ---- public site ---------------------------------------------------------------------------

  @Override
  public Mono<List<TaxonomyResponses.PublicTerm>> publicList(Long siteId, TaxonomyKind kind) {
    return Mono.zip(allOf(siteId, kind), repository.publishedCounts(siteId, kind))
        .map(
            t -> {
              Map<Long, String> names = namesOf(t.getT1());
              return t.getT1().stream()
                  .filter(TaxonomyServiceImpl::isActive)
                  .filter(i -> !isDefault(i))
                  .map(i -> term(i, names, t.getT2()))
                  .sorted(Comparator.comparing(p -> p.name().toLowerCase(Locale.ROOT)))
                  .toList();
            });
  }

  @Override
  public Mono<TaxonomyResponses.PublicTerm> publicTerm(
      Long siteId, TaxonomyKind kind, String slug) {
    return items
        .findBySlug(siteId, kind.type(), slug)
        .filter(TaxonomyServiceImpl::isActive)
        .switchIfEmpty(Mono.error(fail("Not found")))
        .flatMap(
            item ->
                Mono.zip(allOf(siteId, kind), repository.publishedCounts(siteId, kind))
                    .map(t -> term(item, namesOf(t.getT1()), t.getT2())));
  }

  private static TaxonomyResponses.PublicTerm term(
      ContentItem i, Map<Long, String> names, Map<Long, Long> counts) {
    JsonNode d = i.getData();
    Long parentId = d.hasNonNull("parentId") ? d.get("parentId").asLong() : null;
    return new TaxonomyResponses.PublicTerm(
        i.getId(),
        d.path("name").asText(""),
        d.path("slug").asText(""),
        d.hasNonNull("description") ? d.get("description").asText() : null,
        parentId == null ? null : names.get(parentId),
        counts.getOrDefault(i.getId(), 0L));
  }

  // ---- small helpers -------------------------------------------------------------------------

  private Mono<User> actor() {
    return auditor.getCurrentAuditor();
  }

  private static List<Long> longs(JsonNode node) {
    List<Long> out = new ArrayList<>();
    if (node != null && node.isArray()) node.forEach(n -> out.add(n.asLong()));
    return out;
  }

  private ArrayNode idArray(List<Long> ids) {
    ArrayNode a = objectMapper.createArrayNode();
    ids.forEach(a::add);
    return a;
  }

  private static List<Long> remove(List<Long> ids, Long id) {
    List<Long> out = new ArrayList<>(ids);
    out.removeIf(id::equals);
    return out;
  }

  private static List<Long> add(List<Long> ids, Long id) {
    List<Long> out = new ArrayList<>(ids);
    if (!out.contains(id)) out.add(id);
    return out;
  }

  private static String slugify(String text) {
    return Slugs.slugify(text, "item");
  }

  private static ApplicationException fail(String message) {
    return new ApplicationException(Message.Application.ERROR, message);
  }
}
