package com.takypok.contentservice.model.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.takypok.core.model.IdEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString(callSuper = true)
@Table("footer_nav_category")
public class FooterNavCategory extends IdEntity {
  private Long siteId;
  private String label;

  /** string[] of link labels. */
  private JsonNode links;

  private Integer displayOrder;
  private Boolean active;
  private String status;
  @Version private Integer version;
}
