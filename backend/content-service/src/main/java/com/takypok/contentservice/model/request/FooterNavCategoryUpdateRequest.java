package com.takypok.contentservice.model.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FooterNavCategoryUpdateRequest {
  @NotNull private Long id;
  @NotBlank private String label;
  private JsonNode links;
  private Integer displayOrder;
  private Boolean active;
  private String status;
}
