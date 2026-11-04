package com.takypok.contentservice.model.content.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import com.takypok.contentservice.model.content.blocks.AboutBlock;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * The About page (/about). There is exactly one per site: a hero and SEO fields of its own and a
 * list of {@link AboutBlock}s for everything in between. A block with no content (or marked hidden)
 * is simply not rendered.
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

  /**
   * The sections below the hero, in page order. Each is one of the types {@link AboutBlock} lists,
   * and each may appear once.
   */
  @Valid
  @Size(max = 20)
  private List<AboutBlock> blocks;

  /** Search-engine title: when set it is the whole page title. */
  private String seoTitle;

  private String seoDescription;

  /** Social-share image (media URL): when blank the page's main image is used. */
  private String seoImage;

  /** Asks search engines not to index the page. */
  private Boolean seoNoindex;

  @JsonIgnore
  @AssertTrue(message = "each section may appear only once")
  public boolean isEachBlockTypeUnique() {
    return blocks == null
        || blocks.stream().map(b -> b.getProps().getClass()).distinct().count() == blocks.size();
  }

  @JsonIgnore
  @AssertTrue(message = "block ids must be unique")
  public boolean isBlockIdsUnique() {
    return blocks == null
        || blocks.stream().map(AboutBlock::getId).distinct().count() == blocks.size();
  }
}
