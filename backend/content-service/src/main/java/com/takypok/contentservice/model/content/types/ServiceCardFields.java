package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** One card of the homepage "What we do" section. */
@Getter
@Setter
@ContentTypeKey("service_card")
public class ServiceCardFields implements ContentFields {
  @NotBlank private String name;

  /** Revealed on hover/tap over the card. */
  private String description;

  /** Falls back to a design placeholder when absent. */
  private String image;
}
