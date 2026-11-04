package com.takypok.contentservice.model.content.blocks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Where the site's industries strip shows. Its content is the site's, not the page's. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IndustriesBlock implements BlockProps {}
