package com.takypok.contentservice.model.content.blocks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** The solutions carousel: the arrows move between groups, the tabs between a group's slides. */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SolutionsBlock implements BlockProps {
  private String eyebrow;
  private String title;
  private String body;
  @Valid private List<Group> groups;

  @Getter
  @Setter
  public static class Group {
    @NotBlank private String name;
    @Valid private List<Slide> slides;
  }

  @Getter
  @Setter
  public static class Slide {
    /** Name shown on the tab list beside the slide. */
    @NotBlank private String tabLabel;

    /** Large heading on the slide's photo card. */
    private String title;

    /** Partner logo (SVG/PNG) shown in the badge on the slide. */
    private String logo;

    private String image;

    /** First and second bullet columns: one bullet per line. */
    private String columnA;

    private String columnB;
  }
}
