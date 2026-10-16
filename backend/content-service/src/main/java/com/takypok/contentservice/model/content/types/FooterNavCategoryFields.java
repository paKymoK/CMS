package com.takypok.contentservice.model.content.types;

import com.fasterxml.jackson.databind.JsonNode;
import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** One column of the footer navigation. */
@Getter
@Setter
@ContentTypeKey("footer_nav_category")
public class FooterNavCategoryFields implements ContentFields {
  @NotBlank private String label;

  /** string[] of link labels. */
  private JsonNode links;
}
