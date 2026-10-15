package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** One card of the "Industries" strip shown near the bottom of every service page. */
@Getter
@Setter
@ContentTypeKey("industry_card")
public class IndustryCardFields implements ContentFields {
  @NotBlank private String label;

  /** Icon artwork (transparent); when empty the label is shown as text. */
  private String image;

  /** Where the card links to; empty = not a link. */
  private String url;
}
