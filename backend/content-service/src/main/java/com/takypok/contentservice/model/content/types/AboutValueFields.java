package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** One of the "Our Values 4C" photo cards on the About page. Display order sets the order. */
@Getter
@Setter
@ContentTypeKey("about_value")
public class AboutValueFields implements ContentFields {
  /** Use a line break for a two-line label (e.g. "Customer\nCentricity"). */
  @NotBlank private String title;

  private String image;
}
