package com.takypok.contentservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.takypok.contentservice.audit.ContentItemWriter;
import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.model.mapper.ContentItemMapper;
import com.takypok.contentservice.model.request.ContentItemCreateRequest;
import com.takypok.contentservice.model.request.ContentItemUpdateRequest;
import com.takypok.contentservice.repository.ContentItemRepository;
import com.takypok.contentservice.service.ContentItemService;
import com.takypok.contentservice.service.SlugRegistry;
import com.takypok.contentservice.util.StaleEdit;
import com.takypok.contentservice.util.StatusRules;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ContentItemServiceImpl implements ContentItemService {
  /**
   * Types with rules the generic path doesn't apply (rich-text sanitizing, slug generation), so
   * they are written only through their own service and endpoint.
   */
  private static final Set<String> SPECIALIZED_TYPES =
      Set.of(CaseStudyServiceImpl.TYPE, PostServiceImpl.TYPE);

  /**
   * Types a site has exactly one of (the About page); the schema backs this with a unique index.
   */
  private static final Set<String> SINGLETON_TYPES = Set.of(AboutPageServiceImpl.TYPE);

  private final ContentItemRepository contentItemRepository;
  private final ContentItemMapper contentItemMapper;
  private final ContentItemWriter contentItemWriter;
  private final SlugRegistry slugRegistry;

  @Override
  public Mono<List<ContentItem>> getAll(Long siteId, String contentType) {
    return contentItemRepository
        .findAllBySiteIdAndContentTypeOrderByDisplayOrderAsc(siteId, contentType)
        .collectList();
  }

  @Override
  public Mono<ContentItem> getById(Long id, Long siteId) {
    return contentItemRepository
        .findByIdAndSiteId(id, siteId)
        .switchIfEmpty(
            Mono.error(
                new ApplicationException(Message.Application.ERROR, "Content item not found")));
  }

  @Override
  public Mono<ContentItem> create(Long siteId, ContentItemCreateRequest request) {
    if (SPECIALIZED_TYPES.contains(request.getContentType())) {
      return Mono.error(specializedTypeError(request.getContentType()));
    }
    return Mono.<Void>fromRunnable(
            () -> StatusRules.requireAllowed(request.getContentType(), request.getStatus()))
        .then(
            Mono.defer(
                () ->
                    contentItemMapper.resolveAndValidate(
                        request.getContentType(), request.getData())))
        .flatMap(
            data ->
                ensureSingleton(siteId, request.getContentType(), null)
                    .then(ensureUnique(siteId, request.getContentType(), data, null))
                    .thenReturn(data))
        .flatMap(
            validatedData -> {
              ContentItem item = new ContentItem();
              item.setSiteId(siteId);
              item.setContentType(request.getContentType());
              item.setData(validatedData);
              item.setDisplayOrder(
                  request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
              item.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
              return contentItemWriter.save(item);
            });
  }

  @Override
  public Mono<ContentItem> update(Long siteId, ContentItemUpdateRequest request) {
    if (SPECIALIZED_TYPES.contains(request.getContentType())) {
      return Mono.error(specializedTypeError(request.getContentType()));
    }
    return Mono.<Void>fromRunnable(
            () -> StatusRules.requireAllowed(request.getContentType(), request.getStatus()))
        .then(Mono.defer(() -> getById(request.getId(), siteId)))
        .filter(item -> !SPECIALIZED_TYPES.contains(item.getContentType()))
        .switchIfEmpty(Mono.error(specializedTypeError("case_study or post")))
        .doOnNext(item -> StaleEdit.check(item, request.getVersion()))
        .flatMap(
            item ->
                contentItemMapper
                    .resolveAndValidate(request.getContentType(), request.getData())
                    .flatMap(
                        data ->
                            ensureSingleton(siteId, request.getContentType(), item.getId())
                                .then(
                                    ensureUnique(
                                        siteId, request.getContentType(), data, item.getId()))
                                .thenReturn(data))
                    .flatMap(
                        validatedData -> {
                          item.setContentType(request.getContentType());
                          item.setData(validatedData);
                          if (request.getDisplayOrder() != null) {
                            item.setDisplayOrder(request.getDisplayOrder());
                          }
                          if (request.getStatus() != null) item.setStatus(request.getStatus());
                          return contentItemWriter.save(item);
                        }));
  }

  private static ApplicationException specializedTypeError(String contentType) {
    return new ApplicationException(
        Message.Application.ERROR,
        "Content type " + contentType + " is managed through its own endpoint");
  }

  @Override
  public Mono<Void> delete(Long id, Long siteId) {
    return getById(id, siteId).flatMap(contentItemWriter::delete);
  }

  @Override
  public Flux<ContentItem> getPublished(Long siteId, String contentType) {
    return contentItemRepository.findAllBySiteIdAndContentTypeAndStatusOrderByDisplayOrderAsc(
        siteId, contentType, "PUBLISHED");
  }

  @Override
  public Flux<ContentItem> getAllPublished(Long siteId) {
    return contentItemRepository.findAllBySiteIdAndStatusOrderByDisplayOrderAsc(
        siteId, "PUBLISHED");
  }

  /** A site has one item of each singleton type: a second one is rejected with a plain message. */
  private Mono<Void> ensureSingleton(Long siteId, String contentType, Long selfId) {
    if (!SINGLETON_TYPES.contains(contentType)) return Mono.empty();
    return contentItemRepository
        .findAllBySiteIdAndContentTypeOrderByDisplayOrderAsc(siteId, contentType)
        .filter(other -> !other.getId().equals(selfId))
        .hasElements()
        .flatMap(
            exists ->
                exists
                    ? Mono.<Void>error(
                        new ApplicationException(
                            Message.Application.ERROR,
                            "type: this site already has its " + contentType + "; edit that one"))
                    : Mono.<Void>empty());
  }

  /**
   * Service categories and industry pages are keyed by slug and service pages by category + slug
   * (they form the page URL), so a second item with the same key is rejected here rather than
   * silently shadowed. For the pages that keep old addresses, an old slug of another item counts as
   * taken too.
   */
  private Mono<Void> ensureUnique(Long siteId, String contentType, JsonNode data, Long selfId) {
    boolean category = "service_category".equals(contentType);
    boolean industry = "industry_page".equals(contentType);
    boolean slugOnly = category || industry;
    if (!slugOnly && !"service_page".equals(contentType)) return Mono.empty();
    String slug = data.path("slug").asText();
    String parent = slugOnly ? "" : data.path("category").asText();
    return contentItemRepository
        .findAllBySiteIdAndContentTypeOrderByDisplayOrderAsc(siteId, contentType)
        .filter(other -> !other.getId().equals(selfId))
        .filter(
            other ->
                slug.equals(other.getData().path("slug").asText())
                    && (slugOnly || parent.equals(other.getData().path("category").asText())))
        .hasElements()
        .flatMap(
            current ->
                current || category
                    ? Mono.just(current)
                    : slugRegistry.isAliasOfOther(siteId, contentType, parent, slug, selfId))
        .flatMap(
            taken ->
                taken
                    ? Mono.error(
                        new ApplicationException(
                            Message.Application.ERROR,
                            "slug: another "
                                + (category
                                    ? "category"
                                    : industry ? "industry" : "service in this category")
                                + " already uses \""
                                + slug
                                + "\""))
                    : Mono.<Void>empty());
  }
}
