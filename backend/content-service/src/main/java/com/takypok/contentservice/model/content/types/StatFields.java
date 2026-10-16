package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** A headline figure on the homepage, rendered verbatim (e.g. value "32+", label "Years"). */
@Getter
@Setter
@ContentTypeKey("stat")
public class StatFields implements ContentFields {
  @NotBlank private String value;
  @NotBlank private String label;
  private String note;
}
