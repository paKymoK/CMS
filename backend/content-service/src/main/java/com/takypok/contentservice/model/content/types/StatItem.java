package com.takypok.contentservice.model.content.types;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** A headline figure shown verbatim, e.g. value "3000+" with label "Employees". */
@Getter
@Setter
public class StatItem {
  @NotBlank private String value;
  @NotBlank private String label;
}
