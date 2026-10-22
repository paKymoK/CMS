package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * The About page (/about). There is exactly one per site; every section below is its content. A
 * section with no content (or listed in {@code hiddenSections}) is simply not rendered.
 */
@Getter
@Setter
@ContentTypeKey("about_page")
public class AboutPageFields implements ContentFields {
  // ── Hero: breadcrumb, headline, intro line, button, photo and three figures ──
  private String heroBreadcrumb;
  @NotBlank private String heroTitle;
  private String heroSubtitle;
  private String ctaLabel;
  private String ctaUrl;
  private String heroImage;
  @Valid private List<StatItem> heroStats;

  // ── "Meet CMC Global": photo, story and the growth figures ──
  private String introEyebrow;
  private String introTitle;

  /** Paragraphs separated by blank lines. */
  private String introBody;

  private String introImage;

  /** Decorative artwork behind the block. */
  private String introDecorImage;

  /** Heading above the growth figures (the first five are shown). */
  private String growthTitle;

  @Valid private List<StatItem> growthStats;

  // ── Vision band (dark, one centred headline) ──
  private String visionEyebrow;
  private String visionTitle;
  private String visionImage;

  // ── Mission band (light, paragraphs separated by blank lines) ──
  private String missionEyebrow;
  private String missionBody;
  private String missionImage;

  // ── Leadership team ──
  private String leadersEyebrow;
  private String leadersTitle;

  /** Ring graphic behind the portraits. */
  private String leadersRingImage;

  @Valid private List<Leader> leaders;

  // ── "Our Values 4C" (the first four are shown) ──
  private String valuesEyebrow;
  private String valuesTitle;
  private String valuesBody;
  @Valid private List<Value> values;

  /** intro | vision | mission | leaders | values */
  private List<String> hiddenSections;

  private String seoTitle;
  private String seoDescription;

  @Getter
  @Setter
  public static class Leader {
    @NotBlank private String name;
    private String title;

    /** Cut-out portrait (transparent background). */
    private String photo;
  }

  @Getter
  @Setter
  public static class Value {
    /** Use a line break for a two-line label (e.g. "Customer\nCentricity"). */
    @NotBlank private String title;

    private String image;
  }
}
