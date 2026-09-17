package com.takypok.contentservice.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaseStudyUpdateRequest {
  @NotNull private Long id;
  private String date;
  private String image;
  @NotBlank private String title;
  private String category;
  private Integer displayOrder;
  private Boolean active;
  private String status;
}
