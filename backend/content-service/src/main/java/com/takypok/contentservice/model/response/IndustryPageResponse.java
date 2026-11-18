package com.takypok.contentservice.model.response;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/**
 * One industry page: its authored content (the industry_page item's data, as stored) and the case
 * studies it features, already resolved from their slugs.
 */
public record IndustryPageResponse(
    JsonNode data, List<ServicePageResponse.FeaturedCaseStudy> caseStudies) {}
