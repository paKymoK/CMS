package com.takypok.contentservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.contentservice.audit.ContentItemWriter;
import com.takypok.contentservice.model.TaxonomyKind;
import com.takypok.contentservice.model.content.types.PostFields;
import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.model.mapper.ContentItemMapper;
import com.takypok.contentservice.model.request.PostCreateRequest;
import com.takypok.contentservice.model.request.PostUpdateRequest;
import com.takypok.contentservice.model.response.PostDetailResponse;
import com.takypok.contentservice.model.response.PostListResponses;
import com.takypok.contentservice.model.response.PostResponse;
import com.takypok.contentservice.model.response.PostSummaryResponse;
import com.takypok.contentservice.repository.ContentItemRepository;
import com.takypok.contentservice.search.SearchRepository;
import com.takypok.contentservice.service.PostService;
import com.takypok.contentservice.service.SearchService;
import com.takypok.contentservice.service.SlugRegistry;
import com.takypok.contentservice.service.TaxonomyService;
import com.takypok.contentservice.util.FieldRules;
import com.takypok.contentservice.util.RichTextSanitizer;
import com.takypok.contentservice.util.ScheduleRules;
import com.takypok.contentservice.util.StatusRules;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Posts are content_item rows of type "post"; this service keeps what is specific to them — slug
 * generation/uniqueness, rich-text sanitizing, layout validation, derived excerpt/date and the
 * "related" rail — on top of the generic table.
 */
