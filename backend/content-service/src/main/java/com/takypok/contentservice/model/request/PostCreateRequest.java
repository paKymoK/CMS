package com.takypok.contentservice.model.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
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
  private Boolean active;
  private String status;
  private String slug;
  private String body;
  private String authorName;
  private String authorRole;
  private String authorBio;
  private String authorAvatar;
  private JsonNode tags;
  private Boolean featured;
}
