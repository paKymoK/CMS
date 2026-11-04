package com.takypok.contentservice.model.content.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import com.takypok.contentservice.model.content.blocks.IndustryBlock;
import com.takypok.contentservice.model.content.blocks.InsightsBlock;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * One page of the Industries section (/industries/{slug}), e.g. "Banking, Finance &amp; Insurance".
 * A page has a hero and SEO fields of its own and a list of {@link IndustryBlock}s for everything
 * in between. A block with no content (or marked hidden) is simply not rendered.
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

  /**
   * The sections below the hero, in page order. Each is one of the types {@link IndustryBlock}
   * lists; the insights strip may appear once.
   */
  @Valid
  @Size(max = 40)
  private List<IndustryBlock> blocks;

  /** Search-engine title: when set it is the whole page title. */
  private String seoTitle;

  private String seoDescription;

  /** Social-share image (media URL): when blank the page's main image is used. */
  private String seoImage;

  /** Asks search engines not to index the page. */
  private Boolean seoNoindex;

  @JsonIgnore
  @AssertTrue(message = "the insights strip may appear only once")
  public boolean isInsightsBlockUnique() {
    return blocks == null
        || blocks.stream().filter(b -> b.getProps() instanceof InsightsBlock).count() <= 1;
  }

  @JsonIgnore
  @AssertTrue(message = "block ids must be unique")
  public boolean isBlockIdsUnique() {
    return blocks == null
        || blocks.stream().map(IndustryBlock::getId).distinct().count() == blocks.size();
  }
}
