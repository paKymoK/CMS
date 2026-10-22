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
 * One page of the Services section (/services/{category}/{slug}), e.g. "Web App Development". Every
 * service page uses the same template; this is its content. A section with no content (or listed in
 * {@code hiddenSections}) is simply not rendered.
 */
@Getter
@Setter
@ContentTypeKey("service_page")
public class ServicePageFields implements ContentFields {
  @NotBlank
  @Pattern(
      regexp = "^[a-z0-9]+(-[a-z0-9]+)*$",
      message = "use lowercase letters, digits and dashes")
  private String slug;

  /** Slug of the service_category this page belongs to. */
  @NotBlank private String category;

  /** Name in the header menu (e.g. "Web App Development"). */
  @NotBlank private String navLabel;

  /** Shorter name for the breadcrumb (e.g. "Web App"); defaults to the menu name. */
  private String shortLabel;

  // ── Hero ──
  @NotBlank private String heroTitle;
  private String heroSubtitle;
  private String ctaLabel;
  private String ctaUrl;
  private String heroImage;
  @Valid private List<StatItem> heroStats;

  // ── Capabilities ──
  private String capabilitiesEyebrow;
  private String capabilitiesTitle;
  private String capabilitiesSubtitle;
  @Valid private List<Capability> capabilities;

  // ── Process ──
  private String processTitle;
  private String processSubtitle;
  @Valid private List<ProcessStep> processSteps;

  // ── Success stories (case study slugs; figures come from the first one) ──
  private String storiesHeading;
  private List<String> caseStudies;

  // ── Reasons to partner ──
  private String reasonsTitle;
  private String reasonsSubtitle;
  @Valid private List<Reason> reasons;

  /** capabilities | process | stories | reasons | industries | highlights */
  private List<String> hiddenSections;

  /** Search-engine title: when set it is the whole page title. */
  private String seoTitle;

  private String seoDescription;

  /** Social-share image (media URL): when blank the page's main image is used. */
  private String seoImage;

  /** Asks search engines not to index the page. */
  private Boolean seoNoindex;

  @Getter
  @Setter
  public static class Capability {
    @NotBlank private String title;
    private String body;
    private String image;
  }

  @Getter
  @Setter
  public static class ProcessStep {
    @NotBlank private String title;
    private String image;
  }

  @Getter
  @Setter
  public static class Reason {
    @NotBlank private String title;
    private String body;
    private String image;
    private String sideImage;
  }
}
