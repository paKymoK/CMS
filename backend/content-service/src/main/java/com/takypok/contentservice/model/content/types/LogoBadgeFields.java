package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * A logo in one of the homepage's recognition strips. Awards, certifications and partners share
 * this shape and differ only by content type ({@link AwardFields}, {@link CertificationFields},
 * {@link PartnerFields}).
 */
@Getter
@Setter
public class LogoBadgeFields implements ContentFields {
  @NotBlank private String name;
  @NotBlank private String logo;
}
