package com.takypok.contentservice.model.content.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.annotation.JsonLd;
import com.takypok.contentservice.model.content.ContentFields;
import com.takypok.contentservice.model.content.blocks.PageBlock;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * The About page (/about). There is exactly one per site: a hero and SEO fields of its own and a
 * list of {@link PageBlock}s for everything in between. A block with no content (or marked hidden)
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
   * The sections below the hero, in page order. Each is one of the types {@link PageBlock} lists,
   * and each may appear once.
   */
  @Valid
  @Size(max = 20)
  private List<PageBlock> blocks;

  /**
   * The parent page's id (any page type); empty for a top-level page. Structure only: the address
   * does not change.
   */
  private Long parentId;

  /**
   * The page's thumbnail (media URL) for cards, lists and sharing; when blank the hero image is
   * used.
   */
  private String featuredImage;

  /**
   * A short description for cards and lists; when blank the first 55 words of the hero subtitle are
   * used.
   */
  @Size(max = 500)
  private String summary;

  /**
   * Show the creator's name publicly on the page (the website has no byline design yet: it is only
   * offered to it).
   */
  private Boolean showAuthor;

  /** Search-engine title: when set it is the whole page title. */
  private String seoTitle;

  private String seoDescription;

  /** Social-share image (media URL): when blank the page's main image is used. */
  private String seoImage;

  /** Asks search engines not to index the page. */
  private Boolean seoNoindex;

  /** Canonical address (https only); blank means the page's own address. */
  @Pattern(regexp = "^$|^https://\\S+$", message = "must be an https:// address")
  @Size(max = 2000)
  private String seoCanonical;

  /** Asks search engines not to follow the page's links (independent of noindex). */
  private Boolean seoNofollow;

  /** Open Graph title: when blank the search-engine title is used. */
  @Size(max = 255)
  private String seoOgTitle;

  /** Open Graph description: when blank the search-engine description is used. */
  @Size(max = 500)
  private String seoOgDescription;

  /** Structured data (JSON-LD) added to the page: one JSON object, or a list of them. */
  @JsonLd private String seoJsonLd;

  @JsonIgnore
  @AssertTrue(message = "intro, vision, mission, leaders and values may each appear only once")
  public boolean isEachBlockTypeUnique() {
    return PageBlock.singleUseOnce(blocks);
  }

  @JsonIgnore
  @AssertTrue(message = "block ids must be unique")
  public boolean isBlockIdsUnique() {
    return PageBlock.idsUnique(blocks);
  }
}
