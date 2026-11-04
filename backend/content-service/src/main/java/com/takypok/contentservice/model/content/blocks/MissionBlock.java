package com.takypok.contentservice.model.content.blocks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/** The light band with artwork on the left and paragraphs on the right. */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MissionBlock implements BlockProps {
  private String eyebrow;

  /** Paragraphs separated by blank lines. */
  private String body;

  private String image;
}
