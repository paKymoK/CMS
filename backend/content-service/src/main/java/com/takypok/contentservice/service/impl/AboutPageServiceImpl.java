package com.takypok.contentservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
  private static final String STAT = "stat";

  private final ContentItemRepository contentItemRepository;

  @Override
  public Mono<AboutPageResponse> getPage(Long siteId) {
    return contentItemRepository
        .findAllBySiteIdAndContentTypeAndStatusAndActiveOrderByDisplayOrderAsc(
            siteId, TYPE, "PUBLISHED", true)
        .next()
        .flatMap(item -> withGrowthStats(siteId, item.getData()))
        .map(AboutPageResponse::new)
        .switchIfEmpty(
            Mono.error(
                new ApplicationException(Message.Application.ERROR, "About page not found")));
  }

  /**
   * The page as stored, except that when its growth figures come from the library they are the
   * site's published stats (value and label, in display order), so callers never see the mode.
   */
  private Mono<JsonNode> withGrowthStats(Long siteId, JsonNode data) {
    if (!"library".equals(data.path("growthSource").asText())) return Mono.just(data);
    return contentItemRepository
        .findAllBySiteIdAndContentTypeAndStatusAndActiveOrderByDisplayOrderAsc(
            siteId, STAT, "PUBLISHED", true)
        .map(
            stat -> {
              ObjectNode figure = JsonNodeFactory.instance.objectNode();
              figure.set("value", stat.getData().path("value"));
              figure.set("label", stat.getData().path("label"));
              return figure;
            })
        .collect(JsonNodeFactory.instance::arrayNode, ArrayNode::add)
        .map(
            figures -> {
              ObjectNode resolved = data.deepCopy();
              resolved.set("growthStats", figures);
              return resolved;
            });
  }
}
