package com.takypok.contentservice.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrimaryNavItemCreateRequest {
  private Boolean hasDropdown;
  @NotBlank private String label;
  private Integer displayOrder;
  private Boolean active;
  private String status;
}
