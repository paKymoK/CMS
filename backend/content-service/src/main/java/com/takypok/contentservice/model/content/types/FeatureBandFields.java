package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * A full-width feature band: eyebrow, large heading, paragraph, an outlined button and a backdrop —
 * a photo, or a looping video when {@code mediaType} is "video" (the photo then doubles as the
 * poster shown before/instead of playback).
 */
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

  /** image | video — which backdrop to render. Absent means image. */
  @Pattern(regexp = "^(image|video)$")
  private String mediaType;

  /** Origin-less media-service HLS playlist path of an uploaded video. */
  @Pattern(regexp = "^/media-service/v1/videos/[0-9a-zA-Z-]+/master\\.m3u8$")
  private String video;

  /**
   * Heading colour; with titleColorEnd set, a gradient from one to the other. Absent = the
   * design's.
   */
  @Pattern(regexp = "^#[0-9a-fA-F]{6}$")
  private String titleColor;

  @Pattern(regexp = "^#[0-9a-fA-F]{6}$")
  private String titleColorEnd;

  /** Direction of the heading gradient, in degrees. */
  @Min(0)
  @Max(360)
  private Integer titleGradientAngle;
}
