package com.takypok.contentservice.service.impl;

import com.takypok.contentservice.model.entity.LogoBadge;
import com.takypok.contentservice.model.request.LogoBadgeCreateRequest;
import com.takypok.contentservice.model.request.LogoBadgeUpdateRequest;
import com.takypok.contentservice.repository.LogoBadgeRepository;
import com.takypok.contentservice.service.LogoBadgeService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LogoBadgeServiceImpl implements LogoBadgeService {
  private final LogoBadgeRepository logoBadgeRepository;

  @Override
  public Mono<List<LogoBadge>> getAll(Long siteId, String type) {
    Flux<LogoBadge> all =
        type != null
            ? logoBadgeRepository.findAllBySiteIdAndTypeOrderByDisplayOrderAsc(siteId, type)
            : logoBadgeRepository.findAllBySiteIdOrderByDisplayOrderAsc(siteId);
    return all.collectList();
  }

  @Override
  public Mono<LogoBadge> getById(Long id, Long siteId) {
    return logoBadgeRepository
        .findByIdAndSiteId(id, siteId)
        .switchIfEmpty(
            Mono.error(
                new ApplicationException(Message.Application.ERROR, "Logo badge not found")));
  }

  @Override
  public Mono<LogoBadge> create(Long siteId, LogoBadgeCreateRequest request) {
    LogoBadge badge = new LogoBadge();
    badge.setSiteId(siteId);
    badge.setType(request.getType());
    badge.setName(request.getName());
    badge.setLogo(request.getLogo());
    badge.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
    badge.setActive(request.getActive() != null ? request.getActive() : true);
    badge.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
    return logoBadgeRepository.save(badge);
  }

  @Override
  public Mono<LogoBadge> update(Long siteId, LogoBadgeUpdateRequest request) {
    return getById(request.getId(), siteId)
        .flatMap(
            badge -> {
              badge.setType(request.getType());
              badge.setName(request.getName());
              badge.setLogo(request.getLogo());
              if (request.getDisplayOrder() != null)
                badge.setDisplayOrder(request.getDisplayOrder());
              if (request.getActive() != null) badge.setActive(request.getActive());
              if (request.getStatus() != null) badge.setStatus(request.getStatus());
              return logoBadgeRepository.save(badge);
            });
  }

  @Override
  public Mono<Void> delete(Long id, Long siteId) {
    return getById(id, siteId).flatMap(logoBadgeRepository::delete);
  }

  @Override
  public Flux<LogoBadge> getPublished(Long siteId, String type) {
    return type != null
        ? logoBadgeRepository.findAllBySiteIdAndTypeAndStatusAndActiveOrderByDisplayOrderAsc(
            siteId, type, "PUBLISHED", true)
        : logoBadgeRepository.findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
            siteId, "PUBLISHED", true);
  }
}
