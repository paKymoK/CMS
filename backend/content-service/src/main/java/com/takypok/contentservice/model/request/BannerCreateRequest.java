package com.takypok.contentservice.model.request;

import jakarta.validation.constraints.NotBlank;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BannerCreateRequest {
  @NotBlank private String title;
  private String body;
  private String image;
  private String ctaLabel;
  private String ctaUrl;
  private ZonedDateTime activeFrom;
  private ZonedDateTime activeUntil;
  private Integer displayOrder;
  private Boolean active;
  private String status;
}
