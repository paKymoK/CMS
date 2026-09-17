package com.takypok.contentservice.model.request;

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
}