@Component
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
  static final String TYPE = "post";
  private static final int MAX_RELATED = 3;
  private static final int MAX_SEARCH_HITS = 200;
  private static final Set<String> LAYOUTS = Set.of("default", "focused", "wide", "landing");
  private static final int EXCERPT_MAX = 200;
  private static final DateTimeFormatter DISPLAY_DATE =
      DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);

  private final ContentItemRepository contentItemRepository;
  private final ContentItemMapper contentItemMapper;
  private final ObjectMapper objectMapper;
  private final SearchService searchService;
  private final ContentItemWriter contentItemWriter;
  private final SlugRegistry slugRegistry;
  private final TaxonomyService taxonomyService;

  @Override
  public Mono<List<PostResponse>> getAll(Long siteId) {
    return contentItemRepository
        .findAllBySiteIdAndContentTypeOrderByDisplayOrderAsc(siteId, TYPE)
        .map(this::toResponse)
        .collectList();
  }

  @Override
  public Mono<PostResponse> getById(Long id, Long siteId) {
    return findItem(id, siteId).map(this::toResponse);
  }

  @Override
  public Mono<PostResponse> create(Long siteId, PostCreateRequest request) {
    // defer: sanitizeOrReject / validLayout throw, and that must surface as a Mono error, not at
    // assembly time
    return Mono.defer(
        () -> {
          StatusRules.requireAllowed(TYPE, request.getStatus());
          String status = request.getStatus() != null ? request.getStatus() : "DRAFT";
          ScheduleRules.Times times =
              ScheduleRules.resolve(
                  status,
                  request.getPublishAt(),
                  request.getUnpublishAt(),
                  null,
                  null,
                  null,
                  Instant.now());
          PostFields fields = new PostFields();
          fields.setLayout(validLayout(request.getLayout()));
          fields.setFeatured(request.getFeatured() != null ? request.getFeatured() : false);
          applySeo(
              fields,
              null,
              request.getSeoTitle(),
              request.getSeoDescription(),
              request.getSeoImage(),
              request.getSeoNoindex(),
              request.getSeoCanonical(),
              request.getSeoNofollow(),
              request.getSeoOgTitle(),
              request.getSeoOgDescription(),
              request.getSeoJsonLd());
          copyShared(
              fields,
              status,
              times.publishAt(),
              request.getImage(),
              request.getTitle(),
              request.getExcerpt(),
              request.getDate(),
              request.getBody(),
              request.getAuthorName(),
              request.getAuthorRole(),
              request.getAuthorBio(),
              request.getAuthorAvatar());
          return taxonomyService
              .resolveForPost(siteId, request.getCategoryIds(), request.getTagIds(), null, null)
              .flatMap(
                  taxonomy -> {
                    applyTaxonomy(fields, taxonomy);
                    return save(
                        siteId,
                        new ContentItem(),
                        request.getSlug(),
                        fields,
                        request.getDisplayOrder(),
                        status,
                        times);
                  });
        });
  }

  @Override
  public Mono<PostResponse> update(Long siteId, PostUpdateRequest request) {
    return findItem(request.getId(), siteId)
        .flatMap(
            item ->
                Mono.defer(
                    () -> {
                      StatusRules.requireAllowed(TYPE, request.getStatus());
                      PostFields existing = toFields(item);
                      String status =
                          request.getStatus() != null ? request.getStatus() : item.getStatus();
                      ScheduleRules.Times times =
                          ScheduleRules.resolve(
                              status,
                              request.getPublishAt(),
                              request.getUnpublishAt(),
                              item.getStatus(),
                              item.getPublishAt(),
                              item.getUnpublishAt(),
                              Instant.now());
                      PostFields fields = new PostFields();
                      fields.setLayout(
                          request.getLayout() != null
                              ? validLayout(request.getLayout())
                              : existing.getLayout());
                      fields.setFeatured(
                          request.getFeatured() != null
                              ? request.getFeatured()
                              : existing.getFeatured());
                      copyShared(
                          fields,
                          status,
                          times.publishAt(),
                          request.getImage(),
                          request.getTitle(),
                          request.getExcerpt(),
                          request.getDate(),
                          request.getBody(),
                          request.getAuthorName(),
                          request.getAuthorRole(),
                          request.getAuthorBio(),
                          request.getAuthorAvatar());
                      // Not sent by every client: left out keeps the stored value, blank clears it.
                      fields.setAuthorBio(
                          FieldRules.keepOrClear(request.getAuthorBio(), existing.getAuthorBio()));
                      fields.setAuthorAvatar(
                          FieldRules.keepOrClear(
                              request.getAuthorAvatar(), existing.getAuthorAvatar()));
                      applySeo(
                          fields,
                          existing,
                          request.getSeoTitle(),
                          request.getSeoDescription(),
                          request.getSeoImage(),
                          request.getSeoNoindex(),
                          request.getSeoCanonical(),
                          request.getSeoNofollow(),
                          request.getSeoOgTitle(),
                          request.getSeoOgDescription(),
                          request.getSeoJsonLd());
                      return taxonomyService
                          .resolveForPost(
                              siteId,
                              request.getCategoryIds(),
                              request.getTagIds(),
                              existing.getCategoryIds(),
                              existing.getTagIds())
                          .flatMap(
                              taxonomy -> {
                                applyTaxonomy(fields, taxonomy);
                                return save(
                                    siteId,
                                    item,
                                    request.getSlug(),
                                    fields,
                                    request.getDisplayOrder(),
                                    status,
                                    times);
                              });
                    }));
  }

  /**
   * The search-engine fields. On update a field the request leaves out keeps its stored value, and
   * a blank one clears it, so a client that does not know these fields never wipes them.
   */
  private static void applySeo(
      PostFields fields,
      PostFields existing,
      String seoTitle,
      String seoDescription,
      String seoImage,
      Boolean seoNoindex,
      String seoCanonical,
      Boolean seoNofollow,
      String seoOgTitle,
      String seoOgDescription,
      String seoJsonLd) {
    fields.setSeoTitle(
        FieldRules.keepOrClear(seoTitle, existing == null ? null : existing.getSeoTitle()));
    fields.setSeoDescription(
        FieldRules.keepOrClear(
            seoDescription, existing == null ? null : existing.getSeoDescription()));
    fields.setSeoImage(
        FieldRules.keepOrClear(seoImage, existing == null ? null : existing.getSeoImage()));
    fields.setSeoNoindex(
        seoNoindex != null
            ? seoNoindex
            : existing != null && Boolean.TRUE.equals(existing.getSeoNoindex()));
    fields.setSeoCanonical(
        FieldRules.keepOrClear(seoCanonical, existing == null ? null : existing.getSeoCanonical()));
    fields.setSeoNofollow(
        seoNofollow != null
            ? seoNofollow
            : existing != null && Boolean.TRUE.equals(existing.getSeoNofollow()));
    fields.setSeoOgTitle(
        FieldRules.keepOrClear(seoOgTitle, existing == null ? null : existing.getSeoOgTitle()));
    fields.setSeoOgDescription(
        FieldRules.keepOrClear(
            seoOgDescription, existing == null ? null : existing.getSeoOgDescription()));
    fields.setSeoJsonLd(
        FieldRules.keepOrClear(seoJsonLd, existing == null ? null : existing.getSeoJsonLd()));
  }

  /** The fields create and update treat identically: replaced wholesale from the request. */
  private static void copyShared(
      PostFields fields,
      String status,
      Instant publishAt,
      String image,
      String title,
      String excerpt,
      String date,
      String body,
      String authorName,
      String authorRole,
      String authorBio,
      String authorAvatar) {
    String cleanBody = RichTextSanitizer.sanitizeOrReject(body);
    fields.setImage(image);
    fields.setTitle(title);
    fields.setBody(cleanBody);
    fields.setExcerpt(excerptOrDerived(excerpt, cleanBody));
    fields.setDate(dateOrToday(date, status, publishAt));
    fields.setAuthorName(authorName);
    fields.setAuthorRole(authorRole);
    fields.setAuthorBio(authorBio);
    fields.setAuthorAvatar(authorAvatar);
  }

  /** The categories and tags a post stores: the ids, and the names derived from them. */
  private void applyTaxonomy(PostFields fields, TaxonomyService.PostTaxonomy taxonomy) {
    fields.setCategoryIds(taxonomy.categoryIds());
    fields.setTagIds(taxonomy.tagIds());
    fields.setCategory(taxonomy.category());
    var names = objectMapper.createArrayNode();
    taxonomy.tags().forEach(names::add);
    fields.setTags(names);
  }

  /** Resolves the slug, validates and persists {@code item} (new, or the row being updated). */
  private Mono<PostResponse> save(
      Long siteId,
      ContentItem item,
      String requestedSlug,
      PostFields fields,
      Integer displayOrder,
      String status,
      ScheduleRules.Times times) {
    return resolveSlug(siteId, item.getId(), requestedSlug, fields.getTitle())
        .flatMap(
            slug -> {
              fields.setSlug(slug);
              return contentItemMapper.resolveAndValidate(TYPE, objectMapper.valueToTree(fields));
            })
        .flatMap(
            data -> {
              item.setSiteId(siteId);
              item.setContentType(TYPE);
              item.setData(data);
              if (displayOrder != null) item.setDisplayOrder(displayOrder);
              else if (item.getDisplayOrder() == null) item.setDisplayOrder(0);
              item.setStatus(status);
              item.setPublishAt(times.publishAt());
              item.setUnpublishAt(times.unpublishAt());
              return contentItemWriter.save(item);
            })
        .map(this::toResponse);
  }

  private Mono<ContentItem> findItem(Long id, Long siteId) {
    return contentItemRepository
        .findByIdAndSiteId(id, siteId)
        .filter(item -> TYPE.equals(item.getContentType()))
        .switchIfEmpty(
            Mono.error(new ApplicationException(Message.Application.ERROR, "Post not found")));
  }

  private PostFields toFields(ContentItem item) {
    return objectMapper.convertValue(
        item.getData() != null ? item.getData() : objectMapper.createObjectNode(),
        PostFields.class);
  }

  private PostResponse toResponse(ContentItem item) {
    return PostResponse.from(item, toFields(item));
  }

  static String validLayout(String layout) {
    if (layout == null || layout.isBlank()) return "default";
    if (!LAYOUTS.contains(layout)) {
      throw new ApplicationException(Message.Application.ERROR, "Unknown layout: " + layout);
    }
    return layout;
  }

  /** The excerpt is optional: left blank, it is taken from the start of the body text. */
  private static String excerptOrDerived(String excerpt, String body) {
    if (excerpt != null && !excerpt.isBlank()) return excerpt;
    if (body == null || body.isBlank()) return null;
    String text = Jsoup.parseBodyFragment(body).body().text().trim();
    if (text.isEmpty()) return null;
    if (text.length() <= EXCERPT_MAX) return text;
    int cut = text.lastIndexOf(' ', EXCERPT_MAX);
    return text.substring(0, cut > EXCERPT_MAX / 2 ? cut : EXCERPT_MAX).trim() + "…";
  }

  /**
   * The display date is optional: a post published without one is dated today, and a scheduled one
   * the day it goes live.
   */
  private static String dateOrToday(String date, String status, Instant publishAt) {
    if (date != null && !date.isBlank()) return date;
    if ("SCHEDULED".equals(status) && publishAt != null) {
      return publishAt.atZone(ZoneOffset.UTC).toLocalDate().format(DISPLAY_DATE);
    }
    return "PUBLISHED".equals(status) ? LocalDate.now().format(DISPLAY_DATE) : date;
  }

  @Override
  public Mono<PostListResponses.BulkResult> bulkStatus(Long siteId, List<Long> ids, String action) {
    // deferred so a bad action is an error signal (a clean message), not an exception thrown here
    return Mono.defer(
            () -> {
              String target = bulkTarget(action);
              List<Long> unique = ids.stream().distinct().toList();
              // One at a time: each is a normal save (audit trail, version, validation), and a
              // failure
              // of one must not stop the rest.
              return Flux.fromIterable(unique)
                  .concatMap(id -> changeStatus(siteId, id, target))
                  .collectList();
            })
        .map(
            outcomes ->
                new PostListResponses.BulkResult(
                    outcomes.stream().filter(o -> o.failure() == null).map(Outcome::id).toList(),
                    outcomes.stream()
                        .map(Outcome::failure)
                        .filter(java.util.Objects::nonNull)
                        .toList()));
  }

  private record Outcome(Long id, PostListResponses.Failure failure) {}

  private static String bulkTarget(String action) {
    if (action == null) {
      throw new ApplicationException(Message.Application.ERROR, "Choose an action");
    }
    return switch (action.trim().toUpperCase()) {
      case "PUBLISH" -> "PUBLISHED";
      case "UNPUBLISH" -> "DRAFT";
      case "ARCHIVE" -> "ARCHIVED";
      default ->
          throw new ApplicationException(Message.Application.ERROR, "Unknown action: " + action);
    };
  }

  private Mono<Outcome> changeStatus(Long siteId, Long id, String status) {
    return findItem(id, siteId)
        .flatMap(
            item -> {
              String title = toFields(item).getTitle();
              if (status.equals(item.getStatus())) {
                return Mono.just(new Outcome(id, null)); // already there
              }
              return Mono.defer(
                      () -> {
                        StatusRules.requireAllowed(TYPE, status);
                        PostFields fields = toFields(item);
                        ScheduleRules.Times times =
                            ScheduleRules.resolve(
                                status,
                                null,
                                null,
                                item.getStatus(),
                                item.getPublishAt(),
                                item.getUnpublishAt(),
                                Instant.now());
                        fields.setDate(dateOrToday(fields.getDate(), status, times.publishAt()));
                        return save(siteId, item, fields.getSlug(), fields, null, status, times);
                      })
                  .map(saved -> new Outcome(id, null))
                  .onErrorResume(
                      ex ->
                          Mono.just(
                              new Outcome(
                                  id, new PostListResponses.Failure(id, title, reasonOf(ex)))));
            })
        .onErrorResume(
            ex ->
                Mono.just(new Outcome(id, new PostListResponses.Failure(id, null, reasonOf(ex)))));
  }

  /** The sentence to show for a failed item; anything unexpected is not leaked. */
  private static String reasonOf(Throwable ex) {
    if (ex instanceof ApplicationException app
        && app.getArgs() != null
        && app.getArgs().length > 0
        && app.getArgs()[0] != null) {
      return app.getArgs()[0];
    }
    return "It could not be changed";
  }

  @Override
  public Flux<PostResponse> getPublished(Long siteId) {
    return contentItemRepository
        .findAllBySiteIdAndContentTypeAndStatusOrderByDisplayOrderAsc(siteId, TYPE, "PUBLISHED")
        .map(this::toResponse);
  }

  @Override
  public Mono<PostDetailResponse> getPublishedDetailBySlug(Long siteId, String slug) {
    return detailOf(
        siteId,
        contentItemRepository
            .findBySlug(siteId, TYPE, slug)
            .filter(i -> "PUBLISHED".equals(i.getStatus())));
  }

  private Mono<PostDetailResponse> detailOf(Long siteId, Mono<ContentItem> lookup) {
    return lookup
        .switchIfEmpty(
            Mono.error(new ApplicationException(Message.Application.ERROR, "Post not found")))
        .map(this::toResponse)
        .flatMap(
            post ->
                getPublished(siteId)
                    .collectList()
                    .map(all -> relatedTo(post, all))
                    .map(related -> PostDetailResponse.from(post, related)));
  }

  @Override
  public Mono<List<PostSummaryResponse>> searchPublished(
      Long siteId, String tag, String q, boolean exact) {
    boolean hasQuery = q != null && !q.isBlank();
    Mono<Map<Long, Integer>> ranking =
        hasQuery
            ? searchService
                .resolve(siteId, List.of(TYPE), q, true, !exact, MAX_SEARCH_HITS)
                .map(PostServiceImpl::rankById)
            : Mono.just(Map.<Long, Integer>of());
    return ranking.flatMap(
        rank ->
            getPublished(siteId)
                .filter(post -> tag == null || tag.isBlank() || tagsContain(post, tag))
                .filter(post -> !hasQuery || rank.containsKey(post.id()))
                // With a query the best match comes first; without one, the stored order stands.
                .sort(hasQuery ? Comparator.comparingInt(post -> rank.get(post.id())) : (a, b) -> 0)
                .map(PostSummaryResponse::from)
                .collectList());
  }

  /** Position of each hit in the search's best-first order. */
  private static Map<Long, Integer> rankById(SearchService.Resolution resolution) {
    Map<Long, Integer> rank = new HashMap<>();
    for (SearchRepository.Hit hit : resolution.hits()) {
      rank.putIfAbsent(hit.id(), rank.size());
    }
    return rank;
  }

  @Override
  public Mono<List<PostSummaryResponse>> publishedWith(
      Long siteId, TaxonomyKind kind, Long taxonomyId) {
    return getPublished(siteId)
        .filter(
            post ->
                (kind == TaxonomyKind.CATEGORY ? post.categoryIds() : post.tagIds())
                    .contains(taxonomyId))
        .map(PostSummaryResponse::from)
        .collectList();
  }

  private boolean tagsContain(PostResponse post, String tag) {
    if (post.tags() == null || !post.tags().isArray()) {
      return false;
    }
    for (var node : post.tags()) {
      if (tag.equalsIgnoreCase(node.asText())) {
        return true;
      }
    }
    return false;
  }

  /** Same-category first (excluding self), backfilled with the next most-recent others. */
  private List<PostSummaryResponse> relatedTo(PostResponse post, List<PostResponse> published) {
    List<PostResponse> sameCategory = new ArrayList<>();
    List<PostResponse> others = new ArrayList<>();
    for (PostResponse candidate : published) {
      if (candidate.id().equals(post.id())) {
        continue;
      }
      if (candidate.category() != null && candidate.category().equals(post.category())) {
        sameCategory.add(candidate);
      } else {
        others.add(candidate);
      }
    }
    List<PostResponse> combined = new ArrayList<>(sameCategory);
    for (PostResponse candidate : others) {
      if (combined.size() >= MAX_RELATED) {
        break;
      }
      combined.add(candidate);
    }
    return combined.stream().limit(MAX_RELATED).map(PostSummaryResponse::from).toList();
  }

  private Mono<String> resolveSlug(
      Long siteId, Long excludeId, String requestedSlug, String title) {
    return Slugs.resolve(requestedSlug, title, "post", slug -> slugTaken(siteId, slug, excludeId));
  }

  private Mono<Boolean> slugTaken(Long siteId, String slug, Long excludeId) {
    return contentItemRepository
        .findBySlug(siteId, TYPE, slug)
        .filter(other -> excludeId == null || !other.getId().equals(excludeId))
        .hasElement()
        .flatMap(
            current ->
                current
                    ? Mono.just(true)
                    : slugRegistry.isAliasOfOther(siteId, TYPE, "", slug, excludeId));
  }
}
