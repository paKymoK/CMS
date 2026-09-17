package com.takypok.contentservice.model.entity;

import com.takypok.core.model.IdEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString(callSuper = true)
@Table("nav_section")
public class NavSection extends IdEntity {
  private Long siteId;
  private String anchor;
  private String label;
  private Integer displayOrder;
  private Boolean active;
  private String status;
  @Version private Integer version;
}
