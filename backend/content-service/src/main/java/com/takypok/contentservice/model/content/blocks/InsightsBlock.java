package com.takypok.contentservice.model.content.blocks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/** The insights strip: its intro is the page's, the latest posts themselves are the site's. */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class InsightsBlock implements BlockProps {
  private String eyebrow;
  private String title;
  private String body;
  private String linkLabel;
  private String linkUrl;
}
