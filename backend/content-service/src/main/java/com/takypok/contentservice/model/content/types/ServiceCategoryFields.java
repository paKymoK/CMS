package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * A group in the header's Services menu (e.g. "Digital Engineering"). Display order sets the column
 * order; a service page joins a category through its slug.
 */
@Getter
@Setter
@ContentTypeKey("service_category")
public class ServiceCategoryFields implements ContentFields {
  @NotBlank
  @Pattern(
      regexp = "^[a-z0-9]+(-[a-z0-9]+)*$",
      message = "use lowercase letters, digits and dashes")
  private String slug;

  @NotBlank private String name;
}
