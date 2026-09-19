package com.takypok.contentservice.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceCardUpdateRequest {
  @NotNull private Long id;
  @NotBlank private String name;
  private String image;
  private Integer displayOrder;
  private Boolean active;
  private String status;
}
