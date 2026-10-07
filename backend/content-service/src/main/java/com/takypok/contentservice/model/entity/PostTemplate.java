package com.takypok.contentservice.model.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.takypok.core.model.IdEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString(callSuper = true)
@Table("post_template")
public class PostTemplate extends IdEntity {
  private Long siteId;
  private String name;
  private String description;
  private String layout;
  private String category;

  /** ["ai-governance","events"] */
  private JsonNode tags;

  private String body;
}
