package com.takypok.contentservice.model.content.blocks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.takypok.contentservice.model.content.types.StatItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Success stories typed into the page (unlike {@link StoriesBlock}, which features case studies).
 * The left column's heading and figures belong to the block, not to a card.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class IndustryStoriesBlock implements BlockProps {
  private String eyebrow;
  private String title;
  private String body;
  private String heading;
  @Valid private List<StatItem> metrics;
  @Valid private List<Story> items;

  @Getter
  @Setter
  public static class Story {
    /** Small label above the title (e.g. "BFSI"). */
    private String tag;

    @NotBlank private String title;
    private String body;
    private String image;
    private String linkLabel;
    private String linkUrl;
  }
}
