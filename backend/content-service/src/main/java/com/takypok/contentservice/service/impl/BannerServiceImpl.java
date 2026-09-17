package com.takypok.contentservice.service.impl;

import com.takypok.contentservice.model.entity.Banner;
import com.takypok.contentservice.model.request.BannerCreateRequest;
import com.takypok.contentservice.model.request.BannerUpdateRequest;
import com.takypok.contentservice.repository.BannerRepository;
import com.takypok.contentservice.service.BannerService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {
  private final BannerRepository bannerRepository;

  @Override
  public Mono<List<Banner>> getAll(Long siteId) {
    return bannerRepository.findAllBySiteIdOrderByDisplayOrderAsc(siteId).collectList();
  }

  @Override
  public Mono<Banner> getById(Long id, Long siteId) {
    return bannerRepository
        .findByIdAndSiteId(id, siteId)
        .switchIfEmpty(
            Mono.error(new ApplicationException(Message.Application.ERROR, "Banner not found")));
  }

  @Override
  public Mono<Banner> create(Long siteId, BannerCreateRequest request) {
    Banner banner = new Banner();
    banner.setSiteId(siteId);
    banner.setTitle(request.getTitle());
    banner.setBody(request.getBody());
    banner.setImage(request.getImage());
    banner.setCtaLabel(request.getCtaLabel());
    banner.setCtaUrl(request.getCtaUrl());
    banner.setActiveFrom(request.getActiveFrom());
    banner.setActiveUntil(request.getActiveUntil());
    banner.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
    banner.setActive(request.getActive() != null ? request.getActive() : true);
    banner.setStatus(request.getStatus() != null ? request.getStatus() : "DRAFT");
    return bannerRepository.save(banner);
  }

  @Override
  public Mono<Banner> update(Long siteId, BannerUpdateRequest request) {
    return getById(request.getId(), siteId)
        .flatMap(
            banner -> {
              banner.setTitle(request.getTitle());
              banner.setBody(request.getBody());
              banner.setImage(request.getImage());
              banner.setCtaLabel(request.getCtaLabel());
              banner.setCtaUrl(request.getCtaUrl());
              banner.setActiveFrom(request.getActiveFrom());
              banner.setActiveUntil(request.getActiveUntil());
              if (request.getDisplayOrder() != null)
                banner.setDisplayOrder(request.getDisplayOrder());
              if (request.getActive() != null) banner.setActive(request.getActive());
              if (request.getStatus() != null) banner.setStatus(request.getStatus());
              return bannerRepository.save(banner);
            });
  }

  @Override
  public Mono<Void> delete(Long id, Long siteId) {
    return getById(id, siteId).flatMap(bannerRepository::delete);
  }

  @Override
  public Flux<Banner> getPublished(Long siteId) {
    return bannerRepository.findAllBySiteIdAndStatusAndActiveOrderByDisplayOrderAsc(
        siteId, "PUBLISHED", true);
  }
}
