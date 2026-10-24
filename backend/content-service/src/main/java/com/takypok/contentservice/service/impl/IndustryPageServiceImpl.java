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

  @Override
  public Mono<IndustryPageResponse> getPage(Long siteId, String slug) {
    return contentItemRepository
        .findBySlug(siteId, TYPE, slug)
        .filter(item -> "PUBLISHED".equals(item.getStatus()))
        .map(item -> new IndustryPageResponse(item.getData()))
        .switchIfEmpty(
            Mono.error(new ApplicationException(Message.Application.ERROR, "Industry not found")));
  }
}
