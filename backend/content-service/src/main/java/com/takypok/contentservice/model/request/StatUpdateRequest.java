package com.takypok.contentservice.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatUpdateRequest {
  @NotNull private Long id;
  @NotBlank private String value;
  @NotBlank private String label;
  private String note;
  private Integer displayOrder;
  private Boolean active;
  private String status;
}
