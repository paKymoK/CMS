package com.takypok.contentservice.model.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentItemUpdateRequest {
  @NotNull private Long id;
  @NotBlank private String contentType;
  private JsonNode data;
  private Integer displayOrder;
  private Boolean active;
  private String status;
}
