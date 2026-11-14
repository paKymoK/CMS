package com.takypok.contentservice.model.content.types;

import com.fasterxml.jackson.databind.JsonNode;
import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.annotation.JsonLd;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * An article (Insights today; "category" lets it cover other post-like content such as press
 * releases). The slug is the URL segment of its detail page and is unique per site (enforced by the
 * content_slug table, which also keeps its old slugs, and resolved by PostServiceImpl).
 */
@Getter
@Setter
@ContentTypeKey("post")
public class PostFields implements ContentFields {
  @NotBlank private String title;
  private String slug;

  /**
   * The post's categories and tags, by id (the source of truth), in the order they were chosen. At
   * most 15 of both together, and always at least one category (the default "Uncategorized").
   */
  private List<Long> categoryIds;

  private List<Long> tagIds;

  /**
   * Derived from {@link #categoryIds}: the name of the first active category. Kept in the post so
   * the website and the search read it without a lookup; rewritten whenever the post or the
   * category changes.
   */
  private String category;

  private String image;
  private String excerpt;
  private String date;

  /** Rich text (HTML), sanitized by PostServiceImpl before it is stored. */
  private String body;

  private String authorName;
  private String authorRole;
  private String authorBio;
  private String authorAvatar;

  /** Derived from tagIds: the names of its active tags, e.g. ["Events", "AI & Automation"]. */
  private JsonNode tags;

  private Boolean featured;

  /** default | focused | wide | landing — see PostServiceImpl.LAYOUTS. */
  private String layout;

  /** Search-engine title: when set it is the whole page title. */
  @Size(max = 255)
  private String seoTitle;

  /** Search-engine description: when blank the excerpt is used. */
  @Size(max = 500)
  private String seoDescription;

  /** Social-share image (media URL): when blank the cover image is used. */
  private String seoImage;

  /** Asks search engines not to index the post. */
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
}
