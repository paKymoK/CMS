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

  @Override
  public Mono<AboutPageResponse> getPage(Long siteId) {
    return contentItemRepository
        .findAllBySiteIdAndContentTypeAndStatusAndActiveOrderByDisplayOrderAsc(
            siteId, TYPE, "PUBLISHED", true)
        .next()
        .map(item -> new AboutPageResponse(item.getData()))
        .switchIfEmpty(
            Mono.error(
                new ApplicationException(Message.Application.ERROR, "About page not found")));
  }
}
