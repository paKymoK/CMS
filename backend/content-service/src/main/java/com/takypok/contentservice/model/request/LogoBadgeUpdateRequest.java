package com.takypok.contentservice.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoBadgeUpdateRequest {
  @NotNull private Long id;

  @NotBlank
  @Pattern(regexp = "AWARD|CERTIFICATION|PARTNER")
  private String type;

  @NotBlank private String name;
  @NotBlank private String logo;
  private Integer displayOrder;
  private Boolean active;
  private String status;
}
