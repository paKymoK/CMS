package com.takypok.contentservice.model.content.blocks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** A looping strip of numbered step cards. */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessBlock implements BlockProps {
  private String title;
  private String subtitle;
  @Valid private List<Step> steps;

  @Getter
  @Setter
  public static class Step {
    @NotBlank private String title;
    private String image;
  }
}
