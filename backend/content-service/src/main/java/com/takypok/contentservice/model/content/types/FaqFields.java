package com.takypok.contentservice.model.content.types;

import com.takypok.contentservice.model.annotation.ContentTypeKey;
import com.takypok.contentservice.model.content.ContentFields;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * First real content type on the generic content_item path — a working example of the "strict
 * tier", not a stand-in for any of the 9 existing typed tables. Demonstrates the whole flow: a
 * content_type row (see seed-content-types.sql) + this one small class is all a new
 * content_item-backed section needs, and {@code question}/{@code answer} are enforced by ordinary
 * Bean Validation, not just "whatever JSON the admin form happened to send".
 */
@Getter
@Setter
@ContentTypeKey("faq")
public class FaqFields implements ContentFields {
  @NotBlank private String question;
  @NotBlank private String answer;
}
