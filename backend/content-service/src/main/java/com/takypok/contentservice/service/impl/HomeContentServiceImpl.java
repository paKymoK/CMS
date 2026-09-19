package com.takypok.contentservice.service.impl;

import com.takypok.contentservice.model.entity.LogoBadge;
import com.takypok.contentservice.model.response.HomeContentResponse;
import com.takypok.contentservice.service.CaseStudyService;
import com.takypok.contentservice.service.FooterNavCategoryService;
import com.takypok.contentservice.service.HomeContentService;
import com.takypok.contentservice.service.LogoBadgeService;
import com.takypok.contentservice.service.OfficeService;
import com.takypok.contentservice.service.PostService;
import com.takypok.contentservice.service.ServiceCardService;
import com.takypok.contentservice.service.StatService;
import com.takypok.contentservice.service.TestimonialService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Composes all 8 content repositories (10 public sources, since award/certification/partner all
 * read from the one LogoBadge table) into the site's single public HomeContentResponse. Uses the
 * Mono.zip array-combinator overload since the fluent zip only goes up to 8 sources and there are
 * 10 here.
 */
@Component
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class HomeContentServiceImpl implements HomeContentService {
  private final StatService statService;
  private final ServiceCardService serviceCardService;
  private final OfficeService officeService;
  private final CaseStudyService caseStudyService;
  private final PostService postService;
  private final LogoBadgeService logoBadgeService;
  private final TestimonialService testimonialService;
  private final FooterNavCategoryService footerNavCategoryService;

  @Override
  public Mono<HomeContentResponse> getHomeContent(Long siteId) {
    Mono<List<HomeContentResponse.Stat>> stats =
        statService
            .getPublished(siteId)
            .map(s -> new HomeContentResponse.Stat(s.getValue(), s.getLabel(), s.getNote()))
            .collectList();

    Mono<List<HomeContentResponse.ServiceCard>> services =
        serviceCardService
            .getPublished(siteId)
            .map(s -> new HomeContentResponse.ServiceCard(s.getName()))
            .collectList();

    Mono<List<HomeContentResponse.Office>> offices =
        officeService
            .getPublished(siteId)
            .map(
                o ->
                    new HomeContentResponse.Office(
                        o.getCity(),
                        o.getLat(),
                        o.getLon(),
                        o.getFlagColor(),
                        o.getBig(),
                        o.getAddress(),
                        o.getImage()))
            .collectList();

    Mono<List<HomeContentResponse.CaseStudy>> caseStudies =
        caseStudyService
            .getPublished(siteId)
            .map(
                c ->
                    new HomeContentResponse.CaseStudy(
                        c.getTitle(), c.getDate(), c.getCategory(), c.getImage()))
            .collectList();

    // Only "insight"-category posts render on the homepage today — other categories exist for
    // future post types (per the decision log) but aren't part of HomeContent yet.
    Mono<List<HomeContentResponse.Insight>> insights =
        postService
            .getPublished(siteId)
            .filter(p -> "insight".equals(p.getCategory()))
            .map(p -> new HomeContentResponse.Insight(p.getTitle(), p.getImage()))
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

    Mono<List<HomeContentResponse.Testimonial>> testimonials =
        testimonialService
            .getPublished(siteId)
            .map(
                t ->
                    new HomeContentResponse.Testimonial(
                        t.getName(),
                        t.getTitle(),
                        t.getCompany(),
                        t.getQuote(),
                        t.getPhoto(),
                        t.getFlankLogos()))
            .collectList();

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
            footerNav);

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
                (List<HomeContentResponse.FooterNavCategory>) results[9]));
  }

  private static HomeContentResponse.LogoBadge toLogoBadge(LogoBadge badge) {
    return new HomeContentResponse.LogoBadge(badge.getName(), badge.getLogo());
  }
}
