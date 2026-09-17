package com.takypok.contentservice.service.impl;

import com.takypok.contentservice.model.entity.Stat;
import com.takypok.contentservice.model.request.StatCreateRequest;
import com.takypok.contentservice.model.request.StatUpdateRequest;
import com.takypok.contentservice.repository.StatRepository;
import com.takypok.contentservice.service.StatService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {
  private final StatRepository statRepository;

  @Override
  public Mono<List<Stat>> getAll(Long siteId) {
    return statRepository.findAllBySiteIdOrderByDisplayOrderAsc(siteId).collectList();
  }

  @Override
  public Mono<Stat> getById(Long id, Long siteId) {
    return statRepository
        .findByIdAndSiteId(id, siteId)
        .switchIfEmpty(
            Mono.error(new ApplicationException(Message.Application.ERROR, "Stat not found")));
  }

  @Override
  public Mono<Stat> create(Long siteId, StatCreateRequest request) {
    Stat stat = new Stat();
    stat.setSiteId(siteId);
    stat.setValue(request.getValue());
    stat.setLabel(request.getLabel());
    stat.setNote(request.getNote());
    stat.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
    stat.setActive(request.getActive() != null ? request.getActive() : true);
    stat.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
    return statRepository.save(stat);
  }

  @Override
  public Mono<Stat> update(Long siteId, StatUpdateRequest request) {
    return getById(request.getId(), siteId)
        .flatMap(
            stat -> {
              stat.setValue(request.getValue());
              stat.setLabel(request.getLabel());
              stat.setNote(request.getNote());
              if (request.getDisplayOrder() != null)
                stat.setDisplayOrder(request.getDisplayOrder());
              if (request.getActive() != null) stat.setActive(request.getActive());
              if (request.getStatus() != null) stat.setStatus(request.getStatus());
              return statRepository.save(stat);
            });
  }

  @Override
  public Mono<Void> delete(Long id, Long siteId) {
    return getById(id, siteId).flatMap(statRepository::delete);
  }

  @Override
  public Flux<Stat> getPublished(Long siteId) {
    return statRepository.findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
        siteId, "PUBLISHED", true);
  }
}
