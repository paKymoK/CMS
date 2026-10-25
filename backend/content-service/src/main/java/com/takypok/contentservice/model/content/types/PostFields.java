package com.takypok.contentservice.model.content.types;

import com.fasterxml.jackson.databind.JsonNode;
import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

  /** ["ai-governance","events"] */
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
}
