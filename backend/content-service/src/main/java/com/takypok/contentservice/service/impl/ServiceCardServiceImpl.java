package com.takypok.contentservice.service.impl;

import com.takypok.contentservice.model.entity.ServiceCard;
import com.takypok.contentservice.model.request.ServiceCardCreateRequest;
import com.takypok.contentservice.model.request.ServiceCardUpdateRequest;
import com.takypok.contentservice.repository.ServiceCardRepository;
import com.takypok.contentservice.service.ServiceCardService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ServiceCardServiceImpl implements ServiceCardService {
  private final ServiceCardRepository serviceCardRepository;

  @Override
  public Mono<List<ServiceCard>> getAll(Long siteId) {
    return serviceCardRepository.findAllBySiteIdOrderByDisplayOrderAsc(siteId).collectList();
  }

  @Override
  public Mono<ServiceCard> getById(Long id, Long siteId) {
    return serviceCardRepository
        .findByIdAndSiteId(id, siteId)
        .switchIfEmpty(
            Mono.error(new ApplicationException(Message.Application.ERROR, "Service not found")));
  }

  @Override
  public Mono<ServiceCard> create(Long siteId, ServiceCardCreateRequest request) {
    ServiceCard card = new ServiceCard();
    card.setSiteId(siteId);
    card.setName(request.getName());
    card.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
    card.setActive(request.getActive() != null ? request.getActive() : true);
    card.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
    return serviceCardRepository.save(card);
  }

  @Override
  public Mono<ServiceCard> update(Long siteId, ServiceCardUpdateRequest request) {
    return getById(request.getId(), siteId)
        .flatMap(
            card -> {
              card.setName(request.getName());
              if (request.getDisplayOrder() != null)
                card.setDisplayOrder(request.getDisplayOrder());
              if (request.getActive() != null) card.setActive(request.getActive());
              if (request.getStatus() != null) card.setStatus(request.getStatus());
              return serviceCardRepository.save(card);
            });
  }

  @Override
  public Mono<Void> delete(Long id, Long siteId) {
    return getById(id, siteId).flatMap(serviceCardRepository::delete);
  }

  @Override
  public Flux<ServiceCard> getPublished(Long siteId) {
    return serviceCardRepository.findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
        siteId, "PUBLISHED", true);
  }
}
