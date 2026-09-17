package com.takypok.contentservice.service.impl;

import com.takypok.contentservice.model.entity.Testimonial;
import com.takypok.contentservice.model.request.TestimonialCreateRequest;
import com.takypok.contentservice.model.request.TestimonialUpdateRequest;
import com.takypok.contentservice.repository.TestimonialRepository;
import com.takypok.contentservice.service.TestimonialService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TestimonialServiceImpl implements TestimonialService {
  private final TestimonialRepository testimonialRepository;

  @Override
  public Mono<List<Testimonial>> getAll(Long siteId) {
    return testimonialRepository.findAllBySiteIdOrderByDisplayOrderAsc(siteId).collectList();
  }

  @Override
  public Mono<Testimonial> getById(Long id, Long siteId) {
    return testimonialRepository
        .findByIdAndSiteId(id, siteId)
        .switchIfEmpty(
            Mono.error(
                new ApplicationException(Message.Application.ERROR, "Testimonial not found")));
  }

  @Override
  public Mono<Testimonial> create(Long siteId, TestimonialCreateRequest request) {
    Testimonial testimonial = new Testimonial();
    testimonial.setSiteId(siteId);
    testimonial.setName(request.getName());
    testimonial.setCompany(request.getCompany());
    testimonial.setPhoto(request.getPhoto());
    testimonial.setFlankLogos(request.getFlankLogos());
    testimonial.setTitle(request.getTitle());
    testimonial.setQuote(request.getQuote());
    testimonial.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
    testimonial.setActive(request.getActive() != null ? request.getActive() : true);
    testimonial.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
    return testimonialRepository.save(testimonial);
  }

  @Override
  public Mono<Testimonial> update(Long siteId, TestimonialUpdateRequest request) {
    return getById(request.getId(), siteId)
        .flatMap(
            testimonial -> {
              testimonial.setName(request.getName());
              testimonial.setCompany(request.getCompany());
              testimonial.setPhoto(request.getPhoto());
              testimonial.setFlankLogos(request.getFlankLogos());
              testimonial.setTitle(request.getTitle());
              testimonial.setQuote(request.getQuote());
              if (request.getDisplayOrder() != null)
                testimonial.setDisplayOrder(request.getDisplayOrder());
              if (request.getActive() != null) testimonial.setActive(request.getActive());
              if (request.getStatus() != null) testimonial.setStatus(request.getStatus());
              return testimonialRepository.save(testimonial);
            });
  }

  @Override
  public Mono<Void> delete(Long id, Long siteId) {
    return getById(id, siteId).flatMap(testimonialRepository::delete);
  }

  @Override
  public Flux<Testimonial> getPublished(Long siteId) {
    return testimonialRepository.findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
        siteId, "PUBLISHED", true);
  }
}
