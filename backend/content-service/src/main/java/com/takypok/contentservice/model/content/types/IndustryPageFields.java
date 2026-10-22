package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * One page of the Industries section (/industries/{slug}), e.g. "Banking, Finance &amp; Insurance".
 * Every industry page uses the same template; this is its content. A section with no content (or
 * listed in {@code hiddenSections}) is simply not rendered.
 */
@Getter
@Setter
@ContentTypeKey("industry_page")
public class IndustryPageFields implements ContentFields {
  @NotBlank
  @Pattern(
      regexp = "^[a-z0-9]+(-[a-z0-9]+)*$",
      message = "use lowercase letters, digits and dashes")
  private String slug;

  /** Name of the industry in lists and menus. */
  @NotBlank private String navLabel;

  // ── Hero ──
  private String heroBreadcrumb;
  @NotBlank private String heroTitle;
  private String heroSubtitle;
  private String ctaLabel;
  private String ctaUrl;

  /** Artwork on the right (transparent background). */
  private String heroImage;

  @Valid private List<StatItem> heroStats;

  // ── Solutions carousel: the arrows move between groups, the tabs between a group's slides ──
  private String solutionsEyebrow;
  private String solutionsTitle;
  private String solutionsBody;
  @Valid private List<SolutionGroup> solutionGroups;

  // ── Reasons to partner (the first three are shown) ──
  private String reasonsEyebrow;
  private String reasonsTitle;
  private String reasonsBody;
  @Valid private List<Reason> reasons;

  // ── Success stories; the left column's heading and figures belong to the page, not a card ──
  private String storiesEyebrow;
  private String storiesTitle;
  private String storiesBody;
  private String storiesHeading;
  @Valid private List<StatItem> storiesMetrics;
  @Valid private List<Story> stories;

  // ── Insights strip (the latest posts themselves are read from the site) ──
  private String insightsEyebrow;
  private String insightsTitle;
  private String insightsBody;
  private String insightsLinkLabel;
  private String insightsLinkUrl;

  /** solutions | reasons | stories | insights */
  private List<String> hiddenSections;

  private String seoTitle;
  private String seoDescription;

  @Getter
  @Setter
  public static class SolutionGroup {
    @NotBlank private String name;
    @Valid private List<Slide> slides;
  }

  @Getter
  @Setter
  public static class Slide {
    /** Name shown on the tab list beside the slide. */
    @NotBlank private String tabLabel;

    /** Large heading on the slide's photo card. */
    private String title;

    /** Partner logo (SVG/PNG) shown in the badge on the slide. */
    private String logo;

    private String image;

    /** First and second bullet columns — one bullet per line. */
    private String columnA;

    private String columnB;
  }

  @Getter
  @Setter
  public static class Reason {
    @NotBlank private String title;
    private String image;
  }

  @Getter
  @Setter
  public static class Story {
    /** Small label above the title (e.g. "BFSI"). */
    private String tag;

    @NotBlank private String title;
    private String body;
    private String image;
    private String linkLabel;
    private String linkUrl;
  }
}
