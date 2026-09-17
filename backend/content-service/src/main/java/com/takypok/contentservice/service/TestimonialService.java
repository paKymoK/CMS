package com.takypok.contentservice.service;

import com.takypok.contentservice.model.entity.Testimonial;
import com.takypok.contentservice.model.request.TestimonialCreateRequest;
import com.takypok.contentservice.model.request.TestimonialUpdateRequest;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TestimonialService {
  Mono<List<Testimonial>> getAll(Long siteId);

  Mono<Testimonial> getById(Long id, Long siteId);

  Mono<Testimonial> create(Long siteId, TestimonialCreateRequest request);

  Mono<Testimonial> update(Long siteId, TestimonialUpdateRequest request);

  Mono<Void> delete(Long id, Long siteId);

  Flux<Testimonial> getPublished(Long siteId);
}
