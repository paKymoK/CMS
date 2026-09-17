package com.takypok.contentservice.service.impl;

import com.takypok.contentservice.model.entity.FooterNavCategory;
import com.takypok.contentservice.model.request.FooterNavCategoryCreateRequest;
import com.takypok.contentservice.model.request.FooterNavCategoryUpdateRequest;
import com.takypok.contentservice.repository.FooterNavCategoryRepository;
import com.takypok.contentservice.service.FooterNavCategoryService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FooterNavCategoryServiceImpl implements FooterNavCategoryService {
  private final FooterNavCategoryRepository footerNavCategoryRepository;

  @Override
  public Mono<List<FooterNavCategory>> getAll(Long siteId) {
    return footerNavCategoryRepository.findAllBySiteIdOrderByDisplayOrderAsc(siteId).collectList();
  }

  @Override
  public Mono<FooterNavCategory> getById(Long id, Long siteId) {
    return footerNavCategoryRepository
        .findByIdAndSiteId(id, siteId)
        .switchIfEmpty(
            Mono.error(
                new ApplicationException(
                    Message.Application.ERROR, "Footer nav category not found")));
  }

  @Override
  public Mono<FooterNavCategory> create(Long siteId, FooterNavCategoryCreateRequest request) {
    FooterNavCategory category = new FooterNavCategory();
    category.setSiteId(siteId);
    category.setLabel(request.getLabel());
    category.setLinks(request.getLinks());
    category.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
    category.setActive(request.getActive() != null ? request.getActive() : true);
    category.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
    return footerNavCategoryRepository.save(category);
  }

  @Override
  public Mono<FooterNavCategory> update(Long siteId, FooterNavCategoryUpdateRequest request) {
    return getById(request.getId(), siteId)
        .flatMap(
            category -> {
              category.setLabel(request.getLabel());
              category.setLinks(request.getLinks());
              if (request.getDisplayOrder() != null)
                category.setDisplayOrder(request.getDisplayOrder());
              if (request.getActive() != null) category.setActive(request.getActive());
              if (request.getStatus() != null) category.setStatus(request.getStatus());
              return footerNavCategoryRepository.save(category);
            });
  }

  @Override
  public Mono<Void> delete(Long id, Long siteId) {
    return getById(id, siteId).flatMap(footerNavCategoryRepository::delete);
  }

  @Override
  public Flux<FooterNavCategory> getPublished(Long siteId) {
    return footerNavCategoryRepository.findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
        siteId, "PUBLISHED", true);
  }
}
