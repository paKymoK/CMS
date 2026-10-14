package com.takypok.contentservice.model.response;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Map;

/**
 * {@code sections} carries every generic content_item type (hero_slide, assistant_band, ...) keyed
 * by content_type, each list in display order and holding the items' validated {@code data} objects
 * — so a new design section is a content_type row plus website code, with no change here.
 *
 * <p>Mirrors the landing page's content/home/types.ts HomeContent field-for-field. Only PUBLISHED +
 * active rows ever land here — draft/inactive content never reaches the public API. Banner is
 * deliberately excluded: it's not part of HomeContent yet (unused by the current homepage build,
 * per the plan's decision log), even though it's fully manageable via its own admin endpoint.
 */
public record HomeContentResponse(
    List<Stat> stats,
    List<ServiceCard> services,
    List<Office> offices,
    List<CaseStudy> caseStudies,
    List<Insight> insights,
    List<LogoBadge> awards,
    List<LogoBadge> certifications,
    List<LogoBadge> partners,
    List<Testimonial> testimonials,
    List<FooterNavCategory> footerNav,
    Map<String, List<JsonNode>> sections) {

  public record Stat(String value, String label, String note) {}

  public record ServiceCard(String name, String description, String image) {}

  public record Office(
      String city,
      Double lat,
      Double lon,
      String flag,
      Boolean big,
      String address,
      String image) {}

  public record CaseStudy(String slug, String title, String date, String category, String image) {}

  /** category="insight" posts only — matches content/home/types.ts's InsightItem shape. */
  public record Insight(String slug, String title, String date, String image) {}

  public record LogoBadge(String name, String logo) {}

  public record Testimonial(
      String name,
      String title,
      String company,
      String quote,
      String photo,
      String logo,
      String backgroundImage,
      String flankLogo,
      String flankFrom,
      String flankTo) {}

  public record FooterNavCategory(String label, JsonNode links) {}
}
