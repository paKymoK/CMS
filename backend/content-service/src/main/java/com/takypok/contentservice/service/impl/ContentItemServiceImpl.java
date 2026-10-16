package com.takypok.contentservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.model.mapper.ContentItemMapper;
import com.takypok.contentservice.model.request.ContentItemCreateRequest;
import com.takypok.contentservice.model.request.ContentItemUpdateRequest;
import com.takypok.contentservice.repository.ContentItemRepository;
import com.takypok.contentservice.service.ContentItemService;
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
  private static final Set<String> SPECIALIZED_TYPES = Set.of(CaseStudyServiceImpl.TYPE);

  private final ContentItemRepository contentItemRepository;
  private final ContentItemMapper contentItemMapper;

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
    return contentItemMapper
        .resolveAndValidate(request.getContentType(), request.getData())
        .flatMap(
            data -> ensureUnique(siteId, request.getContentType(), data, null).thenReturn(data))
        .flatMap(
            validatedData -> {
              ContentItem item = new ContentItem();
              item.setSiteId(siteId);
              item.setContentType(request.getContentType());
              item.setData(validatedData);
              item.setDisplayOrder(
                  request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
              item.setActive(request.getActive() != null ? request.getActive() : true);
              item.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
              return contentItemRepository.save(item);
            });
  }

  @Override
  public Mono<ContentItem> update(Long siteId, ContentItemUpdateRequest request) {
    if (SPECIALIZED_TYPES.contains(request.getContentType())) {
      return Mono.error(specializedTypeError(request.getContentType()));
    }
    return getById(request.getId(), siteId)
        .filter(item -> !SPECIALIZED_TYPES.contains(item.getContentType()))
        .switchIfEmpty(Mono.error(specializedTypeError(CaseStudyServiceImpl.TYPE)))
        .flatMap(
            item ->
                contentItemMapper
                    .resolveAndValidate(request.getContentType(), request.getData())
                    .flatMap(
                        data ->
                            ensureUnique(siteId, request.getContentType(), data, item.getId())
                                .thenReturn(data))
                    .flatMap(
                        validatedData -> {
                          item.setContentType(request.getContentType());
                          item.setData(validatedData);
                          if (request.getDisplayOrder() != null) {
                            item.setDisplayOrder(request.getDisplayOrder());
                          }
                          if (request.getActive() != null) item.setActive(request.getActive());
                          if (request.getStatus() != null) item.setStatus(request.getStatus());
                          return contentItemRepository.save(item);
                        }));
  }

  private static ApplicationException specializedTypeError(String contentType) {
    return new ApplicationException(
        Message.Application.ERROR,
        "Content type " + contentType + " is managed through its own endpoint");
  }

  @Override
  public Mono<Void> delete(Long id, Long siteId) {
    return getById(id, siteId).flatMap(contentItemRepository::delete);
  }

  @Override
  public Flux<ContentItem> getPublished(Long siteId, String contentType) {
    return contentItemRepository
        .findAllBySiteIdAndContentTypeAndStatusAndActiveOrderByDisplayOrderAsc(
            siteId, contentType, "PUBLISHED", true);
  }

  @Override
  public Flux<ContentItem> getAllPublished(Long siteId) {
    return contentItemRepository.findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
        siteId, "PUBLISHED", true);
  }

  @Override
  public Flux<ContentItem> getEveryItem(Long siteId) {
    return contentItemRepository.findAllBySiteIdOrderByDisplayOrderAsc(siteId);
  }

  /**
   * Service categories are keyed by slug and service pages by category + slug (they form the page
   * URL), so a second item with the same key is rejected here rather than silently shadowed.
   */
  private Mono<Void> ensureUnique(Long siteId, String contentType, JsonNode data, Long selfId) {
    boolean category = "service_category".equals(contentType);
    if (!category && !"service_page".equals(contentType)) return Mono.empty();
    String slug = data.path("slug").asText();
    String parent = category ? "" : data.path("category").asText();
    return contentItemRepository
        .findAllBySiteIdAndContentTypeOrderByDisplayOrderAsc(siteId, contentType)
        .filter(other -> !other.getId().equals(selfId))
        .filter(
            other ->
                slug.equals(other.getData().path("slug").asText())
                    && (category || parent.equals(other.getData().path("category").asText())))
        .hasElements()
        .flatMap(
            taken ->
                taken
                    ? Mono.error(
                        new ApplicationException(
                            Message.Application.ERROR,
                            "slug: another "
                                + (category ? "category" : "service in this category")
                                + " already uses \""
                                + slug
                                + "\""))
                    : Mono.<Void>empty());
  }
}
