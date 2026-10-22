package com.takypok.contentservice.model.response;

import com.fasterxml.jackson.databind.JsonNode;

/** One industry page: its authored content, the industry_page item's data as stored. */
public record IndustryPageResponse(JsonNode data) {}
