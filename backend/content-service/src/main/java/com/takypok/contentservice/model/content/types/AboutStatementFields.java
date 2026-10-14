package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/** The Vision (dark, centred headline) and Mission (light, paragraph) bands of the About page. */
@Getter
@Setter
@ContentTypeKey("about_statement")
public class AboutStatementFields implements ContentFields {
  /** vision | mission — picks the layout. */
  @NotBlank
  @Pattern(regexp = "^(vision|mission)$")
  private String variant;

  private String eyebrow;
  private String title;
  private String body;
  private String image;
}
