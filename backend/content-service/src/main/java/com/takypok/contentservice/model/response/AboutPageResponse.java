package com.takypok.contentservice.model.response;

import com.fasterxml.jackson.databind.JsonNode;

/** The About page: its authored content, the about_page item's data as stored. */
public record AboutPageResponse(JsonNode data) {}
