package com.takypok.contentservice.service;

import com.takypok.contentservice.model.entity.Banner;
import com.takypok.contentservice.model.request.BannerCreateRequest;
import com.takypok.contentservice.model.request.BannerUpdateRequest;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BannerService {
  Mono<List<Banner>> getAll(Long siteId);

  Mono<Banner> getById(Long id, Long siteId);

  Mono<Banner> create(Long siteId, BannerCreateRequest request);

  Mono<Banner> update(Long siteId, BannerUpdateRequest request);

  Mono<Void> delete(Long id, Long siteId);

  Flux<Banner> getPublished(Long siteId);
}
