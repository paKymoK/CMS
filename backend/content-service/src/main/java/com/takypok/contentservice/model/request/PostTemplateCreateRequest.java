package com.takypok.contentservice.model.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostTemplateCreateRequest {
  @NotBlank private String name;
  private String description;
  private String layout;
  private String category;
  private JsonNode tags;
  private String body;
}
