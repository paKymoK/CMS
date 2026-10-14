package com.takypok.contentservice.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestimonialCreateRequest {
  @NotBlank private String name;
  private String company;
  private String photo;
  private String logo;
  private String backgroundImage;
  private String flankLogo;
  private String flankFrom;
  private String flankTo;
  private String title;
  private String quote;
  private Integer displayOrder;
  private Boolean active;
  private String status;
}
