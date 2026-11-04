package com.takypok.contentservice.model.content.blocks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** "Reasons to partner with us": a slider of a title, a text and two overlapping photos. */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReasonsBlock implements BlockProps {
  private String title;
  private String subtitle;
  @Valid private List<Reason> items;

  @Getter
  @Setter
  public static class Reason {
    @NotBlank private String title;
    private String body;
    private String image;
    private String sideImage;
  }
}
