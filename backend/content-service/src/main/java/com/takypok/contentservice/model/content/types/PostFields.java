package com.takypok.contentservice.model.content.types;

import com.fasterxml.jackson.databind.JsonNode;
import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * An article (Insights today; "category" lets it cover other post-like content such as press
 * releases). The slug is the URL segment of its detail page and is unique per site (enforced by the
 * uq_content_item_slug index and resolved by PostServiceImpl).
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
}
