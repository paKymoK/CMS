package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** Hero of the About page: breadcrumb, headline, intro line, button, photo and three figures. */
@Getter
@Setter
@ContentTypeKey("about_hero")
public class AboutHeroFields implements ContentFields {
  private String breadcrumb;
  @NotBlank private String title;
  private String subtitle;
  private String ctaLabel;
  private String ctaUrl;
  private String image;
  @Valid private List<StatItem> stats;
}
