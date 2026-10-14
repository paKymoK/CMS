package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** A full-width feature band: eyebrow, large heading, paragraph, an outlined button and a photo. */
@Getter
@Setter
@ContentTypeKey("feature_band")
public class FeatureBandFields implements ContentFields {
  private String eyebrow;
  @NotBlank private String title;
  private String body;
  private String ctaLabel;
  private String ctaUrl;
  private String image;
}
