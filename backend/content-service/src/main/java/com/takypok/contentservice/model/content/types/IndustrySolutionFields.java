package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * One slide of the industry page's solutions carousel; display order sets the slide (and tab)
 * order.
 */
@Getter
@Setter
@ContentTypeKey("industry_solution")
public class IndustrySolutionFields implements ContentFields {
  /**
   * Carousel page this slide belongs to; the arrows move between groups, the tabs between a group's
   * slides.
   */
  @NotBlank private String group;

  /** Name shown on the tab list beside the slide. */
  @NotBlank private String tabLabel;

  /** Large heading on the slide's photo card. */
  private String title;

  /** Partner logo (SVG/PNG) shown in the badge on the slide. */
  private String logo;

  private String image;

  /** First and second bullet columns — one bullet per line. */
  private String columnA;

  private String columnB;
}
