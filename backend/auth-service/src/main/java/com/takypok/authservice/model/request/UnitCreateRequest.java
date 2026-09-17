package com.takypok.authservice.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnitCreateRequest {
  @NotBlank private String name;
  @NotNull private Long departmentId;
  private String head;
  private String location;
}
