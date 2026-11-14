package com.takypok.contentservice.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUpdateRequest {
  @NotNull private Long id;

  /** The categories and tags by id; left out on an update they keep their stored value. */
  private java.util.List<Long> categoryIds;

  private java.util.List<Long> tagIds;
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
  private Boolean featured;
  private String layout;
  private String seoTitle;
  private String seoDescription;
  private String seoImage;
  private Boolean seoNoindex;
  private String seoCanonical;
  private Boolean seoNofollow;
  private String seoOgTitle;
  private String seoOgDescription;
  private String seoJsonLd;
}
