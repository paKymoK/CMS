package com.takypok.contentservice.model.content.blocks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** A heading and a row of image cards, each with a title and a short description. */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CapabilitiesBlock implements BlockProps {
  private String eyebrow;
  private String title;
  private String subtitle;
  @Valid private List<Card> items;

  @Getter
  @Setter
  public static class Card {
    @NotBlank private String title;
    private String body;
    private String image;
  }
}
