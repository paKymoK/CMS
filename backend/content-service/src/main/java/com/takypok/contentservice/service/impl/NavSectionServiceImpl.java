package com.takypok.contentservice.service.impl;

import com.takypok.contentservice.model.entity.NavSection;
import com.takypok.contentservice.model.request.NavSectionCreateRequest;
import com.takypok.contentservice.model.request.NavSectionUpdateRequest;
import com.takypok.contentservice.repository.NavSectionRepository;
import com.takypok.contentservice.service.NavSectionService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class NavSectionServiceImpl implements NavSectionService {
  private final NavSectionRepository navSectionRepository;

  @Override
  public Mono<List<NavSection>> getAll(Long siteId) {
    return navSectionRepository.findAllBySiteIdOrderByDisplayOrderAsc(siteId).collectList();
  }

  @Override
  public Mono<NavSection> getById(Long id, Long siteId) {
    return navSectionRepository
        .findByIdAndSiteId(id, siteId)
        .switchIfEmpty(
            Mono.error(
                new ApplicationException(Message.Application.ERROR, "Nav section not found")));
  }

  @Override
  public Mono<NavSection> create(Long siteId, NavSectionCreateRequest request) {
    NavSection section = new NavSection();
    section.setSiteId(siteId);
    section.setAnchor(request.getAnchor());
    section.setLabel(request.getLabel());
    section.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
    section.setActive(request.getActive() != null ? request.getActive() : true);
    section.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
    return navSectionRepository.save(section);
  }

  @Override
  public Mono<NavSection> update(Long siteId, NavSectionUpdateRequest request) {
    return getById(request.getId(), siteId)
        .flatMap(
            section -> {
              section.setAnchor(request.getAnchor());
              section.setLabel(request.getLabel());
              if (request.getDisplayOrder() != null)
                section.setDisplayOrder(request.getDisplayOrder());
              if (request.getActive() != null) section.setActive(request.getActive());
              if (request.getStatus() != null) section.setStatus(request.getStatus());
              return navSectionRepository.save(section);
            });
  }

  @Override
  public Mono<Void> delete(Long id, Long siteId) {
    return getById(id, siteId).flatMap(navSectionRepository::delete);
  }

  @Override
  public Flux<NavSection> getPublished(Long siteId) {
    return navSectionRepository.findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
        siteId, "PUBLISHED", true);
  }
}
