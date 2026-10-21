package com.takypok.contentservice.model.content.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * One slide of the homepage hero carousel: finished artwork with its copy baked in, one image per
 * screen class (the site swaps them with media queries), so editors never lay text out. {@code alt}
 * carries the slide's wording for search engines and screen readers. Images are origin-less
 * media-service paths. The admin checks each image's proportions on selection: desktop 1920×699
 * (≥1280px), tablet 1536×864 (768–1279px), phone 750×1000 (&lt;768px).
 *
 * <p>Slides saved before this shape (title, subtitle, layout…) still load — the unknown properties
 * are ignored — but have no image, so the site skips them until they are re-saved.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@ContentTypeKey("hero_slide")
public class HeroSlideFields implements ContentFields {
  /** What the artwork says — also rendered as the slide's visually hidden heading. */
  @NotBlank private String alt;

  /** Optional: the whole slide becomes a link to this URL or anchor (e.g. #contact). */
  private String linkUrl;

  @NotBlank private String image;
  @NotBlank private String tabletImage;
  @NotBlank private String mobileImage;
}
