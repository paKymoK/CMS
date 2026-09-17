package com.takypok.contentservice.model.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestimonialCreateRequest {
  @NotBlank private String name;
  private String company;
  private String photo;
  private JsonNode flankLogos;
  private String title;
  private String quote;
  private Integer displayOrder;
  private Boolean active;
  private String status;
}
