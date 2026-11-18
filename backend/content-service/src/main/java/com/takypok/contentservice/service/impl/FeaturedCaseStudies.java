package com.takypok.contentservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.takypok.contentservice.model.response.ServicePageResponse.FeaturedCaseStudy;
import com.takypok.contentservice.repository.ContentItemRepository;
import com.takypok.contentservice.service.SlugRegistry;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The case studies a page features: its case-studies block (at most one per page) names them by
 * slug, and this finds the published ones, in the order named. Service and industry pages both use
 * it.
 */
@Component
@RequiredArgsConstructor
public class FeaturedCaseStudies {
  private final ContentItemRepository contentItemRepository;
  private final SlugRegistry slugRegistry;

  public Mono<List<FeaturedCaseStudy>> resolve(Long siteId, JsonNode pageData) {
    List<String> wanted = new ArrayList<>();
    for (JsonNode block : pageData.path("blocks")) {
      if (!"case-studies".equals(block.path("type").asText())) continue;
      block.path("props").path("caseStudies").forEach(s -> wanted.add(s.asText()));
    }
    return Flux.fromIterable(wanted)
        .concatMap(
            s ->
                contentItemRepository
                    .findBySlug(siteId, CaseStudyServiceImpl.TYPE, s)
                    // a renamed case study is still found under its old slug
                    .switchIfEmpty(
                        slugRegistry.findPublishedByAlias(
                            siteId, CaseStudyServiceImpl.TYPE, "", s)))
        .filter(c -> "PUBLISHED".equals(c.getStatus()))
        .map(
            c -> {
              JsonNode d = c.getData();
              return new FeaturedCaseStudy(
                  text(d, "slug"),
                  text(d, "title"),
                  text(d, "summary"),
                  text(d, "image"),
                  text(d, "category"),
                  d.get("results"));
            })
        .collectList();
  }

  private static String text(JsonNode data, String field) {
    JsonNode node = data == null ? null : data.get(field);
    return node == null || node.isNull() ? null : node.asText();
  }
}
