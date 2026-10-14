package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** Footer copy that isn't a link column: the "Let's connect" block, social links and legal line. */
@Getter
@Setter
@ContentTypeKey("site_footer")
public class SiteFooterFields implements ContentFields {
  @NotBlank private String title;
  private String ctaLabel;
  private String ctaUrl;
  private String copyright;
  private String privacyLabel;
  private String privacyUrl;
  private String termsLabel;
  private String termsUrl;
  private String facebookUrl;
  private String twitterUrl;
  private String instagramUrl;
}
