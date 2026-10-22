package com.takypok.contentservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.contentservice.model.content.types.CaseStudyFields;
import com.takypok.contentservice.model.content.types.FooterNavCategoryFields;
import com.takypok.contentservice.model.content.types.LogoBadgeFields;
import com.takypok.contentservice.model.content.types.OfficeFields;
import com.takypok.contentservice.model.content.types.PostFields;
import com.takypok.contentservice.model.content.types.ServiceCardFields;
import com.takypok.contentservice.model.content.types.StatFields;
import com.takypok.contentservice.model.content.types.TestimonialFields;
import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.model.response.HomeContentResponse;
import com.takypok.contentservice.service.ContentItemService;
import com.takypok.contentservice.service.HomeContentService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Builds the site's single public HomeContentResponse from content_item, which is read once per
 * request: the homepage's lists (stats, service cards, offices, case studies, insights, logo
 * strips, testimonials, footer navigation) are picked out of it by content type, and every other
 * type lands in {@code sections}.
 */
@Component
@RequiredArgsConstructor
public class HomeContentServiceImpl implements HomeContentService {
  /**
   * Types with their own field in the response; everything else is a generic section. The service_*
   * types are served by /v1/services — there can be dozens, so they stay out of the homepage
   * payload. Banners are not part of HomeContent yet (unused by the current homepage).
   */
  private static final Set<String> DEDICATED_TYPES =
      Set.of(
          "stat",
          "service_card",
          "office",
          "case_study",
          "post",
          "award",
          "certification",
          "partner",
          "testimonial",
          "footer_nav_category",
          "banner");

  private final ContentItemService contentItemService;
  private final ObjectMapper objectMapper;

  @Override
  public Mono<HomeContentResponse> getHomeContent(Long siteId) {
    return contentItemService.getAllPublished(siteId).collectList().map(this::build);
  }

  private HomeContentResponse build(List<ContentItem> items) {
    return new HomeContentResponse(
        stats(items),
        services(items),
        offices(items),
        caseStudies(items),
        insights(items),
        logos(items, "award"),
        logos(items, "certification"),
        logos(items, "partner"),
        testimonials(items),
        footerNav(items),
        sectionsOf(items));
  }

  /** Groups the generic items by content_type, keeping display order inside each group. */
  private static Map<String, List<JsonNode>> sectionsOf(List<ContentItem> items) {
    Map<String, List<JsonNode>> grouped = new LinkedHashMap<>();
    for (ContentItem item : items) {
      String type = item.getContentType();
      if (DEDICATED_TYPES.contains(type) || type.startsWith("service_")) continue;
      grouped.computeIfAbsent(type, k -> new ArrayList<>()).add(item.getData());
    }
    return grouped;
  }

  private <T> List<T> fieldsOf(List<ContentItem> items, String type, Class<T> fieldsClass) {
    return items.stream()
        .filter(i -> type.equals(i.getContentType()))
        .map(i -> objectMapper.convertValue(i.getData(), fieldsClass))
        .toList();
  }

  private List<HomeContentResponse.Stat> stats(List<ContentItem> items) {
    return fieldsOf(items, "stat", StatFields.class).stream()
        .map(s -> new HomeContentResponse.Stat(s.getValue(), s.getLabel(), s.getNote()))
        .toList();
  }

  private List<HomeContentResponse.ServiceCard> services(List<ContentItem> items) {
    return fieldsOf(items, "service_card", ServiceCardFields.class).stream()
        .map(
            s -> new HomeContentResponse.ServiceCard(s.getName(), s.getDescription(), s.getImage()))
        .toList();
  }

  private List<HomeContentResponse.Office> offices(List<ContentItem> items) {
    return fieldsOf(items, "office", OfficeFields.class).stream()
        .map(
            o ->
                new HomeContentResponse.Office(
                    o.getCity(),
                    o.getLat(),
                    o.getLon(),
                    o.getFlagColor(),
                    Boolean.TRUE.equals(o.getBig()),
                    o.getAddress(),
                    o.getImage()))
        .toList();
  }

  private List<HomeContentResponse.CaseStudy> caseStudies(List<ContentItem> items) {
    return fieldsOf(items, "case_study", CaseStudyFields.class).stream()
        .map(
            c ->
                new HomeContentResponse.CaseStudy(
                    c.getSlug(), c.getTitle(), c.getDate(), c.getCategory(), c.getImage()))
        .toList();
  }

  /**
   * Only "insight"-category posts render on the homepage today — other categories exist for future
   * post types but aren't part of HomeContent yet. This also keeps a draft "press-release" post out
   * of the homepage, for the same reason it wouldn't render once published.
   */
  private List<HomeContentResponse.Insight> insights(List<ContentItem> items) {
    return fieldsOf(items, "post", PostFields.class).stream()
        .filter(p -> "insight".equals(p.getCategory()))
        .map(
            p ->
                new HomeContentResponse.Insight(
                    p.getSlug(), p.getTitle(), p.getDate(), p.getImage()))
        .toList();
  }

  private List<HomeContentResponse.LogoBadge> logos(List<ContentItem> items, String type) {
    return fieldsOf(items, type, LogoBadgeFields.class).stream()
        .map(b -> new HomeContentResponse.LogoBadge(b.getName(), b.getLogo()))
        .toList();
  }

  private List<HomeContentResponse.Testimonial> testimonials(List<ContentItem> items) {
    return fieldsOf(items, "testimonial", TestimonialFields.class).stream()
        .map(
            t ->
                new HomeContentResponse.Testimonial(
                    t.getName(),
                    t.getTitle(),
                    t.getCompany(),
                    t.getQuote(),
                    t.getPhoto(),
                    t.getLogo(),
                    t.getBackgroundImage(),
                    t.getFlankLogo(),
                    t.getFlankFrom(),
                    t.getFlankTo()))
        .toList();
  }

  private List<HomeContentResponse.FooterNavCategory> footerNav(List<ContentItem> items) {
    return fieldsOf(items, "footer_nav_category", FooterNavCategoryFields.class).stream()
        .map(f -> new HomeContentResponse.FooterNavCategory(f.getLabel(), f.getLinks()))
        .toList();
  }
}
