package com.takypok.contentservice.model.request;

import lombok.Getter;
import lombok.Setter;

/** The basic properties editable on the list: a field left out keeps its stored value. */
@Getter
@Setter
public class PageQuickEditRequest {
  private String title;
  private String slug;

  /** DRAFT, PUBLISHED or ARCHIVED (SCHEDULED needs a time: use the editor). */
  private String status;

  /** The parent page's id; a null with {@code parentIdSent} means "no parent". */
  private Long parentId;

  private boolean parentIdSent;

  public void setParentId(Long parentId) {
    this.parentId = parentId;
    this.parentIdSent = true;
  }

  /** The version the row was loaded with, to refuse an edit over a newer one. */
  private Integer version;
}
