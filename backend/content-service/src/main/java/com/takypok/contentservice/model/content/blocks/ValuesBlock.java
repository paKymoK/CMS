package com.takypok.contentservice.model.content.blocks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** "Our Values 4C": tall photo cards with the value's name across the bottom (four are shown). */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValuesBlock implements BlockProps {
  private String eyebrow;
  private String title;
  private String body;
  @Valid private List<Value> items;

  @Getter
  @Setter
  public static class Value {
    /** Use a line break for a two-line label (e.g. "Customer\nCentricity"). */
    @NotBlank private String title;

    private String image;
  }
}
