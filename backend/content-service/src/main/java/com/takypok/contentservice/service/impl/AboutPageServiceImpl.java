package com.takypok.contentservice.service.impl;

import com.takypok.contentservice.model.response.AboutPageResponse;
import com.takypok.contentservice.repository.ContentItemRepository;
import com.takypok.contentservice.service.AboutPageService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AboutPageServiceImpl implements AboutPageService {
  static final String TYPE = "about_page";
  private final ContentItemRepository contentItemRepository;
  private final GrowthStats growthStats;
  private final FeaturedCaseStudies featuredCaseStudies;

  @Override
  public Mono<AboutPageResponse> getPage(Long siteId) {
    return contentItemRepository
        .findAllBySiteIdAndContentTypeAndStatusOrderByDisplayOrderAsc(siteId, TYPE, "PUBLISHED")
        .next()
        .flatMap(
            item ->
                growthStats
                    .apply(siteId, item.getData())
                    .map(data -> com.takypok.contentservice.util.PageExtras.apply(item, data))
                    .flatMap(
                        data ->
                            featuredCaseStudies
                                .resolve(siteId, data)
                                .map(studies -> new AboutPageResponse(data, studies))))
        .switchIfEmpty(
            Mono.error(
                new ApplicationException(Message.Application.ERROR, "About page not found")));
  }
}
