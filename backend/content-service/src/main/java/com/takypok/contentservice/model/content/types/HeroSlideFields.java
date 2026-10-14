package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * One slide of the homepage hero carousel. {@code layout} picks a text-placement preset from the
 * design (type size, weight and colour come with it, so an editor can't break the design by
 * accident); the rest is plain copy and media. Images are origin-less media-service paths.
 */
@Getter
@Setter
@ContentTypeKey("hero_slide")
public class HeroSlideFields implements ContentFields {
  private static final String HEX = "^#[0-9a-fA-F]{6}$";

  @NotBlank private String title;
  private String subtitle;
  private String ctaLabel;
  private String ctaUrl;

  /** center | left | right | left-wide | bottom-left */
  @Pattern(regexp = "^(center|left|right|left-wide|bottom-left)$")
  private String layout = "center";

  /** white | blue | light — the CTA pill's look. */
  @Pattern(regexp = "^(white|blue|light)$")
  private String ctaStyle = "white";

  private String backgroundImage;

  /** Transparent artwork drawn over backgroundColor/backgroundImage (e.g. the swirl on slide 1). */
  private String overlayImage;

  @Pattern(regexp = HEX)
  private String backgroundColor;

  /** When set, the background is a diagonal gradient from backgroundColor to this. */
  @Pattern(regexp = HEX)
  private String backgroundColorEnd;
}
