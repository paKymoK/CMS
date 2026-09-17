package com.takypok.contentservice.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatCreateRequest {
  @NotBlank private String value;
  @NotBlank private String label;
  private String note;
  private Integer displayOrder;
  private Boolean active;
  private String status;
}
