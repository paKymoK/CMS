package com.takypok.contentservice.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostBulkRequest {
  /** The posts to change; at most 100 at a time. */
  @NotEmpty
  @Size(max = 100)
  private List<Long> ids;

  /** PUBLISH, UNPUBLISH (back to draft) or ARCHIVE. */
  @NotBlank private String action;
}
