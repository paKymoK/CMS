package com.takypok.contentservice.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** Create or edit a category or tag. On an edit, a field left out keeps its stored value. */
@Getter
@Setter
public class TaxonomyRequest {
  @NotBlank
  @Size(max = 100)
  private String name;

  /** Left blank, made from the name. */
  private String slug;

  @Size(max = 500)
  private String description;

  /** Categories only: the parent's id; a blank or null value means top level. */
  private Long parentId;

  /** Edit only: true when {@code parentId} was sent (so a null can mean "no parent"). */
  private boolean parentIdSent;

  /**
   * ACTIVE or INACTIVE; left out: a new item is Active (unless its parent is not), an edit keeps
   * it.
   */
  private String status;

  /** Edit only: the version the form was loaded with, to refuse a save over a newer one. */
  private Integer version;

  public void setParentId(Long parentId) {
    this.parentId = parentId;
    this.parentIdSent = true;
  }
}
