package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * "Meet CMC Global" block of the About page: photo, story and the heading for the growth figures.
 */
@Getter
@Setter
@ContentTypeKey("about_intro")
public class AboutIntroFields implements ContentFields {
  private String eyebrow;
  @NotBlank private String title;

  /** Paragraphs separated by blank lines. */
  private String body;

  private String image;

  /** Decorative artwork behind the block. */
  private String decorImage;

  /** Heading above the growth figures, which come from the Stats screen. */
  private String statsTitle;
}
