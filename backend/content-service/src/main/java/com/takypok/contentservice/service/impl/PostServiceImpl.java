package com.takypok.contentservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.contentservice.model.content.types.PostFields;
import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.model.mapper.ContentItemMapper;
import com.takypok.contentservice.model.request.PostCreateRequest;
import com.takypok.contentservice.model.request.PostUpdateRequest;
import com.takypok.contentservice.model.response.PostDetailResponse;
import com.takypok.contentservice.model.response.PostResponse;
import com.takypok.contentservice.model.response.PostSummaryResponse;
import com.takypok.contentservice.repository.ContentItemRepository;
import com.takypok.contentservice.search.SearchRepository;
import com.takypok.contentservice.service.PostService;
import com.takypok.contentservice.service.SearchService;
import com.takypok.contentservice.util.RichTextSanitizer;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.time.LocalDate;
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
          String status = request.getStatus() != null ? request.getStatus() : "DRAFT";
          PostFields fields = new PostFields();
          fields.setCategory(request.getCategory() != null ? request.getCategory() : "insight");
          fields.setLayout(validLayout(request.getLayout()));
          fields.setFeatured(request.getFeatured() != null ? request.getFeatured() : false);
          copyShared(
              fields,
              status,
              request.getImage(),
              request.getTitle(),
              request.getExcerpt(),
              request.getDate(),
              request.getBody(),
              request.getAuthorName(),
              request.getAuthorRole(),
              request.getAuthorBio(),
              request.getAuthorAvatar(),
              request.getTags());
          return save(
              siteId,
              new ContentItem(),
              request.getSlug(),
              fields,
              request.getDisplayOrder(),
              request.getActive(),
              status);
        });
  }

  @Override
  public Mono<PostResponse> update(Long siteId, PostUpdateRequest request) {
    return findItem(request.getId(), siteId)
        .flatMap(
            item ->
                Mono.defer(
                    () -> {
                      PostFields existing = toFields(item);
                      String status =
                          request.getStatus() != null ? request.getStatus() : item.getStatus();
                      PostFields fields = new PostFields();
                      fields.setCategory(
                          request.getCategory() != null
                              ? request.getCategory()
                              : existing.getCategory());
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
                          request.getImage(),
                          request.getTitle(),
                          request.getExcerpt(),
                          request.getDate(),
                          request.getBody(),
                          request.getAuthorName(),
                          request.getAuthorRole(),
                          request.getAuthorBio(),
                          request.getAuthorAvatar(),
                          request.getTags());
                      return save(
                          siteId,
                          item,
                          request.getSlug(),
                          fields,
                          request.getDisplayOrder(),
                          request.getActive(),
                          request.getStatus());
                    }));
  }

  /** The fields create and update treat identically: replaced wholesale from the request. */
  private static void copyShared(
      PostFields fields,
      String status,
      String image,
      String title,
      String excerpt,
      String date,
      String body,
      String authorName,
      String authorRole,
      String authorBio,
      String authorAvatar,
      JsonNode tags) {
    String cleanBody = RichTextSanitizer.sanitizeOrReject(body);
    fields.setImage(image);
    fields.setTitle(title);
    fields.setBody(cleanBody);
    fields.setExcerpt(excerptOrDerived(excerpt, cleanBody));
    fields.setDate(dateOrToday(date, status));
    fields.setAuthorName(authorName);
    fields.setAuthorRole(authorRole);
    fields.setAuthorBio(authorBio);
    fields.setAuthorAvatar(authorAvatar);
    fields.setTags(tags);
  }

  /** Resolves the slug, validates and persists {@code item} (new, or the row being updated). */
  private Mono<PostResponse> save(
      Long siteId,
      ContentItem item,
      String requestedSlug,
      PostFields fields,
      Integer displayOrder,
      Boolean active,
      String status) {
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
              if (active != null) item.setActive(active);
              else if (item.getActive() == null) item.setActive(true);
              if (status != null) item.setStatus(status);
              else if (item.getStatus() == null) item.setStatus("DRAFT");
              return contentItemRepository.save(item);
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

  /** The display date is optional: a post published without one is dated today. */
  private static String dateOrToday(String date, String status) {
    if (date != null && !date.isBlank()) return date;
    return "PUBLISHED".equals(status) ? LocalDate.now().format(DISPLAY_DATE) : date;
  }

  @Override
  public Mono<Void> delete(Long id, Long siteId) {
    return findItem(id, siteId).flatMap(contentItemRepository::delete);
  }

  @Override
  public Flux<PostResponse> getPublished(Long siteId) {
    return contentItemRepository
        .findAllBySiteIdAndContentTypeAndStatusAndActiveOrderByDisplayOrderAsc(
            siteId, TYPE, "PUBLISHED", true)
        .map(this::toResponse);
  }

  @Override
  public Mono<PostDetailResponse> getPublishedDetailBySlug(Long siteId, String slug) {
    return detailOf(
        siteId,
        contentItemRepository
            .findBySlug(siteId, TYPE, slug)
            .filter(i -> "PUBLISHED".equals(i.getStatus()) && Boolean.TRUE.equals(i.getActive())));
  }

  @Override
  public Mono<PostDetailResponse> getPreviewDetailBySlug(Long siteId, String slug) {
    return detailOf(siteId, contentItemRepository.findBySlug(siteId, TYPE, slug));
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
        .hasElement();
  }
}
