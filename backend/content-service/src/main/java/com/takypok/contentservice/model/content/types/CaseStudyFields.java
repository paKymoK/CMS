package com.takypok.contentservice.model.content.types;

import com.fasterxml.jackson.databind.JsonNode;
import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * A case study. The slug is the URL segment of its detail page and is unique per site (enforced by
 * the content_slug table, which also keeps its old slugs, and resolved by CaseStudyServiceImpl).
 */
@Getter
@Setter
@ContentTypeKey("case_study")
public class CaseStudyFields implements ContentFields {
  @NotBlank private String title;
  private String slug;

  /** Display string (e.g. "Jun 2, 2026"), not a real date type. */
  private String date;

  private String image;
  private String category;
  private String summary;

  /** Rich text (HTML), sanitized by CaseStudyServiceImpl before it is stored. */
  private String body;

  /** [{"value":"40%","label":"Faster deployment cycle"}] — headline figures on the detail page. */
  private JsonNode results;

  /** content_item id of a "testimonial" item of the same site; null when there is no quote. */
  private Long testimonialId;

  /** Search-engine title: when set it is the whole page title. */
  @Size(max = 255)
  private String seoTitle;

  /** Search-engine description: when blank the summary is used. */
  @Size(max = 500)
  private String seoDescription;

  /** Social-share image (media URL): when blank the cover image is used. */
  private String seoImage;

  /** Asks search engines not to index the case study. */
  private Boolean seoNoindex;
}
