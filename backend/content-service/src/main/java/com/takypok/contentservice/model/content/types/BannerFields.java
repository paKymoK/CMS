package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** Not used by the current homepage build — available for other pages. */
@Getter
@Setter
@ContentTypeKey("banner")
public class BannerFields implements ContentFields {
  @NotBlank private String title;
  private String body;
  private String image;
  private String ctaLabel;
  private String ctaUrl;

  /** ISO-8601 instants bounding when the banner shows. */
  private String activeFrom;

  private String activeUntil;
}
