package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Hero of the Banking, Finance &amp; Insurance page: breadcrumb, headline, intro line, button,
 * artwork and two figures.
 */
@Getter
@Setter
@ContentTypeKey("industry_hero")
public class IndustryHeroFields implements ContentFields {
  private String breadcrumb;
  @NotBlank private String title;
  private String subtitle;
  private String ctaLabel;
  private String ctaUrl;

  /** Artwork on the right (transparent background). */
  private String image;

  @Valid private List<StatItem> stats;
}
