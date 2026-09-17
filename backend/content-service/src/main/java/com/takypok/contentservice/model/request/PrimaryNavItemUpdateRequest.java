package com.takypok.contentservice.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrimaryNavItemUpdateRequest {
  @NotNull private Long id;
  private Boolean hasDropdown;
  @NotBlank private String label;
  private Integer displayOrder;
  private Boolean active;
  private String status;
}
