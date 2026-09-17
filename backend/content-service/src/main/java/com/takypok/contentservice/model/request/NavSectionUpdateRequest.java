package com.takypok.contentservice.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NavSectionUpdateRequest {
  @NotNull private Long id;
  @NotBlank private String anchor;
  @NotBlank private String label;
  private Integer displayOrder;
  private Boolean active;
  private String status;
}
