package com.takypok.contentservice.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaseStudyCreateRequest {
  private String date;
  private String image;
  @NotBlank private String title;
  private String category;
  private Integer displayOrder;
  private Boolean active;
  private String status;
}
