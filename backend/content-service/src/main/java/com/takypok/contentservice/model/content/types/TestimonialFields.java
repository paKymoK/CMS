package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** A client quote. Case studies reference one by its content_item id. */
@Getter
@Setter
@ContentTypeKey("testimonial")
public class TestimonialFields implements ContentFields {
  @NotBlank private String name;
  private String company;
  private String photo;

  /** The client's logo on the quote card (dark artwork, drawn on a light background). */
  private String logo;

  /** Abstract artwork behind the quote text. */
  private String backgroundImage;

  /**
   * Logo variant for the brand card that flanks the neighbouring slides, and that card's gradient
   * (#RRGGBB start and end).
   */
  private String flankLogo;

  private String flankFrom;
  private String flankTo;

  private String title;
  private String quote;
}
