package com.takypok.contentservice.model.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.takypok.core.model.IdEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Registry row for a content type available through content_item — global, not site-scoped (see
 * add-content-type-registry.sql's comment: a type's shape is the same in every region). {@code key}
 * matches a {@link com.takypok.contentservice.model.annotation.ContentTypeKey} value.
 */
@Getter
@Setter
@ToString(callSuper = true)
@Table("content_type")
public class ContentType extends IdEntity {
  private String key;
  private String label;
  private JsonNode fieldSchema;
  private Boolean active;
}
