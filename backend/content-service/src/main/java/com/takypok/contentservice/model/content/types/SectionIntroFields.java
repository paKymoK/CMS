package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * The eyebrow / heading / paragraph that opens a homepage section. {@code section} ties the item to
 * the section's in-page anchor (whatwedo, testimonials, ...), so every section's intro copy is
 * editable in one place without a table per section.
 */
@Getter
@Setter
@ContentTypeKey("section_intro")
public class SectionIntroFields implements ContentFields {
  @NotBlank
  @Pattern(regexp = "^[a-z0-9-]+$")
  private String section;

  private String eyebrow;
  @NotBlank private String title;
  private String body;

  /** Small label above a section's list (e.g. "Latest Case Study"). */
  private String kicker;

  /** The section's "see all" link. */
  private String linkLabel;

  private String linkUrl;

  /** Decorative artwork used by the section (e.g. the rings behind the leadership team). */
  private String image;
}
