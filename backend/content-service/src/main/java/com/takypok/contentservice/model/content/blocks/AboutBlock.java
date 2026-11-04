package com.takypok.contentservice.model.content.blocks;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * One block of the About page, stored as {@code {id, type, hidden, props}} (see {@link
 * ServiceBlock}).
 */
@Getter
@Setter
public class AboutBlock {
  @NotBlank
  @Pattern(
      regexp = "^[A-Za-z0-9_-]{1,64}$",
      message = "use letters, digits, dashes and underscores")
  private String id;

  /** Kept in the page but not shown on the website. */
  private Boolean hidden;

  @NotNull
  @Valid
  @JsonTypeInfo(
      use = JsonTypeInfo.Id.NAME,
      include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
      property = "type")
  @JsonSubTypes({
    @JsonSubTypes.Type(value = IntroBlock.class, name = "intro"),
    @JsonSubTypes.Type(value = VisionBlock.class, name = "vision"),
    @JsonSubTypes.Type(value = MissionBlock.class, name = "mission"),
    @JsonSubTypes.Type(value = LeadersBlock.class, name = "leaders"),
    @JsonSubTypes.Type(value = ValuesBlock.class, name = "values")
  })
  private BlockProps props;
}
