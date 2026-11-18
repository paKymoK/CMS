package com.takypok.contentservice.model.response;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/**
 * The About page: its authored content (the about_page item's data, growth figures resolved) and
 * the case studies it features, already resolved from their slugs.
 */
public record AboutPageResponse(
    JsonNode data, List<ServicePageResponse.FeaturedCaseStudy> caseStudies) {}
