package com.takypok.contentservice.service.impl;

import com.takypok.contentservice.model.entity.Office;
import com.takypok.contentservice.model.request.OfficeCreateRequest;
import com.takypok.contentservice.model.request.OfficeUpdateRequest;
import com.takypok.contentservice.repository.OfficeRepository;
import com.takypok.contentservice.service.OfficeService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class OfficeServiceImpl implements OfficeService {
  private final OfficeRepository officeRepository;

  @Override
  public Mono<List<Office>> getAll(Long siteId) {
    return officeRepository.findAllBySiteIdOrderByDisplayOrderAsc(siteId).collectList();
  }

  @Override
  public Mono<Office> getById(Long id, Long siteId) {
    return officeRepository
        .findByIdAndSiteId(id, siteId)
        .switchIfEmpty(
            Mono.error(new ApplicationException(Message.Application.ERROR, "Office not found")));
  }

  @Override
  public Mono<Office> create(Long siteId, OfficeCreateRequest request) {
    Office office = new Office();
    office.setSiteId(siteId);
    office.setLat(request.getLat());
    office.setLon(request.getLon());
    office.setFlagColor(request.getFlagColor());
    office.setBig(request.getBig() != null ? request.getBig() : false);
    office.setCity(request.getCity());
    office.setAddress(request.getAddress());
    office.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
    office.setActive(request.getActive() != null ? request.getActive() : true);
    office.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
    return officeRepository.save(office);
  }

  @Override
  public Mono<Office> update(Long siteId, OfficeUpdateRequest request) {
    return getById(request.getId(), siteId)
        .flatMap(
            office -> {
              office.setLat(request.getLat());
              office.setLon(request.getLon());
              office.setFlagColor(request.getFlagColor());
              if (request.getBig() != null) office.setBig(request.getBig());
              office.setCity(request.getCity());
              office.setAddress(request.getAddress());
              if (request.getDisplayOrder() != null)
                office.setDisplayOrder(request.getDisplayOrder());
              if (request.getActive() != null) office.setActive(request.getActive());
              if (request.getStatus() != null) office.setStatus(request.getStatus());
              return officeRepository.save(office);
            });
  }

  @Override
  public Mono<Void> delete(Long id, Long siteId) {
    return getById(id, siteId).flatMap(officeRepository::delete);
  }

  @Override
  public Flux<Office> getPublished(Long siteId) {
    return officeRepository.findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
        siteId, "PUBLISHED", true);
  }
}
