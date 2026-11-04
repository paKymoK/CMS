package com.takypok.contentservice.model.content.blocks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.takypok.contentservice.model.content.types.StatItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** "Meet CMC Global": the team photo beside the company story, then the growth figures. */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class IntroBlock implements BlockProps {
  private String eyebrow;
  private String title;

  /** Paragraphs separated by blank lines. */
  private String body;

  private String image;

  /** Decorative artwork behind the block. */
  private String decorImage;

  /** Heading above the growth figures (the first five are shown). */
  private String growthTitle;

  /**
   * Where the growth figures come from: {@code custom} (the list below, typed into this page) or
   * {@code library} (the published Stats screen, in display order; the public endpoint swaps them
   * in before answering). Blank means custom.
   */
  @Pattern(regexp = "^(custom|library)$", message = "must be custom or library")
  private String growthSource;

  /**
   * The figures when {@code growthSource} is custom; kept when it is library, so switching back
   * loses nothing.
   */
  @Valid private List<StatItem> growthStats;
}
