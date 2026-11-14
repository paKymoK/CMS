package com.takypok.contentservice.model.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentItemUpdateRequest {
  @NotNull private Long id;
  @NotBlank private String contentType;
  private JsonNode data;
  private Integer displayOrder;
  private String status;

  /**
   * Pages only: for SCHEDULED, when it goes live; and when a live one is taken off the site
   * (archived).
   */
  private java.time.Instant publishAt;

  private java.time.Instant unpublishAt;
  private Integer version;

  /**
   * Pages only: on a PUBLISHED page, store {@code data} as a pending draft and leave the live
   * content, status and times alone. Ignored for a page that is not live.
   */
  private Boolean saveAsDraft;

  /**
   * Set {@code data} but leave a pending draft as it is (used by the quick edit, which patches both
   * itself).
   */
  private Boolean keepDraft;
}
