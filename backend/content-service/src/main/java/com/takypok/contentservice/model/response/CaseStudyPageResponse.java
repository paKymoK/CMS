package com.takypok.contentservice.model.response;

import java.util.List;

/**
 * One page of the public Case Studies listing. {@code categories} is every category in use across
 * the site's published case studies (not just this page's), for the filter dropdown.
 */
public record CaseStudyPageResponse(
    List<CaseStudyCardResponse> items,
    int page,
    int size,
    long totalItems,
    int totalPages,
    List<String> categories) {}
