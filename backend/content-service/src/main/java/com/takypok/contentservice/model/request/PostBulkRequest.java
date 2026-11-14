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
public class PostBulkRequest {
  /** The posts to change; at most 100 at a time. */
  @NotEmpty
  @Size(max = 100)
  private List<Long> ids;

  /**
   * PUBLISH, UNPUBLISH (back to draft), ARCHIVE, or for the Trash: TRASH, RESTORE (back to draft)
   * and DELETE_FOREVER (of items already in the Trash); or ASSIGN_CATEGORY, ASSIGN_TAG,
   * REMOVE_CATEGORY and REMOVE_TAG with {@code taxonomyId}.
   */
  @NotBlank private String action;

  /**
   * ASSIGN_CATEGORY, ASSIGN_TAG, REMOVE_CATEGORY, REMOVE_TAG only: the category or tag to add or
   * take off.
   */
  private Long taxonomyId;

  /** RESTORE only: the address to restore a post under instead of its own, by post id. */
  private Map<Long, String> slugs;
}
