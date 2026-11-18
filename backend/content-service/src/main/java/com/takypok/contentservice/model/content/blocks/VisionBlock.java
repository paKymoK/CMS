package com.takypok.contentservice.model.content.blocks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/** The dark band with one centred headline over a photo. */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VisionBlock implements SingleUseBlock {
  private String eyebrow;
  private String title;
  private String image;
}
