package com.takypok.contentservice.model.content.blocks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** The leadership team: cut-out portraits on cards, in list order. */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeadersBlock implements BlockProps {
  private String eyebrow;
  private String title;

  /** Ring graphic behind the portraits. */
  private String ringImage;

  @Valid private List<Leader> items;

  @Getter
  @Setter
  public static class Leader {
    @NotBlank private String name;
    private String title;

    /** Cut-out portrait (transparent background). */
    private String photo;
  }
}
