package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/** A pin on the homepage globe. */
@Getter
@Setter
@ContentTypeKey("office")
public class OfficeFields implements ContentFields {
  @NotBlank private String city;
  @NotBlank private String address;
  @NotNull private Double lat;
  @NotNull private Double lon;

  /** Placeholder swatch until real flag assets land. */
  @NotBlank private String flagColor;

  /** Renders larger with a white border (headquarters). */
  private Boolean big;

  /** The globe hover card falls back to a placeholder when absent. */
  private String image;
}
