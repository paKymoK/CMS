package com.takypok.authservice.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentCreateRequest {
  @NotBlank private String name;
  private String head;
  private String location;
}
