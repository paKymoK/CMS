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
 * One block of a service page, stored as {@code {id, type, hidden, props}}. {@code type} picks the
 * class of {@code props}; a type not listed here cannot be stored, and unknown props are dropped
 * like on every other content type.
 */
@Getter
@Setter
public class ServiceBlock {
  /** Chosen by the editor so a block keeps its identity while it is moved. */
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
    @JsonSubTypes.Type(value = CapabilitiesBlock.class, name = "capabilities"),
    @JsonSubTypes.Type(value = ProcessBlock.class, name = "process"),
    @JsonSubTypes.Type(value = StoriesBlock.class, name = "stories"),
    @JsonSubTypes.Type(value = ReasonsBlock.class, name = "reasons"),
    @JsonSubTypes.Type(value = IndustriesBlock.class, name = "industries"),
    @JsonSubTypes.Type(value = HighlightsBlock.class, name = "highlights")
  })
  private BlockProps props;
}
