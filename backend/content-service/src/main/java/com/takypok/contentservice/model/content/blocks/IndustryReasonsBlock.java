package com.takypok.contentservice.model.content.blocks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** A dark band of tall photo cards with numbered captions; the first three reasons are shown. */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class IndustryReasonsBlock implements BlockProps {
  private String eyebrow;
  private String title;
  private String body;
  @Valid private List<Reason> items;

  @Getter
  @Setter
  public static class Reason {
    @NotBlank private String title;
    private String image;
  }
}
