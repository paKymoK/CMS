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
@Table("testimonial")
public class Testimonial extends IdEntity {
  private Long siteId;
  private String name;
  private String company;
  private String photo;

  /** [{name, color}, {name, color}] — never translated, just a name+colour placeholder pair. */
  private JsonNode flankLogos;

  private String title;
  private String quote;
  private Integer displayOrder;
  private Boolean active;
  private String status;
  @Version private Integer version;
}
