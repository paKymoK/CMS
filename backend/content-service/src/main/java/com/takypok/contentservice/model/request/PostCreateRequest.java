package com.takypok.contentservice.model.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateRequest {
  private String category;
  private String image;
  @NotBlank private String title;
  private String excerpt;
  private String date;
  private Integer displayOrder;
  private String status;

  /** For SCHEDULED: when it goes live. For PUBLISHED or SCHEDULED: when it is archived. */
  private Instant publishAt;

  private Instant unpublishAt;
  private String slug;
  private String body;
  private String authorName;
  private String authorRole;
  private String authorBio;
  private String authorAvatar;
  private JsonNode tags;
  private Boolean featured;
  private String layout;
  private String seoTitle;
  private String seoDescription;
  private String seoImage;
  private Boolean seoNoindex;
}
