package com.takypok.contentservice.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceCardCreateRequest {
  @NotBlank private String name;
  private String image;
  private Integer displayOrder;
  private Boolean active;
  private String status;
}
