package com.takypok.contentservice.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaxonomyBulkRequest {
  /** The categories or tags to change; at most 100 at a time. */
  @NotEmpty
  @Size(max = 100)
  private List<Long> ids;

  /**
   * ACTIVATE, DEACTIVATE, DELETE (to the Trash), REMOVE_FROM_POSTS; or for the Trash: RESTORE and
   * DELETE_FOREVER.
   */
  @NotBlank private String action;

  /** RESTORE only: the address to restore an item under instead of its own, by id. */
  private Map<Long, String> slugs;
}
