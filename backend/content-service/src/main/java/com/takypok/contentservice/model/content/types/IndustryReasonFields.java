package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** One "reason to partner with us" card on the industry page. */
@Getter
@Setter
@ContentTypeKey("industry_reason")
public class IndustryReasonFields implements ContentFields {
  @NotBlank private String title;
  private String image;
}
