package com.takypok.contentservice.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfficeUpdateRequest {
  @NotNull private Long id;
  @NotNull private Double lat;
  @NotNull private Double lon;
  @NotBlank private String flagColor;
  private Boolean big;
  @NotBlank private String city;
  @NotBlank private String address;
  private Integer displayOrder;
  private Boolean active;
  private String status;
}
