package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** One member of the leadership team. Display order sets the order on the About page. */
@Getter
@Setter
@ContentTypeKey("leader")
public class LeaderFields implements ContentFields {
  @NotBlank private String name;
  private String title;

  /** Cut-out portrait (transparent background). */
  private String photo;
}
