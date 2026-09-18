package com.takypok.chatservice.model;

import java.util.List;

/**
 * Mirrors content-service's HomeContentResponse field-for-field, for deserializing its public GET
 * /v1/home response during ingestion. A duplicated DTO, not a shared module — same "fork, accept
 * drift" reasoning as the rest of this platform's cross-service duplication.
 */
public record HomeContentDto(
    List<PrimaryNavItem> primaryNav,
    List<Object> navSections,
    List<Stat> stats,
    List<ServiceCard> services,
    List<Office> offices,
    List<CaseStudy> caseStudies,
    List<Insight> insights,
    List<LogoBadge> awards,
    List<LogoBadge> certifications,
    List<LogoBadge> partners,
    List<Testimonial> testimonials,
    List<Object> footerNav) {

  public record PrimaryNavItem(String label, Boolean hasDropdown) {}

  public record Stat(String value, String label, String note) {}

  public record ServiceCard(String name) {}

  public record Office(
      String city, Double lat, Double lon, String flag, Boolean big, String address) {}

  public record CaseStudy(String title, String date, String category, String image) {}

  public record Insight(String title, String image) {}

  public record LogoBadge(String name, String logo) {}

  public record Testimonial(
      String name, String title, String company, String quote, String photo) {}
}
