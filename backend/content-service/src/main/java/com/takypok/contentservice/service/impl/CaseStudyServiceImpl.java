package com.takypok.contentservice.service.impl;

import com.takypok.contentservice.model.entity.CaseStudy;
import com.takypok.contentservice.model.request.CaseStudyCreateRequest;
import com.takypok.contentservice.model.request.CaseStudyUpdateRequest;
import com.takypok.contentservice.repository.CaseStudyRepository;
import com.takypok.contentservice.service.CaseStudyService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CaseStudyServiceImpl implements CaseStudyService {
  private final CaseStudyRepository caseStudyRepository;

  @Override
  public Mono<List<CaseStudy>> getAll(Long siteId) {
    return caseStudyRepository.findAllBySiteIdOrderByDisplayOrderAsc(siteId).collectList();
  }

  @Override
  public Mono<CaseStudy> getById(Long id, Long siteId) {
    return caseStudyRepository
        .findByIdAndSiteId(id, siteId)
        .switchIfEmpty(
            Mono.error(
                new ApplicationException(Message.Application.ERROR, "Case study not found")));
  }

  @Override
  public Mono<CaseStudy> create(Long siteId, CaseStudyCreateRequest request) {
    CaseStudy caseStudy = new CaseStudy();
    caseStudy.setSiteId(siteId);
    caseStudy.setDate(request.getDate());
    caseStudy.setImage(request.getImage());
    caseStudy.setTitle(request.getTitle());
    caseStudy.setCategory(request.getCategory());
    caseStudy.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
    caseStudy.setActive(request.getActive() != null ? request.getActive() : true);
    caseStudy.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
    return caseStudyRepository.save(caseStudy);
  }

  @Override
  public Mono<CaseStudy> update(Long siteId, CaseStudyUpdateRequest request) {
    return getById(request.getId(), siteId)
        .flatMap(
            caseStudy -> {
              caseStudy.setDate(request.getDate());
              caseStudy.setImage(request.getImage());
              caseStudy.setTitle(request.getTitle());
              caseStudy.setCategory(request.getCategory());
              if (request.getDisplayOrder() != null)
                caseStudy.setDisplayOrder(request.getDisplayOrder());
              if (request.getActive() != null) caseStudy.setActive(request.getActive());
              if (request.getStatus() != null) caseStudy.setStatus(request.getStatus());
              return caseStudyRepository.save(caseStudy);
            });
  }

  @Override
  public Mono<Void> delete(Long id, Long siteId) {
    return getById(id, siteId).flatMap(caseStudyRepository::delete);
  }

  @Override
  public Flux<CaseStudy> getPublished(Long siteId) {
    return caseStudyRepository.findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
        siteId, "PUBLISHED", true);
  }
}
