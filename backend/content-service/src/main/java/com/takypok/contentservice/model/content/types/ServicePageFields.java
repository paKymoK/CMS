package com.takypok.contentservice.model.content.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import com.takypok.contentservice.model.content.blocks.HighlightsBlock;
import com.takypok.contentservice.model.content.blocks.IndustriesBlock;
import com.takypok.contentservice.model.content.blocks.ServiceBlock;
import com.takypok.contentservice.model.content.blocks.StoriesBlock;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * One page of the Services section (/services/{category}/{slug}), e.g. "Web App Development". Every
 * service page has a hero and a search/SEO block of its own fields, and a list of {@link
 * ServiceBlock}s for everything in between. A block with no content (or marked hidden) is simply
 * not rendered.
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

  /**
   * The sections below the hero, in page order. Each is one of the types {@link ServiceBlock}
   * lists; the stories, industries and highlights sections may appear once.
   */
  @Valid
  @Size(max = 40)
  private List<ServiceBlock> blocks;

  /** Search-engine title: when set it is the whole page title. */
  private String seoTitle;

  private String seoDescription;

  /** Social-share image (media URL): when blank the page's main image is used. */
  private String seoImage;

  /** Asks search engines not to index the page. */
  private Boolean seoNoindex;

  @JsonIgnore
  @AssertTrue(message = "stories, industries and highlights may each appear only once")
  public boolean isSingleUseBlocksUnique() {
    if (blocks == null) return true;
    long stories = blocks.stream().filter(b -> b.getProps() instanceof StoriesBlock).count();
    long industries = blocks.stream().filter(b -> b.getProps() instanceof IndustriesBlock).count();
    long highlights = blocks.stream().filter(b -> b.getProps() instanceof HighlightsBlock).count();
    return stories <= 1 && industries <= 1 && highlights <= 1;
  }

  @JsonIgnore
  @AssertTrue(message = "block ids must be unique")
  public boolean isBlockIdsUnique() {
    if (blocks == null) return true;
    return blocks.stream().map(ServiceBlock::getId).distinct().count() == blocks.size();
  }
}
