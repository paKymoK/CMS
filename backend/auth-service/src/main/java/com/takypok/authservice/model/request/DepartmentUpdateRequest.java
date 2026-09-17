package com.takypok.authservice.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentUpdateRequest {
  @NotNull private Long id;
  @NotBlank private String name;
  private String head;
  private String location;
}
