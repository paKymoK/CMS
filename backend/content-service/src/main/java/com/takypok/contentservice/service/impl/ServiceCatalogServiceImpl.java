package com.takypok.contentservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.model.response.ServiceNavResponse;
import com.takypok.contentservice.model.response.ServicePageResponse;
import com.takypok.contentservice.repository.ContentItemRepository;
import com.takypok.contentservice.service.ServiceCatalogService;
import com.takypok.core.exception.ApplicationException;
import com.takypok.core.model.Message;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ServiceCatalogServiceImpl implements ServiceCatalogService {
  static final String CATEGORY = "service_category";
  static final String PAGE = "service_page";

  private final ContentItemRepository contentItemRepository;

  private Flux<ContentItem> items(Long siteId, String type) {
    return contentItemRepository.findAllBySiteIdAndContentTypeAndStatusOrderByDisplayOrderAsc(
        siteId, type, "PUBLISHED");
  }

  private static String text(JsonNode data, String field) {
    JsonNode node = data == null ? null : data.get(field);
    return node == null || node.isNull() ? null : node.asText();
  }

  @Override
  public Mono<ServiceNavResponse> getNav(Long siteId) {
    return Mono.zip(items(siteId, CATEGORY).collectList(), items(siteId, PAGE).collectList())
        .map(
            t -> {
              List<ServiceNavResponse.Category> categories = new ArrayList<>();
              for (ContentItem category : t.getT1()) {
                String slug = text(category.getData(), "slug");
                List<ServiceNavResponse.Link> links = new ArrayList<>();
                for (ContentItem page : t.getT2()) {
                  if (slug != null && slug.equals(text(page.getData(), "category"))) {
                    links.add(
                        new ServiceNavResponse.Link(
                            text(page.getData(), "slug"), text(page.getData(), "navLabel")));
                  }
                }
                categories.add(
                    new ServiceNavResponse.Category(slug, text(category.getData(), "name"), links));
              }
              return new ServiceNavResponse(categories);
            });
  }

  @Override
  public Mono<ServicePageResponse> getPage(Long siteId, String category, String slug) {
    Mono<ApplicationException> notFound =
        Mono.just(new ApplicationException(Message.Application.ERROR, "Service not found"));
    return Mono.zip(items(siteId, CATEGORY).collectList(), items(siteId, PAGE).collectList())
        .flatMap(
            t -> {
              ContentItem categoryItem =
                  t.getT1().stream()
                      .filter(c -> category.equals(text(c.getData(), "slug")))
                      .findFirst()
                      .orElse(null);
              ContentItem pageItem =
                  t.getT2().stream()
                      .filter(
                          p ->
                              category.equals(text(p.getData(), "category"))
                                  && slug.equals(text(p.getData(), "slug")))
                      .findFirst()
                      .orElse(null);
              if (categoryItem == null || pageItem == null) return notFound.flatMap(Mono::error);

              List<ServiceNavResponse.Link> siblings =
                  t.getT2().stream()
                      .filter(p -> category.equals(text(p.getData(), "category")))
                      .map(
                          p ->
                              new ServiceNavResponse.Link(
                                  text(p.getData(), "slug"), text(p.getData(), "navLabel")))
                      .toList();
              ServiceNavResponse.Category cat =
                  new ServiceNavResponse.Category(
                      category, text(categoryItem.getData(), "name"), siblings);

              JsonNode slugs = pageItem.getData().get("caseStudies");
              List<String> wanted = new ArrayList<>();
              if (slugs != null && slugs.isArray()) slugs.forEach(s -> wanted.add(s.asText()));

              return Flux.fromIterable(wanted)
                  .concatMap(
                      s -> contentItemRepository.findBySlug(siteId, CaseStudyServiceImpl.TYPE, s))
                  .filter(c -> "PUBLISHED".equals(c.getStatus()))
                  .map(ServiceCatalogServiceImpl::featured)
                  .collectList()
                  .map(list -> new ServicePageResponse(pageItem.getData(), cat, list));
            });
  }

  private static ServicePageResponse.FeaturedCaseStudy featured(ContentItem c) {
    JsonNode d = c.getData();
    return new ServicePageResponse.FeaturedCaseStudy(
        text(d, "slug"),
        text(d, "title"),
        text(d, "summary"),
        text(d, "image"),
        text(d, "category"),
        d.get("results"));
  }
}
