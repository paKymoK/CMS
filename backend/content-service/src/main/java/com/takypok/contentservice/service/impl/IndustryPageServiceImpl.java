package com.takypok.contentservice.service.impl;

import com.takypok.contentservice.model.response.IndustryPageResponse;
import com.takypok.contentservice.repository.ContentItemRepository;
import com.takypok.contentservice.service.IndustryPageService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class IndustryPageServiceImpl implements IndustryPageService {
  static final String TYPE = "industry_page";

  private final ContentItemRepository contentItemRepository;
  private final FeaturedCaseStudies featuredCaseStudies;
  private final GrowthStats growthStats;

  @Override
  public Mono<IndustryPageResponse> getPage(Long siteId, String slug) {
    return contentItemRepository
        .findBySlug(siteId, TYPE, slug)
        .filter(item -> "PUBLISHED".equals(item.getStatus()))
        .flatMap(
            item ->
                growthStats
                    .apply(siteId, item.getData())
                    .flatMap(
                        data ->
                            featuredCaseStudies
                                .resolve(siteId, data)
                                .map(
                                    studies ->
                                        new IndustryPageResponse(
                                            com.takypok.contentservice.util.PageExtras.apply(
                                                item, data),
                                            studies))))
        .switchIfEmpty(
            Mono.error(new ApplicationException(Message.Application.ERROR, "Industry not found")));
  }
}
