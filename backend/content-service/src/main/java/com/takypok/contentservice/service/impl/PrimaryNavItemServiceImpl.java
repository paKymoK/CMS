package com.takypok.contentservice.service.impl;

import com.takypok.contentservice.model.entity.PrimaryNavItem;
import com.takypok.contentservice.model.request.PrimaryNavItemCreateRequest;
import com.takypok.contentservice.model.request.PrimaryNavItemUpdateRequest;
import com.takypok.contentservice.repository.PrimaryNavItemRepository;
import com.takypok.contentservice.service.PrimaryNavItemService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PrimaryNavItemServiceImpl implements PrimaryNavItemService {
  private final PrimaryNavItemRepository primaryNavItemRepository;

  @Override
  public Mono<List<PrimaryNavItem>> getAll(Long siteId) {
    return primaryNavItemRepository.findAllBySiteIdOrderByDisplayOrderAsc(siteId).collectList();
  }

  @Override
  public Mono<PrimaryNavItem> getById(Long id, Long siteId) {
    return primaryNavItemRepository
        .findByIdAndSiteId(id, siteId)
        .switchIfEmpty(
            Mono.error(
                new ApplicationException(Message.Application.ERROR, "Primary nav item not found")));
  }

  @Override
  public Mono<PrimaryNavItem> create(Long siteId, PrimaryNavItemCreateRequest request) {
    PrimaryNavItem item = new PrimaryNavItem();
    item.setSiteId(siteId);
    item.setHasDropdown(request.getHasDropdown() != null ? request.getHasDropdown() : false);
    item.setLabel(request.getLabel());
    item.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
    item.setActive(request.getActive() != null ? request.getActive() : true);
    item.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
    return primaryNavItemRepository.save(item);
  }

  @Override
  public Mono<PrimaryNavItem> update(Long siteId, PrimaryNavItemUpdateRequest request) {
    return getById(request.getId(), siteId)
        .flatMap(
            item -> {
              if (request.getHasDropdown() != null) item.setHasDropdown(request.getHasDropdown());
              item.setLabel(request.getLabel());
              if (request.getDisplayOrder() != null)
                item.setDisplayOrder(request.getDisplayOrder());
              if (request.getActive() != null) item.setActive(request.getActive());
              if (request.getStatus() != null) item.setStatus(request.getStatus());
              return primaryNavItemRepository.save(item);
            });
  }

  @Override
  public Mono<Void> delete(Long id, Long siteId) {
    return getById(id, siteId).flatMap(primaryNavItemRepository::delete);
  }

  @Override
  public Flux<PrimaryNavItem> getPublished(Long siteId) {
    return primaryNavItemRepository.findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
        siteId, "PUBLISHED", true);
  }
}
