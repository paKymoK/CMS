package com.takypok.contentservice.model.content.blocks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Where the latest published insights show. Its content is the site's, not the page's. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HighlightsBlock implements BlockProps {}
