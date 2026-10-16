package com.takypok.contentservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takypok.contentservice.model.content.types.CaseStudyFields;
import com.takypok.contentservice.model.content.types.OfficeFields;
import com.takypok.contentservice.model.content.types.ServiceCardFields;
import com.takypok.contentservice.model.content.types.TestimonialFields;
import com.takypok.contentservice.model.entity.ContentItem;
import com.takypok.contentservice.model.entity.LogoBadge;
import com.takypok.contentservice.model.response.HomeContentResponse;
import com.takypok.contentservice.service.ContentItemService;
import com.takypok.contentservice.service.FooterNavCategoryService;
import com.takypok.contentservice.service.HomeContentService;
import com.takypok.contentservice.service.LogoBadgeService;
import com.takypok.contentservice.service.PostService;
import com.takypok.contentservice.service.StatService;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Composes the typed tables (stats, posts, logo badges, footer nav) and the generic content_item
 * table into the site's single public HomeContentResponse. content_item is read once per request:
 * service cards, offices, case studies and testimonials are picked out of it by type, and every
 * other type lands in {@code sections}. Uses the Mono.zip array-combinator overload since the
 * fluent zip only goes up to 8 sources.
 */
@Component
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class HomeContentServiceImpl implements HomeContentService {
  private final StatService statService;
  private final PostService postService;
  private final LogoBadgeService logoBadgeService;
  private final FooterNavCategoryService footerNavCategoryService;
  private final ContentItemService contentItemService;
  private final ObjectMapper objectMapper;

  /**
   * Types with their own field in the response; everything else is a generic section. The
   * service_* types are served by /v1/services — there can be dozens, so they stay out of the
   * homepage payload.
   */
  private static final Set<String> DEDICATED_TYPES =
      Set.of("service_card", "office", "case_study", "testimonial");

  @Override
  public Mono<HomeContentResponse> getHomeContent(Long siteId) {
    Mono<List<HomeContentResponse.Stat>> stats =
        statService
            .getPublished(siteId)
            .map(s -> new HomeContentResponse.Stat(s.getValue(), s.getLabel(), s.getNote()))
            .collectList();

    Mono<List<ContentItem>> items =
        contentItemService.getAllPublished(siteId).collectList().cache();
    Mono<List<HomeContentResponse.ServiceCard>> services = items.map(this::toServices);
    Mono<List<HomeContentResponse.Office>> offices = items.map(this::toOffices);
    Mono<List<HomeContentResponse.CaseStudy>> caseStudies = items.map(this::toCaseStudies);

    // Only "insight"-category posts render on the homepage today — other categories exist for
    // future post types (per the decision log) but aren't part of HomeContent yet.
    Mono<List<HomeContentResponse.Insight>> insights =
        postService
            .getPublished(siteId)
            .filter(p -> "insight".equals(p.getCategory()))
            .map(
                p ->
                    new HomeContentResponse.Insight(
                        p.getSlug(), p.getTitle(), p.getDate(), p.getImage()))
            .collectList();

    Mono<List<HomeContentResponse.LogoBadge>> awards =
        logoBadgeService
            .getPublished(siteId, "AWARD")
            .map(HomeContentServiceImpl::toLogoBadge)
            .collectList();
    Mono<List<HomeContentResponse.LogoBadge>> certifications =
        logoBadgeService
            .getPublished(siteId, "CERTIFICATION")
            .map(HomeContentServiceImpl::toLogoBadge)
            .collectList();
    Mono<List<HomeContentResponse.LogoBadge>> partners =
        logoBadgeService
            .getPublished(siteId, "PARTNER")
            .map(HomeContentServiceImpl::toLogoBadge)
            .collectList();

    Mono<List<HomeContentResponse.Testimonial>> testimonials = items.map(this::toTestimonials);

    Mono<List<HomeContentResponse.FooterNavCategory>> footerNav =
        footerNavCategoryService
            .getPublished(siteId)
            .map(f -> new HomeContentResponse.FooterNavCategory(f.getLabel(), f.getLinks()))
            .collectList();

    List<Mono<?>> sources =
        List.of(
            stats,
            services,
            offices,
            caseStudies,
            insights,
            awards,
            certifications,
            partners,
            testimonials,
            footerNav,
            items.map(HomeContentServiceImpl::sectionsOf));

    return Mono.zip(
        sources,
        results ->
            new HomeContentResponse(
                (List<HomeContentResponse.Stat>) results[0],
                (List<HomeContentResponse.ServiceCard>) results[1],
                (List<HomeContentResponse.Office>) results[2],
                (List<HomeContentResponse.CaseStudy>) results[3],
                (List<HomeContentResponse.Insight>) results[4],
                (List<HomeContentResponse.LogoBadge>) results[5],
                (List<HomeContentResponse.LogoBadge>) results[6],
                (List<HomeContentResponse.LogoBadge>) results[7],
                (List<HomeContentResponse.Testimonial>) results[8],
                (List<HomeContentResponse.FooterNavCategory>) results[9],
                (Map<String, List<JsonNode>>) results[10]));
  }

  /**
   * Mirrors {@link #getHomeContent} field-for-field, with every getPublished(siteId) swapped for
   * the same getAll(siteId) the admin list screens already use — draft and inactive rows included.
   * getAll already returns Mono&lt;List&lt;X&gt;&gt; rather than Flux&lt;X&gt;, so this maps over
   * the list directly instead of collectList()-ing a Flux; behaviorally identical composition
   * otherwise. Only ever called from the token-gated preview path (PreviewController) — never
   * cached, never reachable without a valid preview_token.
   */
  @Override
  public Mono<HomeContentResponse> getPreviewContent(Long siteId) {
    Mono<List<HomeContentResponse.Stat>> stats =
        statService
            .getAll(siteId)
            .map(
                list ->
                    list.stream()
                        .map(
                            s ->
                                new HomeContentResponse.Stat(
                                    s.getValue(), s.getLabel(), s.getNote()))
                        .toList());

    Mono<List<ContentItem>> items =
        contentItemService.getEveryItem(siteId).collectList().cache();
    Mono<List<HomeContentResponse.ServiceCard>> services = items.map(this::toServices);
    Mono<List<HomeContentResponse.Office>> offices = items.map(this::toOffices);
    Mono<List<HomeContentResponse.CaseStudy>> caseStudies = items.map(this::toCaseStudies);

    // Same "insight"-only filter as getHomeContent — a preview of a draft "press-release"-category
    // post (once that category is actually used) still shouldn't render on the homepage preview,
    // for the same reason it wouldn't render once published.
    Mono<List<HomeContentResponse.Insight>> insights =
        postService
            .getAll(siteId)
            .map(
                list ->
                    list.stream()
                        .filter(p -> "insight".equals(p.getCategory()))
                        .map(
                            p ->
                                new HomeContentResponse.Insight(
                                    p.getSlug(), p.getTitle(), p.getDate(), p.getImage()))
                        .toList());

    Mono<List<HomeContentResponse.LogoBadge>> awards =
        logoBadgeService
            .getAll(siteId, "AWARD")
            .map(list -> list.stream().map(HomeContentServiceImpl::toLogoBadge).toList());
    Mono<List<HomeContentResponse.LogoBadge>> certifications =
        logoBadgeService
            .getAll(siteId, "CERTIFICATION")
            .map(list -> list.stream().map(HomeContentServiceImpl::toLogoBadge).toList());
    Mono<List<HomeContentResponse.LogoBadge>> partners =
        logoBadgeService
            .getAll(siteId, "PARTNER")
            .map(list -> list.stream().map(HomeContentServiceImpl::toLogoBadge).toList());

    Mono<List<HomeContentResponse.Testimonial>> testimonials = items.map(this::toTestimonials);

    Mono<List<HomeContentResponse.FooterNavCategory>> footerNav =
        footerNavCategoryService
            .getAll(siteId)
            .map(
                list ->
                    list.stream()
                        .map(
                            f ->
                                new HomeContentResponse.FooterNavCategory(
                                    f.getLabel(), f.getLinks()))
                        .toList());

    List<Mono<?>> sources =
        List.of(
            stats,
            services,
            offices,
            caseStudies,
            insights,
            awards,
            certifications,
            partners,
            testimonials,
            footerNav,
            items.map(HomeContentServiceImpl::sectionsOf));

    return Mono.zip(
        sources,
        results ->
            new HomeContentResponse(
                (List<HomeContentResponse.Stat>) results[0],
                (List<HomeContentResponse.ServiceCard>) results[1],
                (List<HomeContentResponse.Office>) results[2],
                (List<HomeContentResponse.CaseStudy>) results[3],
                (List<HomeContentResponse.Insight>) results[4],
                (List<HomeContentResponse.LogoBadge>) results[5],
                (List<HomeContentResponse.LogoBadge>) results[6],
                (List<HomeContentResponse.LogoBadge>) results[7],
                (List<HomeContentResponse.Testimonial>) results[8],
                (List<HomeContentResponse.FooterNavCategory>) results[9],
                (Map<String, List<JsonNode>>) results[10]));
  }

  /** Groups the generic items by content_type, keeping display order inside each group. */
  private static Map<String, List<JsonNode>> sectionsOf(List<ContentItem> items) {
    Map<String, List<JsonNode>> grouped = new LinkedHashMap<>();
    for (ContentItem item : items) {
      String type = item.getContentType();
      if (DEDICATED_TYPES.contains(type) || type.startsWith("service_")) continue;
      grouped.computeIfAbsent(type, k -> new java.util.ArrayList<>()).add(item.getData());
    }
    return grouped;
  }

  private <T> List<T> fieldsOf(List<ContentItem> items, String type, Class<T> fieldsClass) {
    return items.stream()
        .filter(i -> type.equals(i.getContentType()))
        .map(i -> objectMapper.convertValue(i.getData(), fieldsClass))
        .toList();
  }

  private List<HomeContentResponse.ServiceCard> toServices(List<ContentItem> items) {
    return fieldsOf(items, "service_card", ServiceCardFields.class).stream()
        .map(
            s ->
                new HomeContentResponse.ServiceCard(
                    s.getName(), s.getDescription(), s.getImage()))
        .toList();
  }

  private List<HomeContentResponse.Office> toOffices(List<ContentItem> items) {
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

  private List<HomeContentResponse.CaseStudy> toCaseStudies(List<ContentItem> items) {
    return fieldsOf(items, "case_study", CaseStudyFields.class).stream()
        .map(
            c ->
                new HomeContentResponse.CaseStudy(
                    c.getSlug(), c.getTitle(), c.getDate(), c.getCategory(), c.getImage()))
        .toList();
  }

  private List<HomeContentResponse.Testimonial> toTestimonials(List<ContentItem> items) {
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

  private static HomeContentResponse.LogoBadge toLogoBadge(LogoBadge badge) {
    return new HomeContentResponse.LogoBadge(badge.getName(), badge.getLogo());
  }
}
