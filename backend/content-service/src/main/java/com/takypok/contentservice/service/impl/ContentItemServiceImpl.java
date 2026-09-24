package com.takypok.contentservice.service.impl;

import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.model.mapper.ContentItemMapper;
import com.takypok.contentservice.model.request.ContentItemCreateRequest;
import com.takypok.contentservice.model.request.ContentItemUpdateRequest;
import com.takypok.contentservice.repository.ContentItemRepository;
import com.takypok.contentservice.service.ContentItemService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ContentItemServiceImpl implements ContentItemService {
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
    return contentItemMapper
        .resolveAndValidate(request.getContentType(), request.getData())
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
    return getById(request.getId(), siteId)
        .flatMap(
            item ->
                contentItemMapper
                    .resolveAndValidate(request.getContentType(), request.getData())
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
}
